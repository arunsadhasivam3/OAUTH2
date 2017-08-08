OPENID vs OAUTH
================

OPENID  - Authentication.

OAUTH   - Authorization.

The term OpenID may also refer to an identifier as specified in the OpenID standard;
these identifiers take the form of a unique Uniform Resource Identifier (URI), 
and are managed by some 'OpenID provider' that handles authentication.[1]

OpenID Connect is an interoperable authentication protocol based on the OAuth 2.0 family of specifications. It uses straightforward REST/JSON message flows with a design goal of “making simple things simple and complicated things possible”. It’s uniquely easy for developers to integrate, compared to any preceding Identity protocol.

(Identity, Authentication) + OAuth 2.0 = OpenID Connect



In Okta OPENID and OAUTH2 works this way:
=========================================
Security> Identity Providers > Add IDentity Provider > choose(linkedin,microsoft,facebook )  - Oauth2

1)STEPS FOR OAUTH2 ID Generation:
=================================

      GENERAL SETTINGS
      =================
      Name -Linkedin Oauth Authorization 
      Protocol- OAuth2
      Type -Linkedin


      0oaalvrh7iLJX0FUQ0hdd - oauth generated id . see below uses protocol oauth2 to integrate with social app.
      Authorize URL
      ============
      https://dev-878414.oktapreview.com/oauth2/v1/authorize
      ?idp=0oaalvrh7iLJX0FUQ0hdd&client_id={clientId}
      &response_type={responseType}&response_mode={responseMode}
      &scope={scopes}&redirect_uri={redirectUri}&state={state}&nonce={nonce}


      Redirect URI
      ============
      https://dev-878414.oktapreview.com/oauth2/v1/authorize/callback

      Give client id : #linked in app client id #

       SIGN ON METHODS:
                        o OpenID Connect
                        
ACTUAL MAPPINGS between OKTA and linkedin page entered username happens here:
==============================================================================

Security> Identity Providers > Add IDentity Provider >

      AUTHENTICATION SETTINGS

            IdP Username :idpuser.email
            Match against :Okta Username



NOTE:
=====
since cant change redirect uri adding in linkedin settings the redirect uri as okta redirect uri since otherwise
error will be shown like "redirect uri should match exactly as in provider"

Linkedin.com settings:
======================

            Authentication Keys
            Client ID:	75fd832l448dddd

            Client Secret:	Qmxv9eOB3fTpXZMU

            Default Application Permissions
            ================================
            r_basicprofile	r_emailaddress	rw_company_admin
            w_share

            OAuth 2.0
            Authorized Redirect URLs:

            Enter fully qualified URLs to define valid OAuth 2.0 callback paths. Maximum 200.
                  Add
            http://localhost:8080/OauthProfile
            http://localhost:8080/AuthorApp
            https://dev-878414.oktapreview.com/oauth2/v1/authorize/callback
        
           


2)OPENID
=========

To call from outside and to communicate with okta you need to use openid. it only create "client_id" which
okta framework understand from outside request.

STEPS for OPENID  App creation:
===============================

Applicaitons>Add Applications > create New App> Create a New Application Integration

Platform :Web


      Sign on method :
                  X Secure Web Authentication (SWA) 
                        Users credentials to sign in. This integration works with most apps.
                  X SAML 2.0
                        Uses the SAML protocol to log users into the app. This is a better option than SWA,
                        if the app supports it.
                  O OpenID Connect(checked)
                    Uses the OpenID Connect protocol to log users into an app you've built.

      Client Credentials:
                  Client ID :3pgiRciiwvdG6bHru52ddd
                              Public identifier for the client that is required for all OAuth flows.
                  Client secret:aZ0vGS7vyMQpUaD5bDP7LlcYy-eHaFBmrlfcATcK
                              Secret used by the client to exchange an authorization
 
To invoke Okta App from outside okta we use above client id:
=============================================================
since okta authorization server below i.e dev-878414.oktapreview.com/oauth2/v1/authorize cannot understand
linkedin clientid.hence we need OPENID app .

 oauth2:
 ======
 
      https://dev-878414.oktapreview.com/oauth2/v1/authorize?
      idp=0oaalvrh7iLJX0FUQ0hdd
      &client_id=3pgiRciiwvdG6bHru52ddd  #openid app id
      &response_type=code
      &response_mode=fragment
      &scope=offline_access
      &redirect_uri=http://localhost:8080/OauthProfile
      &state=arun@1984 
      &nonce=arun@1984





Note:
=====
1)client id above is OPENID 

oauth2:
========
https://dev-878414.oktapreview.com/oauth2/v1/authorize?idp=0oaalvrh7iLJX0FUQ0hdd&client_id={clientId}&response_type={responseType}&response_mode={responseMode}&scope={scopes}&redirect_uri={redirectUri}&state={state}&nonce={nonce}
https://dev-878414.oktapreview.com/oauth2/v1/authorize/callback


&username=arunsadhasivam@yahoo.co.in
&password=arun@1984


FLOW:
=====

below url > oauth2(linkedin) get authorized by linked then go to okta> since we give client id as 3pgiRciiwvdG6bHru52ddd(OPENID of okta) goes to okta app > OPENID


connects to linkedin OAUTH2  since we have given client id as linkedin client in Add Identity provider settings.


in OPENID App General settings we give allowed grant types authorizationcode .
so it uses to generate accesstoken and connect to okta using openid app.

      General Settings
      ================
      Allowed grant types :
             Authorization Code
'

      https://dev-878414.oktapreview.com/oauth2/v1/authorize?
      idp=0oaalvrh7iLJX0FUQ0hdd
      &client_id=3pgiRciiwvdG6bHru52ddd
      &response_type=code
      &response_mode=fragment
      &scope=offline_access
      &redirect_uri=http://localhost:8080/OauthProfile
      &state=arun@1984 
      &nonce=arun@1984
      
      
      

IMPORTANT:
===========
from external we use OAUTH2 to do login via linkedin and then redirect to OPENID .



TYPES of OKTA APP:
==================

1)An Application.

2)on OPENID Application.

identity provider app but wont get listed.

NEED FOR OPENID APP
====================
since OPENID OpenID Connect  is a simple identity layer on top of the OAuth 2.0 protocol. so it understand OAUTH2 but cannot
understand SAML other protocol. 


NOTE important:
================
OAUTH2 provider- which we add by secuirty > Add Identity Provider > provides the URI to generate the Authcode, AccessToken.
This wont come in okta Applications hence we use OAUTH2 to authorize login user and redirect via OPENID to actual app.


OPENID and OAUTH2 can handshake to make redirect




OPENID
=======

https://dev-878414-admin.oktapreview.com/admin/app/oidc_client/instance/0oaale0tqjDgEmSxi0abcd


      General Settings >      Edit
      
      AApplication label: OIDC APP
      Application type :Native
      Allowed grant types :

                          Authorization Code
                          Refresh Token
                          Resource Owner Password
                          Implicit (Hybrid)
                            Allow ID Token with implicit grant type
                            Allow Access Token with implicit grant type
      Redirect URIs        :
                          http://localhost:8080/OauthProfile


OAUTH
=====
 
https://dev-878414-admin.oktapreview.com/admin/app/oidc_client/instance/0oaal77f7mYb3paaj0hdfdfd


      Application label :LINKEDIN OKTA INTEGRATION
      Application type: Web
      Allowed grant types :
      Authorization Code:

                        Refresh Token
                        Implicit (Hybrid)
                        Allow ID Token with implicit grant type
                        Allow Access Token with implicit grant type
      Redirect URIs :
                        http://localhost:8080/OauthProfile	
      Login initiated by: App Only
