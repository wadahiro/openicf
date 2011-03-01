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
import org.identityconnectors.framework.common.exceptions.InvalidPasswordException;

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

    public void init(Configuration configuration) {
        final String method = "init";
        log.info("Entry {0}", method);

        Assertions.nullCheck(configuration, "cfg");
        
        this.config = (XMLConfiguration) configuration;
        this.schemaParser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        this.xmlHandler = new XMLHandlerImpl(config, schema(), schemaParser.getXsdSchema());

        log.info("XMLConnector initialized");
        log.info("Exit {0}", method);
    }

    public void dispose() {}

    public void checkAlive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uid authenticate(final ObjectClass objectClass, final String username, final GuardedString password, final OperationOptions options) {
        final String method = "authenticate";
        log.info("Entry {0}", method);

        Assertions.nullCheck(objectClass, "objectClass");
        Assertions.nullCheck(username, "username");
        Assertions.nullCheck(password, "password");
        
        Assertions.blankCheck(username, "username");

        Uid uid = xmlHandler.authenticate(username, password);

        if(uid == null){
            throw new InvalidPasswordException("Invalid password for user: " + username);
        }

        log.info("Exit {0}", method);
        return uid;
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
                
        Collection<ConnectorObject> hits = xmlHandler.search(queryBuilder.toString(), objClass);

        for (ConnectorObject hit : hits) {
            handler.handle(hit);
        }

        log.info("Exit {0}", method);
    }

    @Override
    public void test() {
        final String startErrorMessage = "File does not exists at filepath " ;
        final String method = "test";
        log.info("Entry {0}", method);

        Assertions.nullCheck(config, "config");
        Assertions.nullCheck(xmlHandler, "xmlHandler");
        Assertions.nullCheck(schemaParser, "schemaParser");

        File fileXml = new File(config.getXmlFilePath());
        if (!fileXml.exists()) {
            throw new IllegalArgumentException(startErrorMessage + config.getXmlFilePath() );
        }

        File fileXsd = new File(config.getXsdFilePath());
        if (!fileXsd.exists()) {
            throw new IllegalArgumentException(startErrorMessage + config.getXsdFilePath());
        }

        if (config.getXsdIcfFilePath() != null) {
            File fileXsdIcf = new File(config.getXsdFilePath());
            if (!fileXsdIcf.exists()) {
                throw new IllegalArgumentException(startErrorMessage + config.getXsdIcfFilePath());
            }
        }
        log.info("Exit {0}", method);
    }
}
