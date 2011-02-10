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
package com.forgerock.openicf;

import java.util.*;

import org.identityconnectors.common.security.*;
import org.identityconnectors.framework.spi.*;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;

/**
 * Main implementation of the Liferay Connector
 * 
 * @author johannes
 * @version 1.0
 * @since 1.0
 */
@ConnectorClass(
    displayNameKey = "Liferay",
    configurationClass = LiferayConfiguration.class)
public class LiferayConnector implements PoolableConnector, CreateOp, DeleteOp, SchemaOp, TestOp, UpdateOp
{
    /**
     * Setup logging for the {@link LiferayConnector}.
     */
    private static final Log log = Log.getLog(LiferayConnector.class);

    /**
     * Place holder for the Connection created in the init method
     */
    private LiferayConnection connection;

    /**
     * Place holder for the {@link Configuration} passed into the init() method
     * {@link LiferayConnector#init}.
     */
    private LiferayConfiguration config;

    /**
     * Gets the Configuration context for this connector.
     */
    public Configuration getConfiguration() {
        return this.config;
    }

    /**
     * Callback method to receive the {@link Configuration}.
     * 
     * @see Connector#init
     */
    public void init(Configuration cfg) {
        this.config = (LiferayConfiguration) cfg;
        this.connection = new LiferayConnection(this.config);
    }

    /**
     * Disposes of the {@link LiferayConnector}'s resources.
     * 
     * @see Connector#dispose()
     */
    public void dispose() {
        config = null;
        if ( connection != null ) {
            connection.dispose();
            connection = null;
        }
    }

    public void checkAlive() {
        connection.test();
    }

    /******************
     * SPI Operations
     * 
     * Implement the following operations using the contract and
     * description found in the Javadoc for these methods.
     ******************/     
    
    
    
    /**
     * {@inheritDoc}
     */
    public Uid create(final ObjectClass objClass, final Set<Attribute> attrs, final OperationOptions options) { 
        throw new UnsupportedOperationException();
    } 
       
    
    /**
     * {@inheritDoc}
     */ 
    public void delete(final ObjectClass objClass, final Uid uid, final OperationOptions options) { 
        throw new UnsupportedOperationException();
    } 
    
    
    /**
     * {@inheritDoc}
     */
    public Schema schema() {
        throw new UnsupportedOperationException();
    } 
    
    
    
    
    
    
    
    
          
    
    /**
     * {@inheritDoc}
     */   
    public void test() {
       throw new UnsupportedOperationException();
    } 
     
    
    /**
     * {@inheritDoc}
     */
    public Uid update(ObjectClass objclass,
            Uid uid,
            Set<Attribute> replaceAttributes,
            OperationOptions options) {
        throw new UnsupportedOperationException();
    }  
    
    
}
