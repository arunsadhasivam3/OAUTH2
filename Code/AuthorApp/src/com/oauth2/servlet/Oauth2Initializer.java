package com.oauth2.authorization.servlet;

import javax.servlet.Filter;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.oauth2.authorization.config.AppConfig;
import com.oauth2.authorization.filter.CORSFilter;

public class Oauth2Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	  @Override
	    protected Class<?>[] getRootConfigClasses() {
	        return new Class[] { AppConfig.class };
	    }
	  
	    @Override
	    protected Class<?>[] getServletConfigClasses() {
	        return null;
	    }
	  
	    @Override
	    protected String[] getServletMappings() {
	        return new String[] { "/" };
	    }
	    
	   @Override
	    protected Filter[] getServletFilters() {
	    	Filter [] singleton = { new CORSFilter()};
	    	return singleton;
	    }
}
