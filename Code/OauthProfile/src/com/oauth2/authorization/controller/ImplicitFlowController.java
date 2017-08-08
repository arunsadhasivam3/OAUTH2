package com.oauth2.authorization.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.oauth2.authorization.utils.HttpUtils;

/**
 * Okta supported implicit flow control example
 * @author Arunkumar_Sadhasivam
 *
 */
@Controller 
public class ImplicitFlowController  extends BaseController{
	private static Logger log = Logger.getLogger(ImplicitFlowController.class);

	
	/**
	 * Login page for redirect to oauth page.
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/implicitCodeFlow")
	public ModelAndView implicit(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/oktaimplicitlogin.jsp", true));

		return mv;
		
	}
	
	
	/**
	 * to test okta login based on the value in login.jsp
	 * @param request
	 * @param model
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/implicitLoginFlow")
	public void login(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
		//google
		
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		System.err.println("OktaProfileController.login():userName:"+userName);
		System.err.println("OktaProfileController.login():password:"+password);
		//client
		String sessionTokenURL = "https://dev-878414.oktapreview.com/api/v1/authn";
		
		
		JSONObject jsonData = new JSONObject();
		jsonData.accumulate("username", userName);
		jsonData.accumulate("password", password);
		
		String sessionToken = HttpUtils.getRequestedToken(sessionTokenURL,1,jsonData,"sessionToken");
		
		String okta_URL= "https://dev-878414.oktapreview.com/oauth2/v1/authorize"+
			"?sessionToken="+sessionToken +
			"&client_id=3pgiRciiwvdG6bHru52e"+
			"&scope=openid%20email%20profile"+
			"&response_type=id_token"+
			"&response_mode=fragment"+
			"&state=Test12345"+
			"&nonce=Test12345"+
			"&redirect_uri=http://localhost:8080/OauthProfile";
		response.setContentType("application/json");
		log.debug("OktaProfileController.login():okta_URL:"+okta_URL);
		response.sendRedirect(okta_URL);

	}
	


}
