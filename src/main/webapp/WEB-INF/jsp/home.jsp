<%@ include file="header.jspf" %>
<h1>Home</h1>

<c:if test="${not empty announcements}">
    <h2>Announcements</h2>
    <c:forEach var="a" items="${announcements}">
        <div class="card">
            <h3><c:out value="${a.title}"/></h3>
            <p><c:out value="${a.message}"/></p>
        </div>
    </c:forEach>
</c:if>

<h2>Messages
    <c:if test="${unread > 0}"><span class="badge">${unread} unread</span></c:if>
</h2>
<c:choose>
    <c:when test="${empty recentMessages}"><p>No messages.
        <a href="${pageContext.request.contextPath}/inbox">Go to inbox</a>.</p></c:when>
    <c:otherwise>
        <ul class="list">
            <c:forEach var="m" items="${recentMessages}">
                <li>
                    <span class="mtype">${m.type}</span>
                    from <strong><c:out value="${m.senderUsername}"/></strong>
                    <fmt:formatDate value="${m.createdAt}" pattern="MM-dd HH:mm"/>
                </li>
            </c:forEach>
        </ul>
        <p><a href="${pageContext.request.contextPath}/inbox">Go to inbox</a></p>
    </c:otherwise>
</c:choose>

<h2>Popular Quizzes</h2>
<c:choose>
    <c:when test="${empty popularQuizzes}"><p>No quizzes yet.</p></c:when>
    <c:otherwise>
        <c:forEach var="q" items="${popularQuizzes}">
            <p><a href="${pageContext.request.contextPath}/quiz?id=${q.id}"><c:out value="${q.title}"/></a></p>
        </c:forEach>
    </c:otherwise>
</c:choose>

<h2>Recently Created Quizzes</h2>
<c:choose>
    <c:when test="${empty recentQuizzes}"><p>No quizzes yet.</p></c:when>
    <c:otherwise>
        <c:forEach var="q" items="${recentQuizzes}">
            <p><a href="${pageContext.request.contextPath}/quiz?id=${q.id}"><c:out value="${q.title}"/></a></p>
        </c:forEach>
    </c:otherwise>
</c:choose>

<h2>Your Recent Quiz Activity</h2>
<c:choose>
    <c:when test="${empty history}"><p>No quizzes taken yet.</p></c:when>
    <c:otherwise>
        <table class="tbl">
            <tr><th>Quiz</th><th>Score</th><th>Date</th></tr>
            <c:forEach var="h" items="${history}">
                <tr>
                    <td><c:out value="${h.quizTitle}"/></td>
                    <td>${h.scoreCorrect} / ${h.scoreTotal} (${h.percent}%)</td>
                    <td><fmt:formatDate value="${h.takenAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<c:if test="${not empty myQuizzes}">
    <h2>Your Quizzes</h2>
    <c:forEach var="q" items="${myQuizzes}">
        <p><a href="${pageContext.request.contextPath}/quiz?id=${q.id}"><c:out value="${q.title}"/></a></p>
    </c:forEach>
</c:if>

<p><a class="btn" href="${pageContext.request.contextPath}/createQuiz">Create a Quiz</a></p>

<h2>Your Achievements</h2>
<c:choose>
    <c:when test="${empty achievements}"><p>None yet go take a quiz!</p></c:when>
    <c:otherwise>
        <c:forEach var="ach" items="${achievements}">
            <span title="${ach.description}"><c:out value="${ach.label}"/></span>
        </c:forEach>
    </c:otherwise>
</c:choose>

<h2>Friends' Recent Activity</h2>
<c:choose>
    <c:when test="${empty friendActivity}"><p>No recent activity from your friends.</p></c:when>
    <c:otherwise>
        <table class="tbl">
            <tr><th>Friend</th><th>Quiz</th><th>Score</th><th>Date</th></tr>
            <c:forEach var="f" items="${friendActivity}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/profile?userId=${f.userId}">
                        <c:out value="${f.username}"/></a></td>
                    <td><a href="${pageContext.request.contextPath}/quiz?id=${f.quizId}">
                        <c:out value="${f.quizTitle}"/></a></td>
                    <td>${f.scoreCorrect} / ${f.scoreTotal} (${f.percent}%)</td>
                    <td><fmt:formatDate value="${f.takenAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>
