package com.idp.filter;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

public class ProfileDelegationFilterProxy
  extends GenericFilterBean
{
  private String contextAttribute;
  private WebApplicationContext webApplicationContext;
  private String targetBeanName;
  private boolean targetFilterLifecycle = false;
  private volatile Filter delegate;
  private final Object delegateMonitor = new Object();
  
  public ProfileDelegationFilterProxy() {}
  
  public ProfileDelegationFilterProxy(Filter delegate)
  {
    Assert.notNull(delegate, "delegate Filter object must not be null");
    this.delegate = delegate;
  }
  
  public ProfileDelegationFilterProxy(String targetBeanName)
  {
    this(targetBeanName, null);
    
    System.out.println("ProfileDelegationFilterProxy.ProfileDelegationFilterProxy()"+targetBeanName);
  }
  
  
  
  public ProfileDelegationFilterProxy(String targetBeanName, WebApplicationContext wac)
  {
	  
	System.out.println("ProfileDelegationFilterProxy.ProfileDelegationFilterProxy()"+targetBeanName);
	
	
    Assert.hasText(targetBeanName, "target Filter bean name must not be null or empty");
    setTargetBeanName(targetBeanName);
    this.webApplicationContext = wac;
    if (wac != null) {
      setEnvironment(wac.getEnvironment());
    }
  }
  
  public void setContextAttribute(String contextAttribute)
  {
    this.contextAttribute = contextAttribute;
  }
  
  public String getContextAttribute()
  {
    return this.contextAttribute;
  }
  
  public void setTargetBeanName(String targetBeanName)
  {
    this.targetBeanName = targetBeanName;
  }
  
  protected String getTargetBeanName()
  {
    return this.targetBeanName;
  }
  
  public void setTargetFilterLifecycle(boolean targetFilterLifecycle)
  {
    this.targetFilterLifecycle = targetFilterLifecycle;
  }
  
  protected boolean isTargetFilterLifecycle()
  {
    return this.targetFilterLifecycle;
  }
  
  protected void initFilterBean()
    throws ServletException
  {
    synchronized (this.delegateMonitor)
    {
      if (this.delegate == null)
      {
        if (this.targetBeanName == null) {
          this.targetBeanName = getFilterName();
        }
        WebApplicationContext wac = findWebApplicationContext();
        if (wac != null) {
          this.delegate = initDelegate(wac);
        }
      }
    }
  }
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
    throws ServletException, IOException
  {
    Filter delegateToUse = this.delegate;
    if (delegateToUse == null) {
      synchronized (this.delegateMonitor)
      {
        if (this.delegate == null)
        {
          WebApplicationContext wac = findWebApplicationContext();
          if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
          }
          this.delegate = initDelegate(wac);
        }
        delegateToUse = this.delegate;
      }
    }
    invokeDelegate(delegateToUse, request, response, filterChain);
  }
  
  public void destroy()
  {
    Filter delegateToUse = this.delegate;
    if (delegateToUse != null) {
      destroyDelegate(delegateToUse);
    }
  }
  
  protected WebApplicationContext findWebApplicationContext()
  {
    if (this.webApplicationContext != null)
    {
      if (((this.webApplicationContext instanceof ConfigurableApplicationContext)) && 
        (!((ConfigurableApplicationContext)this.webApplicationContext).isActive())) {
        ((ConfigurableApplicationContext)this.webApplicationContext).refresh();
      }
      return this.webApplicationContext;
    }
    String attrName = getContextAttribute();
    if (attrName != null) {
      return WebApplicationContextUtils.getWebApplicationContext(getServletContext(), attrName);
    }
    return WebApplicationContextUtils.getWebApplicationContext(getServletContext());
  }
  
  protected Filter initDelegate(WebApplicationContext wac)
    throws ServletException
  {
    Filter delegate = (Filter)wac.getBean(getTargetBeanName(), Filter.class);
    if (isTargetFilterLifecycle()) {
      delegate.init(getFilterConfig());
    }
    return delegate;
  }
  
  protected void invokeDelegate(Filter delegate, ServletRequest request, ServletResponse response, FilterChain filterChain)
    throws ServletException, IOException
  {
    delegate.doFilter(request, response, filterChain);
  }
  
  protected void destroyDelegate(Filter delegate)
  {
    if (isTargetFilterLifecycle()) {
      delegate.destroy();
    }
  }
  

	/**
	 * To log the request parameters
	 * @param request
	 */
	public void logParam(HttpServletRequest request){
		Map<String,String[]> reqMap = request.getParameterMap();
		
		Set<String> reqSet = reqMap.keySet();
		
		Iterator<String> it = reqSet.iterator();
		while(it.hasNext()){
			String key = it.next();
			
			System.out.println("Key:"+key +  " value: "+ request.getParameter(key));
		}
		
		

	
		
	}
	
	
}
