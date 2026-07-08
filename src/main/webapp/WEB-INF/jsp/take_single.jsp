<%@ include file="header.jspf" %>
<h1><c:out value="${quiz.title}"/></h1>
<p class="desc"><c:out value="${quiz.description}"/></p>

<form method="post" action="${pageContext.request.contextPath}/take">
    <c:forEach var="q" items="${quiz.questions}">
        <%@ include file="question_fragment.jspf" %>
    </c:forEach>
    <button type="submit" class="btn primary">Submit Quiz</button>
</form>
<%@ include file="footer.jspf" %>
