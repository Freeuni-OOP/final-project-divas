package org.example.quiz.servlet;

import org.example.quiz.util.SessionUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * PLACEHOLDER auth for testing Part C without Part A. (remove at integration)
 *
 * POST /devlogin  -> stores userId/username in the session
 * GET  /logout    -> clears the session
 */
@WebServlet({"/devlogin", "/logout"})
public class DevAuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long id = Long.parseLong(req.getParameter("userId"));
            HttpSession s = req.getSession(true);
            s.setAttribute(SessionUtil.USER_ID, id);
            s.setAttribute(SessionUtil.USERNAME, req.getParameter("username"));
        } catch (Exception ignore) { }
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
