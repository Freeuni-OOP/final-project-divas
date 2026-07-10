<%@ include file="header.jspf" %>
<c:choose>
    <c:when test="${empty profileUser}">
        <h1>User not found</h1>
    </c:when>
    <c:otherwise>
        <h1><c:out value="${profileUser.username}"/></h1>
        <p class="muted">Placeholder profile — Part B/D own the full version.</p>
        <form method="post" action="${pageContext.request.contextPath}/friends" class="inline">
            <input type="hidden" name="to" value="${profileUser.id}"/>
            <button class="btn primary" name="action" value="request">Add Friend</button>
        </form>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
