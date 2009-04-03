/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.     
 * 
 * The contents of this file are subject to the terms of the Common Development 
 * and Distribution License("CDDL") (the "License").  You may not use this file 
 * except in compliance with the License.
 * 
 * You can obtain a copy of the License at 
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields 
 * enclosed by brackets [] replaced by your own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.ldap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.EqualsHashCodeBuilder;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

/**
 * Encapsulates the LDAP connector's configuration.
 *
 * @author Andrei Badea
 */
public class LdapConfiguration extends AbstractConfiguration {

    // XXX should try to connect to the resource.
    // XXX add @ConfigurationProperty.

    private static final Log log = Log.getLog(LdapConfiguration.class);

    private static final int DEFAULT_PORT = 389;
    private static final int DEFAULT_SSL_PORT = 636;

    /**
     * The LDAP host server to connect to.
     */
    private String host = "localhost";

    /**
     * The port the server is listening on.
     */
    private int port = -1;

    /**
     * Whether the port is a secure SSL port.
     */
    private boolean ssl;

    /**
     * LDAP URL's to connect to if the main server specified through the host and port
     * properties is not available.
     */
    private String[] failover = { };

    /**
     * The bind DN for performing operations on the server.
     */
    private String principal;

    /**
     * The bind password associated with the bind DN.
     */
    private GuardedString credentials;

//    /**
//     * The name of the password attribute.
//     */
//    private String passwordAttribute = null;

    /**
     * The authentication mechanism to use against the LDAP server.
     */
    private String authentication = null;

    /**
     * The base DNs for operations on the server.
     */
    private String[] baseContexts = { "dc=MYDOMAIN,dc=com" };

    /**
     * A search filter that any account needs to match in order to be returned.
     */
    private String accountSearchFilter = null;

    /**
     * The LDAP attribute holding the member for non-POSIX static groups.
     */
    private String groupMemberAttr = "uniqueMember";

    /**
     * If true, when binding check for the Password Expired control (and also Password Policy control)
     * and throw exceptions (PasswordExpiredException, etc.) appropriately.
     */
    private boolean respectResourcePasswordPolicyChangeAfterReset;

    /**
     * Whether to use block-based LDAP controls like simple paged results or VLV control.
     */
    private boolean useBlocks = true;

    /**
     * The block size (not count, but that's what IDM calls it) for paged and VLV index searches.
     */
    private int blockCount = 100;

    /**
     * If true, simple paged search will be preferred over VLV index search
     * when both are available.
     */
    private boolean usePagedResultControl;

    /**
     * The LDAP attribute to map Uid to.
     */
    private String uidAttribute = "entryUUID";

    /**
     * Whether to read the schema from the server.
     */
    private boolean readSchema = true;

    /**
     * The set of object classes to return in the schema
     * (apart from those returned by default).
     */
    private String[] extendedObjectClasses = new String[0];

    /**
     * The naming attributes for the extended object classes:
     * {@code extendedNamingAttributes[i]} is the naming attribute for
     * {@code extendedObjectClasses[i]}.
     */
    private String[] extendedNamingAttributes = new String[0];

    // Exposed configuration properties end here.

    private final ObjectClassMappingConfig accountConfig = new ObjectClassMappingConfig(ObjectClass.ACCOUNT,
            CollectionUtil.newList("top", "person", "organizationalPerson", "inetOrgPerson"));

    private final ObjectClassMappingConfig groupConfig = new ObjectClassMappingConfig(ObjectClass.GROUP,
            CollectionUtil.newList("top", "groupOfUniqueNames"));

    private List<LdapName> baseContextsAsLdapNames;

    public LdapConfiguration() {
        // Note: order is important!

        accountConfig.setNameAttribute("entryDN");
        accountConfig.addAttributeMapping("uid", "uid");
        accountConfig.addAttributeMapping("cn", "cn");
        accountConfig.addAttributeMapping("givenName", "givenName");
        accountConfig.addAttributeMapping("sn", "sn");
        accountConfig.addAttributeMapping("modifyTimeStamp", "modifyTimeStamp");

        groupConfig.setNameAttribute("entryDN");
        groupConfig.addAttributeMapping("cn", "cn");
    }

