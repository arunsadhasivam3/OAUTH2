
Okta Oauth2 guide:

http://developer.okta.com/docs/api/resources/oauth2.html

STEP 1:
=======
setup oauth2.0 in okta using security>IdentityProviders > Add Identity provider(choose linkedin,google,facebook,microsoft)
it generate oauth2.0 url using below steps..

Configure your Social Authentication provider:

    Name:
        We suggest using the name you would expect to see on a button, something like “Log in to Facebook”.
        
    IdP Username:
      Set to “idpuser.email”.
      
    Match against:
        Leave set to the default.
        
    Account Link Policy:
        Leave set to “Automatic” for now.
    
    Auto-Link Restrictions:
        Leave set to the default.
        
    Provisioning Policy:
        Leave set to “Automatic” for now.
        
    Profile Master: 
        Leave unchecked.
    
    Group Assignments: 
        Leave set to the default.
        
    Client Id: 
        Set this to appropriate value for the Social Authentication provider that you are configuring.
        
    Client Secret: 
        Set this to appropriate value for the Social Authentication provider that you are configuring.
        
    Scopes: 
        Leave set to the default.


1)AuthorizeUrl
==============
format:
=======
https://dev-878414.oktapreview.com/oauth2/v1/authorize?idp=0oaaknke24jz9xFck0h7&client_id={clientId}&response_type={responseType}&response_mode={responseMode}&scope={scopes}&redirect_uri={redirectUri}&state={state}&nonce={nonce}

2)redirectUrl-
==============

https://dev-878414.oktapreview.com/oauth2/v1/authorize/callback

STEP 2:
=======

generate Authorize Token using below url.

https://dev-878414.oktapreview.com/oauth2/v1/authorize

?idp=0oaaknke24jz9xFck0h7

&client_id=# clientid of okta app#

&response_type=code

&scope=openid

&redirect_uri=http://localhost:8080/OauthProfile

&state=arun@1984


Allowed scope parameter by okta openid provider:
================================================
openid’, ‘profile’, ‘email’, ‘address’, ‘phone’, ‘offline_access’, all defined in OpenID Connect.
