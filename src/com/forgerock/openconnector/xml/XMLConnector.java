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
package com.forgerock.openconnector.xml;

import java.util.*;

import org.identityconnectors.common.security.*;
import org.identityconnectors.framework.spi.*;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.common.logging.Log;

/**
 * Main implementation of the XML Connector
 * 
 * @author slogum
 * @version 1.0
 * @since 1.0
 */
@ConnectorClass(
    displayNameKey = "XML",
    configurationClass = XMLConfiguration.class)
public class XMLConnector implements PoolableConnector, AuthenticateOp, CreateOp, DeleteOp, SchemaOp, ScriptOnConnectorOp, ScriptOnResourceOp, SearchOp<String>, SyncOp, TestOp, UpdateOp
{
    /**
     * Setup logging for the {@link XMLConnector}.
     */
    private static final Log log = Log.getLog(XMLConnector.class);

    private XMLHandler xmlHandler;
    
    private XMLConfiguration config;

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
        this.config = (XMLConfiguration) cfg;
        this.xmlHandler = new XMLHandlerImpl(config.getXmlFilePath());
    }

    /**
     * Disposes of the {@link XMLConnector}'s resources.
     * 
     * @see Connector#dispose()
     */
    public void dispose() {
        
    }

    public void checkAlive() {
        
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
    public Uid authenticate(final ObjectClass objectClass, final String username, final GuardedString password, final OperationOptions options) { 
       throw new UnsupportedOperationException();
    } 
    
    
    /**
     * {@inheritDoc}
     */
    public Uid create(final ObjectClass objClass, final Set<Attribute> attrs, final OperationOptions options) {

        for (Attribute attribute : attrs) {
            
        }


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
    public Object runScriptOnConnector(ScriptContext request, OperationOptions options) { 
        throw new UnsupportedOperationException();
    } 
    
    
    /**
     * {@inheritDoc}
     */     
    public Object runScriptOnResource(ScriptContext request, OperationOptions options) { 
        throw new UnsupportedOperationException();
    } 
    
    
    /**
     * {@inheritDoc}
     */     
    public FilterTranslator<String> createFilterTranslator(ObjectClass objClass, OperationOptions options) { 
        throw new UnsupportedOperationException();
    }
    
    /**
     * {@inheritDoc}
     */
    public void executeQuery(ObjectClass objClass, String query, ResultsHandler handler, OperationOptions options) {
        throw new UnsupportedOperationException();
    } 
    
    
    /**
     * {@inheritDoc}
     */   
    public void sync(ObjectClass objClass, SyncToken token, SyncResultsHandler handler, final OperationOptions options) {
        throw new UnsupportedOperationException();
    } 
    /**
     * {@inheritDoc}
     */   
    public SyncToken getLatestSyncToken(ObjectClass objectClass) {
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
