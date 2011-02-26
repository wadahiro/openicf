/*
 *
 * Copyright (c) 2010 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * $Id$
 */
package com.forgerock.openicf.tam;

import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;

/**
 * Extends the {@link AbstractConfiguration} class to provide all the necessary
 * parameters to initialize the BPC Connector.
 *
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */
public class TAMConfiguration extends AbstractConfiguration {

    /**
     * Setup logging for the {@link TAMConfiguration}.
     */
    //private static final Log log = Log.getLog(TAMConfiguration.class);
    /*
     * Set up base configuration elements
     */
    private String _adminUser = null;
    private GuardedString _adminPassword = null;
    private boolean _certificateBased = false;
    private String _configUrl = "file:///";
    /* syncing TAM GSO passwords */
    private boolean _syncGSOCredentials = false;
    private boolean _deleteFromRegistry = true;
    public static final String CONNECTOR_NAME = "AccessManagerConnector";

    /**
     * Constructor
     */
    public TAMConfiguration() {
    }

    @ConfigurationProperty(order = 1, displayMessageKey = "TAM_CERT_AUTH_DISPLAY", helpMessageKey = "TAM_CERT_AUTH_HELP")
    public boolean isCertificateBased() {
        return _certificateBased;
    }

    public void setCertificateBased(boolean _certificateBased) {
        this._certificateBased = _certificateBased;
    }

    @ConfigurationProperty(order = 2, displayMessageKey = "TAM_ADMIN_USER_DISPLAY", helpMessageKey = "TAM_ADMIN_USER_HELP", required = true)
    public String getAdminUser() {
        return _adminUser;
    }

    public void setAdminUser(String _adminUser) {
        this._adminUser = _adminUser;
    }

    @ConfigurationProperty(order = 3, displayMessageKey = "TAM_ADMIN_PASSWORD_DISPLAY", helpMessageKey = "TAM_ADMIN_PASSWORD_HELP", confidential = true, required = true)
    public GuardedString getAdminPassword() {
        return _adminPassword;
    }

    public void setAdminPassword(GuardedString adminPassword) {
        this._adminPassword = adminPassword;
    }

    @ConfigurationProperty(order = 4, displayMessageKey = "TAM_CONFIG_URL_DISPLAY", helpMessageKey = "TAM_CONFIG_URL_DISPLAY", required = true)
    public String getConfigUrl() {
        return _configUrl;
    }

    public void setConfigUrl(String _configUrl) {
        this._configUrl = _configUrl;
    }

    @ConfigurationProperty(order = 5, displayMessageKey = "TAM_DELETE_FROM_RGY_DISPLAY", helpMessageKey = "TAM_DELETE_FROM_RGY_HELP", required = true)
    public boolean isDeleteFromRegistry() {
        return _deleteFromRegistry;
    }

    public void setDeleteFromRegistry(boolean _deleteFromRegistry) {
        this._deleteFromRegistry = _deleteFromRegistry;
    }

    @ConfigurationProperty(order = 6, displayMessageKey = "TAM_SYNC_GSO_CREDS_DISPLAY", helpMessageKey = "TAM_SYNC_GSO_CREDS_HELP", required = true)
    public boolean isSyncGSOCredentials() {
        return _syncGSOCredentials;
    }

    public void setSyncGSOCredentials(boolean _syncGSOCredentials) {
        this._syncGSOCredentials = _syncGSOCredentials;
    }

    /**
     * {@inheritDoc}
     */
    public void validate() {
        if (!_certificateBased) {
            if (StringUtil.isBlank(_adminUser)) {
                throw new IllegalArgumentException("Admin User ID can not be null or empty.");
            }
            if (null == _adminPassword) {
                throw new IllegalArgumentException("Password can not be null or empty.");
            }
        }
        if (StringUtil.isBlank(_configUrl)) {
            throw new IllegalArgumentException("Configuration URL can not be null or empty.");
        } else {
            try {
                URL configfile = new URL(_configUrl);
                File f = new File(configfile.toURI());
                if (!f.exists()) {
                    throw new IllegalArgumentException("Configuration file does not exist");
                }
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException(ex);
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TAMConfiguration other = (TAMConfiguration) obj;
        if ((this._adminUser == null) ? (other._adminUser != null) : !this._adminUser.equals(other._adminUser)) {
            return false;
        }
        if (this._adminPassword != other._adminPassword && (this._adminPassword == null || !this._adminPassword.equals(other._adminPassword))) {
            return false;
        }
        if (this._certificateBased != other._certificateBased) {
            return false;
        }
        if ((this._configUrl == null) ? (other._configUrl != null) : !this._configUrl.equals(other._configUrl)) {
            return false;
        }
        if (this._syncGSOCredentials != other._syncGSOCredentials) {
            return false;
        }
        if (this._deleteFromRegistry != other._deleteFromRegistry) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this._adminUser != null ? this._adminUser.hashCode() : 0);
        hash = 29 * hash + (this._adminPassword != null ? this._adminPassword.hashCode() : 0);
        hash = 29 * hash + (this._certificateBased ? 1 : 0);
        hash = 29 * hash + (this._configUrl != null ? this._configUrl.hashCode() : 0);
        hash = 29 * hash + (this._syncGSOCredentials ? 1 : 0);
        hash = 29 * hash + (this._deleteFromRegistry ? 1 : 0);
        return hash;
    }
}
