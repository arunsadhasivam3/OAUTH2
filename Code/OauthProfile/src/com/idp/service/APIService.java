package com.idp.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.models.users.LoginCredentials;
import com.okta.sdk.models.users.Password;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;
import com.idp.exception.UserException;

public class APIService {

	private static final ApiClientConfiguration oktaSettings = new ApiClientConfiguration(
            "https://dev-878414-admin.oktapreview.com",
            "00FyouxjIfux6XTJWTGQv49MCOMguh_Do2emn05VWH");//api token click security>api>generate token
	Map<String, String> headers = new HashMap<String, String>();

	
	public User getUserDetails(String userName , String password) throws IOException {
		AuthApiClient authClient = new AuthApiClient(oktaSettings);
		User user = new User();
		boolean isConnected = false;	
		
		try {
			AuthResult result = authClient.authenticate(userName, password, null);
			String status = result.getStatus();
			isConnected = status.equalsIgnoreCase("SUCCESS")?true:false;
			if(isConnected){
				UserApiClient userApiClient = new UserApiClient(oktaSettings);
				user = userApiClient.getUser(userName);
			}
		
		} catch (IOException e) {
			throw new UserException("IO Exception:"+e.getMessage());
		} catch (UserException e) {
			// TODO Auto-generated catch block
			throw new UserException("User Exception:"+e.getMessage());
		}
		
		
		return user;
	}
	
	/**
	 * To create user
	 * @throws IOException 
	 */
	public static void createUser(User user) throws IOException {
		UserApiClient userApiClient = new UserApiClient(oktaSettings);

		boolean activate = true;
		userApiClient.createUser(user, activate);

	}
	
	
	
	public static void main(String args[]) throws IOException{
		UserProfile userProfile = new UserProfile();
		userProfile.setLogin("API USER3");
		userProfile.setFirstName("Test3");
		userProfile.setLastName("Api");
		userProfile.setEmail("arunsadhasivam6@gmail.com");
		userProfile.setLogin("arunsadhasivam6@gmail.com");
		
		
		//userid
		User user = new User();
		user.setId("TESTAPI1");
		
		//password
		Password password = new Password();
		password.setValue("Test@2017");
		LoginCredentials loginCredentials = new LoginCredentials();
		loginCredentials.setPassword(password);

		
		user.setCredentials(loginCredentials);
		user.setProfile(userProfile);
		
		APIService.createUser(user);
		//new APIService().getUserDetails("","");
	}

}
