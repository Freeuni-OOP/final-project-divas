package org.example.quiz.servlet;

import org.example.quiz.dao.UserDAO;
import org.example.quiz.model.User;
import org.example.quiz.util.SessionUtil;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RegisterServletTest {

    private RegisterServlet servlet;
    private StubUserDAO stubDAO;

    private Map<String, String> params;
    private Map<String, Object> attributes;
    private String forwardedPath;
    private String redirectedPath;
    private Map<String, Object> sessionAttributes;

    @Before
    public void setUp() throws Exception {
        stubDAO = new StubUserDAO();
        servlet = new RegisterServlet();
        servlet.setUserDAO(stubDAO);

        params = new HashMap<>();
        attributes = new HashMap<>();
        sessionAttributes = new HashMap<>();
        forwardedPath = null;
        redirectedPath = null;
    }

    private HttpServletRequest fakeRequest() {
        return new FakeRequest(params, attributes, sessionAttributes);
    }

    private HttpServletResponse fakeResponse() {
        return new FakeResponse();
    }

    @Test
    public void missingUsernameShowsError() throws Exception {
        params.put("username", "");
        params.put("password", "pass1234");
        params.put("confirm", "pass1234");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("Username and password are required.", attributes.get("error"));
        assertEquals("/WEB-INF/jsp/register.jsp", forwardedPath);
        assertNull(redirectedPath);
    }

    @Test
    public void missingPasswordShowsError() throws Exception {
        params.put("username", "alice");
        params.put("password", "");
        params.put("confirm", "");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("Username and password are required.", attributes.get("error"));
        assertEquals("/WEB-INF/jsp/register.jsp", forwardedPath);
    }

    @Test
    public void shortPasswordShowsError() throws Exception {
        params.put("username", "alice");
        params.put("password", "ab");
        params.put("confirm", "ab");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("Password must be at least 4 characters.", attributes.get("error"));
        assertEquals("alice", attributes.get("username"));
        assertEquals("/WEB-INF/jsp/register.jsp", forwardedPath);
    }

    @Test
    public void mismatchedPasswordsShowError() throws Exception {
        params.put("username", "alice");
        params.put("password", "pass1234");
        params.put("confirm", "different");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("Passwords do not match.", attributes.get("error"));
        assertEquals("alice", attributes.get("username"));
        assertEquals("/WEB-INF/jsp/register.jsp", forwardedPath);
    }

    @Test
    public void duplicateUsernameShowsError() throws Exception {
        stubDAO.existingUsername = "alice";
        params.put("username", "alice");
        params.put("password", "pass1234");
        params.put("confirm", "pass1234");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("Username is already taken.", attributes.get("error"));
        assertEquals("alice", attributes.get("username"));
        assertEquals("/WEB-INF/jsp/register.jsp", forwardedPath);
    }

    @Test
    public void successfulRegistrationRedirectsToHome() throws Exception {
        stubDAO.nextId = 42;
        params.put("username", "bob");
        params.put("password", "pass1234");
        params.put("confirm", "pass1234");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertNull(forwardedPath);
        assertEquals("/home", redirectedPath);
        assertEquals(42L, sessionAttributes.get(SessionUtil.USER_ID));
        assertEquals("bob", sessionAttributes.get(SessionUtil.USERNAME));
    }

    @Test
    public void successfulRegistrationCallsCreateUser() throws Exception {
        stubDAO.nextId = 7;
        params.put("username", "charlie");
        params.put("password", "secret99");
        params.put("confirm", "secret99");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("charlie", stubDAO.createdUsername);
        assertEquals("secret99", stubDAO.createdPassword);
    }

    @Test
    public void usernameIsTrimmed() throws Exception {
        stubDAO.nextId = 10;
        params.put("username", "  bob  ");
        params.put("password", "pass1234");
        params.put("confirm", "pass1234");

        servlet.doPost(fakeRequest(), fakeResponse());

        assertEquals("bob", stubDAO.createdUsername);
        assertEquals("/home", redirectedPath);
    }

    // --- Stubs ---

    private static class StubUserDAO extends UserDAO {
        String existingUsername;
        long nextId = 1;
        String createdUsername;
        String createdPassword;

        @Override
        public boolean usernameExists(String username) {
            return username.equals(existingUsername);
        }

        @Override
        public long createUser(String username, String password) {
            createdUsername = username;
            createdPassword = password;
            return nextId;
        }
    }

    private class FakeRequest implements HttpServletRequest {
        private final Map<String, String> params;
        private final Map<String, Object> attrs;
        private final Map<String, Object> sessionAttrs;

        FakeRequest(Map<String, String> params, Map<String, Object> attrs,
                    Map<String, Object> sessionAttrs) {
            this.params = params;
            this.attrs = attrs;
            this.sessionAttrs = sessionAttrs;
        }

        @Override public String getParameter(String name) { return params.get(name); }
        @Override public void setAttribute(String name, Object o) { attrs.put(name, o); }
        @Override public Object getAttribute(String name) { return attrs.get(name); }
        @Override public String getContextPath() { return ""; }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return new RequestDispatcher() {
                @Override
                public void forward(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) {
                    forwardedPath = path;
                }
                @Override
                public void include(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) { }
            };
        }

        @Override
        public HttpSession getSession(boolean create) {
            return new FakeSession(sessionAttrs);
        }

        @Override public HttpSession getSession() { return getSession(true); }

        // Unused methods - minimal stubs
        @Override public String getAuthType() { return null; }
        @Override public javax.servlet.http.Cookie[] getCookies() { return new javax.servlet.http.Cookie[0]; }
        @Override public long getDateHeader(String s) { return 0; }
        @Override public String getHeader(String s) { return null; }
        @Override public java.util.Enumeration<String> getHeaders(String s) { return null; }
        @Override public java.util.Enumeration<String> getHeaderNames() { return null; }
        @Override public int getIntHeader(String s) { return 0; }
        @Override public String getMethod() { return "POST"; }
        @Override public String getPathInfo() { return null; }
        @Override public String getPathTranslated() { return null; }
        @Override public String getQueryString() { return null; }
        @Override public String getRemoteUser() { return null; }
        @Override public boolean isUserInRole(String s) { return false; }
        @Override public java.security.Principal getUserPrincipal() { return null; }
        @Override public String getRequestedSessionId() { return null; }
        @Override public String getRequestURI() { return "/register"; }
        @Override public StringBuffer getRequestURL() { return new StringBuffer("http://localhost/register"); }
        @Override public String getServletPath() { return "/register"; }
        @Override public boolean isRequestedSessionIdValid() { return false; }
        @Override public boolean isRequestedSessionIdFromCookie() { return false; }
        @Override public boolean isRequestedSessionIdFromURL() { return false; }
        @Override public boolean isRequestedSessionIdFromUrl() { return false; }
        @Override public boolean authenticate(HttpServletResponse resp) { return false; }
        @Override public void login(String u, String p) { }
        @Override public void logout() { }
        @Override public java.util.Collection<javax.servlet.http.Part> getParts() { return null; }
        @Override public javax.servlet.http.Part getPart(String s) { return null; }
        @Override public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> c) { return null; }
        @Override public String changeSessionId() { return null; }
        @Override public String getCharacterEncoding() { return null; }
        @Override public void setCharacterEncoding(String s) { }
        @Override public int getContentLength() { return 0; }
        @Override public long getContentLengthLong() { return 0; }
        @Override public String getContentType() { return null; }
        @Override public javax.servlet.ServletInputStream getInputStream() { return null; }
        @Override public java.util.Enumeration<String> getParameterNames() { return null; }
        @Override public String[] getParameterValues(String s) { return null; }
        @Override public Map<String, String[]> getParameterMap() { return null; }
        @Override public String getProtocol() { return null; }
        @Override public String getScheme() { return null; }
        @Override public String getServerName() { return null; }
        @Override public int getServerPort() { return 0; }
        @Override public java.io.BufferedReader getReader() { return null; }
        @Override public String getRemoteAddr() { return null; }
        @Override public String getRemoteHost() { return null; }
        @Override public java.util.Locale getLocale() { return null; }
        @Override public java.util.Enumeration<java.util.Locale> getLocales() { return null; }
        @Override public boolean isSecure() { return false; }
        @Override public int getRemotePort() { return 0; }
        @Override public String getLocalName() { return null; }
        @Override public String getLocalAddr() { return null; }
        @Override public int getLocalPort() { return 0; }
        @Override public javax.servlet.ServletContext getServletContext() { return null; }
        @Override public javax.servlet.AsyncContext startAsync() { return null; }
        @Override public javax.servlet.AsyncContext startAsync(javax.servlet.ServletRequest req, javax.servlet.ServletResponse resp) { return null; }
        @Override public boolean isAsyncStarted() { return false; }
        @Override public boolean isAsyncSupported() { return false; }
        @Override public javax.servlet.AsyncContext getAsyncContext() { return null; }
        @Override public javax.servlet.DispatcherType getDispatcherType() { return null; }
        @Override public void removeAttribute(String s) { }
        @Override public java.util.Enumeration<String> getAttributeNames() { return null; }
        @Override public String getRealPath(String s) { return null; }
    }

    private static class FakeSession implements HttpSession {
        private final Map<String, Object> attrs;
        FakeSession(Map<String, Object> attrs) { this.attrs = attrs; }

        @Override public void setAttribute(String name, Object value) { attrs.put(name, value); }
        @Override public Object getAttribute(String name) { return attrs.get(name); }

        @Override public long getCreationTime() { return 0; }
        @Override public String getId() { return "fake"; }
        @Override public long getLastAccessedTime() { return 0; }
        @Override public javax.servlet.ServletContext getServletContext() { return null; }
        @Override public void setMaxInactiveInterval(int i) { }
        @Override public int getMaxInactiveInterval() { return 0; }
        @Override @SuppressWarnings("deprecation") public javax.servlet.http.HttpSessionContext getSessionContext() { return null; }
        @Override public Object getValue(String s) { return null; }
        @Override public java.util.Enumeration<String> getAttributeNames() { return null; }
        @Override public String[] getValueNames() { return new String[0]; }
        @Override public void putValue(String s, Object o) { }
        @Override public void removeValue(String s) { }
        @Override public void removeAttribute(String s) { }
        @Override public void invalidate() { }
        @Override public boolean isNew() { return false; }
    }

    private class FakeResponse implements HttpServletResponse {
        @Override public void sendRedirect(String location) { redirectedPath = location; }

        @Override public void setContentType(String s) { }
        @Override public String getContentType() { return null; }
        @Override public javax.servlet.ServletOutputStream getOutputStream() { return null; }
        @Override public java.io.PrintWriter getWriter() { return null; }
        @Override public void setCharacterEncoding(String s) { }
        @Override public String getCharacterEncoding() { return null; }
        @Override public void setContentLength(int i) { }
        @Override public void setContentLengthLong(long l) { }
        @Override public void setBufferSize(int i) { }
        @Override public int getBufferSize() { return 0; }
        @Override public void flushBuffer() { }
        @Override public void resetBuffer() { }
        @Override public boolean isCommitted() { return false; }
        @Override public void reset() { }
        @Override public void setLocale(java.util.Locale locale) { }
        @Override public java.util.Locale getLocale() { return null; }
        @Override public void addCookie(javax.servlet.http.Cookie cookie) { }
        @Override public boolean containsHeader(String s) { return false; }
        @Override public String encodeURL(String s) { return s; }
        @Override public String encodeRedirectURL(String s) { return s; }
        @Override public String encodeUrl(String s) { return s; }
        @Override public String encodeRedirectUrl(String s) { return s; }
        @Override public void sendError(int i, String s) { }
        @Override public void sendError(int i) { }
        @Override public void setDateHeader(String s, long l) { }
        @Override public void addDateHeader(String s, long l) { }
        @Override public void setHeader(String s, String s1) { }
        @Override public void addHeader(String s, String s1) { }
        @Override public void setIntHeader(String s, int i) { }
        @Override public void addIntHeader(String s, int i) { }
        @Override public void setStatus(int i) { }
        @Override public void setStatus(int i, String s) { }
        @Override public int getStatus() { return 200; }
        @Override public String getHeader(String s) { return null; }
        @Override public java.util.Collection<String> getHeaders(String s) { return null; }
        @Override public java.util.Collection<String> getHeaderNames() { return null; }
    }
}
