<%@ include file="header.jspf" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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

<%@ include file="footer.jspf" %>
