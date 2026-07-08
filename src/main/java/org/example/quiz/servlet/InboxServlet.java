package org.example.quiz.servlet;

import org.example.quiz.dao.FriendshipDAO;
import org.example.quiz.dao.MessageDAO;
import org.example.quiz.dao.QuizAttemptDAO;
import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.Message;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Internal mail inbox. (Part C)
 *
 * GET  /inbox                                  -> list messages (read/unread)
 * POST /inbox action=read&id=..                -> mark read
 * POST /inbox action=delete&id=..              -> delete
 * POST /inbox action=note&to=..&body=..        -> send a NOTE
 * POST /inbox action=challenge&to=..&quizId=.. -> send a CHALLENGE
 * POST /inbox action=acceptFriend&from=..      -> accept a friend request from mail
 * POST /inbox action=rejectFriend&from=..      -> reject a friend request from mail
 */
@WebServlet("/inbox")
public class InboxServlet extends HttpServlet {

    private final MessageDAO messageDao = new MessageDAO();
    private final FriendshipDAO friendDao = new FriendshipDAO();
    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();
    private final UserLookupDAO userDao = new UserLookupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        try {
            List<Message> inbox = messageDao.getInbox(userId);
            req.setAttribute("inbox", inbox);
            req.setAttribute("unread", messageDao.countUnread(userId));
            req.getRequestDispatcher("/WEB-INF/jsp/inbox.jsp").forward(req, resp);
        } catch (SQLException e) { throw new ServletException(e); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        String action = req.getParameter("action");
        try {
            switch (action == null ? "" : action) {
                case "read":
                    messageDao.markRead(parseLong(req.getParameter("id")), userId);
                    break;
                case "delete":
                    messageDao.delete(parseLong(req.getParameter("id")), userId);
                    break;
                case "note": {
                    User to = userDao.getByUsername(req.getParameter("to"));
                    String body = req.getParameter("body");
                    if (to != null) messageDao.sendNote(userId, to.getId(), body);
                    break;
                }
                case "challenge": {
                    long to = parseLong(req.getParameter("to"));
                    long quizId = parseLong(req.getParameter("quizId"));
                    if (to > 0 && quizId > 0) {
                        int[] best = attemptDao.getBestScore(userId, quizId);
                        messageDao.sendChallenge(userId, to, quizId, best[0], best[1]);
                    }
                    break;
                }
                case "acceptFriend": {
                    long from = parseLong(req.getParameter("from"));
                    friendDao.accept(from, userId);
                    break;
                }
                case "rejectFriend": {
                    long from = parseLong(req.getParameter("from"));
                    friendDao.reject(from, userId);
                    break;
                }
                default: /* no-op */ ;
            }
        } catch (SQLException e) { throw new ServletException(e); }

        resp.sendRedirect(req.getContextPath() + "/inbox");
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return -1; }
    }
}
