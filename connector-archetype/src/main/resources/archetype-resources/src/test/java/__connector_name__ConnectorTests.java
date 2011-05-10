#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ${symbol_dollar}Id${symbol_dollar}
 */
package ${package};

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
 * Attempts to test the {@link ${connector_name}Connector} with the framework.
 *
 * @author ${symbol_dollar}author${symbol_dollar}
 * @version ${symbol_dollar}Revision${symbol_dollar} ${symbol_dollar}Date${symbol_dollar}
 */
public class ${connector_name}ConnectorTests {

    /*
    * Example test properties.
    * See the Javadoc of the TestHelpers class for the location of the public and private configuration files.
    */
    private static final PropertyBag properties = TestHelpers.getProperties(${connector_name}Connector.class);
    // Host is a public property read from public configuration file
    private static final String HOST = properties.getStringProperty("configuration.host");
    // Login and password are private properties read from private configuration file 
    private static final String REMOTE_USER = properties.getStringProperty("configuration.remoteUser");
    private static final GuardedString PASSWORD = properties.getProperty("configuration.password", GuardedString.class);

    //set up logging
    private static final Log log = Log.getLog(${connector_name}ConnectorTests.class);

    @BeforeClass
    public static void setUp() {
        Assert.assertNotNull(HOST);
        Assert.assertNotNull(REMOTE_USER);
        Assert.assertNotNull(PASSWORD);

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
    public void exampleTest1() {
        log.info("Running Test 1...");
        //You can use TestHelpers to do some of the boilerplate work in running a search
        //TestHelpers.search(theConnector, ObjectClass.ACCOUNT, filter, handler, null);
    }

    @Test
    public void exampleTest2() {
        log.info("Running Test 2...");
        //Another example using TestHelpers
        //List<ConnectorObject> results = TestHelpers.searchToList(theConnector, ObjectClass.GROUP, filter);
    }

    protected ConnectorFacade getFacade(${connector_name}Configuration config) {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();
        // **test only**
        APIConfiguration impl = TestHelpers.createTestConfiguration(${connector_name}Connector.class, config);
        return factory.newInstance(impl);
    }
}
