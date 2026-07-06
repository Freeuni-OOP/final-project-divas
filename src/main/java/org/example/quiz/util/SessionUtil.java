package org.example.quiz.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Helper for reading login state from the session.
 *
 * Part A's login servlet is expected to set the session attribute "userId"
 * (Long) on successful login. C's servlets only read it.
 */
public final class SessionUtil {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";

    private SessionUtil() { }

    /** Returns the logged-in user id, or -1 if not logged in. */
    public static long currentUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null) return -1;
        Object v = s.getAttribute(USER_ID);
        return (v instanceof Long) ? (Long) v : -1;
    }

    public static boolean isLoggedIn(HttpServletRequest req) {
        return currentUserId(req) > 0;
    }
}
