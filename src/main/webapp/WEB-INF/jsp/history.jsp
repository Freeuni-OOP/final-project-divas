<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jspf" %>
<h1>Your Quiz History</h1>

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

<h2>Achievements</h2>
<c:choose>
    <c:when test="${empty achievements}"><p>None yet go take a quiz!</p></c:when>
    <c:otherwise>
        <c:forEach var="ach" items="${achievements}">
            <span title="${ach.description}"><c:out value="${ach.label}"/></span>
        </c:forEach>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>