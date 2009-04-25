/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.     
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
package org.identityconnectors.solaris.test;


import junit.framework.Assert;

import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.solaris.SolarisConfiguration;
import org.identityconnectors.solaris.SolarisConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolarisTest {

    private static SolarisConfiguration config;
    
    /**
     * set valid credentials based on build.groovy property file
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        config = SolarisTestCommon.createConfiguration();
    }

    @After
    public void tearDown() throws Exception {
        config = null;
    }
    
    /* ************ TEST CONNECTOR ************ */
    
    @Test
    public void basicConnectorTests() {
        testGoodConnection();
        basicSchemaTest();
        testGoodConfiguration();
    }
    

    
    

    /* ************* TEST CONFIGURATION *********** */
    
    private void testGoodConfiguration() {
        try {
            SolarisConfiguration config = getConfig();
            // no IllegalArgumentException should be thrown
            config.validate();
        } catch (IllegalArgumentException ex) {
            Assert
                    .fail("no IllegalArgumentException should be thrown for valid configuration.\n" + ex.getMessage());
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMissingUsername() {
        SolarisConfiguration config = getConfig();
        config.setUserName(null);
        config.validate();
        Assert.fail("Configuration allowed a null admin username.");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMissingPassword() {
        SolarisConfiguration config = getConfig();
        config.setPassword(null);
        config.validate();
        Assert.fail("Configuration allowed a null password.");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMissingHostname() {
        SolarisConfiguration config = getConfig();
        config.setHostNameOrIpAddr(null);
        config.validate();
        Assert.fail("Configuration allowed a null hostname.");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMissingPort() {
        SolarisConfiguration config = getConfig();
        config.setPort(null);
        config.validate();
        Assert.fail("Configuration allowed a null port.");
    }
    
    /* ************* AUXILIARY METHODS *********** */

    /**
     * create configuration based on Unit test account
     * @return
     */
    private SolarisConfiguration getConfig() {
        return config;
    }
    
    /** test connection to the configuration given by default credentials (build.groovy) */
    private void testGoodConnection() {
        SolarisConfiguration config = getConfig();
        SolarisConnector connector = SolarisTestCommon.createConnector(config);
        try {
            connector.checkAlive();
        } finally {
            connector.dispose();
        }
    }
    
    /** elementary schema test */
    private void basicSchemaTest() {
        SolarisConnector connector = SolarisTestCommon.createConnector(getConfig());
        Schema schema = connector.schema();
        Assert.assertNotNull(schema);
    }
    
        
}
