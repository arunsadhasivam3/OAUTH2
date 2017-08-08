package org.springframework.security.saml.metadata;


import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.opensaml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.provider.ChainingMetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataFilter;
import org.opensaml.saml2.metadata.provider.MetadataFilterChain;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.opensaml.saml2.metadata.provider.ObservableMetadataProvider;
import org.opensaml.saml2.metadata.provider.SignatureValidationFilter;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.security.x509.BasicPKIXValidationInformation;
import org.opensaml.xml.security.x509.BasicX509CredentialNameEvaluator;
import org.opensaml.xml.security.x509.CertPathPKIXValidationOptions;
import org.opensaml.xml.security.x509.PKIXValidationInformation;
import org.opensaml.xml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.xml.security.x509.StaticPKIXValidationInformationResolver;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.signature.impl.PKIXSignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.trust.AllowAllSignatureTrustEngine;
import org.springframework.security.saml.trust.CertPathPKIXTrustEvaluator;
import org.springframework.security.saml.trust.httpclient.TLSProtocolConfigurer;
import org.springframework.security.saml.util.SAMLUtil;
import org.springframework.util.Assert;

public class MetadataManager
  extends ChainingMetadataProvider
  implements ExtendedMetadataProvider, InitializingBean, DisposableBean
{
  protected final Logger log = LoggerFactory.getLogger(MetadataManager.class);
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final ReentrantReadWriteLock refreshLock = new ReentrantReadWriteLock();
  private String hostedSPName;
  private String defaultIDP;
  private ExtendedMetadata defaultExtendedMetadata;
  private Timer timer;
  private long refreshCheckInterval = 10000L;
  private boolean refreshRequired = true;
  protected KeyManager keyManager;
  private List<ExtendedMetadataDelegate> availableProviders;
  private Set<String> idpName;
  private Set<String> spName;
  private Set<String> aliasSet;
  
  public MetadataManager(List<MetadataProvider> providers)
    throws MetadataProviderException
  {
    this.idpName = new HashSet();
    this.spName = new HashSet();
    this.defaultExtendedMetadata = new ExtendedMetadata();
    this.availableProviders = new LinkedList();
    
    setProviders(providers);
    //getObservers().add(new MetadataProviderObserver());
  }
  
  public final void afterPropertiesSet()
    throws MetadataProviderException
  {
    Assert.notNull(this.keyManager, "KeyManager must be set");
    if (this.refreshCheckInterval > 0L)
    {
      this.log.debug("Creating metadata reload timer with interval {}", Long.valueOf(this.refreshCheckInterval));
      this.timer = new Timer("Metadata-reload", true);
      this.timer.schedule(new RefreshTask(), this.refreshCheckInterval, this.refreshCheckInterval);
    }
    else
    {
      this.log.debug("Metadata reload timer is not created, refreshCheckInternal is {}", Long.valueOf(this.refreshCheckInterval));
    }
    refreshMetadata();
  }
  
  public void destroy()
  {
    try
    {
      this.refreshLock.writeLock().lock();
      this.lock.writeLock().lock();
      for (MetadataProvider provider : getProviders()) {
        if ((provider instanceof ExtendedMetadataDelegate)) {
          ((ExtendedMetadataDelegate)provider).destroy();
        }
      }
      super.destroy();
      if (this.timer != null)
      {
        this.timer.cancel();
        this.timer.purge();
        this.timer = null;
      }
      try
      {
        Thread.sleep(1000L);
      }
      catch (InterruptedException ie) {}
      setRefreshRequired(false);
    }
    finally
    {
      this.lock.writeLock().unlock();
      this.refreshLock.writeLock().unlock();
    }
  }
  
  public void setProviders(List<MetadataProvider> newProviders)
    throws MetadataProviderException
  {
    try
    {
      this.lock.writeLock().lock();
      
      this.availableProviders.clear();
      if (newProviders != null) {
        for (MetadataProvider provider : newProviders) {
          addMetadataProvider(provider);
        }
      }
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
    setRefreshRequired(true);
  }
  
  public void refreshMetadata()
  {
    this.log.debug("Reloading metadata");
    try
    {
      this.lock.writeLock().lock();
      
     // super.setProviders(Collections.emptyList());
      
      this.idpName = new HashSet();
      this.spName = new HashSet();
      this.aliasSet = new HashSet();
      for (ExtendedMetadataDelegate provider : this.availableProviders) {
        try
        {
          this.log.debug("Refreshing metadata provider {}", provider.toString());
          initializeProviderFilters(provider);
          initializeProvider(provider);
          initializeProviderData(provider);
          
          super.addMetadataProvider(provider);
          this.log.debug("Metadata provider was initialized {}", provider.toString());
        }
        catch (MetadataProviderException e)
        {
          this.log.error("Initialization of metadata provider " + provider + " failed, provider will be ignored", e);
        }
      }
      this.log.debug("Reloading metadata was finished");
    }
    /*catch (MetadataProviderException e)
    {
      throw new RuntimeException("Error clearing existing providers");
    }*/
    finally
    {
      this.lock.writeLock().unlock();
    }
  }
  
  private boolean isRefreshNowAndClear()
  {
    try
    {
      this.refreshLock.writeLock().lock();
      if (!isRefreshRequired())
      {
        this.log.debug("Refresh is not required, isRefreshRequired flag isn't set");
        return false;
      }
      setRefreshRequired(false);
    }
    finally
    {
      this.refreshLock.writeLock().unlock();
    }
    return true;
  }
  
  public void addMetadataProvider(MetadataProvider newProvider)
    throws MetadataProviderException
  {
    if (newProvider == null) {
      throw new IllegalArgumentException("Null provider can't be added");
    }
    try
    {
      this.lock.writeLock().lock();
      
      ExtendedMetadataDelegate wrappedProvider = getWrappedProvider(newProvider);
      this.availableProviders.add(wrappedProvider);
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
    setRefreshRequired(true);
  }
  
  public void removeMetadataProvider(MetadataProvider provider)
  {
    if (provider == null) {
      throw new IllegalArgumentException("Null provider can't be removed");
    }
    try
    {
      this.lock.writeLock().lock();
      
      ExtendedMetadataDelegate wrappedProvider = getWrappedProvider(provider);
      this.availableProviders.remove(wrappedProvider);
    }
    finally
    {
      this.lock.writeLock().unlock();
    }
    setRefreshRequired(true);
  }
  
  public List<MetadataProvider> getProviders()
  {
    try
    {
      this.lock.readLock().lock();
      return new ArrayList(super.getProviders());
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public List<ExtendedMetadataDelegate> getAvailableProviders()
  {
    try
    {
      this.lock.readLock().lock();
      return new ArrayList(this.availableProviders);
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  private ExtendedMetadataDelegate getWrappedProvider(MetadataProvider provider)
  {
    if (!(provider instanceof ExtendedMetadataDelegate))
    {
      this.log.debug("Wrapping metadata provider {} with extendedMetadataDelegate", provider);
      return new ExtendedMetadataDelegate(provider);
    }
    return (ExtendedMetadataDelegate)provider;
  }
  
  protected void initializeProvider(ExtendedMetadataDelegate provider)
    throws MetadataProviderException
  {
    this.log.debug("Initializing extendedMetadataDelegate {}", provider);
    provider.initialize();
  }
  
  protected void initializeProviderData(ExtendedMetadataDelegate provider)
    throws MetadataProviderException
  {
    this.log.debug("Initializing provider data {}", provider);
    
    List<String> stringSet = parseProvider(provider);
    for (String key : stringSet)
    {
      RoleDescriptor idpRoleDescriptor = provider.getRole(key, IDPSSODescriptor.DEFAULT_ELEMENT_NAME, "urn:oasis:names:tc:SAML:2.0:protocol");
      if (idpRoleDescriptor != null) {
        if (this.idpName.contains(key)) {
          this.log.warn("Provider {} contains entity {} with IDP which was already contained in another metadata provider and will be ignored", provider, key);
        } else {
          this.idpName.add(key);
        }
      }
      RoleDescriptor spRoleDescriptor = provider.getRole(key, SPSSODescriptor.DEFAULT_ELEMENT_NAME, "urn:oasis:names:tc:SAML:2.0:protocol");
      if (spRoleDescriptor != null) {
        if (this.spName.contains(key)) {
          this.log.warn("Provider {} contains entity {} which was already included in another metadata provider and will be ignored", provider, key);
        } else {
          this.spName.add(key);
        }
      }
      ExtendedMetadata extendedMetadata = getExtendedMetadata(key, provider);
      if (extendedMetadata != null)
      {
        if (extendedMetadata.isLocal())
        {
          String alias = extendedMetadata.getAlias();
          if (alias != null)
          {
            SAMLUtil.verifyAlias(alias, key);
            if (this.aliasSet.contains(alias))
            {
              this.log.warn("Provider {} contains alias {} which is not unique and will be ignored", provider, alias);
            }
            else
            {
              this.aliasSet.add(alias);
              this.log.debug("Local entity {} available under alias {}", key, alias);
            }
          }
          else
          {
            this.log.debug("Local entity {} doesn't have an alias", key);
          }
          if ((spRoleDescriptor != null) && (getHostedSPName() == null)) {
            setHostedSPName(key);
          }
        }
        else
        {
          this.log.debug("Remote entity {} available", key);
        }
      }
      else {
        this.log.debug("No extended metadata available for entity {}", key);
      }
    }
  }
  
  protected void initializeProviderFilters(ExtendedMetadataDelegate provider)
    throws MetadataProviderException
  {
    if (provider.isTrustFiltersInitialized())
    {
      this.log.debug("Metadata provider was already initialized, signature filter initialization will be skipped");
    }
    else
    {
      boolean requireSignature = provider.isMetadataRequireSignature();
      SignatureTrustEngine trustEngine = getTrustEngine(provider);
      SignatureValidationFilter filter = new SignatureValidationFilter(trustEngine);
      filter.setRequireSignature(requireSignature);
      
      this.log.debug("Created new trust manager for metadata provider {}", provider);
      
      MetadataFilter currentFilter = provider.getMetadataFilter();
      if (currentFilter != null)
      {
        if ((currentFilter instanceof MetadataFilterChain))
        {
          this.log.debug("Adding signature filter into existing chain");
          MetadataFilterChain chain = (MetadataFilterChain)currentFilter;
          chain.getFilters().add(filter);
        }
        else
        {
          this.log.debug("Combining signature filter with the existing in a new chain");
          MetadataFilterChain chain = new MetadataFilterChain();
          chain.getFilters().add(currentFilter);
          chain.getFilters().add(filter);
        }
      }
      else
      {
        this.log.debug("Adding signature filter");
        provider.setMetadataFilter(filter);
      }
      provider.setTrustFiltersInitialized(true);
    }
  }
  
  protected SignatureTrustEngine getTrustEngine(MetadataProvider provider)
  {
    Set<String> trustedKeys = null;
    boolean verifyTrust = true;
    boolean forceRevocationCheck = false;
    if ((provider instanceof ExtendedMetadataDelegate))
    {
      ExtendedMetadataDelegate metadata = (ExtendedMetadataDelegate)provider;
      trustedKeys = metadata.getMetadataTrustedKeys();
      verifyTrust = metadata.isMetadataTrustCheck();
      forceRevocationCheck = metadata.isForceMetadataRevocationCheck();
    }
    if (verifyTrust)
    {
      this.log.debug("Setting trust verification for metadata provider {}", provider);
      
      CertPathPKIXValidationOptions pkixOptions = new CertPathPKIXValidationOptions();
      if (forceRevocationCheck)
      {
        this.log.debug("Revocation checking forced to true");
        pkixOptions.setForceRevocationEnabled(true);
      }
      else
      {
        this.log.debug("Revocation checking not forced");
        pkixOptions.setForceRevocationEnabled(false);
      }
      return new PKIXSignatureTrustEngine(getPKIXResolver(provider, trustedKeys, null), Configuration.getGlobalSecurityConfiguration().getDefaultKeyInfoCredentialResolver(), new CertPathPKIXTrustEvaluator(pkixOptions), new BasicX509CredentialNameEvaluator());
    }
    this.log.debug("Trust verification skipped for metadata provider {}", provider);
    return new AllowAllSignatureTrustEngine(Configuration.getGlobalSecurityConfiguration().getDefaultKeyInfoCredentialResolver());
  }
  
  protected PKIXValidationInformationResolver getPKIXResolver(MetadataProvider provider, Set<String> trustedKeys, Set<String> trustedNames)
  {
    if (trustedKeys == null) {
      trustedKeys = this.keyManager.getAvailableCredentials();
    }
    List<X509Certificate> certificates = new LinkedList();
    for (String key : trustedKeys)
    {
      this.log.debug("Adding PKIX trust anchor {} for metadata verification of provider {}", key, provider);
      X509Certificate certificate = this.keyManager.getCertificate(key);
      if (certificate != null) {
        certificates.add(certificate);
      } else {
        this.log.warn("Cannot construct PKIX trust anchor for key with alias {} for provider {}, key isn't included in the keystore", key, provider);
      }
    }
    List<PKIXValidationInformation> info = new LinkedList();
    info.add(new BasicPKIXValidationInformation(certificates, null, Integer.valueOf(4)));
    return new StaticPKIXValidationInformationResolver(info, trustedNames);
  }
  
  protected List<String> parseProvider(MetadataProvider provider)
    throws MetadataProviderException
  {
    List<String> result = new LinkedList();
    
    XMLObject object = provider.getMetadata();
    if ((object instanceof EntityDescriptor)) {
      addDescriptor(result, (EntityDescriptor)object);
    } else if ((object instanceof EntitiesDescriptor)) {
      addDescriptors(result, (EntitiesDescriptor)object);
    }
    return result;
  }
  
  private void addDescriptors(List<String> result, EntitiesDescriptor descriptors)
    throws MetadataProviderException
  {
    this.log.debug("Found metadata EntitiesDescriptor with ID", descriptors.getID());
    if (descriptors.getEntitiesDescriptors() != null) {
      for (EntitiesDescriptor descriptor : descriptors.getEntitiesDescriptors()) {
        addDescriptors(result, descriptor);
      }
    }
    if (descriptors.getEntityDescriptors() != null) {
      for (EntityDescriptor descriptor : descriptors.getEntityDescriptors()) {
        addDescriptor(result, descriptor);
      }
    }
  }
  
  private void addDescriptor(List<String> result, EntityDescriptor descriptor)
    throws MetadataProviderException
  {
    String entityID = descriptor.getEntityID();
    this.log.debug("Found metadata EntityDescriptor with ID", entityID);
    result.add(entityID);
  }
  
  public Set<String> getIDPEntityNames()
  {
    try
    {
      this.lock.readLock().lock();
      
      return Collections.unmodifiableSet(this.idpName);
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public Set<String> getSPEntityNames()
  {
    try
    {
      this.lock.readLock().lock();
      
      return Collections.unmodifiableSet(this.spName);
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public boolean isIDPValid(String idpID)
  {
    try
    {
      this.lock.readLock().lock();
      return this.idpName.contains(idpID);
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public boolean isSPValid(String spID)
  {
    try
    {
      this.lock.readLock().lock();
      return this.spName.contains(spID);
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public String getHostedSPName()
  {
    return this.hostedSPName;
  }
  
  public void setHostedSPName(String hostedSPName)
  {
    this.hostedSPName = hostedSPName;
  }
  
  public String getDefaultIDP()
    throws MetadataProviderException
  {
    try
    {
      this.lock.readLock().lock();
      if (this.defaultIDP != null) {
        return this.defaultIDP;
      }
      Object iterator = getIDPEntityNames().iterator();
      if (((Iterator)iterator).hasNext()) {
        return (String)((Iterator)iterator).next();
      }
      throw new MetadataProviderException("No IDP was configured, please update included metadata with at least one IDP");
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public void setDefaultIDP(String defaultIDP)
  {
    this.defaultIDP = defaultIDP;
  }
  
  public ExtendedMetadata getExtendedMetadata(String entityID)
    throws MetadataProviderException
  {
    try
    {
      this.lock.readLock().lock();
      for (MetadataProvider provider : getProviders())
      {
        ExtendedMetadata extendedMetadata = getExtendedMetadata(entityID, provider);
        if (extendedMetadata != null) {
          return extendedMetadata;
        }
      }
      return getDefaultExtendedMetadata().clone();
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  private ExtendedMetadata getExtendedMetadata(String entityID, MetadataProvider provider)
    throws MetadataProviderException
  {
    if ((provider instanceof ExtendedMetadataProvider))
    {
      ExtendedMetadataProvider extendedProvider = (ExtendedMetadataProvider)provider;
      ExtendedMetadata extendedMetadata = extendedProvider.getExtendedMetadata(entityID);
      if (extendedMetadata != null) {
        return extendedMetadata.clone();
      }
    }
    return null;
  }
  
  public EntityDescriptor getEntityDescriptor(byte[] hash)
    throws MetadataProviderException
  {
    try
    {
      this.lock.readLock().lock();
      for (String idp : this.idpName) {
        if (SAMLUtil.compare(hash, idp)) {
          return getEntityDescriptor(idp);
        }
      }
      EntityDescriptor localEntityDescriptor;
      for (String sp : this.spName) {
        if (SAMLUtil.compare(hash, sp)) {
          return getEntityDescriptor(sp);
        }
      }
      return null;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public String getEntityIdForAlias(String entityAlias)
    throws MetadataProviderException
  {
    try
    {
      this.lock.readLock().lock();
      if (entityAlias == null) {
        return null;
      }
      String entityId = null;
      for (String idp : this.idpName)
      {
        ExtendedMetadata extendedMetadata = getExtendedMetadata(idp);
        if ((extendedMetadata.isLocal()) && (entityAlias.equals(extendedMetadata.getAlias())))
        {
          if ((entityId != null) && (!entityId.equals(idp))) {
            throw new MetadataProviderException("Alias " + entityAlias + " is used both for entity " + entityId + " and " + idp);
          }
          entityId = idp;
        }
      }
      for (String sp : this.spName)
      {
        ExtendedMetadata extendedMetadata = getExtendedMetadata(sp);
        if ((extendedMetadata.isLocal()) && (entityAlias.equals(extendedMetadata.getAlias())))
        {
          if ((entityId != null) && (!entityId.equals(sp))) {
            throw new MetadataProviderException("Alias " + entityAlias + " is used both for entity " + entityId + " and " + sp);
          }
          entityId = sp;
        }
      }
      return entityId;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public ExtendedMetadata getDefaultExtendedMetadata()
  {
    try
    {
      this.lock.readLock().lock();
      return this.defaultExtendedMetadata;
    }
    finally
    {
      this.lock.readLock().unlock();
    }
  }
  
  public void setDefaultExtendedMetadata(ExtendedMetadata defaultExtendedMetadata)
  {
    Assert.notNull(defaultExtendedMetadata, "ExtendedMetadata parameter mustn't be null");
    this.lock.writeLock().lock();
    this.defaultExtendedMetadata = defaultExtendedMetadata;
    this.lock.writeLock().unlock();
  }
  
  public boolean isRefreshRequired()
  {
    try
    {
      this.refreshLock.readLock().lock();
      return this.refreshRequired;
    }
    finally
    {
      this.refreshLock.readLock().unlock();
    }
  }
  
  public void setRefreshRequired(boolean refreshRequired)
  {
    try
    {
      this.refreshLock.writeLock().lock();
      this.refreshRequired = refreshRequired;
    }
    finally
    {
      this.refreshLock.writeLock().unlock();
    }
  }
  
  public void setRefreshCheckInterval(long refreshCheckInterval)
  {
    this.refreshCheckInterval = refreshCheckInterval;
  }
  
  private class RefreshTask
    extends TimerTask
  {
    private RefreshTask() {}
    
    public void run()
    {
      try
      {
        MetadataManager.this.log.trace("Executing metadata refresh task");
        for (MetadataProvider provider : MetadataManager.this.getProviders()) {
          provider.getMetadata();
        }
        if ((MetadataManager.this.isRefreshRequired()) && 
          (MetadataManager.this.isRefreshNowAndClear())) {
          MetadataManager.this.refreshMetadata();
        }
      }
      catch (Throwable e)
      {
        MetadataManager.this.log.warn("Metadata refreshing has failed", e);
      }
    }
  }
  
  private class MetadataProviderObserver
    implements ObservableMetadataProvider.Observer
  {
    private MetadataProviderObserver() {}
    
    public void onEvent(MetadataProvider provider)
    {
      MetadataManager.this.setRefreshRequired(true);
    }
  }
  
  @Autowired
  public void setKeyManager(KeyManager keyManager)
  {
    this.keyManager = keyManager;
  }
  
  @Autowired(required=false)
  public void setTLSConfigurer(TLSProtocolConfigurer configurer) {}
}
