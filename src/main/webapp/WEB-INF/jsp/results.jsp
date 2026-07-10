<%@ include file="header.jspf" %>
<h1>Results: <c:out value="${quiz.title}"/></h1>

<div class="scorecard">
    <p class="score">You scored
        <strong>${attempt.scoreCorrect} / ${attempt.scoreTotal}</strong>
        (${attempt.percent}%)</p>
    <p>Time taken: ${attempt.timeSeconds} seconds</p>
    <c:if test="${attempt.practice}"><p class="tag">Practice mode — not recorded on the leaderboard</p></c:if>
</div>

<c:if test="${not empty details}">
    <h2>Your Answers</h2>
    <table class="tbl">
        <tr><th>Question</th><th>Your answer(s)</th><th>Score</th></tr>
        <c:forEach var="d" items="${details}">
            <tr class="${d.correct == d.total ? 'row-ok' : 'row-bad'}">
                <td><c:out value="${d.question.question}"/></td>
                <td>
                    <c:forEach var="r" items="${d.responses}" varStatus="s">
                        <c:out value="${r}"/><c:if test="${!s.last}">, </c:if>
                    </c:forEach>
                </td>
                <td>${d.correct} / ${d.total}</td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<h2>Your Past Attempts</h2>
<table class="tbl">
    <tr><th>Date</th><th>Score</th><th>Time (s)</th></tr>
    <c:forEach var="h" items="${history}">
        <tr>
            <td><fmt:formatDate value="${h.takenAt}" pattern="yyyy-MM-dd HH:mm"/></td>
            <td>${h.scoreCorrect} / ${h.scoreTotal} (${h.percent}%)</td>
            <td>${h.timeSeconds}</td>
        </tr>
    </c:forEach>
</table>

<h2>Top Scorers</h2>
<table class="tbl">
    <tr><th>#</th><th>User</th><th>Score</th><th>Time (s)</th></tr>
    <c:forEach var="t" items="${topScorers}" varStatus="s">
        <tr>
            <td>${s.index + 1}</td>
            <td><a href="${pageContext.request.contextPath}/profile?userId=${t.userId}">
                <c:out value="${t.username}"/></a></td>
            <td>${t.scoreCorrect} / ${t.scoreTotal}</td>
            <td>${t.timeSeconds}</td>
        </tr>
    </c:forEach>
</table>

<p><a class="btn" href="${pageContext.request.contextPath}/take?quizId=${quiz.id}">Retake</a>
   <a class="btn" href="${pageContext.request.contextPath}/home">Home</a></p>
<%@ include file="footer.jspf" %>

