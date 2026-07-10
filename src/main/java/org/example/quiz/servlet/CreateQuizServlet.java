package org.example.quiz.servlet;

import org.example.quiz.dao.QuestionDAO;
import org.example.quiz.dao.QuizDao;
import org.example.quiz.model.Question;
import org.example.quiz.model.QuestionAnswer;
import org.example.quiz.model.QuestionOption;
import org.example.quiz.model.Quiz;
import org.example.quiz.util.AchievementChecker;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 'GET' shows the quiz creation form
 * 'POST' saves the quiz and its questions
 */
@WebServlet("/createQuiz")
public class CreateQuizServlet extends HttpServlet {

    private final QuizDao quizDao = new QuizDao();
    private final QuestionDAO questionDao = new QuestionDAO();
    private final AchievementChecker achievementChecker = new AchievementChecker();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/quiz_create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            Quiz quiz = new Quiz();
            quiz.setTitle(req.getParameter("title"));
            quiz.setDescription(req.getParameter("description"));
            quiz.setCreatorId(userId);
            quiz.setRandomize(req.getParameter("randomize") != null);
            quiz.setMultiPage(req.getParameter("multiPage") != null);
            quiz.setImmediateCorrect(req.getParameter("immediateCorrect") != null);
            quiz.setPracticeAllowed(req.getParameter("practiceAllowed") != null);
            long quizId = quizDao.createQuiz(quiz);

            int questionCount = parseInt(req.getParameter("questionCount"));
            for (int i = 0; i < questionCount; i++) {
                String type = req.getParameter("type" + i);
                String text = req.getParameter("text" + i);

                Question question = new Question(text);
                question.setQuizId(quizId);
                question.setQuestionType(type);
                question.setNum(i);
                if ("PICTURE_RESPONSE".equals(type)) {
                    question.setImageUrl(req.getParameter("image" + i));
                }
                long questionId = questionDao.createQuestion(question);

                if ("MULTIPLE_CHOICE".equals(type)) {
                    String[] options = req.getParameterValues("option" + i);
                    int correctIndex = parseInt(req.getParameter("correct" + i));
                    List<QuestionOption> optionList = new ArrayList<>();
                    if (options != null) {
                        for (int j = 0; j < options.length; j++) {
                            QuestionOption option = new QuestionOption(options[j]);
                            option.setCorrect(j == correctIndex);
                            option.setPosition(j);
                            optionList.add(option);
                        }
                    }
                    questionDao.addOptions(questionId, optionList);
                } else {
                    String[] answers = req.getParameterValues("answer" + i);
                    List<QuestionAnswer> answerList = new ArrayList<>();
                    if (answers != null) {
                        for (String a : answers) {
                            answerList.add(new QuestionAnswer(a));
                        }
                    }
                    questionDao.addAcceptedAnswers(questionId, answerList);
                }
            }

            achievementChecker.onQuizCreated(userId);

            resp.sendRedirect(req.getContextPath() + "/quiz?id=" + quizId);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
