 
 
 var xmlhttp =null;
 var baseUrl = 'https://dev-878414.oktapreview.com';
 function getxmlRequest(){
	   try{
		   xmlhttp = new XMLHttpRequest();
		   
		   if ("withCredentials" in xmlhttp) {
			   xmlhttp.onerror = function() {
				      alert('Invalid URL or Cross-Origin Request Blocked.  You must explicitly add this site (' + window.location.origin + ') to the list of allowed websites in your Okta Admin Dashboard');
				    }
			   xmlhttp.onload = function() {
				        alert(this.responseText);
				    };
				    //xmlhttp.open('GET', baseUrl + '/api/v1/users/me', true);
				    //xmlhttp.withCredentials = true;
				   // xmlhttp.send();
			   
		   }else{
			    alert("CORS is not supported for this browser!")
		   }
	   }catch (e){
	      try{
	    	  xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
	      }catch (e) {
	         
	         try{
	        	 xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	         }catch (e){
	         
	            // Something went wrong
	            alert("Your browser broke!");
	            return false;
	         }
	      }
	   }
	   
	   return xmlhttp;
 }  
 
 
 function renderResponse(url){
	 xmlhttp = getxmlRequest();
	 xmlhttp.onreadystatechange = statechanged;
	 xmlhttp.open('GET', url);
	 xmlhttp.send(null);
	 
 }
 
 /*function postData(xmlhttp,url,param,base64EncodedSecKey){
	 xmlhttp.open('POST', url);
	 xmlhttp.setRequestHeader('Access-Control-Allow-Headers', '*');
	 xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	 xmlhttp.setRequestHeader('Access-Control-Allow-Origin', '*');
	 xmlhttp.setRequestHeader('Authorization','Basic ' + base64EncodedSecKey);
	 
	 xmlhttp.onreadystatechange = poststatechanged;
	
	 xmlhttp.send(param);
	 
	 
 }
 

 function poststatechanged() {
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		// document.getElementById("content").innerHTML = xmlhttp.responseText;
		//alert('xmlhttp.responseText:'+xmlhttp.responseText['access_token'])
		
		
		sendRedirect(xmlhttp);

	}
}
 
 function sendRedirect( xmlhttp){
	 var response =  JSON.parse(xmlhttp.responseText);
		var accessToken = response['access_token'];
		var idToken = response['id_token'];
		var expires_in = response["expires_in"];
		var param = 'access_token='+accessToken+'&id_token='+idToken+'&expires_in='+expires_in;
		
	
		var url = 'http://localhost:8081/NSL/oktaAuthFlowCallback.htm';
		// var url = 'https://dev-878414.oktapreview.com/home/testdev878414_springmvcwithsecurity_1/0oaacwehjt9QDo5wR0h7/alnacwxx1gvLqv6e70h7';
		// var url = 'https://dev-878414-admin.oktapreview.com/admin/app/oidc_client/instance/0oaalpj8dcZSL5B530h7';
		 xmlhttp.open('POST', url); 
		// xmlhttp.onreadystatechange = statechanged;
		 xmlhttp.send(param);
		 
	 
 }
 */
 
 function postData(xmlhttp,url,accessToken){
	 xmlhttp.open('GET', url);
	 xmlhttp.setRequestHeader('Access-Control-Allow-Headers', '*');
	 xmlhttp.setRequestHeader('Access-Control-Allow-Origin', '*');
	 
	 //xmlhttp.setRequestHeader("Content-type", "application/json");
	 xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	 xmlhttp.setRequestHeader('Authorization','Bearer ' + accessToken);
	 //xmlhttp.setRequestHeader('Authorization','SSWS 00FyouxjIfux6XTJWTGQv49MCOMguh_Do2emn05VWH');

	 xmlhttp.onreadystatechange = statechanged;
	
	 xmlhttp.send(null);
	 
	 
 }
 
 
 
 function statechanged() { 
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		//document.getElementById("content").innerHTML = xmlhttp.responseText;
		alert('xmlHttp:responseText:'+xmlhttp.responseText);
	}
}
 
 
 function getValue(val){
	 if(val=='' && val==null ){
		 return 'NA'
		 
	 }
	 
	 return  val;
 }
 
 
   