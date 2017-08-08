package com.oauth2.authorization.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

@Controller
public class OktaIdentityProviderFlow extends BaseController{

	private static Logger log = Logger.getLogger(OktaIdentityProviderFlow.class);

	
	/**
	 * Login page for redirect to oauth page.
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/oktaIdentityFlow")
	public ModelAndView oktaauthorize(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/oktalogin.jsp", true));

		return mv;
		
	}
	
	
	
	
	/**
	 * to test okta login based on the value in login.jsp
	 * @param request
	 * @param model
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/auth")
	public void login(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		log.debug("OktaProfileController.login():userName:"+userName);
		log.debug("OktaProfileController.login():password:"+password);
		
		String okta_URL= "https://dev-878414.oktapreview.com/oauth2/v1/authorize"+
			"?idp=0oaalvrh7iLJX0FUQ0h7"+
			"&client_id=3pgiRciiwvdG6bHru52e"+
			"&scope=openid%20email%20profile"+
			"&response_type=id_token"+
			"&response_mode=fragment"+
			"&state=Test12345"+
			"&nonce=Test12345"+
			"&redirect_uri=http://localhost:8080/OauthProfile";
			//"&redirect_uri=http://localhost:8081/NSL/callback";
			//"redirect_uri=https://dev-878414.oktapreview.com";
			
		response.setContentType("application/json");
		response.sendRedirect(okta_URL);

	}
	
	
	
	
	@RequestMapping("/callback")
	public void callback(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
		log.debug("OktaProfileController.callback()");
		logParam(request);
		String accessToken = request.getParameter("access_token");
		//String idToken = request.getParameter("id_token");
		String code = request.getParameter("code");
		
		if(accessToken==null){
			accessToken =code;
		}
		
		
		String clientURL= "http://localhost:8080/OauthProfile";

		log.debug("OktaProfileController.callback():clientURL:"+clientURL);
		response.setHeader("Authorization",  accessToken);
		response.setContentType("application/json");
		response.sendRedirect(clientURL);

	}
	
	
}
