<%@ include file="header.jspf" %>
<h1><c:out value="${quiz.title}"/></h1>

<div class="feedback ${gotCorrect == outOf ? 'correct' : 'incorrect'}">
    <p class="prompt"><c:out value="${question.question}"/></p>
    <p>You scored ${gotCorrect} / ${outOf} on this question.</p>

    <p><strong>Your answer(s):</strong>
        <c:forEach var="r" items="${responses}" varStatus="s">
            <c:out value="${r}"/><c:if test="${!s.last}">, </c:if>
        </c:forEach>
    </p>

    <c:if test="${not empty acceptedAnswers}">
        <p><strong>Accepted answer(s):</strong>
            <c:forEach var="a" items="${acceptedAnswers}" varStatus="s">
                <c:out value="${a.answer}"/><c:if test="${!s.last}">, </c:if>
            </c:forEach>
        </p>
    </c:if>
</div>

<c:choose>
    <c:when test="${hasNext}">
        <a class="btn primary" href="${pageContext.request.contextPath}/take?resume=1">Next Question</a>
    </c:when>
    <c:otherwise>
        <a class="btn primary" href="${pageContext.request.contextPath}/take?resume=1">See Results</a>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>

