package org.example.quiz.servlet;

import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.dao.QuizReadDAO;
import org.example.quiz.model.Question;
import org.example.quiz.model.QuestionAnswer;
import org.example.quiz.model.Quiz;
import org.example.quiz.model.QuizAttempt;
import org.example.quiz.util.AchievementChecker;
import org.example.quiz.util.Grader;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Quiz-taking flow.
 *
 * GET  /take?quizId=..[&practice=true]  -> start the quiz
 * POST /take                            -> submit answers
 *
 * Supports both single-page (all questions at once) and multi-page
 * (one question per page) modes, with optional immediate correction on
 * multi-page. State for an in-progress attempt lives in the session.
 */
@WebServlet("/take")
public class TakeQuizServlet extends HttpServlet {

    private final QuizReadDAO quizDao = new QuizReadDAO();
    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();
    private final AchievementChecker achievementChecker = new AchievementChecker();

    /** Session key holding the in-progress attempt state. */
    private static final String ATTEMPT = "takeState";

    /** Server-side state for one in-progress attempt. */
    static class TakeState {
        long quizId;
        boolean practice;
        boolean multiPage;
        boolean immediateCorrect;
        long startMillis;
        List<Long> order = new ArrayList<>();   // question ids in presentation order
        int index;                               // current question (multi-page)
        int correct;                             // running correct-slot count
        int total;                               // running total-slot count
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        // Resume flow: after immediate-correction feedback, show next question
        // (or finish) using the state already in the session.
        if ("1".equals(req.getParameter("resume"))) {
            handleResume(req, resp, userId);
            return;
        }

        long quizId = parseLong(req.getParameter("quizId"), -1);
        boolean practice = "true".equals(req.getParameter("practice"));
        if (quizId <= 0) { resp.sendRedirect(req.getContextPath() + "/home"); return; }

        Quiz quiz;
        try {
            quiz = quizDao.getQuizWithQuestions(quizId);
        } catch (SQLException e) { throw new ServletException(e); }
        if (quiz == null || quiz.getQuestions().isEmpty()) {
            req.setAttribute("error", "This quiz has no questions.");
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
            return;
        }

        // Build presentation order (respecting randomize option).
        TakeState st = new TakeState();
        st.quizId = quizId;
        st.practice = practice && quiz.isPracticeAllowed();
        st.multiPage = quiz.isMultiPage();
        st.immediateCorrect = quiz.isImmediateCorrect();
        st.startMillis = System.currentTimeMillis();
        for (Question q : quiz.getQuestions()) st.order.add(q.getId());
        if (quiz.isRandomize()) Collections.shuffle(st.order);

        req.getSession().setAttribute(ATTEMPT, st);
        req.setAttribute("quiz", quiz);

        if (st.multiPage) {
            forwardCurrentQuestion(req, resp, quiz, st);
        } else {
            try {
                req.setAttribute("optionsMap", buildOptionsMap(quiz));
            } catch (SQLException e) { throw new ServletException(e); }
            req.getRequestDispatcher("/WEB-INF/jsp/take_single.jsp").forward(req, resp);
        }
    }

    /** Options for every MULTIPLE_CHOICE question in the quiz, keyed by question id (for the single-page JSP). */
    private Map<Long, List<org.example.quiz.model.QuestionOption>> buildOptionsMap(Quiz quiz) throws SQLException {
        Map<Long, List<org.example.quiz.model.QuestionOption>> map = new HashMap<>();
        for (Question q : quiz.getQuestions()) {
            map.put(q.getId(), quizDao.getOptions(q.getId()));
        }
        return map;
    }

