package com.oauth2.authorization.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;


public class CORSFilter implements Filter {

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		//System.out.println("Filtering on...........................................................");
		
		//logRequest(req);
		HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");

        
        
		chain.doFilter(req, res);
	}
	
	private void logRequest(ServletRequest req){
 		
		Enumeration<String> parmEnum = req.getParameterNames();
		while(parmEnum.hasMoreElements()){
			
			String result  = parmEnum.nextElement();
			System.out.println("key:"+result + "\t value:"+req.getParameter(result));
		}
		
		
	}

	public void init(FilterConfig filterConfig) {}

	public void destroy() {}

}
