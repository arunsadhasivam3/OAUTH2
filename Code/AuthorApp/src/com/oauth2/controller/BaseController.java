package com.oauth2.authorization.controller;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * class Responsible for handling core functionalities
 * @author Arunkumar_Sadhasivam
 *
 */
public class BaseController {
	private static Logger log = Logger.getLogger(BaseController.class);
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
	 * To log cookies
	 * @param request
	 */
	protected void logsessionToken(HttpServletRequest request){

		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie: cookies){
			
			String key = cookie.getName();
			
			String value = cookie.getValue();
			
			System.out.println("cookie name:"+key +" value:"+value);
			
		}
		
	}

	
	/**
	 * To log the request parameters
	 * @param request
	 */
	public void logParam(HttpServletRequest request){
		Map<String,String[]> reqMap = request.getParameterMap();
		System.err.println("=============BaseController.logParam()::::BEGIN");
		Set<String> reqSet = reqMap.keySet();
		
		Iterator<String> it = reqSet.iterator();
		while(it.hasNext()){
			String key = it.next();
			
			System.err.println(">>> Key:"+key +  " value: "+ request.getParameter(key));
		}
		
		System.err.println("=============BaseController.logParam()::::END");
	}
}
