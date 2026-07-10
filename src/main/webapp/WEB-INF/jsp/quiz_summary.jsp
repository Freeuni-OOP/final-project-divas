<%@ include file="header.jspf" %>

<h1><c:out value="${quiz.title}"/></h1>

<div class="card">
    <p><c:out value="${quiz.description}"/></p>

    <p>Created by
        <c:choose>
            <c:when test="${not empty creator}">
                <a href="${pageContext.request.contextPath}/profile?userId=${creator.id}">
                    <c:out value="${creator.username}"/>
                </a>
            </c:when>
            <c:otherwise>unknown</c:otherwise>
        </c:choose>
    </p>

    <a class="btn primary" href="${pageContext.request.contextPath}/take?quizId=${quiz.id}">Start Quiz</a>
    <c:if test="${quiz.practiceAllowed}">
        <a class="btn ghost" href="${pageContext.request.contextPath}/take?quizId=${quiz.id}&practice=true">Practice Mode</a>
    </c:if>
</div>

<c:if test="${not empty friends}">
    <h2>Challenge a Friend</h2>
    <form method="post" action="${pageContext.request.contextPath}/inbox" class="card inline">
        <input type="hidden" name="action" value="challenge"/>
        <input type="hidden" name="quizId" value="${quiz.id}"/>
        <select name="to">
            <c:forEach var="f" items="${friends}">
                <option value="${f.id}"><c:out value="${f.username}"/></option>
            </c:forEach>
        </select>
        <button class="btn small" type="submit">Send Challenge</button>
    </form>
</c:if>

<h2>Quiz Statistics</h2>
<div class="card">
    <p>Times taken: <strong>${attemptsCount}</strong></p>
    <p>Average score: <strong>${avgPercent}%</strong></p>
    <p>Average time: <strong>${avgTime} seconds</strong></p>
</div>

<h2>Your Past Attempts</h2>
<c:choose>
    <c:when test="${empty myHistory}"><p>You haven't taken this quiz yet.</p></c:when>
    <c:otherwise>
        <table class="tbl">
            <tr><th>Date</th><th>Score</th><th>Time (s)</th></tr>
            <c:forEach var="h" items="${myHistory}">
                <tr>
                    <td><fmt:formatDate value="${h.takenAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td>${h.scoreCorrect} / ${h.scoreTotal} (${h.percent}%)</td>
                    <td>${h.timeSeconds}</td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<h2>Top Performers (All Time)</h2>
<c:choose>
    <c:when test="${empty topScorers}"><p>No attempts yet.</p></c:when>
    <c:otherwise>
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
    </c:otherwise>
</c:choose>

<h2>Top Performers (Last 24 Hours)</h2>
<c:choose>
    <c:when test="${empty topRecent}"><p>No attempts in the last day.</p></c:when>
    <c:otherwise>
        <table class="tbl">
            <tr><th>#</th><th>User</th><th>Score</th><th>Time (s)</th></tr>
            <c:forEach var="t" items="${topRecent}" varStatus="s">
                <tr>
                    <td>${s.index + 1}</td>
                    <td><a href="${pageContext.request.contextPath}/profile?userId=${t.userId}">
                        <c:out value="${t.username}"/></a></td>
                    <td>${t.scoreCorrect} / ${t.scoreTotal}</td>
                    <td>${t.timeSeconds}</td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<h2>Recent Test Takers</h2>
<c:choose>
    <c:when test="${empty recentAttempts}"><p>No attempts yet.</p></c:when>
    <c:otherwise>
        <table class="tbl">
            <tr><th>User</th><th>Score</th><th>Date</th></tr>
            <c:forEach var="r" items="${recentAttempts}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/profile?userId=${r.userId}">
                        <c:out value="${r.username}"/></a></td>
                    <td>${r.scoreCorrect} / ${r.scoreTotal} (${r.percent}%)</td>
                    <td><fmt:formatDate value="${r.takenAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<%@ include file="footer.jspf" %>
