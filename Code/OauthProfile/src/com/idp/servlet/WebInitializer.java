package com.idp.servlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import com.idp.config.AppConfig;
import com.idp.config.WebConfig;

/**
 * Class responsible for loading context and web application
 * 
 * @author Arunkumar_Sadhasivam
 *
 */
public class WebInitializer implements WebApplicationInitializer {
	// @Autowired Environment env;

	@Override
	public void onStartup(ServletContext context) throws ServletException {

		// like <context-param> available throughout webapp , available to
		// all servlet, filter
		AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
		appContext.register(AppConfig.class);
		registerDispatcherServlet(context);

		context.addListener(new ContextLoaderListener(appContext));

	}

	private void registerDispatcherServlet(ServletContext servletContext) {

		AnnotationConfigWebApplicationContext dispatcherContext = createContext(WebConfig.class);

		ServletRegistration.Dynamic defaultDispatcher = servletContext.addServlet("requestDispatcher",
				new DispatcherServlet(dispatcherContext));
		defaultDispatcher.setLoadOnStartup(1);
		defaultDispatcher.addMapping("*.htm");
		
		
		//System.setProperty("spring.security.strategy",SecurityContextHolder.MODE_GLOBAL);
		// mapping for /saml/web,/saml/sso,/saml/logout
		 FilterRegistration.Dynamic filter = servletContext.addFilter("springSecurityFilterChain",
				DelegatingFilterProxy.class);
		filter.addMappingForUrlPatterns(null, false, "/*");
	}

	private AnnotationConfigWebApplicationContext createContext(final Class<?>... annotatedClasses) {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(annotatedClasses);
		return context;
	}

}
