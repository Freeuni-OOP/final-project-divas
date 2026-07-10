package org.example.quiz.servlet;

import org.example.quiz.dao.UserDAO;
import org.example.quiz.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");

        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()) {
            req.setAttribute("error", "Username and password are required.");
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        username = username.trim();

        if (password.length() < 4) {
            req.setAttribute("error", "Password must be at least 4 characters.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirm)) {
            req.setAttribute("error", "Passwords do not match.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
            return;
        }

        try {
            if (userDAO.usernameExists(username)) {
                req.setAttribute("error", "Username is already taken.");
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
                return;
            }

            long userId = userDAO.createUser(username, password);

            HttpSession session = req.getSession(true);
            session.setAttribute(SessionUtil.USER_ID, userId);
            session.setAttribute(SessionUtil.USERNAME, username);

            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
