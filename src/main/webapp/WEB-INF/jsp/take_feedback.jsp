<%@ include file="header.jspf" %>
<h1><c:out value="${quiz.title}"/></h1>

<div class="feedback ${gotCorrect == outOf ? 'correct' : 'incorrect'}">
    <p class="prompt"><c:out value="${question.prompt}"/></p>
    <p>You scored ${gotCorrect} / ${outOf} on this question.</p>

    <p><strong>Your answer(s):</strong>
        <c:forEach var="r" items="${responses}" varStatus="s">
            <c:out value="${r}"/><c:if test="${!s.last}">, </c:if>
        </c:forEach>
    </p>

    <c:if test="${not empty acceptedAnswers}">
        <p><strong>Accepted answer(s):</strong>
            <c:forEach var="a" items="${acceptedAnswers}" varStatus="s">
                <c:out value="${a.text}"/><c:if test="${!s.last}">, </c:if>
            </c:forEach>
        </p>
    </c:if>
</div>

<c:choose>
    <c:when test="${hasNext}">
        <a class="btn primary" href="${pageContext.request.contextPath}/take?resume=1">Next Question</a>
    </c:when>
    <c:otherwise>
        <%-- No more questions: post an empty form to finish, or the servlet
             already recorded on the last non-immediate path. Provide a link. --%>
        <form method="post" action="${pageContext.request.contextPath}/take">
            <button type="submit" class="btn primary">See Results</button>
        </form>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jspf" %>