    /**
     * {@inheritDoc}
     */
    public void validate() {
        if (baseContexts == null || baseContexts.length < 1) {
            throw new ConfigurationException("No base context was provided in the LDAP configuration");
        }
        Set<String> baseContextSet = CollectionUtil.newCaseInsensitiveSet();
        baseContextSet.addAll(Arrays.asList(baseContexts));
        if (baseContextSet.size() != baseContexts.length) {
            throw new ConfigurationException("The list of base contexts in the LDAP configuration contains duplicates");
        }
        for (String baseContext : baseContexts) {
            try {
                if (StringUtil.isBlank(baseContext)) {
                    throw new ConfigurationException("The list of base contexts cannot contain blank values");
                }
                new LdapName(baseContext);
            } catch (InvalidNameException e) {
                throw new ConfigurationException("The base context " + baseContext + " in the LDAP configuration cannot be parsed");
            }
        }

        if (accountConfig.getLdapClasses().size() < 1) {
            throw new ConfigurationException("No base context was provided in the LDAP configuration");
        }
        Set<String> accountObjectClassSet = CollectionUtil.newCaseInsensitiveSet();
        accountObjectClassSet.addAll(accountConfig.getLdapClasses());
        if (accountObjectClassSet.size() != accountConfig.getLdapClasses().size()) {
            throw new ConfigurationException("The list of account object clases in the LDAP configuration contains duplicates");
        }
        for (String accountObjectClass : accountConfig.getLdapClasses()) {
            if (StringUtil.isBlank(accountObjectClass)) {
                throw new ConfigurationException("The list of account object classes cannot contain blank values");
            }
        }

        if (StringUtil.isBlank(uidAttribute)) {
            throw new ConfigurationException("The LDAP attribute to map to Uid cannot be blank");
        }

        Set<String> extendedObjectClassSet = CollectionUtil.newCaseInsensitiveSet();
        extendedObjectClassSet.addAll(Arrays.asList(extendedObjectClasses));
        if (extendedObjectClassSet.size() != extendedObjectClasses.length) {
            throw new ConfigurationException("The list of extended object classes in the LDAP configuration contains duplicates");
        }
        for (String extendedObjectClass : extendedObjectClasses) {
            if (StringUtil.isBlank(extendedObjectClass)) {
                throw new ConfigurationException("The list of extended object classes cannot contain blank values");
            }
        }
        if (extendedObjectClasses.length > 0) {
            if (!readSchema) {
                throw new ConfigurationException("The readSchema property must be true when using extended object classes");
            }
            if (extendedNamingAttributes.length < extendedObjectClasses.length) {
                throw new ConfigurationException("No naming attributes were provided for all extended object classes in the LDAP configuration");
            }
            for (String extendedNamingAttribute : extendedNamingAttributes) {
                if (StringUtil.isBlank(extendedNamingAttribute)) {
                    throw new ConfigurationException("The list of extended naming attributes cannot contain blank values");
                }
            }
        }

//        if (passwordAttribute == null) {
//            String msg = "The name of a password attribute was not provided in the LDAP configuration";
//            throw new ConfigurationException(msg);
//        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        if (StringUtil.isBlank(host)) {
            throw new ConfigurationException("The host name should not be null or whitespace");
        }
        this.host = host;
    }

    public int getPort() {
        return port != -1 ? port : (ssl ? DEFAULT_SSL_PORT : DEFAULT_PORT);
    }

    public void setPort(int port) {
        if (port < 0 || port > 0xffff) {
            throw new ConfigurationException("The port number should be 0 through 65535");
        }
        this.port = port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String[] getFailover() {
        return failover;
    }

    public void setFailover(String... failover) {
        if (failover == null) {
            throw new ConfigurationException("The failover parameter cannot be null");
        }
        this.failover = failover;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    @ConfigurationProperty(confidential = true)
    public GuardedString getCredentials() {
        return credentials;
    }

    public void setCredentials(GuardedString credentials) {
        this.credentials = credentials;
    }

//    public String getPasswordAttribute() {
//        return passwordAttribute;
//    }
//
//    public void setPasswordAttribute(String passwordAttribute) {
//        this.passwordAttribute = passwordAttribute;
//    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public String[] getBaseContexts() {
        return baseContexts.clone();
    }

    public void setBaseContexts(String... baseContexts) {
        if (baseContexts == null) {
            throw new ConfigurationException("The baseContexts parameter cannot be null");
        }
        this.baseContexts = baseContexts.clone();
    }

    public String getAccountSearchFilter() {
        return accountSearchFilter;
    }

    public void setAccountSearchFilter(String accountSearchFilter) {
        this.accountSearchFilter = accountSearchFilter;
    }

    public String[] getAccountObjectClasses() {
        List<String> ldapClasses = accountConfig.getLdapClasses();
        return ldapClasses.toArray(new String[ldapClasses.size()]);
    }

    public void setAccountObjectClasses(String... accountObjectClasses) {
        accountConfig.setLdapClasses(Arrays.asList(accountObjectClasses));
    }

    public String getGroupMemberAttr() {
        return groupMemberAttr;
    }

    public void setGroupMemberAttr(String groupMemberAttr) {
        this.groupMemberAttr = groupMemberAttr;
    }

    public boolean isRespectResourcePasswordPolicyChangeAfterReset() {
        return respectResourcePasswordPolicyChangeAfterReset;
    }

    public void setRespectResourcePasswordPolicyChangeAfterReset(boolean respectResourcePasswordPolicyChangeAfterReset) {
        this.respectResourcePasswordPolicyChangeAfterReset = respectResourcePasswordPolicyChangeAfterReset;
    }

    public boolean isUseBlocks() {
        return useBlocks;
    }

    public void setUseBlocks(boolean useBlocks) {
        this.useBlocks = useBlocks;
    }

    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    public boolean isUsePagedResultControl() {
        return usePagedResultControl;
    }

    public void setUsePagedResultControl(boolean usePagedResultControl) {
        this.usePagedResultControl = usePagedResultControl;
    }

    public String getUidAttribute() {
        return uidAttribute;
    }

    public void setUidAttribute(String uidAttribute) {
        this.uidAttribute = uidAttribute;
    }

    public boolean isReadSchema() {
        return readSchema;
    }

    public void setReadSchema(boolean readSchema) {
        this.readSchema = readSchema;
    }

    public String[] getExtendedObjectClasses() {
        return extendedObjectClasses.clone();
    }

    public void setExtendedObjectClasses(String... extendedObjectClasses) {
        if (extendedObjectClasses == null) {
            throw new ConfigurationException("The extended object classes parameter cannot be null");
        }
        this.extendedObjectClasses = (String[]) extendedObjectClasses.clone();
    }

    public String[] getExtendedNamingAttributes() {
        return extendedNamingAttributes.clone();
    }

    public void setExtendedNamingAttributes(String... extendedNamingAttributes) {
        if (extendedNamingAttributes == null) {
            throw new ConfigurationException("The extended naming attributes parameter cannot be null");
        }
        this.extendedNamingAttributes = (String[]) extendedNamingAttributes.clone();
    }

    // Getters and setters for configuration properties end here.

    public boolean isContainedUnderBaseContexts(LdapName entry) {
        for (LdapName container : getBaseContextsAsLdapNames()) {
            if (entry.startsWith(container)) {
                return true;
            }
        }
        return false;
    }

    private List<LdapName> getBaseContextsAsLdapNames() {
        if (baseContextsAsLdapNames == null) {
            List<LdapName> result = new ArrayList<LdapName>(baseContexts.length);
            try {
                for (String baseContext : baseContexts) {
                    result.add(new LdapName(baseContext));
                }
            } catch (InvalidNameException e) {
                throw new ConfigurationException(e);
            }
            baseContextsAsLdapNames = result;
        }
        return baseContextsAsLdapNames;
    }

    public Map<ObjectClass, ObjectClassMappingConfig> getObjectClassMappingConfigs() {
        HashMap<ObjectClass, ObjectClassMappingConfig> result = new HashMap<ObjectClass, ObjectClassMappingConfig>();
        result.put(accountConfig.getObjectClass(), accountConfig);
        result.put(groupConfig.getObjectClass(), groupConfig);

        for (int i = 0; i < extendedObjectClasses.length; i++) {
            String extendedObjectClass = extendedObjectClasses[i];
            ObjectClassMappingConfig config = new ObjectClassMappingConfig(new ObjectClass(extendedObjectClass),
                    Collections.singletonList(extendedObjectClass));
            if (i < extendedNamingAttributes.length) {
                config.setNameAttribute(extendedNamingAttributes[i]);
            } else {
                // Just in the case extendedNamingAttributes is not in sync with
                // extendedObjectClasses, we need to set a default naming attribute -- one
                // that always exists.
                // XXX or perhaps just throw an exception.
                config.setNameAttribute("entryDN");
            }
            if (!result.containsKey(config.getObjectClass())) {
                result.put(config.getObjectClass(), config);
            } else {
                log.warn("Ignoring mapping configuration for object class {0} because it is already mapped", config.getObjectClass().getObjectClassValue());
            }
        }
        return result;
    }

    private EqualsHashCodeBuilder createHashCodeBuilder() {
        EqualsHashCodeBuilder builder = new EqualsHashCodeBuilder();
        builder.append(host);
        builder.append(port);
        builder.append(ssl);
        builder.append(failover);
        builder.append(principal);
        builder.append(credentials);
        builder.append(authentication);
        for (String baseContext : baseContexts) {
            builder.append(baseContext);
        }
        builder.append(accountSearchFilter);
//        builder.append(passwordAttribute);
        builder.append(groupMemberAttr);
        builder.append(respectResourcePasswordPolicyChangeAfterReset);
        builder.append(useBlocks);
        builder.append(blockCount);
        builder.append(usePagedResultControl);
        builder.append(uidAttribute);
        builder.append(readSchema);
        for (String extendedObjectClass : extendedObjectClasses) {
            builder.append(extendedObjectClass);
        }
        for (String extendedNamingAttribute : extendedNamingAttributes) {
            builder.append(extendedNamingAttribute);
        }
        builder.append(accountConfig);
        builder.append(groupConfig);
        return builder;
    }

    @Override
    public int hashCode() {
        return createHashCodeBuilder().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LdapConfiguration) {
            LdapConfiguration that = (LdapConfiguration) obj;
            if (this == that) {
                return true;
            }
            return this.createHashCodeBuilder().equals(that.createHashCodeBuilder());
        }
        return false;
    }
}