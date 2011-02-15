/*
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
 */
package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.util.*;

import org.identityconnectors.common.Assertions;
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
@ConnectorClass(displayNameKey = "XML",
configurationClass = XMLConfiguration.class)
public class XMLConnector implements PoolableConnector, AuthenticateOp, CreateOp, DeleteOp, SearchOp<IQuery>, SchemaOp, TestOp, UpdateOp {

    /**
     * Setup logging for the {@link XMLConnector}.
     */
    private static final Log log = Log.getLog(XMLConnector.class);
    private XMLHandler xmlHandler;
    private XMLConfiguration config;
    private SchemaParser schemaParser;

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
        this.schemaParser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        this.xmlHandler = new XMLHandlerImpl(config.getXmlFilePath(), schema(), schemaParser.getXsdSchema());
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

    public Uid authenticate(final ObjectClass objectClass, final String username, final GuardedString password, final OperationOptions options) {
        throw new UnsupportedOperationException();
    }

    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes, final OperationOptions options) {

        final String method = "create";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objClass, "objectClass");
        Assertions.nullCheck(attributes, "attributes");

        Uid uid = xmlHandler.create(objClass, attributes);

        log.info("Exit {0}", method);

        return uid;
    }

    public void delete(final ObjectClass objClass, final Uid uid, final OperationOptions options) {

        final String method = "delete";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objClass, "objectClass");
        Assertions.nullCheck(uid, "attributes");

        xmlHandler.delete(objClass, uid);

        log.info("Exit {0}", method);
    }

    public Schema schema() {
        return schemaParser.parseSchema();
    }

    @Override
    public FilterTranslator<IQuery> createFilterTranslator(ObjectClass objClass, OperationOptions options) {
        return new XMLFilterTranslator();
    }

    /*
     * oclass - The object class for the search. Will never be null.
    query - The native query to run. A value of null means "return every instance of the given object class".
    handler - Results should be returned to this handler
    options - Additional options that impact the way this operation is run. If the caller passes null,
    the framework will convert this into an empty set of options, so SPI need not guard against options being null.
     *
     *
     * The framework conta
     */
    public void executeQuery(ObjectClass objClass, IQuery query, ResultsHandler handler, OperationOptions options) {
        QueryBuilder queryBuilder = new QueryBuilder(query, objClass);
        Collection<ConnectorObject> hits = xmlHandler.search(queryBuilder.toString(), objClass);
        for (ConnectorObject hit : hits) {
            handler.handle(hit);
        }
    }

    public void test() {
        final String method = "test";
        log.info("Entry {0}", method);

        Assertions.nullCheck(config, "config");
        Assertions.nullCheck(xmlHandler, "xmlHandler");
        Assertions.nullCheck(schemaParser, "schemaParser");

        log.info("Exit {0}", method);
    }

    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes, OperationOptions options) {
        final String method = "update";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objClass, "objectClass");
        Assertions.nullCheck(uid, "attributes");

        Uid returnUid = xmlHandler.update(objClass, uid, replaceAttributes);

        log.info("Exit {0}", method);
        
        return returnUid;
    }
}
