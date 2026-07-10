<%@ include file="header.jspf" %>
<c:choose>
    <c:when test="${empty profileUser}">
        <h1>User not found</h1>
    </c:when>
    <c:otherwise>
        <h1><c:out value="${profileUser.username}"/></h1>

        <c:if test="${profileUser.id != sessionScope.userId}">
            <form method="post" action="${pageContext.request.contextPath}/friends" class="inline">
                <input type="hidden" name="to" value="${profileUser.id}"/>
                <button class="btn primary" name="action" value="request">Add Friend</button>
            </form>
        </c:if>

        <h2>Achievements</h2>
        <c:choose>
            <c:when test="${empty profileAchievements}"><p>No achievements yet.</p></c:when>
            <c:otherwise>
                <c:forEach var="ach" items="${profileAchievements}">
                    <span title="${ach.description}"><c:out value="${ach.label}"/></span>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <h2>Quizzes Created</h2>
        <c:choose>
            <c:when test="${empty profileQuizzes}"><p>No quizzes created yet.</p></c:when>
            <c:otherwise>
                <c:forEach var="q" items="${profileQuizzes}">
                    <p><a href="${pageContext.request.contextPath}/quiz?id=${q.id}"><c:out value="${q.title}"/></a></p>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
