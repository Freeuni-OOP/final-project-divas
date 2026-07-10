package org.example.quiz.servlet;

import org.example.quiz.dao.AdminDAO;
import org.example.quiz.dao.AnnouncementDAO;
import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.Announcement;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

//GET shows the admin dashboard, POST handles the admin actions based on the "action" parameter

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final AdminDAO adminDao = new AdminDAO();
    private final AnnouncementDAO announcementDao = new AnnouncementDAO();
    private final UserLookupDAO userDao = new UserLookupDAO();
    private final org.example.quiz.dao.QuizDao quizDao = new org.example.quiz.dao.QuizDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        try {
            req.setAttribute("announcements", announcementDao.getActiveAnnouncements());
            req.setAttribute("userCount", adminDao.countUsers());
            req.setAttribute("quizzesTaken", adminDao.countQuizzesTaken());
            req.setAttribute("allQuizzes", quizDao.getAll());
            req.setAttribute("error", req.getParameter("error"));
            req.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        long adminId = SessionUtil.currentUserId(req);
        String action = req.getParameter("action");
        try {
            switch (action == null ? "" : action) {
                case "createAnnouncement": {
                    Announcement a = new Announcement(
                            req.getParameter("title"),
                            req.getParameter("body"),
                            adminId,
                            true);
                    announcementDao.createAnnouncement(a);
                    break;
                }
                case "editAnnouncement": {
                    long id = parseLong(req.getParameter("id"));
                    announcementDao.editAnnouncement(id, req.getParameter("title"), req.getParameter("body"));
                    break;
                }
                case "deactivateAnnouncement": {
                    long id = parseLong(req.getParameter("id"));
                    announcementDao.deactivateAnnouncement(id);
                    break;
                }
                case "removeUser": {
                    String username = req.getParameter("username");
                    User target = userDao.getByUsername(username);
                    if (target != null && target.getId() != adminId) {
                        adminDao.removeUser(target.getId());
                    }
                    break;
                }
                case "removeQuiz": {
                    long quizId = parseLong(req.getParameter("quizId"));
                    adminDao.removeQuiz(quizId);
                    break;
                }
                case "promoteToAdmin": {
                    String username = req.getParameter("username");
                    User target = userDao.getByUsername(username);
                    if (target != null) {
                        adminDao.promoteToAdmin(target.getId());
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/admin?error=User+not+found");
                        return;
                    }
                    break;
                }
                case "clearQuizHistory": {
                    long quizId = parseLong(req.getParameter("quizId"));
                    adminDao.clearQuizHistory(quizId);
                    break;
                }
                default:
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }

        resp.sendRedirect(req.getContextPath() + "/admin");
    }

    private boolean isAdmin(HttpServletRequest req) throws ServletException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) return false;
        try {
            User user = userDao.getById(userId);
            return user != null && user.isAdmin();
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
