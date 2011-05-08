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
package org.forgerock.openicf.usd;

import java.net.URL;
import java.net.MalformedURLException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;

/**
 * Extends the {@link AbstractConfiguration} class to provide all the necessary
 * parameters to initialize the Connector.
 *
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */
public class CAServiceDeskConfiguration extends AbstractConfiguration {

    /**
     * Setup logging for the {@link CAServiceDeskConfiguration}.
     */
    //private static final Log log = Log.getLog(CAServiceDeskConfiguration.class);
    /**
     * The WSDL file location:
     * http://<servername>:<port>/axis/services/USD_R11_WebService?WSDL
     * Example: http://ca-test:8080/axis/services/USD_R11_WebService?wsdl
     *
     */
    private String USDWebServiceWSDL = "http://ca-test:8080/axis/services/USD_R11_WebService?wsdl";
    /**
     * The username that must be used for accessing the web service.
     * The user must have rights to query, create and update any cnt object.
     */
    private String adminUserID = null;
    /**
     * The password associated with the administrator user ID.
     * (The password is encrypted by the Identity Manager)
     */
    private GuardedString adminPassword = null;
    //Optional parameters
    /**
     * If you want to specific other web service than the one in WSDL file the
     * enter the URL like this: http://<otherservername>:<port>/axis/services/USD_R11_WebService
     * Example: http://ca-test:8080/axis/services/USD_R11_WebService
     */
    private String USDWebServiceLocation = "http://ca-test:8080/axis/services/USD_R11_WebService";
    /**
     * Policy is required, but can be empty, and you must use plain text.
     * Use the policy code defined in a policy.
     */
    private String policy = null;
    /**
     * Number of items to retrieve by one getListValues() request. Default is 100.
     */
    private int blockCount = 100;

    @ConfigurationProperty(order = 1, displayMessageKey = "USD_WEBSERVICEWSDL_DISPLAY", helpMessageKey = "USD_WEBSERVICEWSDL_DISPLAY", required = true)
    public String getUSDWebServiceWSDL() {
        return USDWebServiceWSDL;
    }

    public void setUSDWebServiceWSDL(String USDWebServiceWSDL) {
        this.USDWebServiceWSDL = USDWebServiceWSDL;
    }

    @ConfigurationProperty(order = 2, displayMessageKey = "USD_ADMIN_USER_DISPLAY", helpMessageKey = "USD_ADMIN_USER_HELP", required = true)
    public String getAdminUserID() {
        return adminUserID;
    }

    public void setAdminUserID(String _adminUser) {
        this.adminUserID = _adminUser;
    }

    @ConfigurationProperty(order = 3, displayMessageKey = "USD_ADMIN_PASSWORD_DISPLAY", helpMessageKey = "USD_ADMIN_PASSWORD_HELP", confidential = true, required = true)
    public GuardedString getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(GuardedString adminPassword) {
        this.adminPassword = adminPassword;
    }


    @ConfigurationProperty(order = 4, displayMessageKey = "USD_WEBSERVICELOCATION_DISPLAY", helpMessageKey = "USD_WEBSERVICELOCATION_HELP")
    public String getUSDWebServiceLocation() {
        return USDWebServiceLocation;
    }

    public void setUSDWebServiceLocation(String USDWebServiceLocation) {
        this.USDWebServiceLocation = USDWebServiceLocation;
    }

    @ConfigurationProperty(order = 5, displayMessageKey = "USD_BLOCKCOUNT_DISPLAY", helpMessageKey = "USD_BLOCKCOUNT_HELP")
    public int getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(int blockCount) {
        this.blockCount = blockCount;
    }

    @ConfigurationProperty(order = 6, displayMessageKey = "USD_POLICY_DISPLAY", helpMessageKey = "USD_POLICY_HELP")
    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }


    /**
     * {@inheritDoc}
     */
    public void validate() {
        if (StringUtil.isBlank(adminUserID)) {
            throw new IllegalArgumentException("Admin User ID can not be null or empty.");
        }
        if (null == adminPassword) {
            throw new IllegalArgumentException("Password can not be null or empty.");
        }
        if (StringUtil.isBlank(USDWebServiceWSDL)) {
            throw new IllegalArgumentException("Configuration URL can not be null or empty.");
        } else {
            try {
                URL wsdl = new URL(USDWebServiceWSDL);
            } catch (MalformedURLException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
    }
}
