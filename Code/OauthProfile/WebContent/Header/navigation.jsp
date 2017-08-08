

<%@ page import="org.springframework.security.saml.SAMLCredential" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-inverse navbar-fixed-top">
  <div class="container">
    <div class="navbar-header">
  <%-- this is what makes the "hamburger" icon --%>
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="<c:url value="/"/>"> Profile Page</a>
    </div>
    <div id="navbar" class="collapse navbar-collapse">
      <ul class="nav navbar-nav navbar-right">
        <%-- Display logout option if user is logged in --%>
        <c:if test="${SecurityContextHolder.getContext().getAuthentication() != null}"> 
          <c:if test="${SecurityContextHolder.getContext().getAuthentication().isAuthenticated()}">
            <li>
                <form class="left" action="<c:url value="/saml/logout"/>" method="get">
                <input type="hidden" name="local" value="true"/>
                <input style="border-width:0px" type="submit" value="Logout" class="button btn-link navbar-brand"/>
                </form>
            </li>
          </c:if>
        </c:if>
      </ul>
    </div><%--nav-collapse --%>
  </div>
</nav>
