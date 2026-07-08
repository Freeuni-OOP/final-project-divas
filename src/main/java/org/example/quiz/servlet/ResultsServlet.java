package org.example.quiz.servlet;

import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.dao.QuizReadDAO;
import org.example.quiz.model.Quiz;
import org.example.quiz.model.QuizAttempt;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Quiz Results page. (Part C)
 *
 * GET /results?attemptId=..  -> show score, time, and comparison to the
 * user's past performance + top scorers.
 */
@WebServlet("/results")
public class ResultsServlet extends HttpServlet {

    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();
    private final QuizReadDAO quizDao = new QuizReadDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        long attemptId;
        try { attemptId = Long.parseLong(req.getParameter("attemptId")); }
        catch (Exception e) { resp.sendRedirect(req.getContextPath() + "/home"); return; }

        try {
            QuizAttempt attempt = attemptDao.getById(attemptId);
            if (attempt == null || attempt.getUserId() != userId) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            Quiz quiz = quizDao.getQuiz(attempt.getQuizId());
            List<QuizAttempt> history =
                    attemptDao.getUserHistoryForQuiz(userId, attempt.getQuizId());
            List<QuizAttempt> topScorers =
                    attemptDao.getTopScorers(attempt.getQuizId(), 10);

            req.setAttribute("attempt", attempt);
            req.setAttribute("quiz", quiz);
            req.setAttribute("history", history);
            req.setAttribute("topScorers", topScorers);
            // per-answer detail (single-page only), stashed by TakeQuizServlet
            req.setAttribute("details", req.getSession().getAttribute("lastDetails"));
            req.getSession().removeAttribute("lastDetails");

            req.getRequestDispatcher("/WEB-INF/jsp/results.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
