<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container">
    <h1>Login</h1>

    <% if (request.getAttribute("error") != null) { %>
    <p class="error">
        <%= request.getAttribute("error") %>
    </p>
    <% } %>

    <form method="post"
          action="${pageContext.request.contextPath}/login"
          class="card">

        <input type="text"
               name="username"
               placeholder="Username"
               required />

        <input type="password"
               name="password"
               placeholder="Password"
               required />

        <button class="btn primary" type="submit">
            Login
        </button>
    </form>

    <p class="muted">New here?
        <a href="${pageContext.request.contextPath}/register">Create an account</a>.</p>

</div>

</body>
</html>
