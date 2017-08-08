<!doctype html>
<html>
<head>
    <!-- Core widget js and css -->
    <script src="https://ok1static.oktacdn.com/assets/js/sdk/okta-signin-widget/1.4.0/js/okta-sign-in.min.js" type="text/javascript"></script>
    <link href="https://ok1static.oktacdn.com/assets/js/sdk/okta-signin-widget/1.4.0/css/okta-sign-in.min.css" type="text/css" rel="stylesheet">
    <!-- Customizable css theme options. Link your own customized copy of this file or override styles in-line -->
    <link href="https://ok1static.oktacdn.com/assets/js/sdk/okta-signin-widget/1.4.0/css/okta-theme.css" type="text/css" rel="stylesheet">

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.1.0/jquery.min.js" type="text/javascript"></script>
    
</head>
<body>
    <div id="okta-login-container"></div>
    <script type="text/javascript">
    var clientId = "3pgiRciiwvdG6bHru52e";
	var oktaSignIn = new OktaSignIn({baseUrl: 'https://dev-878414.oktapreview.com'});
    oktaSignIn.renderEl(
  		  { el: '#okta-login-container' },
  		  function (res) {
  		    if (res.status === 'SUCCESS') { res.session.setCookieAndRedirect(
  		    		'https://dev-878414.oktapreview.com/home/testdev878414_springmvcwithsecurity_1/0oaacwehjt9QDo5wR0h7/alnacwxx1gvLqv6e70h7'); }
  		  }
  		);
      
    </script>
</body>
</html> 
