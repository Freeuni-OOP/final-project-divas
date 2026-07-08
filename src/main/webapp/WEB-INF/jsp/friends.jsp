<%@ include file="header.jspf" %>
<h1>Friends</h1>

<h2>Pending Requests</h2>
<c:choose>
    <c:when test="${empty incoming}"><p>No pending friend requests.</p></c:when>
    <c:otherwise>
        <ul class="list">
            <c:forEach var="r" items="${incoming}">
                <li>
                    User #${r.requesterId} wants to be your friend.
                    <form method="post" action="${pageContext.request.contextPath}/friends" class="inline">
                        <input type="hidden" name="from" value="${r.requesterId}"/>
                        <button class="btn small" name="action" value="accept">Accept</button>
                        <button class="btn small ghost" name="action" value="reject">Reject</button>
                    </form>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>

<h2>Your Friends</h2>
<c:choose>
    <c:when test="${empty friends}"><p>You have no friends yet. Find some!</p></c:when>
    <c:otherwise>
        <ul class="list">
            <c:forEach var="f" items="${friends}">
                <li>
                    <a href="${pageContext.request.contextPath}/profile?userId=${f.id}">
                        <c:out value="${f.username}"/></a>
                    <form method="post" action="${pageContext.request.contextPath}/friends" class="inline">
                        <input type="hidden" name="other" value="${f.id}"/>
                        <button class="btn small ghost" name="action" value="remove">Remove</button>
                    </form>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
