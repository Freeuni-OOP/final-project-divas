package org.example.quiz.servlet;

import org.example.quiz.dao.UserLookupDAO;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Search users by username. (Part C)
 *
 * GET /search?q=..  -> list of matching users with links to their profiles.
 */
@WebServlet("/search")
public class UserSearchServlet extends HttpServlet {

    private final UserLookupDAO userDao = new UserLookupDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = SessionUtil.currentUserId(req);
        if (userId <= 0) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        String term = req.getParameter("q");
        List<User> results = Collections.emptyList();
        if (term != null && !term.trim().isEmpty()) {
            try {
                results = userDao.search(term.trim(), userId, 50);
            } catch (SQLException e) { throw new ServletException(e); }
        }
        req.setAttribute("term", term);
        req.setAttribute("results", results);
        req.getRequestDispatcher("/WEB-INF/jsp/search.jsp").forward(req, resp);
    }
}
