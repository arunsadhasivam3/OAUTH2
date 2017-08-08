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

@Controller
public class OktaWidgetController extends BaseController{
	
	/**
	 * Login page for redirect to oauth page.
	 * @param request
	 * @param model
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/oktaWidgetFlow")
	public ModelAndView authorize(HttpServletRequest request, Model model, HttpServletResponse response)
			throws  ServletException, IOException {
		
		ModelAndView mv  = new ModelAndView(new InternalResourceView("/WEB-INF/jsps/oktasigninWidget1.jsp", true));

		return mv;
		
	}
	
	

}
