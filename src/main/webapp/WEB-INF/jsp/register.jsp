<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>Create an Account</h1>

    <c:if test="${not empty error}">
        <p class="error"><c:out value="${error}"/></p>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register" class="card">
        <input type="text" name="username" placeholder="username"
               value="<c:out value='${username}'/>" required autofocus/>
        <input type="password" name="password" placeholder="password (min 4 characters)" required/>
        <input type="password" name="confirm" placeholder="confirm password" required/>
        <button class="btn primary" type="submit">Register</button>
    </form>

    <p class="muted">Already have an account?
        <a href="${pageContext.request.contextPath}/login.jsp">Log in</a>.</p>
</div>
</body>
</html>
