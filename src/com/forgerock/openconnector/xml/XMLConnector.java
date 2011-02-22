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
import java.io.File;
import java.util.*;

import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.security.*;
import org.identityconnectors.framework.spi.*;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.common.logging.Log;

@ConnectorClass(displayNameKey = "XML", configurationClass = XMLConfiguration.class)
public class XMLConnector implements PoolableConnector, AuthenticateOp, CreateOp, DeleteOp, SearchOp<IQuery>, SchemaOp, TestOp, UpdateOp {

    private static final Log log = Log.getLog(XMLConnector.class);
    private XMLHandler xmlHandler;
    private XMLConfiguration config;
    private SchemaParser schemaParser;

    private static volatile int invokers = 0;

    public Configuration getConfiguration() {
        return this.config;
    }

    public void init(Configuration cfg) {
        final String method = "init";
        log.info("Entry {0}", method);
        Assertions.nullCheck(cfg, "cfg");
        this.config = (XMLConfiguration) cfg;
        this.schemaParser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        this.xmlHandler = new XMLHandlerImpl(config, schema(), schemaParser.getXsdSchema());
        log.info("XMLConnector initialized");
        log.info("Exit {0}", method);
    }

    public void dispose() {
    }

    public void checkAlive() {
    }

    @Override
    public Uid authenticate(final ObjectClass objectClass, final String username, final GuardedString password, final OperationOptions options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes, final OperationOptions options) {

        final String method = "create";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objClass, "objectClass");
        Assertions.nullCheck(attributes, "attributes");

        Uid returnUid = xmlHandler.create(objClass, attributes);

        log.info("Exit {0}", method);

        return returnUid;
    }

    @Override
    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes, OperationOptions options) {
        final String method = "update";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objClass, "objectClass");
        Assertions.nullCheck(uid, "attributes");

        Uid returnUid = xmlHandler.update(objClass, uid, replaceAttributes);

        log.info("Exit {0}", method);

        return returnUid;
    }

    @Override
    public void delete(final ObjectClass objClass, final Uid uid, final OperationOptions options) {

        final String method = "delete";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objClass, "objectClass");
        Assertions.nullCheck(uid, "uid");

        xmlHandler.delete(objClass, uid);

        log.info("Exit {0}", method);
    }

    @Override
    public Schema schema() {
        return schemaParser.parseSchema();
    }

    @Override
    public FilterTranslator<IQuery> createFilterTranslator(ObjectClass objClass, OperationOptions options) {
        return new XMLFilterTranslator();
    }

    @Override
    public void executeQuery(ObjectClass objClass, IQuery query, ResultsHandler handler, OperationOptions options) {
        final String method = "executeQuery";
        log.info("Entry {0}", method);
        QueryBuilder queryBuilder = new QueryBuilder(query, objClass);
        
        if (log.isInfo()) {
            log.info("Performing query: {0}", queryBuilder.toString());
        }
        
        Collection<ConnectorObject> hits = xmlHandler.search(queryBuilder.toString(), objClass);

        if (log.isInfo()) {
            log.info("Number of hits: {0}", hits.size());
            log.info("ConnectionObjects found: {0}", hits.toString());
        }
        

        for (ConnectorObject hit : hits) {
            handler.handle(hit);
        }

        log.info("Exit {0}", method);
    }

    @Override
    public void test() {
        final String method = "test";
        log.info("Entry {0}", method);

        Assertions.nullCheck(config, "config");
        Assertions.nullCheck(xmlHandler, "xmlHandler");
        Assertions.nullCheck(schemaParser, "schemaParser");

        File fileXml = new File(config.getXmlFilePath());
        if (!fileXml.exists()) {
            throw new IllegalArgumentException("File at filepath " + config.getXmlFilePath() + " does not exists");
        }

        File fileXsd = new File(config.getXsdFilePath());
        if (!fileXsd.exists()) {
            throw new IllegalArgumentException("File at filepath " + config.getXsdFilePath() + " does not exists");
        }

        if (config.getXsdIcfFilePath() != null) {
            File fileXsdIcf = new File(config.getXsdFilePath());
            if (!fileXsdIcf.exists()) {
                throw new IllegalArgumentException("File at filepath " + config.getXsdIcfFilePath() + " does not exists");
            }
        }


        log.info("Exit {0}", method);
    }
}
