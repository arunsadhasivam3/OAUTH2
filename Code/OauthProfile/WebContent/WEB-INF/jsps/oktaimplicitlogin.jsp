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

<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Set" %>
<%!String url=""; %>

<%
 	url = "/implicitLoginFlow.htm";
%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr"> 
    <!--Head--> 
    <jsp:include page="/Header/head.jsp" />
    <body style="padding-top: 60px">
        <!-- Navbar --> 
        <jsp:include page="/Header/navigation.jsp" />
        <div class="container">
            <p class="lead">LOGIN PAGE for OKTA REDIRECT:</p>
            <!-- IDP Selection -->
             <form action="<c:url value="<%=url %>"/>" method="POST">
<%--              <form action="<c:url value="/auth.htm"/>" method="POST">
 --%>				    
 
 
 
 				    User Name : <input type="text" name="username">
				    Password : <input type="password" name="password">
				   
<!-- 				    <input name="submit" value="Login" type="submit"/>
 -->            
 
 
 
 
				    <input name="submit" value="Authorize via Linkedin" type="submit"/>
            </form>
        </div>
    </body>
</html>
