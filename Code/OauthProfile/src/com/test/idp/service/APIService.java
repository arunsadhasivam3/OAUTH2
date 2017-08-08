package com.test.idp.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.clients.UserApiClient;
import com.okta.sdk.clients.UserGroupApiClient;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;
import com.okta.sdk.models.usergroups.UserGroup;
import com.okta.sdk.models.users.User;
import com.okta.sdk.models.users.UserProfile;
import com.test.idp.exception.UserException;

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
		/*UserProfile userProfile = new UserProfile();
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
		
		APIService.createUser(user);*/
		User user = new APIService().getUserDetails("arunsadhasivam@yahoo.co.in","Test@2017");
		UserProfile profile = user.getProfile();
		System.out.println("APIService.main():user#name:"+profile.getUnmapped().keySet());
		printAdditionalAttribute(profile);
		//fetch other then UserProfile data like Department,ManagerID,manager if configured.
		addUserToGroup("arunsadhasivam1@gmail.com");
		
	}
	
	
	
	private static void addUserToGroup(String user)throws IOException{
		UserGroupApiClient groupApi = new UserGroupApiClient(oktaSettings);
	    groupApi.addUserToGroup("APP_SEC_GROUP1", user);
	}
	
	private static void getUserGroupInfo() throws IOException{
		

		UserGroupApiClient groupApi = new UserGroupApiClient(oktaSettings);
		UserGroup group = groupApi.getUserGroup("APP_SEC_GROUP1");
		
		System.out.println("APIService.main()"+group.getUnmapped().keySet());
		
	}
	private static void printAdditionalAttribute(UserProfile profile)
	{
		Map<String,Object> userProfileMap = profile.getUnmapped();
		Set<String> additionalInfo = profile.getUnmapped().keySet();
		Iterator<String> it = additionalInfo.iterator();
		while(it.hasNext()){
			String userAttribute = it.next();
			String attrValue = (String)userProfileMap.get(userAttribute);
			System.out.println("User Attr Key:"+userAttribute + ": Attr Value:"+attrValue);
			
			
		}
	}

}
