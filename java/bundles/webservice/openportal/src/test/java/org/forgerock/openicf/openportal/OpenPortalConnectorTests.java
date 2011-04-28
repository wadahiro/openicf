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
package org.forgerock.openicf.openportal;

import java.net.MalformedURLException;
import java.util.*;


import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.*;
import org.identityconnectors.test.common.TestHelpers;
import org.identityconnectors.test.common.PropertyBag;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Attempts to test the {@link OpenPortalConnector} with the framework.
 *
 * @author $author$
 * @version $Revision$ $Date$
 */
public class OpenPortalConnectorTests {

    /*
    * Example test properties.
    * See the Javadoc of the TestHelpers class for the location of the public and private configuration files.
    */
    private static final PropertyBag properties = TestHelpers.getProperties(OpenPortalConnector.class);
    // Host is a public property read from public configuration file
    private static final String HOST = properties.getStringProperty("configuration.host");
    // Login and password are private properties read from private configuration file 
    private static final String REMOTE_USER  = properties.getStringProperty("configuration.remoteUser");
    private static final GuardedString PASSWORD = properties.getProperty("configuration.password", GuardedString.class);
    //private static final int PORT = properties.getProperty("configuration.port",Integer.class);
    private static final String WSDLFILE = properties.getStringProperty("configuration.wsdlFile");
    //set up logging
    private static final Log log = Log.getLog(OpenPortalConnectorTests.class);

    private static OpenPortalConnector connector;
    private static OpenPortalConfiguration config;
    @BeforeClass
    public static void setUp() {
        

        /*Assert.assertNotNull(HOST);
        Assert.assertNotNull(REMOTE_USER);
        Assert.assertNotNull(PASSWORD);
*/
        config = new OpenPortalConfiguration();
        //config.setSsl(true);
        config.setHost(HOST);
        config.setRemoteUser(REMOTE_USER);
        config.setPassword(PASSWORD);
        //config.setPort(PORT);
        config.setPort(properties.getProperty("configuration.port", Integer.class));

        connector = new OpenPortalConnector();
        connector.init(config);
        //
        //other setup work to do before running tests
        //
    }

    @AfterClass
    public static void tearDown() {
        //
        //clean up resources
        //
    }

    @Test
    public void validateConfigWithSSL() throws MalformedURLException {
        log.info("Checking validation with SSL..");
        //You can use TestHelpers to do some of the boilerplate work in running a search
        //TestHelpers.search(theConnector, ObjectClass.ACCOUNT, filter, handler, null);
        //connector.test();
        //System.out.println("" + config.validate());
        config.setSsl(true);
        config.setWsdlFile(null);
        config.validate();

        System.out.println(config.getUrl());
    }
    @Test
    public void validateConfigWithOutSSL() throws MalformedURLException {
        log.info("Checking validation without SSL..");
        //You can use TestHelpers to do some of the boilerplate work in running a search
        //TestHelpers.search(theConnector, ObjectClass.ACCOUNT, filter, handler, null);
        
        config.setWsdlFile(null);
        config.validate();
        System.out.println(config.getUrl());
    }
    @Test
    public void getWSDLFile() throws MalformedURLException {
        log.info("Getting WSDLFile...");
        config.validate();
        config.setWsdlFile(WSDLFILE);
        System.out.println(config.getUrl());
        //Another example using TestHelpers
        //List<ConnectorObject> results = TestHelpers.searchToList(theConnector, ObjectClass.GROUP, filter);
    }
    /*
    protected ConnectorFacade getFacade(OpenPortalConfiguration config) {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();
        // **test only**
        APIConfiguration impl = TestHelpers.createTestConfiguration(OpenPortalConnector.class, config);
        return factory.newInstance(impl);
    }*/
}
