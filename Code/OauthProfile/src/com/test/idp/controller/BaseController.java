package com.test.idp.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

/**
 * class Responsible for handling core functionalities
 * @author Arunkumar_Sadhasivam
 *
 */
public class BaseController {


	/**
	 * To log the request parameters
	 * @param request
	 */
	protected void logParam(HttpServletRequest request){
		Map<String,String[]> reqMap = request.getParameterMap();
		
		Set<String> reqSet = reqMap.keySet();
		
		Iterator<String> it = reqSet.iterator();
		while(it.hasNext()){
			String key = it.next();
			
			System.out.println("Key:"+key +  " value: "+ request.getParameter(key));
		}
	}
	
	
	/**
	 * To log the request Headers
	 * @param request
	 */
	protected void logHeader(HttpServletRequest request){
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();
			System.out.println(
					"ProfileController.oauthLogin():Header key:" + header + " value:" + request.getHeader(header));
		}
	}
	
	
	/**
	 * Login page
	 * 
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws MetadataProviderException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/login")
	public ModelAndView authorize(HttpServletRequest request, Model model, HttpServletResponse response)
			throws MetadataProviderException, ServletException, IOException {
		
		ModelAndView mv = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/login.jsp", true));

		return mv;

	}
	
	/**
	 * Default servlet / root path
	 * 
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws MetadataProviderException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/auth")
	public void defaultPage(HttpServletRequest request, Model model, HttpServletResponse response)
			throws MetadataProviderException, ServletException, IOException {
		
		
		String okta_URL = "https://dev-878414.oktapreview.com/oauth2/v1/authorize"+
				"?idp=0oaalvrh7iLJX0FUQ0h7&client_id=3pgiRciiwvdG6bHru52e"+
			"&response_type=id_token token"+
			"&response_mode=form_post"+
			"&scope=openid offline_access"+
			"&redirect_uri=http://localhost:8080/OauthProfile"+
			"&state=arun@1984"+
			"&nonce=arun@1984"+
			"&username=arunsadhasivam@yahoo.co.in"+
			"&password=arun@1984";

		response.sendRedirect(okta_URL);

	}
}
