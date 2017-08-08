package com.oauth2.authorization.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.oauth2.authorization.utils.HttpUtils;

/**
 * entry point  for linkedin
 * @author Arunkumar_Sadhasivam
 *
 */
 
@Controller 
public class AuthorizationCodeFlow extends BaseController{
	private static Logger log = Logger.getLogger(AuthorizationCodeFlow.class);

	
	private static String OAUTH_URL="https://dev-878414.oktapreview.com/oauth2/v1/authorize";
	private static String CLIENT_ID="75fd832l448w6u";
	
	public AuthorizationCodeFlow() {
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
	@RequestMapping("/AuthCodeFlow")
	public ModelAndView authorize(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/linkedin.jsp", true));

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
				+"&response_type=code"  //important
				+ "&client_secret=Qmxv9eOB3fTpXZMU"
				+ "&code=" + authCode 
				+ "&client_id=" + CLIENT_ID;

		String token = HttpUtils.getJSONToken(accessTokenURL);
		String tokenURL = ParseToken(accessTokenURL, token);
		String clientURL = "https://www.slideshare.net"+tokenURL;
		//String clientURL = "http://localhost:8080/OauthProfile"+tokenURL;
		log.debug("AuthorizationCodeFlow Linkedin.generateAccessToken():accessTokenURL:"+accessTokenURL);	
		log.debug("AuthorizationCodeFlow Linkedin.generateAccessToken():clientURL:::::"+clientURL);
		response.sendRedirect(clientURL);
	}
	
	
	
	/**
	 * Authorization Flow
	 * To parse the JSON Token
	 * @param url
	 * @param token
	 * @return
	 */
	public String  ParseToken(String url,String token){
		
		StringBuffer buffer = new StringBuffer();
		
		if(token.equals("")){

			return buffer.toString();
		}
		try {
			JSONObject json = new JSONObject(token);
			
			String accessToken = (String)json.get("access_token");
			//String  tokenType = (String)json.get("tokenType");
			Integer  expiresIn = (Integer)json.getInt("expires_in");
			
			buffer.append("?access_token="+accessToken);
			//buffer.append("&tokenType="+tokenType);
			buffer.append("&expires_in="+expiresIn);
			
			
		} catch (JSONException e) {
			log.error("JSON Token Error:"+e.getMessage());
		}

		log.debug("BaseController.ParseToken():::TOKENS:END::::::::::::::::::::"+buffer.toString());
		
		
		return buffer.toString();
	}
}
