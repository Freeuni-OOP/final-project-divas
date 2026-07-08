<%@ include file="header.jspf" %>
<h1>Something went wrong</h1>
<p class="error"><c:out value="${error != null ? error : 'An unexpected error occurred.'}"/></p>
<a class="btn" href="${pageContext.request.contextPath}/home">Back to Home</a>
<%@ include file="footer.jspf" %>
