<%@ include file="header.jspf" %>
<h1>Find Users</h1>

<form method="get" action="${pageContext.request.contextPath}/search" class="card">
    <input type="text" name="q" value="<c:out value='${term}'/>" placeholder="username" required/>
    <button class="btn primary" type="submit">Search</button>
</form>

<c:if test="${not empty term}">
    <h2>Results for "<c:out value='${term}'/>"</h2>
    <c:choose>
        <c:when test="${empty results}"><p>No users found.</p></c:when>
        <c:otherwise>
            <ul class="list">
                <c:forEach var="u" items="${results}">
                    <li>
                        <a href="${pageContext.request.contextPath}/profile?userId=${u.id}">
                            <c:out value="${u.username}"/></a>
                        <form method="post" action="${pageContext.request.contextPath}/friends" class="inline">
                            <input type="hidden" name="to" value="${u.id}"/>
                            <button class="btn small" name="action" value="request">Add Friend</button>
                        </form>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</c:if>
<%@ include file="footer.jspf" %>
