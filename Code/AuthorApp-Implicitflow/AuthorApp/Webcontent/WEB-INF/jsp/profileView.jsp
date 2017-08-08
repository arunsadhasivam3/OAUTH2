<%--
/* -*- coding: utf-8 -*-
 * Copyright 2015 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
--%>

<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
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
            <p class="lead">Profile view Attributes values:</p>
            <div class="col-md-8">
                <%-- Attribute Pair Table --%>            
                <table class="table">
                    <%--Stores Attributes Parsed Form Saml Assertion --%>

                   
                    <%-- Displays Attribute Name and Corresponding Attribute iteratively --%>
                    <c:forEach items= "${attributes}" var="attribute">
                        <tr>
                            <td style="font-weight: bold;">${attribute.getName()}</b></td>
                            <td>${credential.getAttributeAsString(attribute.getName())}</td>
                        </tr>
                    </c:forEach>   
                </table>  
            </div>
        </div> 
        
        <div>
        <input type="text" id="authCode" value="<%=request.getParameter("code") %>" />
        </div>
    </body>
</html>
