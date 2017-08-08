For accessid token require scope to be openid
==============================================

when try to get authorization code with different scope it shows

"Requests for ID tokens or access tokens with OpenID scopes require the openid scope"

Reserved scopes
===============
Reserved scopes include ‘openid’, ‘profile’, ‘email’, ‘address’, ‘phone’, ‘offline_access’, all defined in OpenID Connect.


https://dev-878414.oktapreview.com/oauth2/v1/authorize
?idp=0oaaknke24jz9xFck0h7
&client_id=T5xplQPK67w38DgdvXrR
&response_type=code&scope=profile&redirect_uri=http://localhost:8080/OauthProfile&state=arun@1984


/OauthProfile?state=arun%401984&
error=invalid_scope&error_description=Requests+for+ID+tokens+or+access+tokens+with+OpenID+scopes+require+the+openid+scope.
