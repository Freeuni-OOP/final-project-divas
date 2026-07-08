<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // If already logged in, go to the homepage; otherwise to login.
    Object uid = session.getAttribute("userId");
    if (uid != null) {
        response.sendRedirect(request.getContextPath() + "/home");
    } else {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
%>
