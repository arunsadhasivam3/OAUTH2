package com.idp.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.InternalResourceView;

import com.okta.sdk.models.users.User;
import com.idp.exception.UserException;
import com.idp.saml.ProfileSAMLDiscovery;
import com.idp.service.APIService;

/**
 * entry point
 * 
 * @author Arunkumar_Sadhasivam
 *
 */
@Controller
public class ProfileController extends BaseController {

	private Logger log = Logger.getLogger(ProfileController.class);

	public ProfileController() {
	}

	
	/**
	 * called during webstartup to load the Metadata(idp.xml)
	 * 
	 * @param request
	 * @param model
	 * @param response
	 * @throws MetadataProviderException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/idpSelection")
	public void idpSelection(HttpServletRequest request, Model model, HttpServletResponse response)
			throws MetadataProviderException, ServletException, IOException {
		WebApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(request.getServletContext());
		MetadataManager mm = context.getBean("metadata", MetadataManager.class);

		/*
		 * APIService service = new APIService(); service.connect();
		 */

		String responseURL = request.getParameter("appId");
		Set<String> idps = mm.getIDPEntityNames();
		request.setAttribute("oktaIDPs", idps);
		String samlRedirectURL = (String) request.getAttribute(ProfileSAMLDiscovery.RETURN_URL);
		log.debug("samlRedirectURL:::" + samlRedirectURL);
		if (responseURL != null) {
			samlRedirectURL = responseURL;
		}
		response.sendRedirect(samlRedirectURL);// .forward(request, response);
	}

	/**
	 * To view the profile Page.
	 * 
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws MetadataProviderException
	 * @throws IOException
	 */

	@RequestMapping("/profileView")
	public ModelAndView oauthLogin(HttpServletRequest request, Model model, HttpServletResponse response)
			throws MetadataProviderException, IOException {
		logParam(request);
		
		logsessionToken(request);
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView mv = null;
	
		if (authentication != null) {
			System.out.println("ProfileController.oauthLogin():authentication::::"+authentication.getName());

			SAMLCredential credential = (SAMLCredential) authentication.getCredentials();

			List<Attribute> attributes = credential.getAttributes();
			for(Attribute attribute:attributes){
				System.out.println("Attribute:" + attribute.getName());

			}

			request.setAttribute("credential", credential);
			request.setAttribute("attributes", attributes);

			mv = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/profileView.jsp", true));
		} else {
			return apiProfileView(request, model, response);
		}
		return mv;

	}
	
	private void logsessionToken(HttpServletRequest request){

		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie: cookies){
			
			String key = cookie.getName();
			
			String value = cookie.getValue();
			
			System.out.println("cookie name:"+key +" value:"+value);
			
		}
		/*HttpSession session = request.getSession(false);
		Enumeration<String> sessionkey = session.getAttributeNames();
		while(sessionkey.hasMoreElements()){
			String key = sessionkey.nextElement();
			
			String value = (String)session.getAttribute(key);
			
			System.out.println("session Key:"+key + " value:"+ value);
		}*/
	}

	@RequestMapping("/apiProfileView")
	public ModelAndView apiProfileView(HttpServletRequest request, Model model, HttpServletResponse response)
			throws MetadataProviderException, IOException {
		
		ModelAndView mv = null;
		User user = null;
		
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		
		APIService service = new APIService();
		String message = "";
		try{
			user = service.getUserDetails(userName,password);
		}catch(UserException e){
			message = e.getMessage();
		}
		
		
		mv = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/apiProfileView.jsp", true));
		mv.addObject("user", user);
		mv.addObject("message", message);
		
		
		return mv;
	}

}
