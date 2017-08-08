Request:
========
when firing request

https://dev-878414.oktapreview.com/oauth2/v1/authorize?idp=0oaal8dy3xDhTXDmX0h7
&client_id={clientId}
&response_type={responseType}
&response_mode={responseMode}
&scope={scopes}
&redirect_uri={redirectUri}
&state={state}
&nonce={nonce}


Redirect 1:
============
https://dev-878414.oktapreview.com/oauth2/v1/authorize/callback?
code=AQTsf-Lewu5x5IlkI_0SfK1K_yxShCYG6B0TuFAOVBJJ6qI_8UHe5cH86AuZlbElJdzNkDtO3vIQT6xNYdOnWt8k5gOteokpUZuVhL5zPDBsQ7MOVMI
&state=CdvQzRD9sr7fLTRsd9JP#!


Redirect 2:
===========
http://localhost:8080/OauthProfile#state=arun%401984&error=access_denied&error_description=Unable+to+process+the+username+transform.

 see state is get carried in all redirect so you can track your request when someone altered also
 the request will be changed . so the requestor can identify some one altered it.
 
 stae is nothing but some random string or number to identify that the request you send not changed in transit.
 
 Important:
 ==========
 To confirm and to identiy the response is for your request with the state field in request because in authCode generation
 as you can see only Code is extra parameter along with state, if state is not added there is no way to identify
 that the response is change or altered or it is for your request with state e.g arun@1984.
