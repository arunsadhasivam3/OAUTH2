package com.idp.filter;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.namespace.QName;

import org.opensaml.Configuration;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLRuntimeException;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.samlext.idpdisco.DiscoveryResponse;
import org.opensaml.util.URLBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.keyinfo.KeyInfoGenerator;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.SAMLWebSSOHoKProcessingFilter;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.util.SAMLUtil;

public class ProfileMetadataGenerator
{
  private String id;
  private String entityId;
  private String entityBaseURL;
  private boolean requestSigned = true;
  private boolean wantAssertionSigned = true;
  private int assertionConsumerIndex = 0;
  private ExtendedMetadata extendedMetadata;
  private static TreeMap<String, String> aliases = new TreeMap(String.CASE_INSENSITIVE_ORDER);
  
  static
  {
    aliases.put("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
    aliases.put("post", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
    aliases.put("http-post", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
    aliases.put("urn:oasis:names:tc:SAML:2.0:bindings:PAOS", "urn:oasis:names:tc:SAML:2.0:bindings:PAOS");
    aliases.put("paos", "urn:oasis:names:tc:SAML:2.0:bindings:PAOS");
    aliases.put("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact");
    aliases.put("artifact", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact");
    aliases.put("http-artifact", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact");
    aliases.put("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    aliases.put("redirect", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    aliases.put("http-redirect", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    aliases.put("urn:oasis:names:tc:SAML:2.0:bindings:SOAP", "urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
    aliases.put("soap", "urn:oasis:names:tc:SAML:2.0:bindings:SOAP");
    aliases.put("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
    aliases.put("email", "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
    aliases.put("urn:oasis:names:tc:SAML:2.0:nameid-format:transient", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
    aliases.put("transient", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
    aliases.put("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
    aliases.put("persistent", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
    aliases.put("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
    aliases.put("unspecified", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
    aliases.put("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName", "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
    aliases.put("x509_subject", "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
  }
  
  private Collection<String> bindingsSSO = Arrays.asList(new String[] { "post", "artifact" });
  private Collection<String> bindingsHoKSSO = Arrays.asList(new String[0]);
  private Collection<String> bindingsSLO = Arrays.asList(new String[] { "post", "redirect" });
  private boolean includeDiscoveryExtension;
  private Collection<String> nameID = null;
  public static final Collection<String> defaultNameID = Arrays.asList(new String[] { "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient", "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent", "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified", "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName" });
  protected XMLObjectBuilderFactory builderFactory;
  protected KeyManager keyManager;
  protected SAMLProcessingFilter samlWebSSOFilter;
  protected SAMLWebSSOHoKProcessingFilter samlWebSSOHoKFilter;
  protected SAMLLogoutProcessingFilter samlLogoutProcessingFilter;
  protected SAMLEntryPoint samlEntryPoint;
  protected SAMLDiscovery samlDiscovery;
  protected static final Logger log = LoggerFactory.getLogger(MetadataGenerator.class);
  
  public ProfileMetadataGenerator()
  {
    this.builderFactory = Configuration.getBuilderFactory();
  }
  
  public EntityDescriptor generateMetadata()
  {
    boolean requestSigned = isRequestSigned();
    boolean assertionSigned = isWantAssertionSigned();
    
    Collection<String> includedNameID = getNameID();
    
    String entityId = getEntityId();
    String entityBaseURL = getEntityBaseURL();
    String entityAlias = getEntityAlias();
    
    validateRequiredAttributes(entityId, entityBaseURL);
    if (this.id == null) {
      this.id = SAMLUtil.getNCNameString(entityId);
    }
    SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder)this.builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
    EntityDescriptor descriptor = (EntityDescriptor)builder.buildObject();
    if (this.id != null) {
      descriptor.setID(this.id);
    }
    descriptor.setEntityID(entityId);
    
    SPSSODescriptor ssoDescriptor = buildSPSSODescriptor(entityBaseURL, entityAlias, requestSigned, assertionSigned, includedNameID);
    if (ssoDescriptor != null) {
      descriptor.getRoleDescriptors().add(ssoDescriptor);
    }
    return descriptor;
  }
  
  protected void validateRequiredAttributes(String entityId, String entityBaseURL)
  {
    if ((entityId == null) || (entityBaseURL == null)) {
      throw new RuntimeException("Required attributes entityId or entityBaseURL weren't set");
    }
  }
  
  protected KeyInfo getServerKeyInfo(String alias)
  {
    Credential serverCredential = this.keyManager.getCredential(alias);
    if (serverCredential == null) {
      throw new RuntimeException("Key for alias " + alias + " not found");
    }
    if (serverCredential.getPrivateKey() == null) {
      throw new RuntimeException("Key with alias " + alias + " doesn't have a private key");
    }
    return generateKeyInfoForCredential(serverCredential);
  }
  
  public ExtendedMetadata generateExtendedMetadata()
  {
    ExtendedMetadata metadata;
    if (this.extendedMetadata != null) {
      metadata = this.extendedMetadata.clone();
    } else {
      metadata = new ExtendedMetadata();
    }
    String entityBaseURL = getEntityBaseURL();
    String entityAlias = getEntityAlias();
    if (isIncludeDiscovery())
    {
      metadata.setIdpDiscoveryURL(getDiscoveryURL(entityBaseURL, entityAlias));
      metadata.setIdpDiscoveryResponseURL(getDiscoveryResponseURL(entityBaseURL, entityAlias));
    }
    else
    {
      metadata.setIdpDiscoveryURL(null);
      metadata.setIdpDiscoveryResponseURL(null);
    }
    metadata.setLocal(true);
    
    return metadata;
  }
  
  protected KeyInfo generateKeyInfoForCredential(Credential credential)
  {
    try
    {
      String keyInfoGeneratorName = "MetadataKeyInfoGenerator";
      if ((this.extendedMetadata != null) && (this.extendedMetadata.getKeyInfoGeneratorName() != null)) {
        keyInfoGeneratorName = this.extendedMetadata.getKeyInfoGeneratorName();
      }
      KeyInfoGenerator keyInfoGenerator = SecurityHelper.getKeyInfoGenerator(credential, null, keyInfoGeneratorName);
      return keyInfoGenerator.generate(credential);
    }
    catch (SecurityException e)
    {
      log.error("Can't obtain key from the keystore or generate key info for credential: " + credential, e);
      throw new SAMLRuntimeException("Can't obtain key from keystore or generate key info", e);
    }
  }
  
  protected SPSSODescriptor buildSPSSODescriptor(String entityBaseURL, String entityAlias, boolean requestSigned, boolean wantAssertionSigned, Collection<String> includedNameID)
  {
    SAMLObjectBuilder<SPSSODescriptor> builder = (SAMLObjectBuilder)this.builderFactory.getBuilder(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    SPSSODescriptor spDescriptor = (SPSSODescriptor)builder.buildObject();
    spDescriptor.setAuthnRequestsSigned(Boolean.valueOf(requestSigned));
    spDescriptor.setWantAssertionsSigned(Boolean.valueOf(wantAssertionSigned));
    spDescriptor.addSupportedProtocol("urn:oasis:names:tc:SAML:2.0:protocol");
    
    spDescriptor.getNameIDFormats().addAll(getNameIDFormat(includedNameID));
    
    int index = 0;
    
    Collection<String> bindingsSSO = mapAliases(getBindingsSSO());
    Collection<String> bindingsSLO = mapAliases(getBindingsSLO());
    Collection<String> bindingsHoKSSO = mapAliases(getBindingsHoKSSO());
    for (String binding : bindingsSSO)
    {
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact")) {
        spDescriptor.getAssertionConsumerServices().add(getAssertionConsumerService(entityBaseURL, entityAlias, this.assertionConsumerIndex == index, index++, getSAMLWebSSOProcessingFilterPath(), "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact"));
      }
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")) {
        spDescriptor.getAssertionConsumerServices().add(getAssertionConsumerService(entityBaseURL, entityAlias, this.assertionConsumerIndex == index, index++, getSAMLWebSSOProcessingFilterPath(), "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
      }
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:PAOS")) {
        spDescriptor.getAssertionConsumerServices().add(getAssertionConsumerService(entityBaseURL, entityAlias, this.assertionConsumerIndex == index, index++, getSAMLWebSSOProcessingFilterPath(), "urn:oasis:names:tc:SAML:2.0:bindings:PAOS"));
      }
    }
    for (String binding : bindingsHoKSSO)
    {
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact")) {
        spDescriptor.getAssertionConsumerServices().add(getHoKAssertionConsumerService(entityBaseURL, entityAlias, this.assertionConsumerIndex == index, index++, getSAMLWebSSOHoKProcessingFilterPath(), "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Artifact"));
      }
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")) {
        spDescriptor.getAssertionConsumerServices().add(getHoKAssertionConsumerService(entityBaseURL, entityAlias, this.assertionConsumerIndex == index, index++, getSAMLWebSSOHoKProcessingFilterPath(), "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
      }
    }
    for (String binding : bindingsSLO)
    {
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST")) {
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(entityBaseURL, entityAlias, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"));
      }
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")) {
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(entityBaseURL, entityAlias, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect"));
      }
      if (binding.equals("urn:oasis:names:tc:SAML:2.0:bindings:SOAP")) {
        spDescriptor.getSingleLogoutServices().add(getSingleLogoutService(entityBaseURL, entityAlias, "urn:oasis:names:tc:SAML:2.0:bindings:SOAP"));
      }
    }
    Extensions extensions = buildExtensions(entityBaseURL, entityAlias);
    if (extensions != null) {
      spDescriptor.setExtensions(extensions);
    }
    String signingKey = getSigningKey();
    String encryptionKey = getEncryptionKey();
    String tlsKey = getTLSKey();
    if (signingKey != null) {
      spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.SIGNING, getServerKeyInfo(signingKey)));
    } else {
      log.info("Generating metadata without signing key, KeyStore doesn't contain any default private key, or the signingKey specified in ExtendedMetadata cannot be found");
    }
    if (encryptionKey != null) {
      spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.ENCRYPTION, getServerKeyInfo(encryptionKey)));
    } else {
      log.info("Generating metadata without encryption key, KeyStore doesn't contain any default private key, or the encryptionKey specified in ExtendedMetadata cannot be found");
    }
    if ((tlsKey != null) && (!tlsKey.equals(encryptionKey)) && (!tlsKey.equals(signingKey))) {
      spDescriptor.getKeyDescriptors().add(getKeyDescriptor(UsageType.UNSPECIFIED, getServerKeyInfo(tlsKey)));
    }
    return spDescriptor;
  }
  
  protected Collection<String> mapAliases(Collection<String> values)
  {
    LinkedHashSet<String> result = new LinkedHashSet();
    for (String value : values)
    {
      String alias = (String)aliases.get(value);
      if (alias != null) {
        result.add(alias);
      } else {
        log.warn("Unsupported value " + value + " found");
      }
    }
    return result;
  }
  
  protected Extensions buildExtensions(String entityBaseURL, String entityAlias)
  {
    boolean include = false;
    Extensions extensions = new ExtensionsBuilder().buildObject();
    if (isIncludeDiscoveryExtension())
    {
      DiscoveryResponse discoveryService = getDiscoveryService(entityBaseURL, entityAlias);
      extensions.getUnknownXMLObjects().add(discoveryService);
      include = true;
    }
    if (include) {
      return extensions;
    }
    return null;
  }
  
  protected KeyDescriptor getKeyDescriptor(UsageType type, KeyInfo key)
  {
    SAMLObjectBuilder<KeyDescriptor> builder = (SAMLObjectBuilder)Configuration.getBuilderFactory().getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
    KeyDescriptor descriptor = (KeyDescriptor)builder.buildObject();
    descriptor.setUse(type);
    descriptor.setKeyInfo(key);
    return descriptor;
  }
  
  protected Collection<NameIDFormat> getNameIDFormat(Collection<String> includedNameID)
  {
    includedNameID = mapAliases(includedNameID);
    Collection<NameIDFormat> formats = new LinkedList();
    SAMLObjectBuilder<NameIDFormat> builder = (SAMLObjectBuilder)this.builderFactory.getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);
    for (String nameIDValue : includedNameID)
    {
      if (nameIDValue.equals("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"))
      {
        NameIDFormat nameID = (NameIDFormat)builder.buildObject();
        nameID.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress");
        formats.add(nameID);
      }
      if (nameIDValue.equals("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"))
      {
        NameIDFormat nameID = (NameIDFormat)builder.buildObject();
        nameID.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
        formats.add(nameID);
      }
      if (nameIDValue.equals("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"))
      {
        NameIDFormat nameID = (NameIDFormat)builder.buildObject();
        nameID.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
        formats.add(nameID);
      }
      if (nameIDValue.equals("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"))
      {
        NameIDFormat nameID = (NameIDFormat)builder.buildObject();
        nameID.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
        formats.add(nameID);
      }
      if (nameIDValue.equals("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName"))
      {
        NameIDFormat nameID = (NameIDFormat)builder.buildObject();
        nameID.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");
        formats.add(nameID);
      }
    }
    return formats;
  }
  
  protected AssertionConsumerService getAssertionConsumerService(String entityBaseURL, String entityAlias, boolean isDefault, int index, String filterURL, String binding)
  {
    SAMLObjectBuilder<AssertionConsumerService> builder = (SAMLObjectBuilder)this.builderFactory.getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
    AssertionConsumerService consumer = (AssertionConsumerService)builder.buildObject();
    consumer.setLocation(getServerURL(entityBaseURL, entityAlias, filterURL));
    consumer.setBinding(binding);
    if (isDefault) {
      consumer.setIsDefault(Boolean.valueOf(true));
    }
    consumer.setIndex(Integer.valueOf(index));
    return consumer;
  }
  
  protected AssertionConsumerService getHoKAssertionConsumerService(String entityBaseURL, String entityAlias, boolean isDefault, int index, String filterURL, String binding)
  {
    AssertionConsumerService hokAssertionConsumer = getAssertionConsumerService(entityBaseURL, entityAlias, isDefault, index, filterURL, "urn:oasis:names:tc:SAML:2.0:profiles:holder-of-key:SSO:browser");
    QName consumerName = new QName("urn:oasis:names:tc:SAML:2.0:profiles:holder-of-key:SSO:browser", "ProtocolBinding", "hoksso");
    hokAssertionConsumer.getUnknownAttributes().put(consumerName, binding);
    return hokAssertionConsumer;
  }
  
  protected DiscoveryResponse getDiscoveryService(String entityBaseURL, String entityAlias)
  {
    SAMLObjectBuilder<DiscoveryResponse> builder = (SAMLObjectBuilder)this.builderFactory.getBuilder(DiscoveryResponse.DEFAULT_ELEMENT_NAME);
    DiscoveryResponse discovery = (DiscoveryResponse)builder.buildObject(DiscoveryResponse.DEFAULT_ELEMENT_NAME);
    discovery.setBinding("urn:oasis:names:tc:SAML:profiles:SSO:idp-discovery-protocol");
    discovery.setLocation(getDiscoveryResponseURL(entityBaseURL, entityAlias));
    return discovery;
  }
  
  protected SingleLogoutService getSingleLogoutService(String entityBaseURL, String entityAlias, String binding)
  {
    SAMLObjectBuilder<SingleLogoutService> builder = (SAMLObjectBuilder)this.builderFactory.getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
    SingleLogoutService logoutService = (SingleLogoutService)builder.buildObject();
    logoutService.setLocation(getServerURL(entityBaseURL, entityAlias, getSAMLLogoutFilterPath()));
    logoutService.setBinding(binding);
    return logoutService;
  }
  
  private String getServerURL(String entityBaseURL, String entityAlias, String processingURL)
  {
    return getServerURL(entityBaseURL, entityAlias, processingURL, null);
  }
  
  private String getServerURL(String entityBaseURL, String entityAlias, String processingURL, Map<String, String> parameters)
  {
    StringBuilder result = new StringBuilder();
    result.append(entityBaseURL);
    if (!processingURL.startsWith("/")) {
      result.append("/");
    }
    result.append(processingURL);
    if (entityAlias != null)
    {
      if (!processingURL.endsWith("/")) {
        result.append("/");
      }
      result.append("alias/");
      result.append(entityAlias);
    }
    String resultString = result.toString();
    if ((parameters == null) || (parameters.size() == 0)) {
      return resultString;
    }
    URLBuilder returnUrlBuilder = new URLBuilder(resultString);
    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      returnUrlBuilder.getQueryParams().add(new Pair(entry.getKey(), entry.getValue()));
    }
    return returnUrlBuilder.buildURL();
  }
  
  private String getSAMLWebSSOProcessingFilterPath()
  {
    if (this.samlWebSSOFilter != null) {
      return this.samlWebSSOFilter.getFilterProcessesUrl();
    }
    return "/saml/SSO";
  }
  
  private String getSAMLWebSSOHoKProcessingFilterPath()
  {
    if (this.samlWebSSOHoKFilter != null) {
      return this.samlWebSSOHoKFilter.getFilterProcessesUrl();
    }
    return "/saml/HoKSSO";
  }
  
  private String getSAMLEntryPointPath()
  {
    if (this.samlEntryPoint != null) {
      return this.samlEntryPoint.getFilterProcessesUrl();
    }
    return "/saml/login";
  }
  
  private String getSAMLDiscoveryPath()
  {
    if (this.samlDiscovery != null) {
      return this.samlDiscovery.getFilterProcessesUrl();
    }
    return "/saml/discovery";
  }
  
  private String getSAMLLogoutFilterPath()
  {
    if (this.samlLogoutProcessingFilter != null) {
      return this.samlLogoutProcessingFilter.getFilterProcessesUrl();
    }
    return "/saml/SingleLogout";
  }
  
  @Autowired(required=false)
  @Qualifier("samlWebSSOProcessingFilter")
  public void setSamlWebSSOFilter(SAMLProcessingFilter samlWebSSOFilter)
  {
    this.samlWebSSOFilter = samlWebSSOFilter;
  }
  
  @Autowired(required=false)
  @Qualifier("samlWebSSOHoKProcessingFilter")
  public void setSamlWebSSOHoKFilter(SAMLWebSSOHoKProcessingFilter samlWebSSOHoKFilter)
  {
    this.samlWebSSOHoKFilter = samlWebSSOHoKFilter;
  }
  
  @Autowired(required=false)
  public void setSamlLogoutProcessingFilter(SAMLLogoutProcessingFilter samlLogoutProcessingFilter)
  {
    this.samlLogoutProcessingFilter = samlLogoutProcessingFilter;
  }
  
  @Autowired(required=false)
  public void setSamlEntryPoint(SAMLEntryPoint samlEntryPoint)
  {
    this.samlEntryPoint = samlEntryPoint;
  }
  
  public boolean isRequestSigned()
  {
    return this.requestSigned;
  }
  
  public void setRequestSigned(boolean requestSigned)
  {
    this.requestSigned = requestSigned;
  }
  
  public boolean isWantAssertionSigned()
  {
    return this.wantAssertionSigned;
  }
  
  public void setWantAssertionSigned(boolean wantAssertionSigned)
  {
    this.wantAssertionSigned = wantAssertionSigned;
  }
  
  public Collection<String> getNameID()
  {
    return this.nameID == null ? defaultNameID : this.nameID;
  }
  
  public void setNameID(Collection<String> nameID)
  {
    this.nameID = nameID;
  }
  
  public String getEntityBaseURL()
  {
    return this.entityBaseURL;
  }
  
  public void setEntityBaseURL(String entityBaseURL)
  {
    this.entityBaseURL = entityBaseURL;
  }
  
  @Autowired
  public void setKeyManager(KeyManager keyManager)
  {
    this.keyManager = keyManager;
  }
  
  public void setId(String id)
  {
    this.id = id;
  }
  
  public String getId()
  {
    return this.id;
  }
  
  public void setEntityId(String entityId)
  {
    this.entityId = entityId;
  }
  
  public String getEntityId()
  {
    return this.entityId;
  }
  
  public Collection<String> getBindingsSSO()
  {
    return this.bindingsSSO;
  }
  
  public void setBindingsSSO(Collection<String> bindingsSSO)
  {
    if (bindingsSSO == null) {
      this.bindingsSSO = Collections.emptyList();
    } else {
      this.bindingsSSO = bindingsSSO;
    }
  }
  
  public Collection<String> getBindingsSLO()
  {
    return this.bindingsSLO;
  }
  
  public void setBindingsSLO(Collection<String> bindingsSLO)
  {
    if (bindingsSLO == null) {
      this.bindingsSLO = Collections.emptyList();
    } else {
      this.bindingsSLO = bindingsSLO;
    }
  }
  
  public Collection<String> getBindingsHoKSSO()
  {
    return this.bindingsHoKSSO;
  }
  
  public void setBindingsHoKSSO(Collection<String> bindingsHoKSSO)
  {
    if (bindingsHoKSSO == null) {
      this.bindingsHoKSSO = Collections.emptyList();
    } else {
      this.bindingsHoKSSO = bindingsHoKSSO;
    }
  }
  
  public boolean isIncludeDiscoveryExtension()
  {
    return this.includeDiscoveryExtension;
  }
  
  public void setIncludeDiscoveryExtension(boolean includeDiscoveryExtension)
  {
    this.includeDiscoveryExtension = includeDiscoveryExtension;
  }
  
  public int getAssertionConsumerIndex()
  {
    return this.assertionConsumerIndex;
  }
  
  public void setAssertionConsumerIndex(int assertionConsumerIndex)
  {
    this.assertionConsumerIndex = assertionConsumerIndex;
  }
  
  protected boolean isIncludeDiscovery()
  {
    return (this.extendedMetadata != null) && (this.extendedMetadata.isIdpDiscoveryEnabled());
  }
  
  protected String getDiscoveryURL(String entityBaseURL, String entityAlias)
  {
    if ((this.extendedMetadata != null) && (this.extendedMetadata.getIdpDiscoveryURL() != null) && (this.extendedMetadata.getIdpDiscoveryURL().length() > 0)) {
      return this.extendedMetadata.getIdpDiscoveryURL();
    }
    return getServerURL(entityBaseURL, entityAlias, getSAMLDiscoveryPath());
  }
  
  protected String getDiscoveryResponseURL(String entityBaseURL, String entityAlias)
  {
    if ((this.extendedMetadata != null) && (this.extendedMetadata.getIdpDiscoveryResponseURL() != null) && (this.extendedMetadata.getIdpDiscoveryResponseURL().length() > 0)) {
      return this.extendedMetadata.getIdpDiscoveryResponseURL();
    }
    Map<String, String> params = new HashMap();
    params.put("disco", "true");
    return getServerURL(entityBaseURL, entityAlias, getSAMLEntryPointPath(), params);
  }
  
  protected String getSigningKey()
  {
    if ((this.extendedMetadata != null) && (this.extendedMetadata.getSigningKey() != null)) {
      return this.extendedMetadata.getSigningKey();
    }
    return this.keyManager.getDefaultCredentialName();
  }
  
  protected String getEncryptionKey()
  {
    if ((this.extendedMetadata != null) && (this.extendedMetadata.getEncryptionKey() != null)) {
      return this.extendedMetadata.getEncryptionKey();
    }
    return this.keyManager.getDefaultCredentialName();
  }
  
  protected String getTLSKey()
  {
    if ((this.extendedMetadata != null) && (this.extendedMetadata.getTlsKey() != null)) {
      return this.extendedMetadata.getTlsKey();
    }
    return null;
  }
  
  protected String getEntityAlias()
  {
    if (this.extendedMetadata != null) {
      return this.extendedMetadata.getAlias();
    }
    return null;
  }
  
  public ExtendedMetadata getExtendedMetadata()
  {
    return this.extendedMetadata;
  }
  
  public void setExtendedMetadata(ExtendedMetadata extendedMetadata)
  {
    this.extendedMetadata = extendedMetadata;
  }
}

