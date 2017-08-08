http://localhost:8080/OauthProfile/implicitCodeFlow.htm

username: #mailid#

password: Arun@dobyear

ImplicitFlowController.java - have login page and form session token and login in to okta using session token.

OktaAuthorizationCodeFlow - not support in okta but it shows usage of bearer token without using session token procedure
like linkedin . but okta has default page we could not able to pass the accesstoken which is generated from 1st call authcode.

    i) authcode - get authcode (authorization_code)
    send authcode with header "basic" 

    String client  =  "3pgiRciiwvdG6bHru52e";
        String secretKey= "9x_RcUeU5AY8eWb413LUzE5_dr1BHhiSZXTTLMNF";
        String base64EncodedSecretKey = client + ":" + secretKey;
        String base64ClientIdSecret = DatatypeConverter.printBase64Binary(
             base64EncodedSecretKey.getBytes(StandardCharsets.UTF_8));
        String openIdUrl = "https://dev-878414.oktapreview.com/oauth2/v1/token";
        String param1=	"grant_type=authorization_code"
            + "&code=" + token_id 
            + "&redirect_uri=http://localhost:8080/OauthProfile/oktaAuthFlowCallback" ;
        jsonData = new JSONObject();
        jsonData.accumulate("Authorization","Basic " + base64ClientIdSecret);
        jsonData.accumulate("Content-type", "application/x-www-form-urlencoded");




    2)generate accesstoken by hitting above authcode 
    3)redirect to okta page with the accesstoken. since okta does not access accesstoken url to get accesstoken like in linkedin.
    we need to redirect with the accesstoken appened in request.
    issue faced - if we do sendredirect you can't pass the accesstoken.
    if you do requestdispatcher you can't pass request,response outside context in request dispatcher. 
