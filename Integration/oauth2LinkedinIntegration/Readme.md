linkedin - Oauth2 integration:
=============================

social networks like facebook,google can allow authentication via token .


linkedin
========

                                                               Step 1:
                                                               =======
                                                                
register in developer.linkedin.com get client_id and scope.

get below ids:
=============

1)client_id

2)client_secret

3)scope

                                                                 STEP 2:
                                                                 =======

Authorization Code Request - get Authorization code. this Authorization code is used to generate AuthToken

e.g:
===
https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=123456789
&redirect_uri=http://localhost:8080/OauthProfile&state=987654321&scope=r_basicprofile

test url:
========

    https://www.linkedin.com/oauth/v2/authorization?client_id= #linkedin client id#
    &redirect_uri=http://localhost:8080/OauthProfile
    &response_type=code
    &scope=r_basicprofile
    &state=arun@1984

Baseurl: 
========
https://www.linkedin.com/oauth/v2/authorization  - for Authorization.



NOTE:
=====
response_type=code 
===================
  denotes requesting code this parameter takes you to "Not you | Allow or Cancel" prompt page once you allow it provides authorization code for
  security this allow or cancel is prompted . this token will last (Time to live) is 

state:
------

 Any string of your choice to prevent CSRF (cross site request forgery) if code is the last parameter then in response also it is
 last parameter , so chance of csrf with one parameter. state is like salt in cryptography a random guess of value by user to make sure it\
 is not being hacked in network.
 
USE IE or chrome check network > and check the response code and use it to get Authentication token.

Sample returned code.
---------------------

Key	Value
---------
Request	GET /OauthProfile?code=AQQHP3DVgZI2RsPxfUPr82ON6vwf_2OjLnWIMG6BQNwm_EyaHKaeQF2_IMguapSx9pA8-jsUyoETDw2QSsGwtlh0WMooV9IhDE5ZP5W4huipI8hXnT8 HTTP/1.1


                                                             STEP 3:
                                                             ========

This step to Exchange Authorization Code for an Access Token.

To get AccessToken from authorizationCode .

add above code in the request again.

/Post below linked with "x-www-form-urlencoded"

    https://www.linkedin.com/oauth/v2/accessToken
    ?client_id=#linkedin client id#
    &response_type=code
    &client_secret=Qmxv9eOB3fTpXZMU
    &grant_type=authorization_code
    &code=AQSnxFT71I6yvxFO6H54x8R9_h4NufFqve_2MyhPOxCSpLA4QjQ3nyQ_OjISLbH9UmZLJVia0XZ6X9ByVyXSbeR0oZ2DNNDKnVmX1NE7cevSNLleDXs
    &redirect_uri=http://localhost:8080/OauthProfile

Note:
====
grant_type=authorization_code should be same. it says with generated authorization_code request access_token code.

Important:
==========

Note url for Authorization and AccessToken is different.

Authorization - https://www.linkedin.com/oauth/v2/authorization

AccessToken   - https://www.linkedin.com/oauth/v2/accessToken

 
 
STEP4:
=====

final use the accestoken get from above to login without user and password.

    http://localhost:8080/OauthProfile/
    ?access_token=AQUDICiL4-RNmkIo2vjhf3WBctHjgxfRg5EwUFsP4z4KMHfF-BTMzLvx-s9u_hZT0icMjcF-_      pjAp287Gb7fGtzAXHE8d25IkGwleUaW3DMPkquI1jwt-CgnBy5-dIhhNStCkQcmpPE4EjpOkLyfyMtFyqqV4bdOkHK7ks-NwjezM7-BbOs
