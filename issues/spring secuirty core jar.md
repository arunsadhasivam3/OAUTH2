Issues:
========
if spring-security-core is not added it default download 3.1.2 version jar. so it is good to add in maven and add
to classpath.

see below warning log.

	INFO: Initializing Spring root WebApplicationContext
	- Root WebApplicationContext: initialization started
	- Refreshing Root WebApplicationContext: startup date [Sat May 20 14:43:07 PDT 2017]; root of context hierarchy
	- Registering annotated classes: [class com.test.idp.config.AppConfig]
	- Loading XML bean definitions from class path resource [com/test/idp/config/securityContext.xml]
	- You are running with Spring Security Core 3.1.2.RELEASE
	- *** Spring Major version '3' expected, but you are running with version: 4.3.1.RELEASE. 
	   Please check your classpath for unwanted jar files.
	- Spring Security 'config' module version is 3.1.2.RELEASE


Solution:
==========

Add secuirty-core dependencies in pom.xml below and change the xml beans to appropriate versions.
since 4.3 only exist in xsd no 4.3.1 in actual site so change to it.

	<beans xmlns="http://www.springframework.org/schema/beans"
	       xmlns:security="http://www.springframework.org/schema/security"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xmlns:context="http://www.springframework.org/schema/context"
	       xsi:schemaLocation="http://www.springframework.org/schema/beans
	              http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
		      http://www.springframework.org/schema/security
		      http://www.springframework.org/schema/security/spring-security.xsd 
		      http://www.springframework.org/schema/context 
		      http://www.springframework.org/schema/context/spring-context-4.3.xsd">


		<dependency>
		    <groupId>org.springframework.security</groupId>
		    <artifactId>spring-security-core</artifactId>
		    <version>4.1.1.RELEASE</version>
		</dependency>  
		

	FrameworkServlet 'requestDispatcher': initialization started
	- Refreshing WebApplicationContext for namespace 'requestDispatcher-servlet': startup date [Sat May 20 14:41:25 PDT 2017]; 
	parent: Root WebApplicationContext
	- Registering annotated classes: [class com.test.idp.config.WebConfig]
	- Loading XML bean definitions from class path resource [com/test/idp/config/securityContext.xml]
	- Spring Security 'config' module version is 4.1.1.RELEASE
	- Checking sorted filter chain: [<metadataGeneratorFilter>, order = -2147483648, Root bean: class [org.springframework.security.web.csrf.CsrfFilter]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null, order = 700, <samlFilter>, order = 1601, Root bean: class [org.springframework.security.web.savedrequest.RequestCacheAwareFilter]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null, order = 1700, Root bean: class 
	
	[org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null, order = 1800, Root bean: class 
	
	[org.springframework.security.web.authentication.AnonymousAuthenticationFilter]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null, order = 2100, Root bean: class 
	
	[org.springframework.security.web.session.SessionManagementFilter]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null, order = 2200, Root bean: class 
	
	[org.springframework.security.web.access.ExceptionTranslationFilter]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null, order = 2300, <org.springframework.security.web.access.intercept.FilterSecurityInterceptor#0>, order = 2400]
	- New metadata succesfully loaded for 'C:\ARUN\workspace\Identity\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\OauthProfile\WEB-INF\classes\resources\metadata\idp.xml'
	- Next refresh cycle for meta



pom.xml
=======
without spring-security-core causing issue.

		<properties>
			<baseRootDir>../${project.basedir}</baseRootDir>
			<spring.version>4.3.1.RELEASE</spring.version>
			<httpclient.version>4.5.2</httpclient.version>
			<jackson.version>2.7.5</jackson.version>
			<joda-time.version>2.7</joda-time.version>
			<maven-surefire-plugin.version>2.18.1</maven-surefire-plugin.version>
			<slf4j-api.version>1.7.10</slf4j-api.version>
			<testng.version>6.8.17</testng.version>
			<org.hamcrest.version>1.3</org.hamcrest.version>
			<com.google.guava.version>18.0</com.google.guava.version>
			<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
			<okta.sdk>0.0.4</okta.sdk>
			<springsecurityoauth2.version>2.1.0.RELEASE</springsecurityoauth2.version>
			<springsecurity.version>4.1.1.RELEASE</springsecurity.version>
		</properties>



		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-web</artifactId>
			<version>${springsecurity.version}</version>
		</dependency>

		<dependency>
			<groupId>org.springframework.security.extensions</groupId>
			<artifactId>spring-security-saml2-core</artifactId>
			<version>1.0.0.RELEASE</version>
			<scope>compile</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-config</artifactId>
			<version>${springsecurity.version}</version>
			<scope>compile</scope>
		</dependency>


