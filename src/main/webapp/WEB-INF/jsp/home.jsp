<%@ include file="header.jspf" %>
<h1>Home</h1>
<p class="muted">Placeholder homepage — Part D assembles the full version.</p>

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

<p class="muted">Tip: to test the quiz flow, create a quiz via Part B, then open
   <code>/take?quizId=&lt;id&gt;</code>.</p>
<%@ include file="footer.jspf" %>
