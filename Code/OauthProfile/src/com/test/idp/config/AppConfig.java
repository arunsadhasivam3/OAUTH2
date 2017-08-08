package com.test.idp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:/com/test/idp/config/securityContext.xml" })
public class AppConfig {
	
	

}
