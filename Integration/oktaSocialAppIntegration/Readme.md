Setting up a Social Authentication provider in Okta
====================================================
http://developer.okta.com/docs/api/resources/social_authentication.html


Click the blue “Admin” button to get into the Okta Administrator view.
From the “Security” menu, select “Identity Providers”.
Use the “Add Identity Provider” drop-down menu to select the Identity Provider that you want to configure.

The options for social authentication providers are:

Facebook
Google
LinkedIn
Microsoft

Configure your Social Authentication provider:
==============================================

Name:
=====
  We suggest using the name you would expect to see on a button, something like “Log in to Facebook”.

IdP Username:
=============
  Set to “idpuser.email”.

Match against:
==============
Leave set to the default.

Account Link Policy:
====================
Leave set to “Automatic” for now.

Auto-Link Restrictions:
=======================
Leave set to the default.

Provisioning Policy:
====================
Leave set to “Automatic” for now.

Profile Master:
===============
Leave unchecked.

Group Assignments: 
==================
Leave set to the default.

Client Id:
==========
Set this to appropriate value for the Social Authentication provider that you are configuring.

Client Secret:
==============
Set this to appropriate value for the Social Authentication provider that you are configuring.

Scopes:
=======
Leave set to the default.
Make note of the “Login URL” from the “Identity Providers” page.

Copy this URL somewhere you can refer to it later. You will be using this URL to create an HTTP link that will allow users to log in to your Okta org or custom application using their social credentials.

Note: 
=====
This URL will look similar to this one: 

    https://example.okta.com/oauth2/v1/authorize
    ?idp=0oa0bcde12fghiJkl3m4
    &client_id={clientId}
    &response_type={responseType}
    &response_mode={responseMode}
    &scope={scopes}
    &redirect_uri={redirectUri}
    &state={state}
    &nonce={nonce}

Register an OAuth client using the App Integration Wizard.
==========================================================
Navigate to the Administrator Dashboard.

Select Applications.

Select Add Application.

Select Create New App to launch the App Integration Wizard. It guides you through the necessary configuration
steps and give you back a client_id which you use in Step 7. For social authentication, choose the following:
In the Create a New Application Integration page of the wizard, select OpenID Connect in the Sign on method section.

In the General Settings tab, check the Implicit checkbox for allowed grant types.
Create a Social Auth login (Authorize) URL by replacing the value of the following parameters in the Social Auth “Authorize URL.”

    client_id: use the client_id value you copied in step 6.

    scope: Determines the claims that are returned in the ID token.

    response_type: Determines which flow is used.

    response_mode: Determines how the authorization response should be returned.

    state: Protects against cross-site request forgery (CSRF).

    nonce: A string included in the returned ID Token. Use it to associate a client session with an ID Token,
    and to mitigate replay attacks.

    redirect_url: The location where Okta returns a user after the user has finished authenticating against
    their Social Authentication provider. This URL must start with “https” and must match one of the URLs
    in the redirect_uris array that you configured previously.


When complete, your Authorize URL looks something like this:

    https://example.okta.com/oauth2/v1/authorize?idp=0oa0bcde12fghiJkl3m4&client_id=AbcDE0fGHI1jk2LM34no
    &scope=openid email   profile
    &response_type=id_token
    &response_mode=fragment
    &state=someState
    &nonce=someNonce
    &redirect_uri=https://app.example.com/social_auth


If you log users into Okta, the Authorize URL looks something like this:
=========================================================================
    https://example.okta.com/oauth2/v1/authorize?idp=0oa0bcde12fghiJkl3m4
    &client_id=AbcDE0fGHI1jk2LM34no
    &scope=openid email profile
    &response_type=id_token
    &response_mode=fragment
    &state=someState
    &nonce=someNonce
    &redirect_uri=https://example.okta.com


For learn more about Okta’s requirements for these parameters, see Authorize Endpoint Parameter Details.

Add the Social Auth Authorize URL to the page where you want to enable Social Auth.
==================================================================================
Using the example URL from step 7, your HTML looks 

similar to the following:

    <a href="

    https://example.okta.com/oauth2/v1/authorize?idp=0oa0bcde12fghiJkl3m4
          &client_id=AbcDE0fGHI1jk2LM34no
          &scope=openid email profile
          &response_type=id_token
          &response_mode=fragment
          &state=someState
          &nonce=someNonce
          &redirect_uri=https://app.example.com/social_auth">Log in</a>



      
