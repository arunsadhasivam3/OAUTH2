package com.oauth2.authorization.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

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
	public static String getRequestedToken(String authnUrl ,int requestType, JSONObject jsonData,String key){
		String result = null;
		
		URL url; 
		HttpURLConnection conn = null;
		try {
			url = new URL(authnUrl);
			conn = (HttpURLConnection)url.openConnection();
			
			
			if(requestType==1){
				conn.setDoOutput(true);//POST
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.connect();
				conn.getOutputStream().write(jsonData.toString().getBytes());
			}else{
				conn.setDoInput(true);//POST
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				HttpURLConnection.setFollowRedirects(true);
				conn.setRequestProperty("Authorization", jsonData.getString("Authorization"));
				conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
				conn.connect();
			}
			
			
			result = readValue(conn,key);
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
	private  static String readValue(HttpURLConnection conn,String key){
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
		
		
		if(key!=null){
			String resultToken = readJSON(buffer.toString(),key);
			
			return resultToken;
		}
		
		return buffer.toString();
	}
	
	/**
	 * To read json data.
	 * @param json
	 * @return
	 */
	private static String readJSON(String jsonValue,String key){
		JSONObject json = new JSONObject(jsonValue);
		
		if(key==null){
			return json.toString();
		}
		
		return (String)json.get(key);
	}
	
	
	
	/**
	 * To get All the Token from authorization code
	 * @param authToken
	 * @return
	 */
	public static String getAllTokens(String accessTokenURL) {
		
		StringBuffer buffer = new StringBuffer();
		URL url; 
		HttpURLConnection conn = null;
		BufferedReader br = null;
		try {
			url = new URL(accessTokenURL);
			conn = (HttpURLConnection)url.openConnection();
			//HttpURLConnection.setDefaultAllowUserInteraction(true);
			//HttpURLConnection.setFollowRedirects(true);
			//conn.setDoOutput(true);

			conn.connect();
			String code = checkRedirect(conn);
			if(code!=null){
				return code;
			}
			
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
	 * To check for redirect and return code.
	 * @param conn
	 * @return
	 * @throws IOException 
	 */
	private static String checkRedirect(HttpURLConnection conn) throws IOException {
		int status = conn.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER) {
				
				// get redirect url from "location" header field
				String newUrl = conn.getHeaderField("Location");
				conn = (HttpURLConnection) new URL(newUrl).openConnection();

				//String cookies = conn.getHeaderField("Set-Cookie");
				String query = newUrl;
				int index = query.indexOf("#") + 1;

				if (index != -1) {
					String code = newUrl.substring(index, query.length());

					return code;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * To get the Tokens in map to lookup
	 * @param code
	 * @return
	 */
	public static Map<String,String> getTokenMap(String tokenParam){
		Map<String,String> tokenMap = new HashMap<String,String>();
		
		StringTokenizer tokenizer = new StringTokenizer(tokenParam, "&");
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			
			String keyValPair[] = token.split("=");
			
			tokenMap.put(keyValPair[0], keyValPair[1]);
		}
		
		
		return tokenMap;
	}
	
	public static void main(String args[]) throws IOException {
		/*String userName= "arunsadhasivam@yahoo.co.in";
		String password = "Test@2017";
		
		JSONObject jsonData = new JSONObject();
		jsonData.accumulate("username", userName);
		jsonData.accumulate("password", password);
		log.debug("HttpUtils.main()"+jsonData);
		
		String authURL = "https://dev-878414.oktapreview.com/api/v1/authn";

		String sessionToken = HttpUtils.getRequestedToken(authURL,jsonData,"sessionToken");*/
		

		
	}

}
