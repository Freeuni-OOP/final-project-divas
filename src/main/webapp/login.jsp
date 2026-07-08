<%@ page contentType="text/html;charset=UTF-8" %>
<!-- PLACEHOLDER — Part A (Auth) owns the real login/register.
     This stub lets Part C be tested independently. It sets a fake session
     user so you can navigate friends/inbox/quiz-taking without full auth.
     REMOVE before integration. -->
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><title>Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body>
<div class="container">
    <h1>Login (placeholder)</h1>
    <p class="muted">Part A will replace this with real authentication.</p>
    <form method="post" action="${pageContext.request.contextPath}/devlogin" class="card">
        <input type="text" name="userId" placeholder="user id (e.g. 1)" required/>
        <input type="text" name="username" placeholder="username" required/>
        <button class="btn primary" type="submit">Dev Login</button>
    </form>
</div>
</body></html>
