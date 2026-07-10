package org.example.quiz.servlet;

import org.example.quiz.dao.FriendshipDAO;
import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.dao.QuizDao;
import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.Quiz;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/quiz")
public class QuizSummaryServlet extends HttpServlet {

    private final QuizDao quizDao = new QuizDao();
    private final UserLookupDAO userDao = new UserLookupDAO();
    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();
    private final FriendshipDAO friendDao = new FriendshipDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

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
            req.setAttribute("myHistory", attemptDao.getUserHistoryForQuiz(userId, quizId));
            req.setAttribute("topScorers", attemptDao.getTopScorers(quizId, 10));
            req.setAttribute("topRecent", attemptDao.getTopScorersRecent(quizId, 1440, 10));
            req.setAttribute("recentAttempts", attemptDao.getRecentAttempts(quizId, 10));
            double[] stats = attemptDao.getQuizStats(quizId);
            req.setAttribute("attemptsCount", (int) stats[0]);
            req.setAttribute("avgPercent", Math.round(stats[1]));
            req.setAttribute("avgTime", Math.round(stats[2]));
            req.setAttribute("friends", friendDao.getFriends(userId));
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
