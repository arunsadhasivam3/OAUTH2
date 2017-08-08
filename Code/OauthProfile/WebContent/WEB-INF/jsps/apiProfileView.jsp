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

<%@ page import="org.springframework.security.saml.SAMLCredential" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.opensaml.saml2.core.Attribute" %>
<%@ page import="org.springframework.security.saml.util.SAMLUtil" %>
<%@ page import="org.opensaml.xml.util.XMLHelper" %>
<%@ page import="java.util.*" %>
<%@ page import="com.okta.sdk.models.users.User" %>
<%@ page import="com.okta.sdk.models.users.UserProfile" %>
<%@ page isErrorPage="true" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr">
    <%-- Head --%>
    <jsp:include page="/Header/head.jsp"/>
    <body style="padding-top: 60px">
        <%-- NavBar --%> 
        <jsp:include page="/Header/navigation.jsp" />
        <div class="container">
            <h1>Logged In</h1>
            <p class="lead">API Profile view Attributes Using API Service:</p>
            
            
            
            <c:choose>
              <c:when test="${empty message}">
		            <div class="col-md-8">
		                <%-- Attribute Pair Table --%>            
		                <table class="table">
		                    <%--Stores Attributes Parsed Form Saml Assertion --%>
		
		                   <c:set var="profile" value="${user.profile}"></c:set>
		                    <%-- Displays Attribute Name and Corresponding Attribute iteratively --%>
		                    
		                    	<tr>
		                            <td style="font-weight: bold;">First name</b></td>
		                            <td>${profile.firstName}</td>
		                        </tr>
		                        
		                        <tr>
		                            <td style="font-weight: bold;">Last name</b></td>
		                            <td>${profile.lastName}</td>
		                        </tr>
		                        
		                         <tr>
		                            <td style="font-weight: bold;">mobilePhone</b></td>
		                            <td>${profile.mobilePhone}</td>
		                        </tr>
		                        
		                        
		                        <tr>
		                            <td style="font-weight: bold;">Email</b></td>
		                            <td>${profile.email}</td>
		                        </tr>
		                        
		                         
		                         <tr>
		                            <td style="font-weight: bold;">AltEmail</b></td>
		                            <td>${profile.secondEmail}</td>
		                        </tr>
		                        
		                         
		                        
		                </table>  
		            </div>
            
            </c:when>
 	        <c:otherwise>
 	        		<div style="color:red">${message }</div>
 	        </c:otherwise>
 	
 		  </c:choose>           
            
            
        </div> 
        
    </body>
</html>
