package org.example.quiz.servlet;

import org.example.quiz.dao.AchievementDAO;
import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.model.Achievement;
import org.example.quiz.model.AchievementType;
import org.example.quiz.util.AchievementDisplay;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    private final QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
    private final AchievementDAO achievementDAO = new AchievementDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!SessionUtil.isLoggedIn(req)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        long userId = SessionUtil.currentUserId(req);
        try {
            req.setAttribute("history", attemptDAO.getUserHistory(userId, 50));

            List<Achievement> achievements = achievementDAO.getForUser(userId);
            for (Achievement a : achievements) {
                AchievementType t = AchievementType.valueOf(a.getAchievementCode());
                a.setLabel(AchievementDisplay.getLabel(t));
                a.setDescription(AchievementDisplay.getDescription(t));
            }
            req.setAttribute("achievements", achievements);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
        req.getRequestDispatcher("/WEB-INF/jsp/history.jsp").forward(req, resp);
    }
}