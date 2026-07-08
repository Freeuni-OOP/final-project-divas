package org.example.quiz.servlet;

import org.example.quiz.dao.FriendshipDAO;
import org.example.quiz.dao.MessageDAO;
import org.example.quiz.model.Friendship;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Friend management. (Part C)
 *
 * GET  /friends                          -> friends list + incoming requests
 * POST /friends  action=request&to=..    -> send friend request
 * POST /friends  action=accept&from=..   -> accept request
 * POST /friends  action=reject&from=..   -> reject request
 * POST /friends  action=remove&other=..  -> remove a friend
 */
@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    private final FriendshipDAO friendDao = new FriendshipDAO();
    private final MessageDAO messageDao = new MessageDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }
        try {
            List<User> friends = friendDao.getFriends(userId);
            List<Friendship> incoming = friendDao.getIncomingRequests(userId);
            req.setAttribute("friends", friends);
            req.setAttribute("incoming", incoming);
            req.getRequestDispatcher("/WEB-INF/jsp/friends.jsp").forward(req, resp);
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
                case "request": {
                    long to = parseLong(req.getParameter("to"));
                    if (to > 0 && to != userId
                            && friendDao.getRelationship(userId, to) == null) {
                        friendDao.sendRequest(userId, to);
                        messageDao.sendFriendRequest(userId, to);
                    }
                    break;
                }
                case "accept": {
                    long from = parseLong(req.getParameter("from"));
                    friendDao.accept(from, userId);
                    break;
                }
                case "reject": {
                    long from = parseLong(req.getParameter("from"));
                    friendDao.reject(from, userId);
                    break;
                }
                case "remove": {
                    long other = parseLong(req.getParameter("other"));
                    friendDao.removeFriend(userId, other);
                    break;
                }
                default: /* no-op */ ;
            }
        } catch (SQLException e) { throw new ServletException(e); }

        resp.sendRedirect(req.getContextPath() + "/friends");
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return -1; }
    }
}
