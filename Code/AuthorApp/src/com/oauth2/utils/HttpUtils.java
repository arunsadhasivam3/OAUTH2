package com.oauth2.authorization.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class HttpUtils {
	
	private static Logger log = Logger.getLogger(HttpUtils.class);

	HttpUtils() throws IOException {

	}

	/**
	 * To get the accessToken from authorization code
	 * @param authToken
	 * @return
	 */
	public static String getJSONToken(String accessTokenURL) {
		log.debug("HttpUtils.getJSONToken():BEGIN:");
		
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
	
	
	public static void main(String args[]) throws IOException {
		new HttpUtils();
	}

}
