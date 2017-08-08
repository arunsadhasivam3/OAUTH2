package com.oauth2.authorization.controller;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * class Responsible for handling core functionalities
 * 
 * @author Arunkumar_Sadhasivam
 *
 */
public class BaseController {
	private static Logger log = Logger.getLogger(BaseController.class);

	/**
	 * To log the request Headers
	 * 
	 * @param request
	 */
	protected void logHeader(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String header = headerNames.nextElement();
			log.debug("ProfileController.oauthLogin():Header key:" + header + " value:" + request.getHeader(header));
		}
	}

	/**
	 * To log cookies
	 * 
	 * @param request
	 */
	protected void logsessionToken(HttpServletRequest request) {

		Cookie[] cookies = request.getCookies();

		for (Cookie cookie : cookies) {

			String key = cookie.getName();

			String value = cookie.getValue();

			log.debug("cookie name:" + key + " value:" + value);

		}

	}

	/**
	 * To log the request parameters
	 * 
	 * @param request
	 */
	public void logParam(HttpServletRequest request) {
		Map<String, String[]> reqMap = request.getParameterMap();
		System.err.println("=============BaseController.logParam()::::BEGIN");
		Set<String> reqSet = reqMap.keySet();

		Iterator<String> it = reqSet.iterator();
		while (it.hasNext()) {
			String key = it.next();

			log.debug(">>> Key:" + key + " value: " + request.getParameter(key));
		}

	}
}
