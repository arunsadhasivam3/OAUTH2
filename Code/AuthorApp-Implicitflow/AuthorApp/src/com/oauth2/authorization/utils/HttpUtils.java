package com.oauth2.authorization.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.json.JSONObject;

public class HttpUtils {
	
	private static Logger log = Logger.getLogger(HttpUtils.class);

	HttpUtils() throws IOException {

	}

	
	
	/**
	 * To get the session Token
	 * @param authnUrl
	 * @param jsonData
	 * @return
	 */
	public static String getSessionToken(String authnUrl , JSONObject jsonData){
		String result = null;
		
		URL url; 
		URLConnection conn = null;
		try {
			url = new URL(authnUrl);
			conn = url.openConnection();
			conn.setDoOutput(true);//POST
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type", "application/json");
			
			conn.connect();
			
			
			conn.getOutputStream().write(jsonData.toString().getBytes());
			
			result = readValue(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	
	/**
	 * To read value from http call.
	 * @param conn
	 * @return
	 */
	private  static String readValue(URLConnection conn){
		StringBuffer buffer = new StringBuffer();
		BufferedReader br = null;
		String line = null;
		try {
			 br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while((line=br.readLine())!=null){
				
				buffer.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.debug("HttpUtils.readValue()"+buffer.toString());
		
		String sessionToken = readJSON(buffer.toString());
		
		return sessionToken;
	}
	
	/**
	 * To get the accessToken from authorization code
	 * @param authToken
	 * @return
	 */
	public static String getJSONToken(String accessTokenURL) {
		
		StringBuffer buffer = new StringBuffer();
		URL url; 
		URLConnection conn = null;
		BufferedReader br = null;
		try {
			url = new URL(accessTokenURL);
			conn = url.openConnection();
			conn.connect();
			
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while((line=br.readLine())!=null){
				
				buffer.append(line);
			}

		} catch (MalformedURLException e) {
			log.error("No Access Tokens present:");
		} catch (IOException e) {
			log.error("No Access Tokens present:");
		}finally{
			try {
				if(br!=null){
					br.close();
				}
			} catch (IOException e) {
				log.error("No Access Tokens present:");
			}
		}
		log.debug("HttpUtils.getJSONToken():END:"+buffer.toString());
		
		return buffer.toString();
	}

	
	
	/**
	 * To read json data.
	 * @param json
	 * @return
	 */
	private static String readJSON(String jsonValue){
		JSONObject json = new JSONObject(jsonValue);
		
		return (String)json.get("sessionToken");
	}
	
	
	public static void main(String args[]) throws IOException {
		String userName= "arunsadhasivam@yahoo.co.in";
		String password = "Test@2017";
		
		JSONObject jsonData = new JSONObject();
		jsonData.accumulate("username", userName);
		jsonData.accumulate("password", password);
		log.debug("HttpUtils.main()"+jsonData);
		
		String authURL = "https://dev-878414.oktapreview.com/api/v1/authn";

		String sessionToken = HttpUtils.getSessionToken(authURL,jsonData);
		log.debug("HttpUtils.main()"+ sessionToken);
	}

}
