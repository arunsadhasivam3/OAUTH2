ISSUE:
=======

HTTP Status 403 - Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'.

SOLUTION:
=========
when doing mapping in okta side make sure it is OKTA username then csrf error wont come.
last line - Application username -Okta username

Important main issue is
=======================
if i use spring-security version 3.1.2 it works if i use other versions it is showing Invalid CSRF Token 'null' error.
or  "uld not verify the provided CSRF token because your session was not found"

below pom.xml i changed to 3.1.2 to make it work from 4.1.1.

Important:
==========
Even if i have 4.1.1 & 3.1.2 in classpath it is working fine !!!

1)spring-security-config.jar

2)spring-security-web.jar

3)spring-security-core.jar

	<properties>
		<springsecurity.version>4.1.1.RELEASE</springsecurity.version>
	</properties>

	<dependency>
				<groupId>org.springframework.security</groupId>
				<artifactId>spring-security-config</artifactId>
				<version>${springsecurity.version}</version>
				<scope>compile</scope>
			</dependency>

	  <dependency>
				<groupId>org.springframework.security</groupId>
				<artifactId>spring-security-web</artifactId>
				<version>${springsecurity.version}</version>
			</dependency>

	  <dependency>
			    <groupId>org.springframework.security</groupId>
			    <artifactId>spring-security-core</artifactId>
			    <version>${springsecurity.version}</version>
			</dependency>  
  
  
Another reason could be because of mapping.
in okta profile page> Applications> GENERAL
============================================

SAML Settings>GENERAL
======================
Single sign on URL -http://localhost:8080/OauthProfile/saml/SSO
 
Use this for Recipient URL and Destination URL
 
Allow this app to request other SSO URLs

Audience URI (SP Entity ID) -http://localhost:8080/OauthProfile/saml/metadata

Default RelayState 

If no value is set, a blank RelayState is sent

Name ID format -Unspecified

Application username -Okta username



