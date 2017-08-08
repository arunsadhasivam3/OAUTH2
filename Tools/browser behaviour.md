Mozilla:
=========
mozilla can understand any space or junk in request.
for e.g below request will be misinterpreted and shows error code as invalid request uri since it encode when you enter in
browser


          https://dev-878414.oktapreview.com/oauth2/v1/authorize?idp=0oaaln1spqmLsycWy0h7
          &client_id=MHefPMOy7VL83sjmvzXd                                   
          &response_type=code
          &response_mode=fragment
          &scope=openid
          &redirect_uri=http://localhost:8080/OauthProfile
          &state=arun1984
          &nonce=arun1984
  
  NOTE:
  =====
  Mozilla only can understand this type of format i.e /n/r nextline other browsers provide space (%20) and result
  in error.
          
 Chrome:
 =======
 
          https://dev-878414.oktapreview.com/oauth2/v1/authorize?idp=0oaaln1spqmLsycWy0h7%20
          &client_id=MHefPMOy7VL83sjmvzXd%20&response_type=code%20
          &response_mode=fragment%20&scope=openid%20
          &redirect_uri=http://localhost:8080/OauthProfile%20&state=arun1984%20&nonce=arun1984
 
 
 https://meyerweb.com/eric/tools/dencoder/
 
 After try to decode with encode the result is with space(%20)
 
     https://dev-878414.oktapreview.com/oauth2/v1/authorize?idp=0oaaln1spqmLsycWy0h7 &client_id=MHefPMOy7VL83sjmvzXd &response_type=code 
     
     &response_mode=fragment &scope=openid &redirect_uri=http://localhost:8080/OauthProfile &state=arun1984 &nonce=arun1984


see it has space it causes  Error  "Description: Illegal value for redirect_uri parameter."

mozilla 
=======
does not encode it works Perfect !!!! whatever way you give like . 
 

