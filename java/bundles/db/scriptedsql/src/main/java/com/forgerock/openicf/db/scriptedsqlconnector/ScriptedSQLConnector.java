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
package com.forgerock.openicf.db.scriptedsqlconnector;

import java.util.*;

import org.identityconnectors.common.security.*;
import org.identityconnectors.framework.spi.*;
import org.identityconnectors.framework.spi.operations.*;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;

import org.identityconnectors.common.security.GuardedString.Accessor;
import org.identityconnectors.common.script.ScriptExecutor;
import org.identityconnectors.common.script.ScriptExecutorFactory;

// import org.identityconnectors.dbcommon.DatabaseFilterTranslator;
/**
 * Main implementation of the ScriptedJDBC Connector
 * 
 * @author gael
 * @version 1.0
 * @since 1.0
 */
@ConnectorClass(displayNameKey = "ScriptedSQL", configurationClass = ScriptedSQLConfiguration.class)
public class ScriptedSQLConnector implements PoolableConnector, AuthenticateOp, ResolveUsernameOp, CreateOp, DeleteOp, SchemaOp, SearchOp<Map>, SyncOp, TestOp, UpdateAttributeValuesOp {

    /**
     * Setup logging for the {@link ScriptedSQLConnector}.
     */
    private static final Log log = Log.getLog(ScriptedSQLConnector.class);
    /**
     * Place holder for the Connection created in the init method
     */
    private ScriptedSQLConnection connection;
    /**
     * Place holder for the {@link Configuration} passed into the init() method
     * {@link ScriptedSQLConnector#init}.
     */
    private ScriptedSQLConfiguration config;
    private ScriptExecutorFactory factory = null;
    private ScriptExecutor createExecutor = null;
    private ScriptExecutor updateExecutor = null;
    private ScriptExecutor deleteExecutor = null;
    private ScriptExecutor searchExecutor = null;
    private ScriptExecutor testExecutor = null;

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
        this.config = (ScriptedSQLConfiguration) cfg;
        this.connection = new ScriptedSQLConnection(this.config);
        this.factory = ScriptExecutorFactory.newInstance(this.config.getScriptingLanguage());

