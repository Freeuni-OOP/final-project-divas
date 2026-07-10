package org.example.quiz.servlet;

import org.example.quiz.dao.QuizDao;
import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.Quiz;
import org.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

//GET /quiz?id=.. shows the quiz's description, creator link, and a way to start the quiz
@WebServlet("/quiz")
public class QuizSummaryServlet extends HttpServlet {

    private final QuizDao quizDao = new QuizDao();
    private final UserLookupDAO userDao = new UserLookupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long quizId = parseLong(req.getParameter("id"));
        if (quizId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        try {
            Quiz quiz = quizDao.getQuizById(quizId);
            if (quiz == null) {
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            User creator = userDao.getById(quiz.getCreatorId());
            req.setAttribute("quiz", quiz);
            req.setAttribute("creator", creator);
            req.getRequestDispatcher("/WEB-INF/jsp/quiz_summary.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return -1;
        }
    }
}
