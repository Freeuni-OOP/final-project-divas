<%@ include file="header.jspf" %>
<h1>Inbox <c:if test="${unread > 0}"><span class="badge">${unread} unread</span></c:if></h1>

<h2>Send a Note</h2>
<form method="post" action="${pageContext.request.contextPath}/inbox" class="card">
    <input type="hidden" name="action" value="note"/>
    <input type="text" name="to" placeholder="recipient username" required/>
    <textarea name="body" placeholder="your message" required></textarea>
    <button class="btn primary" type="submit">Send</button>
</form>

<h2>Messages</h2>
<c:choose>
    <c:when test="${empty inbox}"><p>No messages.</p></c:when>
    <c:otherwise>
        <ul class="messages">
            <c:forEach var="m" items="${inbox}">
                <li class="${m.read ? 'read' : 'unread'}">
                    <span class="mtype">${m.type}</span>
                    from <strong><c:out value="${m.senderUsername}"/></strong>
                    <span class="mtime"><fmt:formatDate value="${m.createdAt}" pattern="MM-dd HH:mm"/></span>
                    <p><c:out value="${m.body}"/></p>

                    <c:if test="${m.type == 'CHALLENGE' && not empty m.quizId}">
                        <a class="btn small" href="${pageContext.request.contextPath}/take?quizId=${m.quizId}">
                            Take "<c:out value="${m.quizTitle}"/>"</a>
                    </c:if>

                    <c:if test="${m.type == 'FRIEND_REQUEST'}">
                        <form method="post" action="${pageContext.request.contextPath}/inbox" class="inline">
                            <input type="hidden" name="from" value="${m.senderId}"/>
                            <button class="btn small" name="action" value="acceptFriend">Accept</button>
                            <button class="btn small ghost" name="action" value="rejectFriend">Reject</button>
                        </form>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/inbox" class="inline">
                        <input type="hidden" name="id" value="${m.id}"/>
                        <c:if test="${!m.read}">
                            <button class="btn small ghost" name="action" value="read">Mark read</button>
                        </c:if>
                        <button class="btn small ghost" name="action" value="delete">Delete</button>
                    </form>
                </li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
