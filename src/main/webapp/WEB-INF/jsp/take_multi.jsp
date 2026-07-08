<%@ include file="header.jspf" %>
<h1><c:out value="${quiz.title}"/></h1>
<p class="progress">Question ${questionNumber} of ${questionCount}</p>

<form method="post" action="${pageContext.request.contextPath}/take">
    <c:set var="q" value="${question}"/>
    <%@ include file="question_fragment.jspf" %>
    <button type="submit" class="btn primary">Submit Answer</button>
</form>
<%@ include file="footer.jspf" %>
