

<%@ page import="org.springframework.security.saml.SAMLCredential" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.opensaml.saml2.core.Attribute" %>
<%@ page import="org.springframework.security.saml.util.SAMLUtil" %>
<%@ page import="org.opensaml.xml.util.XMLHelper" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr">
    <%-- Head --%>
    <jsp:include page="/Header/head.jsp"/>
    <body style="padding-top: 60px">
        <%-- NavBar --%> 
        <jsp:include page="/Header/navigation.jsp" />
        <div class="container">
            <h1>Logged In</h1>
            <p class="lead">Unauthorized to view the page</p>
            <div class="col-md-8">
                <%-- Attribute Pair Table --%>            
                <table class="table">
                    <%-- Displays Attribute Name and Corresponding Attribute iteratively --%>
                        <c:forEach items= "${attributes}" var="attribute">
                        <tr>
                            <td style="font-weight: bold;">Not authroized${attribute.getName()}</b></td>
                            <td> Not a member of : ${credential.getAttributeAsString(attribute.getName())}</td>
                        </tr>
                    </c:forEach> 
                </table>  
            </div>
        </div> 
        
    </body>
</html>