        // We need an executor for each and every script. At least, they'll get
        // evaluated and compiled.
        try {
            if (config.getCreateScript() != null && config.getCreateScript().length() > 0) {
                createExecutor = factory.newScriptExecutor(getClass().getClassLoader(), config.getCreateScript(), true);
            }
        } catch (Exception e) {
            throw new ConnectorException("Create script parse error", e);
        }
        try {
            if (config.getUpdateScript() != null && config.getUpdateScript().length() > 0) {
                updateExecutor = factory.newScriptExecutor(getClass().getClassLoader(), config.getUpdateScript(), true);
            }
        } catch (Exception e) {
            throw new ConnectorException("Update script parse error", e);
        }
        try {
            if (config.getDeleteScript() != null && config.getDeleteScript().length() > 0) {
                deleteExecutor = factory.newScriptExecutor(getClass().getClassLoader(), config.getDeleteScript(), true);
            }
        } catch (Exception e) {
            throw new ConnectorException("Delete script parse error", e);
        }
        try {
            if (config.getSearchScript() != null && config.getSearchScript().length() > 0) {
                searchExecutor = factory.newScriptExecutor(getClass().getClassLoader(), config.getSearchScript(), true);
            }
        } catch (Exception e) {
            throw new ConnectorException("Search script parse error", e);
        }
        try {
            if (config.getTestScript() != null && config.getTestScript().length() > 0) {
                testExecutor = factory.newScriptExecutor(getClass().getClassLoader(), config.getTestScript(), true);
            }
        } catch (Exception e) {
            throw new ConnectorException("Test script parse error", e);
        }
    }

    /**
     * Disposes of the {@link ScriptedSQLConnector}'s resources.
     * 
     * @see Connector#dispose()
     */
    public void dispose() {
        config = null;
        if (connection != null) {
            connection.dispose();
            connection = null;
        }
    }

    public void checkAlive() {
        connection.test();
    }

    /******************
     * SPI Operations
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
    public Uid resolveUsername(final ObjectClass objectClass, final String username, final OperationOptions options) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Uid create(final ObjectClass objClass, final Set<Attribute> attrs, final OperationOptions options) {
        if (createExecutor != null) {
            if (objClass == null) {
                throw new IllegalArgumentException(config.getMessage("MSG_ACCOUNT_OBJECT_CLASS_REQUIRED"));
            }
            log.ok("Object class ok:" + objClass.getObjectClassValue());
            if (attrs == null || attrs.isEmpty()) {
                throw new IllegalArgumentException(config.getMessage("MSG_INVALID_ATTRIBUTE_SET"));
            }
            log.ok("Attribute set is not empty");

            final Map<String, Object> arguments = new HashMap<String, Object>();

            arguments.put("connection", connection.getSqlConnection());
            arguments.put("action", "CREATE");
            arguments.put("log", log);
            arguments.put("objectClass", objClass.getObjectClassValue());
            arguments.put("options", options.getOptions());

            Map<String, List> attrMap = new HashMap();
            for (Attribute attr : attrs) {
                attrMap.put(attr.getName(), attr.getValue());
            }
            arguments.put("attributes", attrMap);

            // Password - if allowed we provide it in clear, can't be arsed to do that in the script
            if (config.getClearTextPasswordToScript()) {
                GuardedString gpasswd = AttributeUtil.getPasswordValue(attrs);
                if (gpasswd != null) {
                    gpasswd.access(new Accessor() {

                        public void access(char[] clearChars) {
                            arguments.put("password", clearChars.toString());
                        }
                    });
                } else {
                    arguments.put("password", null);
                }
            }

            try {
                createExecutor.execute(arguments);
                log.ok("create ok");
            } catch (Exception e) {
                throw new ConnectorException("create script error", e);
            }
        }
        return new Uid(AttributeUtil.getNameFromAttributes(attrs).getNameValue());
    }

    /**
     * {@inheritDoc}
     */
    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes, OperationOptions options) {
        if (updateExecutor != null) {
            return genericUpdate("UPDATE", objClass, uid, replaceAttributes, options);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Uid addAttributeValues(ObjectClass objClass, Uid uid, Set<Attribute> valuesToAdd, OperationOptions options) {
        if (updateExecutor != null) {
            return genericUpdate("ADD_ATTRIBUTE_VALUES", objClass, uid, valuesToAdd, options);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public Uid removeAttributeValues(ObjectClass objClass, Uid uid, Set<Attribute> valuesToRemove, OperationOptions options) {
        if (updateExecutor != null) {
            return genericUpdate("REMOVE_ATTRIBUTE_VALUES", objClass, uid, valuesToRemove, options);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final ObjectClass objClass, final Uid uid, final OperationOptions options) {
        if (deleteExecutor != null) {
            if (objClass == null) {
                throw new IllegalArgumentException(config.getMessage("MSG_ACCOUNT_OBJECT_CLASS_REQUIRED"));
            }
            log.ok("Object class ok:" + objClass.getObjectClassValue());

            if (uid == null || (uid.getUidValue() == null)) {
                throw new IllegalArgumentException(config.getMessage("MSG_UID_BLANK"));
            }
            final String id = uid.getUidValue();
            log.ok("The Uid is present");

            final Map<String, Object> arguments = new HashMap<String, Object>();

            arguments.put("connection", connection.getSqlConnection());
            arguments.put("action", "DELETE");
            arguments.put("log", log);
            arguments.put("objectClass", objClass.getObjectClassValue());
            arguments.put("uid", id);
            arguments.put("options", options.getOptions());

            try {
                deleteExecutor.execute(arguments);
                log.ok("delete ok");
            } catch (Exception e) {
                throw new ConnectorException("delete script error", e);
            } finally {
                // clean up.. ??? should we close?
            }
        }
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
    public FilterTranslator<Map> createFilterTranslator(ObjectClass objClass, OperationOptions options) {
        log.info("check the ObjectClass");
        if (objClass == null) {
            throw new IllegalArgumentException(config.getMessage("MSG_ACCOUNT_OBJECT_CLASS_REQUIRED"));
        }
        log.ok("The ObjectClass is ok");
        return new ScriptedSQLFilterTranslator();
    }

    /**
     * {@inheritDoc}
     */
    public void executeQuery(ObjectClass objClass, Map query, ResultsHandler handler, OperationOptions options) {
        log.info("check the ObjectClass");
        if (objClass == null) {
            throw new IllegalArgumentException(config.getMessage("MSG_ACCOUNT_OBJECT_CLASS_REQUIRED"));
        }
        log.ok("The ObjectClass is ok");
        if (handler == null) {
            throw new IllegalArgumentException(config.getMessage("MSG_RESULT_HANDLER_NULL"));
        }
        log.ok("The result handler is ok");
        if (searchExecutor != null) {
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put("connection", connection.getSqlConnection());
            arguments.put("objectClass", objClass.getObjectClassValue());
            arguments.put("action", "SEARCH");
            arguments.put("log", log);
            arguments.put("options", options.getOptions());
            arguments.put("query", query);
            try {
                // We expect the script to return a list of Maps
                List<Map> results = (List<Map>) searchExecutor.execute(arguments);
                log.ok("test ok");
                processResults(objClass, results, handler);
            } catch (Exception e) {
                throw new ConnectorException("search script error", e);
            } finally {
                // clean up.. ??? should we close?
            }
        }
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
        try {
            if (testExecutor != null) {
                Map<String, Object> arguments = new HashMap<String, Object>();
                arguments.put("connection", connection.getSqlConnection());
                arguments.put("action", "TEST");
                arguments.put("log", log);
                // TODO: Do we need to provide some options?
                // Maybe a good idea to pass JDBC config in case
                // connection needs to be worked in script

                // what do we return? Nothing... it's a test.
                // Test should throw an exception if needed
                testExecutor.execute(arguments);
                log.ok("test ok");
            }
        } catch (Exception e) {
            throw new ConnectorException("test script error", e);
        }
    }

    /*******************
     * Private methods
     *******************/
    private void processResults(ObjectClass objClass, List<Map> results, ResultsHandler handler) {

        // Let's iterate over the results:
        for (Map<String, Object> result : results) {
            ConnectorObjectBuilder cobld = new ConnectorObjectBuilder();
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                final String attrName = entry.getKey();
                final Object attrValue = entry.getValue();
                // Special first
                if (attrName.equalsIgnoreCase("uid")) {
                    if (attrValue == null) {
                        log.error("Name cannot be null.");
                        String msg = "Name cannot be null.";
                        throw new IllegalArgumentException(msg);
                    }
                    cobld.setName(attrValue.toString());
                    cobld.setUid(attrValue.toString());
                } else if (attrName.equalsIgnoreCase("password")) {
                    // is there a chance we fetch password from search?
                } else {
                    if (attrValue != null) {
                        cobld.addAttribute(AttributeBuilder.build(attrName, attrValue));
                    } else {
                        cobld.addAttribute(AttributeBuilder.build(attrName));
                    }
                }
            }
            cobld.setObjectClass(objClass);
            handler.handle(cobld.build());
            log.ok("ConnectorObject is builded");
        }
    }

    private Uid genericUpdate(String method, ObjectClass objClass, Uid uid, Set<Attribute> attrs, OperationOptions options) {
        if (objClass == null) {
            throw new IllegalArgumentException(config.getMessage("MSG_ACCOUNT_OBJECT_CLASS_REQUIRED"));
        }
        log.ok("Object class ok:" + objClass.getObjectClassValue());

        if (attrs == null || attrs.isEmpty()) {
            throw new IllegalArgumentException(config.getMessage("MSG_INVALID_ATTRIBUTE_SET"));
        }
        log.ok("Attribute set is not empty");

        if (uid == null || (uid.getUidValue() == null)) {
            throw new IllegalArgumentException(config.getMessage("MSG_UID_BLANK"));
        }
        final String id = uid.getUidValue();
        log.ok("The Uid is present");

        final Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("connection", connection.getSqlConnection());
        arguments.put("action", method);
        arguments.put("log", log);
        arguments.put("objectClass", objClass.getObjectClassValue());
        arguments.put("uid", id);
        arguments.put("options", options.getOptions());

        Map<String, List> attrMap = new HashMap();
        for (Attribute attr : attrs) {
            if (OperationalAttributes.isOperationalAttribute(attr)) {
                if (method.equalsIgnoreCase("UPDATE")) {
                    attrMap.put(attr.getName(), attr.getValue());
                }
            } else {
                attrMap.put(attr.getName(), attr.getValue());
            }
        }
        arguments.put("attributes", attrMap);

        // Do we need to update the password?
        if (config.getClearTextPasswordToScript() && method.equalsIgnoreCase("UPDATE")) {
            GuardedString gpasswd = AttributeUtil.getPasswordValue(attrs);
            if (gpasswd != null) {
                gpasswd.access(new Accessor() {

                    public void access(char[] clearChars) {
                        arguments.put("password", clearChars.toString());
                    }
                });
            } else {
                arguments.put("password", null);
            }
        }
        try {
            updateExecutor.execute(arguments);
            log.ok("update (" + method + ") ok");
        } catch (Exception e) {
            throw new ConnectorException("update(" + method + ") script error", e);
        }
        return new Uid(AttributeUtil.getNameFromAttributes(attrs).getNameValue());
    }
}
