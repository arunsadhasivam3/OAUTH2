package com.idp.filter;

 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.common.SAMLException;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.ws.message.decoder.MessageDecodingException;
import org.opensaml.xml.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLAuthenticationToken;
import org.springframework.security.saml.context.SAMLContextProvider;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.processor.SAMLProcessor;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;

public class ProfileSAMLProcessingFilter
  extends AbstractAuthenticationProcessingFilter
{
  protected static final Logger logger = LoggerFactory.getLogger(ProfileSAMLProcessingFilter.class);
  protected SAMLProcessor processor;
  protected SAMLContextProvider contextProvider;
  private String filterProcessesUrl;
  public static final String FILTER_URL = "/saml/SSO";
  
  public ProfileSAMLProcessingFilter()
  {
    this("/saml/SSO");
  }
  
  protected ProfileSAMLProcessingFilter(String defaultFilterProcessesUrl)
  {
    super(defaultFilterProcessesUrl);
    setFilterProcessesUrl(defaultFilterProcessesUrl);
  }
  
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
    throws AuthenticationException
  {
    try
    {
      logger.debug("Attempting SAML2 authentication using profile {}", getProfileName());
      SAMLMessageContext context = this.contextProvider.getLocalEntity(request, response);
      this.processor.retrieveMessage(context);
      
      context.setCommunicationProfileId(getProfileName());
      context.setLocalEntityEndpoint(SAMLUtil.getEndpoint(context.getLocalEntityRoleMetadata().getEndpoints(), context.getInboundSAMLBinding(), context.getInboundMessageTransport()));
      
      SAMLAuthenticationToken token = new SAMLAuthenticationToken(context);
      System.out.println("ProfileSAMLProcessingFilter.attemptAuthentication():NAME"+token.getName());

      return getAuthenticationManager().authenticate(token);
    }
    catch (SAMLException e)
    {
      logger.debug("Incoming SAML message is invalid", e);
      throw new AuthenticationServiceException("Incoming SAML message is invalid", e);
    }
    catch (MetadataProviderException e)
    {
      logger.debug("Error determining metadata contracts", e);
      throw new AuthenticationServiceException("Error determining metadata contracts", e);
    }
    catch (MessageDecodingException e)
    {
      logger.debug("Error decoding incoming SAML message", e);
      throw new AuthenticationServiceException("Error decoding incoming SAML message", e);
    }
    catch (SecurityException e)
    {
      logger.debug("Incoming SAML message is invalid", e);
      throw new AuthenticationServiceException("Incoming SAML message is invalid", e);
    }
  }
  
  protected String getProfileName()
  {
    return "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";
  }
  
  protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response)
  {
    return SAMLUtil.processFilter(getFilterProcessesUrl(), request);
  }
  
  @Deprecated
  public void setDefaultTargetUrl(String url)
  {
    SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
    handler.setDefaultTargetUrl(url);
    setAuthenticationSuccessHandler(handler);
  }
  
  @Autowired
  public void setSAMLProcessor(SAMLProcessor processor)
  {
    Assert.notNull(processor, "SAML Processor can't be null");
    this.processor = processor;
  }
  
  @Autowired
  public void setContextProvider(SAMLContextProvider contextProvider)
  {
    Assert.notNull(contextProvider, "Context provider can't be null");
    this.contextProvider = contextProvider;
  }
  
  public void afterPropertiesSet()
  {
    super.afterPropertiesSet();
    Assert.notNull(this.processor, "SAMLProcessor must be set");
    Assert.notNull(this.contextProvider, "Context provider must be set");
  }
  
  public void setFilterProcessesUrl(String filterProcessesUrl)
  {
    this.filterProcessesUrl = filterProcessesUrl;
    super.setFilterProcessesUrl(filterProcessesUrl);
  }
  
  public String getFilterProcessesUrl()
  {
    return this.filterProcessesUrl;
  }
}
