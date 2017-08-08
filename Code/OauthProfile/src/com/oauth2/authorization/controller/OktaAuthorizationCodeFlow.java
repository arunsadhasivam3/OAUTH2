package com.oauth2.authorization.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.oauth2.authorization.servlet.HeaderAttributeRequestWrapper;
import com.oauth2.authorization.servlet.HeaderAttributeResponseWrapper;
import com.oauth2.authorization.utils.HttpUtils;

@Controller
public class OktaAuthorizationCodeFlow extends BaseController{
	
	private static Logger log = Logger.getLogger(OktaAuthorizationCodeFlow.class);

	/**
	 * Login page for redirect to oauth page.
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/oktaAuthCodeFlow")
	public ModelAndView implicit(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		log.debug("OktaAuthorizationCodeFlow:BEGIN:::");
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/oktaauthlogin.jsp", true));

		return mv;
		
	}
	
	
	/**
	 * to test okta login based on the value in login.jsp
	 * @param request
	 * @param model
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/oktaAuthLoginFlow",method=RequestMethod.POST )
	public ModelAndView login(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
		
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		//client
		String sessionTokenURL = "https://dev-878414.oktapreview.com/api/v1/authn";
		JSONObject jsonData = new JSONObject();
		jsonData.accumulate("username", userName);
		jsonData.accumulate("password", password);
		
		String sessionToken = HttpUtils.getRequestedToken(sessionTokenURL,1,jsonData,"sessionToken");
		

		String okta_AUTH_URL= "https://dev-878414.oktapreview.com/oauth2/v1/authorize"+
			"?idp=0oaalvrh7iLJX0FUQ0h7"+
			"&sessionToken="+sessionToken +
			"&grant_type=authorization_code"+
			"&client_id=3pgiRciiwvdG6bHru52e"+
			"&scope=openid%20email%20profile"+
			"&response_type=code"+
			"&response_mode=fragment"+
			"&state=Test12345"+
			"&nonce=Test12345"+
			//"&redirect_uri=https://dev-878414.oktapreview.com/oauth2/v1/authorize/callback";
			"&redirect_uri=http://localhost:8080/OauthProfile/oktaAuthFlowCallback";
		
		String tokenParam = HttpUtils.getAllTokens(okta_AUTH_URL);
		
		Map<String,String> tokenMap = HttpUtils.getTokenMap(tokenParam);
		String token_id ="";
		if(tokenMap!=null){
			token_id = tokenMap.get("code");
	    }
		
		String client  =  "3pgiRciiwvdG6bHru52e";
		String secretKey= "9x_RcUeU5AY8eWb413LUzE5_dr1BHhiSZXTTLMNF";
		String base64EncodedSecretKey = client + ":" + secretKey;
		String base64ClientIdSecret = DatatypeConverter.printBase64Binary(
				 base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8));
		String openIdUrl = "https://dev-878414.oktapreview.com/oauth2/v1/token";
		String param1=	"grant_type=authorization_code"
				+ "&code=" + token_id 
				+ "&redirect_uri=http://localhost:8080/OauthProfile/oktaAuthFlowCallback" ;
		

		
		jsonData = new JSONObject();
		jsonData.accumulate("Authorization","Basic " + base64ClientIdSecret);
		jsonData.accumulate("Content-type", "application/x-www-form-urlencoded");
		
		String resultToken = HttpUtils.getRequestedToken(openIdUrl+"?"+param1, 2,jsonData,null);
		JSONObject result = new JSONObject(resultToken);
		String accessToken = result.get("access_token").toString();
		System.out.println("OktaAuthorizationCodeFlow.login()"+accessToken);
		
		
		HeaderAttributeRequestWrapper requestwrapper = new HeaderAttributeRequestWrapper(request);
		requestwrapper.addAuthHeader("Authorization", accessToken, 2);
		requestwrapper.addHeader("Content-type", "application/x-www-form-urlencoded");
		requestwrapper = new HeaderAttributeRequestWrapper(requestwrapper);
		request = (HttpServletRequest)requestwrapper.getRequest();
		//System.err.println("OktaAuthorizationCodeFlow.login()::::Header:::"+wrapper.getHeader("Authorization"));
		

		
		HeaderAttributeResponseWrapper responseWrapper = new HeaderAttributeResponseWrapper(response);
		responseWrapper = new HeaderAttributeResponseWrapper(responseWrapper);
		response = (HttpServletResponse)responseWrapper.getResponse();
		
		logHeader(request);
		
		RequestDispatcher rd = request.getRequestDispatcher("/profileView.htm");
		request.setAttribute("Authorization", "Bearer "+accessToken);
		//response.setHeader("Authorization", "Bearer "+accessToken);
		//response.addHeader("Content-type", "application/x-www-form-urlencoded");
		try {
			//rd.include(request, response);
			System.out.println("OktaAuthorizationCodeFlow.login()"+request.getHeader("Authorization"));
			rd.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
 	  
		return null;
	}
	
}
