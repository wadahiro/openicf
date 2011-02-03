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

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.security.*;
import org.identityconnectors.framework.spi.*;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.filter.Filter;

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
public class XMLConnector implements PoolableConnector, AuthenticateOp, CreateOp, DeleteOp, SearchOp<String>, SchemaOp, ScriptOnConnectorOp, ScriptOnResourceOp, SyncOp, TestOp, UpdateOp
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
        this.xmlHandler = new XMLHandlerImpl(config.getXmlFilePath(), null);
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
    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes, final OperationOptions options) {

        final String method = "create";
        log.info("Entry {0}", method);

        if (objClass == null) {
            throw new IllegalArgumentException("msg"); // TODO: Add exception message
        }

        if (attributes == null || attributes.size() == 0) {
            throw new IllegalArgumentException("msg"); // TODO: Add exception message
        }




        Uid uid = null;
        Map<String, Attribute> attrMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
        Name name = AttributeUtil.getNameFromAttributes(attributes);

        log.info("create({0},{1})", objClass.getObjectClassValue(), name.getNameValue());

        if (objClass.is(ObjectClass.ACCOUNT_NAME)) {
            //connection.getXmlHandler().createEntry();
        }
        else if (objClass.is(ObjectClass.GROUP_NAME)) {
            //connection.getXmlHandler().createGroup(null);
        }
        else
            throw new IllegalArgumentException("Unsupported Object Class=" + objClass.getObjectClassValue());

        log.info("Exit {0}", method);

        return new Uid(name.getNameValue());
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
    @Override
    public FilterTranslator<String> createFilterTranslator(ObjectClass objClass, OperationOptions options) { 
            return new XMLFilterTranslator();
    }
    
    /**
     * {@inheritDoc}
     */
    /*
     * oclass - The object class for the search. Will never be null.
        query - The native query to run. A value of null means "return every instance of the given object class".
        handler - Results should be returned to this handler
        options - Additional options that impact the way this operation is run. If the caller passes null,
     the framework will convert this into an empty set of options, so SPI need not guard against options being null.
     */
    public void executeQuery(ObjectClass objClass, String query, ResultsHandler handler, OperationOptions options) {

        // wich attributes to include in the query
        Set<String> attributesToGet = null;

        // SEARCH FOR __ACCOUNT__
        if (objClass.is(ObjectClass.ACCOUNT_NAME)) {
            
        }
        

        Collection<ConnectorObject> hits = xmlHandler.search("");
        ConnectorObjectBuilder bld = new ConnectorObjectBuilder();
        for (ConnectorObject hit : hits) {
            bld.add(hit);
        }
        // return to handler
        handler.handle(bld.build());
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