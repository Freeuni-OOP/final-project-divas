<%@ include file="header.jspf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<h1>Admin Panel</h1>

<h2>Site Statistics</h2>
<div class="card">
    <p>Total users: <strong>${userCount}</strong></p>
    <p>Total quizzes taken: <strong>${quizzesTaken}</strong></p>
</div>

<h2>Post an Announcement</h2>
<form method="post" action="${pageContext.request.contextPath}/admin" class="card">
    <input type="hidden" name="action" value="createAnnouncement"/>
    <label>Title
        <input type="text" name="title" required/>
    </label>
    <label>Body
        <textarea name="body" required></textarea>
    </label>
    <button class="btn primary" type="submit">Post</button>
</form>

<h2>Active Announcements</h2>
<c:choose>
    <c:when test="${empty announcements}"><p>No active announcements.</p></c:when>
    <c:otherwise>
        <c:forEach var="a" items="${announcements}">
            <div class="card">
                <h3><c:out value="${a.title}"/></h3>
                <p><c:out value="${a.message}"/></p>

                <form method="post" action="${pageContext.request.contextPath}/admin" class="inline">
                    <input type="hidden" name="action" value="editAnnouncement"/>
                    <input type="hidden" name="id" value="${a.id}"/>
                    <input type="text" name="title" value="${a.title}" required/>
                    <input type="text" name="body" value="${a.message}" required/>
                    <button class="btn small" type="submit">Save</button>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/admin" class="inline">
                    <input type="hidden" name="action" value="deactivateAnnouncement"/>
                    <input type="hidden" name="id" value="${a.id}"/>
                    <button class="btn small ghost" type="submit">Deactivate</button>
                </form>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<h2>Manage Users</h2>
<form method="post" action="${pageContext.request.contextPath}/admin" class="card inline">
    <input type="hidden" name="action" value="removeUser"/>
    <label>User ID <input type="text" name="userId" required/></label>
    <button class="btn small ghost" type="submit">Remove User</button>
</form>

<form method="post" action="${pageContext.request.contextPath}/admin" class="card inline">
    <input type="hidden" name="action" value="promoteToAdmin"/>
    <label>User ID <input type="text" name="userId" required/></label>
    <button class="btn small" type="submit">Promote to Admin</button>
</form>

<h2>Manage Quizzes</h2>
<form method="post" action="${pageContext.request.contextPath}/admin" class="card inline">
    <input type="hidden" name="action" value="removeQuiz"/>
    <label>Quiz ID <input type="text" name="quizId" required/></label>
    <button class="btn small ghost" type="submit">Remove Quiz</button>
</form>

<form method="post" action="${pageContext.request.contextPath}/admin" class="card inline">
    <input type="hidden" name="action" value="clearQuizHistory"/>
    <label>Quiz ID <input type="text" name="quizId" required/></label>
    <button class="btn small ghost" type="submit">Clear Quiz History</button>
</form>

<%@ include file="footer.jspf" %>