    /** Continue a multi-page immediate-correction quiz after feedback. */
    private void handleResume(HttpServletRequest req, HttpServletResponse resp, long userId)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        TakeState st = (TakeState) session.getAttribute(ATTEMPT);
        if (st == null) { resp.sendRedirect(req.getContextPath() + "/home"); return; }
        try {
            Quiz quiz = quizDao.getQuizWithQuestions(st.quizId);
            if (st.index < st.order.size()) {
                forwardCurrentQuestion(req, resp, quiz, st);
            } else {
                int seconds = (int) ((System.currentTimeMillis() - st.startMillis) / 1000);
                finish(req, resp, st, userId, st.correct, st.total, seconds, null, session);
            }
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        HttpSession session = req.getSession();
        TakeState st = (TakeState) session.getAttribute(ATTEMPT);
        if (st == null) { resp.sendRedirect(req.getContextPath() + "/home"); return; }

        try {
            Quiz quiz = quizDao.getQuizWithQuestions(st.quizId);
            if (quiz == null) { resp.sendRedirect(req.getContextPath() + "/home"); return; }

            if (st.multiPage) {
                handleMultiPagePost(req, resp, quiz, st, userId, session);
            } else {
                handleSinglePagePost(req, resp, quiz, st, userId, session);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    // ---- single page: grade everything at once ----
    private void handleSinglePagePost(HttpServletRequest req, HttpServletResponse resp,
                                      Quiz quiz, TakeState st, long userId, HttpSession session)
            throws SQLException, ServletException, IOException {
        int correct = 0, total = 0;
        List<GradedAnswer> details = new ArrayList<>();

        for (Question q : quiz.getQuestions()) {
            List<String> responses = collectResponses(req, q);
            Grader.Result r = Grader.grade(q, quizDao.getOptions(q.getId()), quizDao.getAnswers(q.getId()), responses);
            correct += r.correct;
            total += r.total;
            details.add(new GradedAnswer(q, responses, r.correct, r.total));
        }
        int seconds = (int) ((System.currentTimeMillis() - st.startMillis) / 1000);
        finish(req, resp, st, userId, correct, total, seconds, details, session);
    }

    // ---- multi page: grade one question per POST ----
    private void handleMultiPagePost(HttpServletRequest req, HttpServletResponse resp,
                                     Quiz quiz, TakeState st, long userId, HttpSession session)
            throws SQLException, ServletException, IOException {
        Question current = findById(quiz, st.order.get(st.index));
        List<String> responses = collectResponses(req, current);
        List<QuestionAnswer> currentAnswers = quizDao.getAnswers(current.getId());
        Grader.Result r = Grader.grade(current, quizDao.getOptions(current.getId()), currentAnswers, responses);
        st.correct += r.correct;
        st.total += r.total;

        // Immediate correction: show feedback for this question before moving on.
        if (st.immediateCorrect) {
            req.setAttribute("quiz", quiz);
            req.setAttribute("question", current);
            req.setAttribute("responses", responses);
            req.setAttribute("gotCorrect", r.correct);
            req.setAttribute("outOf", r.total);
            req.setAttribute("acceptedAnswers", currentAnswers);
            st.index++;
            req.setAttribute("hasNext", st.index < st.order.size());
            req.getRequestDispatcher("/WEB-INF/jsp/take_feedback.jsp").forward(req, resp);
            return;
        }

        st.index++;
        if (st.index < st.order.size()) {
            forwardCurrentQuestion(req, resp, quiz, st);
        } else {
            int seconds = (int) ((System.currentTimeMillis() - st.startMillis) / 1000);
            finish(req, resp, st, userId, st.correct, st.total, seconds, null, session);
        }
    }

    /** Advance page: used by immediate-correction "Next" button (GET). */
    private void forwardCurrentQuestion(HttpServletRequest req, HttpServletResponse resp,
                                        Quiz quiz, TakeState st)
            throws ServletException, IOException {
        Question q = findById(quiz, st.order.get(st.index));
        req.setAttribute("quiz", quiz);
        req.setAttribute("question", q);
        req.setAttribute("questionNumber", st.index + 1);
        req.setAttribute("questionCount", st.order.size());
        try {
            Map<Long, List<org.example.quiz.model.QuestionOption>> map = new HashMap<>();
            map.put(q.getId(), quizDao.getOptions(q.getId()));
            req.setAttribute("optionsMap", map);
        } catch (SQLException e) { throw new ServletException(e); }
        req.getRequestDispatcher("/WEB-INF/jsp/take_multi.jsp").forward(req, resp);
    }

    private void finish(HttpServletRequest req, HttpServletResponse resp, TakeState st,
                        long userId, int correct, int total, int seconds,
                        List<GradedAnswer> details, HttpSession session)
            throws SQLException, ServletException, IOException {
        // Record the attempt (practice attempts are stored but flagged).
        QuizAttempt a = new QuizAttempt();
        a.setQuizId(st.quizId);
        a.setUserId(userId);
        a.setScoreCorrect(correct);
        a.setScoreTotal(total);
        a.setTimeSeconds(seconds);
        a.setPractice(st.practice);
        long attemptId = attemptDao.record(a);
        a.setId(attemptId);

        achievementChecker.onQuizSubmitted(a);

        session.removeAttribute(ATTEMPT);

        // Redirect to the results page (Post/Redirect/Get avoids re-submit).
        if (details != null) session.setAttribute("lastDetails", details);
        resp.sendRedirect(req.getContextPath() + "/results?attemptId=" + attemptId);
    }

    // ---- helpers ----

    /** Collect the user's response for a question from request params, named "q{id}". */
    private List<String> collectResponses(HttpServletRequest req, Question q) {
        List<String> out = new ArrayList<>();
        String v = req.getParameter("q" + q.getId());
        out.add(v == null ? "" : v);
        return out;
    }

    private Question findById(Quiz quiz, long id) {
        for (Question q : quiz.getQuestions()) if (q.getId() == id) return q;
        return null;
    }

    private long parseLong(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }

    /** Bundle used to render per-answer detail on the results page. */
    public static class GradedAnswer {
        public final Question question;
        public final List<String> responses;
        public final int correct;
        public final int total;
        public GradedAnswer(Question q, List<String> r, int c, int t) {
            this.question = q; this.responses = r; this.correct = c; this.total = t;
        }
    }
}

