package com.idp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Class responsible for loading web components and handlers.
 * 
 * @author Arunkumar_Sadhasivam
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.idp" })
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	/**
	 * load resources from context specified otherwise it cant understand
	 * /resources, images context.
	 * 
	 **/
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/images/**").addResourceLocations("/images/");
	}

	

	public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
		//registry.addInterceptor(new SAMLInterceptor());

	}

	@Override
	/**
	 * welcome file list.
	 */
	public void addViewControllers(ViewControllerRegistry registry) {
		//registry.addViewController("/").setViewName("profileView");
		//registry.addViewController("/okta/").setViewName("profileView");
		
	}

	
}
