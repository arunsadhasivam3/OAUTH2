package com.oauth2.authorization.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.oauth2.authorization.utils.HttpUtils;

/**
 * entry point 
 * @author Arunkumar_Sadhasivam
 *
 */
 
@Controller 
public class ProfileController extends BaseController{

	
	private static String OAUTH_URL="https://dev-878414.oktapreview.com/oauth2/v1/authorize";
	private static String CLIENT_ID="75fd832l448w6u";
	
	public ProfileController() {
	}
	
	/**
	 * Default root path for web application.
	 * @param request
	 * @param model
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/default")
	public void defaultPage(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  IOException {
		String authCode = request.getParameter("code");
		if (authCode != null) {
			generateAccessToken(request, model, response);
		} else {
			linkedin(request, model, response);
		}
	}

	/**
	 * Login page for redirect to oauth page.
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/login")
	public ModelAndView authorize(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/linkedin.jsp", true));

		return mv;
		
	}
	
	/**
	 * Login page for redirect to oauth page.
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/okta")
	public ModelAndView oktaauthorize(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/okta.jsp", true));

		return mv;
		
	}
	
	
	
	/**
	 * To link to linkedin.
	 * @param request
	 * @param model
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/linkedin")
	public void linkedin(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {

		OAUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";
		String param = "?response_type=code" 
						+ "&client_id=" + CLIENT_ID 
						+ "&redirect_uri=http://localhost:8081/NSL";
		String oathURL = OAUTH_URL + param;

		response.sendRedirect(oathURL);
	}

	
	/**
	 * To get the Access Token from AuthCode and redirect to the  authorized page
	 * @param request
	 * @param model
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/accessToken")
	private void generateAccessToken(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  IOException {
		logParam(request);
		
		String authCode = request.getParameter("code");
		String accessTokenURL = "https://www.linkedin.com/oauth/v2/accessToken"
				+ "?grant_type=authorization_code"
				//+"&response_type=code" 
				+ "&client_secret=Qmxv9eOB3fTpXZMU"
				+ "&code=" + authCode 
				+ "&client_id=" + CLIENT_ID;
				//+ "&redirect_uri=https://www.slideshare.net";

		String token = HttpUtils.getJSONToken(accessTokenURL);
		String tokenURL = ParseToken(accessTokenURL, token);
		String clientURL = "https://www.slideshare.net"+tokenURL;
		System.out.println("ProfileController.generateAccessToken():accessTokenURL:"+accessTokenURL);	
		System.out.println("ProfileController.generateAccessToken():clientURL:::::"+clientURL);
		//response.setContentType("application/x-www-form-urlencoded");
		response.sendRedirect(clientURL);
	}
}
