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
 * $Id$
 */
package org.forgerock.openicf.openportal;

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
 * Main implementation of the OpenPortal Connector
 *
 * @author $author$
 * @version $Revision$ $Date$
 */
@ConnectorClass(
        displayNameKey = "OpenPortal",
        configurationClass = OpenPortalConfiguration.class)
public class OpenPortalConnector implements PoolableConnector, AuthenticateOp, ResolveUsernameOp, CreateOp, DeleteOp, SchemaOp, ScriptOnConnectorOp, ScriptOnResourceOp, SearchOp<String>, SyncOp, TestOp, UpdateAttributeValuesOp {
    /**
     * Setup logging for the {@link OpenPortalConnector}.
     */
    private static final Log log = Log.getLog(OpenPortalConnector.class);

    /**
     * Place holder for the Connection created in the init method
     */
    private OpenPortalConnection connection;

    /**
     * Place holder for the {@link Configuration} passed into the init() method
     * {@link OpenPortalConnector#init(org.identityconnectors.framework.spi.Configuration)}.
     */
    private OpenPortalConfiguration config;

    /**
     * Gets the Configuration context for this connector.
     */
    public Configuration getConfiguration() {
        return this.config;
    }

    /**
     * Callback method to receive the {@link Configuration}.
     *
     * @see Connector#init(org.identityconnectors.framework.spi.Configuration)
     */
    public void init(Configuration cfg) {
        this.config = (OpenPortalConfiguration) cfg;
        this.connection = new OpenPortalConnection(this.config);
    }

    /**
     * Disposes of the {@link OpenPortalConnector}'s resources.
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
    public Uid resolveUsername(final ObjectClass objectClass, final String username, final OperationOptions options) {
        throw new UnsupportedOperationException();
    }


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


    /**
     * {@inheritDoc}
     */
    public Uid addAttributeValues(ObjectClass objclass,
                                  Uid uid,
                                  Set<Attribute> valuesToAdd,
                                  OperationOptions options) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Uid removeAttributeValues(ObjectClass objclass,
                                     Uid uid,
                                     Set<Attribute> valuesToRemove,
                                     OperationOptions options) {
        throw new UnsupportedOperationException();
    }
}
