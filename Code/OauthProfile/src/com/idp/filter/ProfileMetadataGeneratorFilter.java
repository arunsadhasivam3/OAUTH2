package com.idp.filter;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.util.SimpleURLCanonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.ExtendedMetadataDelegate;
import org.springframework.security.saml.metadata.MetadataDisplayFilter;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.security.saml.metadata.MetadataMemoryProvider;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

public class ProfileMetadataGeneratorFilter
  extends GenericFilterBean
{
  protected static final Logger log = LoggerFactory.getLogger(ProfileMetadataGeneratorFilter.class);
  protected MetadataManager manager;
  protected ProfileMetadataGenerator generator;
  protected MetadataDisplayFilter displayFilter;
  protected boolean normalizeBaseUrl;
  private static final String DEFAULT_ALIAS = "defaultAlias";
  
  public ProfileMetadataGeneratorFilter(ProfileMetadataGenerator generator)
  {
    this.generator = generator;
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
  {
    processMetadataInitialization((HttpServletRequest)request);
    chain.doFilter(request, response);
  }
  
  protected void processMetadataInitialization(HttpServletRequest request)
    throws ServletException
  {
    if (this.manager.getHostedSPName() == null) {
      synchronized (MetadataManager.class)
      {
        if (this.manager.getHostedSPName() == null) {
          try
          {
            log.info("No default metadata configured, generating with default values, please pre-configure metadata for production use");
            
            String alias = this.generator.getEntityAlias();
            String baseURL = getDefaultBaseURL(request);
            if (this.generator.getEntityBaseURL() == null)
            {
              log.warn("Generated default entity base URL {} based on values in the first server request. Please set property entityBaseURL on MetadataGenerator bean to fixate the value.", baseURL);
              this.generator.setEntityBaseURL(baseURL);
            }
            else
            {
              baseURL = this.generator.getEntityBaseURL();
            }
            if (this.generator.getEntityId() == null) {
              this.generator.setEntityId(getDefaultEntityID(baseURL, alias));
            }
            EntityDescriptor descriptor = this.generator.generateMetadata();
            ExtendedMetadata extendedMetadata = this.generator.generateExtendedMetadata();
            
            log.info("Created default metadata for system with entityID: " + descriptor.getEntityID());
            MetadataMemoryProvider memoryProvider = new MetadataMemoryProvider(descriptor);
            memoryProvider.initialize();
            MetadataProvider metadataProvider = new ExtendedMetadataDelegate(memoryProvider, extendedMetadata);
            
            this.manager.addMetadataProvider(metadataProvider);
            this.manager.setHostedSPName(descriptor.getEntityID());
            this.manager.refreshMetadata();
          }
          catch (MetadataProviderException e)
          {
            log.error("Error generating system metadata", e);
            throw new ServletException("Error generating system metadata", e);
          }
        }
      }
    }
  }
  
  protected String getDefaultEntityID(String entityBaseUrl, String alias)
  {
    String displayFilterUrl = "/saml/metadata";
    if (this.displayFilter != null) {
      displayFilterUrl = this.displayFilter.getFilterProcessesUrl();
    }
    StringBuilder sb = new StringBuilder();
    sb.append(entityBaseUrl);
    sb.append(displayFilterUrl);
    if (StringUtils.hasLength(alias))
    {
      sb.append("/alias/");
      sb.append(alias);
    }
    return sb.toString();
  }
  
  protected String getDefaultBaseURL(HttpServletRequest request)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
    sb.append(request.getContextPath());
    String url = sb.toString();
    if (isNormalizeBaseUrl()) {
      return SimpleURLCanonicalizer.canonicalize(url);
    }
    return url;
  }
  
  @Autowired(required=false)
  public void setDisplayFilter(MetadataDisplayFilter displayFilter)
  {
    this.displayFilter = displayFilter;
  }
  
  @Autowired
  public void setManager(MetadataManager manager)
  {
    this.manager = manager;
  }
  
  public boolean isNormalizeBaseUrl()
  {
    return this.normalizeBaseUrl;
  }
  
  public void setNormalizeBaseUrl(boolean normalizeBaseUrl)
  {
    this.normalizeBaseUrl = normalizeBaseUrl;
  }
  
  public void afterPropertiesSet()
    throws ServletException
  {
    super.afterPropertiesSet();
    Assert.notNull(this.generator, "Metadata generator");
    Assert.notNull(this.manager, "MetadataManager must be set");
  }
}

