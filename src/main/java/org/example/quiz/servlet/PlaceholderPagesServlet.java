package org.example.quiz.servlet;

import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.QuizAttempt;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;

// ADDED by Part D (uncomment to enable achievements + popular quizzes):
// import org.example.quiz.dao.AchievementDAO;
// import org.example.quiz.dao.QuizReadDAO;
// import org.example.quiz.model.Achievement;
// import org.example.quiz.model.AchievementType;
// import org.example.quiz.util.AchievementDisplay;

// ADDED by Part D:
// private final AchievementDAO achievementDao = new AchievementDAO();
// private final QuizReadDAO quizReadDao = new QuizReadDAO();

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * PLACEHOLDER pages for /home and /profile so Part C's links never 404.
 * Part D owns the real homepage; Part A/D own the real profile page.
 * These render just enough to demo the social + quiz-taking flow.
 */
@WebServlet({"/home", "/profile"})
public class PlaceholderPagesServlet extends HttpServlet {

    private final UserLookupDAO userDao = new UserLookupDAO();
    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        String path = req.getServletPath();
        try {
            if ("/profile".equals(path)) {
                long target = parse(req.getParameter("userId"), userId);
                User u = userDao.getById(target);
                req.setAttribute("profileUser", u);
                req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
            } else {
                List<QuizAttempt> history = attemptDao.getUserHistory(userId, 10);
                req.setAttribute("history", history);
                // ADDED by Part D — achievements:
                // List<Achievement> achievements = achievementDao.getForUser(userId);
                // for (Achievement a : achievements) {
                //     AchievementType t = AchievementType.valueOf(a.getAchievementCode());
                //     a.setLabel(AchievementDisplay.getLabel(t));
                //     a.setDescription(AchievementDisplay.getDescription(t));
                // }
                // req.setAttribute("achievements", achievements);

                // ADDED by Part D — popular quizzes:
                // req.setAttribute("popularQuizzes", quizReadDao.getPopular(5));

                req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
            }
        } catch (SQLException e) { throw new ServletException(e); }
    }

    private long parse(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }
}
