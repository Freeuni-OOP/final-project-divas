package org.example.quiz.servlet;

import org.example.quiz.dao.AchievementDAO;
import org.example.quiz.dao.AnnouncementDAO;
import org.example.quiz.dao.MessageDAO;
import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.dao.QuizDao;
import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.Achievement;
import org.example.quiz.model.AchievementType;
import org.example.quiz.model.Message;
import org.example.quiz.model.QuizAttempt;
import org.example.quiz.model.User;
import org.example.quiz.util.AchievementDisplay;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet({"/home", "/profile"})
public class PagesServlet extends HttpServlet {

    private final UserLookupDAO userDao = new UserLookupDAO();
    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();
    private final AchievementDAO achievementDao = new AchievementDAO();
    private final QuizDao quizDao = new QuizDao();
    private final AnnouncementDAO announcementDao = new AnnouncementDAO();
    private final MessageDAO messageDao = new MessageDAO();

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
                if (u != null) {
                    req.setAttribute("profileAchievements", labeled(achievementDao.getForUser(target)));
                    req.setAttribute("profileQuizzes", quizDao.getByCreator(target, 20));
                }
                req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
            } else {
                req.setAttribute("announcements", announcementDao.getActiveAnnouncements());
                req.setAttribute("history", attemptDao.getUserHistory(userId, 10));
                req.setAttribute("achievements", labeled(achievementDao.getForUser(userId)));
                req.setAttribute("popularQuizzes", quizDao.getPopular(5));
                req.setAttribute("recentQuizzes", quizDao.getRecent(5));
                req.setAttribute("myQuizzes", quizDao.getByCreator(userId, 5));
                req.setAttribute("friendActivity", attemptDao.getFriendsRecentActivity(userId, 10));
                req.setAttribute("unread", messageDao.countUnread(userId));
                List<Message> inbox = messageDao.getInbox(userId);
                req.setAttribute("recentMessages", inbox.size() > 3 ? inbox.subList(0, 3) : inbox);
                req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
            }
        } catch (SQLException e) { throw new ServletException(e); }
    }

    private List<Achievement> labeled(List<Achievement> achievements) {
        for (Achievement a : achievements) {
            AchievementType t = AchievementType.valueOf(a.getAchievementCode());
            a.setLabel(AchievementDisplay.getLabel(t));
            a.setDescription(AchievementDisplay.getDescription(t));
        }
        return achievements;
    }

    private long parse(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }
}
