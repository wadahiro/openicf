/*
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * U.S. Government Rights - Commercial software. Government users 
 * are subject to the Sun Microsystems, Inc. standard license agreement
 * and applicable provisions of the FAR and its supplements.
 * 
 * Use is subject to license terms.
 * 
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and Project Identity 
 * Connectors are trademarks or registered trademarks of Sun 
 * Microsystems, Inc. or its subsidiaries in the U.S. and other
 * countries.
 * 
 * UNIX is a registered trademark in the U.S. and other countries,
 * exclusively licensed through X/Open Company, Ltd. 
 * 
 * -----------
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved. 
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License(CDDL) (the License).  You may not use this file
 * except in  compliance with the License. 
 * 
 * You can obtain a copy of the License at
 * http://identityconnectors.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each
 * file and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields 
 * enclosed by brackets [] replaced by your own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * -----------
 */
package org.identityconnectors.contract.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.api.operations.APIOperation;
import org.identityconnectors.framework.api.operations.CreateApiOp;
import org.identityconnectors.framework.api.operations.DeleteApiOp;
import org.identityconnectors.framework.api.operations.GetApiOp;
import org.identityconnectors.framework.api.operations.SearchApiOp;
import org.identityconnectors.framework.api.operations.SyncApiOp;
import org.identityconnectors.framework.api.operations.UpdateApiOp;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.SyncDelta;
import org.identityconnectors.framework.common.objects.SyncDeltaType;
import org.identityconnectors.framework.common.objects.SyncToken;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * <p>
 * Test that attributes satisfy contract.
 * </p>
 * Tests check:
 * <ul>
 * <li>non-readable attributes are not returnedByDefault</li>
 * <li>attributes which are not returnedByDefault really are not returned
 * unless</li>
 * specified in attrsToGet </li>
 * <li>update of non-updateable attribute will fail</li>
 * <li>required attributes must be creatable</li>
 * </ul>
 * 
 * @author David Adam
 */
@RunWith(Parameterized.class)
public class AttributeTests extends ObjectClassRunner {

    /**
     * Logging..
     */
    private static final Log LOG = Log.getLog(AttributeTests.class);
    private static final String TEST_NAME = "Attribute";

    public AttributeTests(ObjectClass oclass) {
        super(oclass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends APIOperation> getAPIOperation() {
        return CreateApiOp.class; // because without create the tests could
        // not be run.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void testRun() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTestName() {
        return TEST_NAME;
    }

    /* ******************** TEST METHODS ******************** */
    /**
     * <p>
     * Non readable attributes are not returned by default
     * </p>
     * <p>
     * API operations of acquiring attributes: <code>GetApiOp</code>
     * </p>
     */
    @Test
    public void testNonReadable() {
        if (ConnectorHelper.operationSupported(getConnectorFacade(),
                getObjectClass(), CreateApiOp.class)) {
            Uid uid = null;
            try {
                ObjectClassInfo oci = getObjectClassInfo();

                // create a new user
                Set<Attribute> attrs = ConnectorHelper.getCreateableAttributes(
                        getDataProvider(), oci, getTestName(), 0, true, false);
                // should throw UnsupportedObjectClass if not supported
                uid = getConnectorFacade().create(getSupportedObjectClass(),
                        attrs, getOperationOptionsByOp(CreateApiOp.class));

                // get the user to make sure it exists now
                ConnectorObject obj = getConnectorFacade().getObject(
                        getObjectClass(), uid,
                        getOperationOptionsByOp(GetApiOp.class));

                assertNotNull("Unable to retrieve newly created object", obj);

                // check: non readable attributes should not be returned by
                // default
                for (Attribute attr : obj.getAttributes()) {
                    if (!ConnectorHelper.isReadable(oci, attr)) {
                        String msg = String
                                .format(
                                        "Non-readable attribute should not be returned by default: %s",
                                        attr.getName());
                        assertTrue(msg, !ConnectorHelper.isReturnedByDefault(
                                oci, attr));
                    }
                }
            } finally {
                if (uid != null) {
                    // delete the object
                    getConnectorFacade().delete(getSupportedObjectClass(), uid,
                            getOperationOptionsByOp(DeleteApiOp.class));
                }
            }
        } else {
            LOG
                    .info("----------------------------------------------------------------------------------------");
            LOG
                    .info(
                            "Skipping test ''testNonReadable'' for object class ''{0}''.",
                            getObjectClass());
            LOG
                    .info("----------------------------------------------------------------------------------------");
        }
    }

    /**
     * <p>
     * not returned by default attributes should not be returned, unless
     * specified in attributesToGet ({@link OperationOptions})
     * </p>
     * <p>
     * API operations of acquiring attributes:
     * </p>
     * <ul>
     * <li>{@link GetApiOp}</li>
     * <li>{@link SearchApiOp}</li>
     * <li>{@link SyncApiOp}</li>
     * </ul>
     */
    @Test
    public void testReturnedByDefault() {
        //run the test for GetApiOp, SearchApiOp and SyncApiOp
        for (ApiOperations apiop : ApiOperations.values()) {
            testReturnedByDefault(apiop);
        }
    }
    
    /**
     * update of non-updateable attribute will fail
     * 
     * <p>
     * API operations of acquiring attributes: {@link GetApiOp}
     * </p>
     */
    @Test
    public void testNonUpdateable() {
        /** is exception caught? (indicating attempt to update non-updateable item) */
        boolean exception = false;
        /** is there any non updateable item? (if not skip this test)*/
        boolean isChanged = false;
        /** cache for exception type */
        Exception exCache = null;
        try {
            if (ConnectorHelper.operationSupported(getConnectorFacade(),
                    getObjectClass(), UpdateApiOp.class)) {
                ConnectorObject obj = null;
                Uid uid = null;

                try {
                    // create an object to update
                    uid = ConnectorHelper.createObject(getConnectorFacade(),
                            getDataProvider(), getObjectClassInfo(),
                            getTestName(), 0,
                            getOperationOptionsByOp(CreateApiOp.class));
                    assertNotNull("Create returned null Uid.", uid);

                    // get by uid
                    obj = getConnectorFacade().getObject(
                            getSupportedObjectClass(), uid,
                            getOperationOptionsByOp(GetApiOp.class));
                    assertNotNull("Cannot retrieve created object.", obj);

                    /*
                     * Acquire replaceable attributes, delete them from all
                     * attributes set.
                     */
                    Set<Attribute> replaceableAttributes = ConnectorHelper
                            .getUpdateableAttributes(getDataProvider(),
                                    getObjectClassInfo(), getTestName(),
                                    SyncApiOpTests.MODIFIED, 0, false, false);

                    Set<Attribute> allAttributes = obj.getAttributes();
                    Set<Attribute> modAllAttributes = new HashSet<Attribute>(
                            allAttributes);
                    boolean changed = modAllAttributes
                            .removeAll(replaceableAttributes);
                    isChanged = changed; // update the indicator
                    allAttributes = modAllAttributes;

                    if (changed || !isObjectClassSupported()) {
                        // update only in case there is something to update or
                        // when
                        // object class is not supported
                        allAttributes.add(uid);

                        assertTrue("no update attributes were found",
                                (allAttributes.size() > 0));
                        Uid newUid = getConnectorFacade().update(
                                UpdateApiOp.Type.REPLACE, getObjectClass(),
                                allAttributes,
                                getOperationOptionsByOp(UpdateApiOp.class));

                        // Update change of Uid must be propagated to
                        // replaceAttributes
                        // set
                        if (!newUid.equals(uid)) {
                            allAttributes.remove(uid);
                            allAttributes.add(newUid);
                            uid = newUid;
                        }
                    } else {
                        // no non-updateable attrs. found, skipping this test.
                        LOG
                                .info("----------------------------------------------------------------------------------------");
                        LOG
                                .info(
                                        "Skipping test ''testNonUpdateable'' for object class ''{0}''. (Reason: non-updateable attrs. missing)",
                                        getObjectClass());
                        LOG
                                .info("----------------------------------------------------------------------------------------");
                    }

                    // verify the change
                    obj = getConnectorFacade().getObject(
                            getSupportedObjectClass(), uid,
                            getOperationOptionsByOp(GetApiOp.class));
                    assertNotNull("Cannot retrieve updated object.", obj);
                    ConnectorHelper.checkObject(getObjectClassInfo(), obj,
                            allAttributes);
                } finally {
                    if (uid != null) {
                        // finally ... get rid of the object
                        ConnectorHelper.deleteObject(getConnectorFacade(),
                                getSupportedObjectClass(), uid, false,
                                getOperationOptionsByOp(DeleteApiOp.class));
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            // OK
            exception = true;
            exCache = ex;
        } finally { 
            String msg;
            if (exception) {
                if (isChanged) {
                    //OK
                    msg = String.format("unexpected exception type caught: %s (expecting IllegalArgumentException)", (exCache != null) ? exCache.getClass().getName() : "");
                    assertTrue(msg, exCache.getClass().equals(IllegalArgumentException.class));
                } else {
                    //WARN
                    msg = String.format("No non-updateable attribute is present, however %s exception caught. (Contact author of the test)", (exCache != null) ? exCache.getClass().getName() : "");
                    fail(msg);
                }
            } else {
                if (isChanged) {
                    // WARN
                    fail("No IllegalArgumentException thrown when non-updateable argument was changed");
                } else {
                    //OK
                }
            }
                
        }
    }
    
    /**
     * Required attributes must be creatable. It is a fialure if a required
     * attribute is not creatable.
     */
    @Test
    public void testRequirableIsCreatable() {
        if (ConnectorHelper.operationSupported(getConnectorFacade(),
                getObjectClass(), CreateApiOp.class)) {
            Uid uid = null;
            try {
                ObjectClassInfo oci = getObjectClassInfo();

                // create a new user
                Set<Attribute> attrs = ConnectorHelper.getCreateableAttributes(
                        getDataProvider(), oci, getTestName(), 0, true, false);
                // should throw UnsupportedObjectClass if not supported
                uid = getConnectorFacade().create(getSupportedObjectClass(),
                        attrs, getOperationOptionsByOp(CreateApiOp.class));

                // get the user to make sure it exists now
                ConnectorObject obj = getConnectorFacade().getObject(
                        getObjectClass(), uid,
                        getOperationOptionsByOp(GetApiOp.class));

                assertNotNull("Unable to retrieve newly created object", obj);

                // check: Required attributes must be creatable.
                for (Attribute attr : obj.getAttributes()) {
                    if (ConnectorHelper.isRequired(oci, attr)) {
                        if (!ConnectorHelper.isCreateable(oci, attr)) {
                            //WARN
                            String msg = String.format("Required attribute is not createable. Attribute name: %s", attr.getName());
                            fail(msg);
                        }
                    }
                }
            } finally {
                if (uid != null) {
                    // delete the object
                    getConnectorFacade().delete(getSupportedObjectClass(), uid,
                            getOperationOptionsByOp(DeleteApiOp.class));
                }
            }
        } else {
            LOG
                    .info("----------------------------------------------------------------------------------------");
            LOG
                    .info(
                            "Skipping test ''testNonReadable'' for object class ''{0}''.",
                            getObjectClass());
            LOG
                    .info("----------------------------------------------------------------------------------------");
        }
    }

    /* ******************** HELPER METHODS ******************** */
    /**
     * {@link AttributeTests#testReturnedByDefault()}
     * 
     * @param apiOp
     *            the type of ApiOperation, that shall be tested.
     */
    private void testReturnedByDefault(ApiOperations apiOp) {
        /** marker in front of every assert message */
        String testMarkMsg = String.format("[testReturnedByDefault/%s]", apiOp);
        
        // run the contract test only if <strong>apiOp</strong> APIOperation is
        // supported
        if (ConnectorHelper.operationSupported(getConnectorFacade(),
                getObjectClass(), apiOp.getClazz())) {
            
            // start synchronizing from now
            SyncToken token = null;
            if (apiOp.equals(ApiOperations.SYNC)) { // just for SyncApiOp test
                token = getConnectorFacade().getLatestSyncToken();
            }

            Uid uid = null;
            try {
                ObjectClassInfo oci = getObjectClassInfo();

                /*
                 * CREATE a new user
                 */ 
                Set<Attribute> attrs = ConnectorHelper.getCreateableAttributes(
                        getDataProvider(), oci, getTestName(), 0, true, false);
                // should throw UnsupportedObjectClass if not supported
                uid = getConnectorFacade().create(getObjectClass(), attrs,
                        null);
                assertNotNull(testMarkMsg + " Create returned null uid.", uid);

                /*
                 * ************ GetApiOp ************
                 */
                // get the user to make sure it exists now
                ConnectorObject obj = null;
                switch (apiOp) {
                case GET:
                    /* last _null_ param - no operation option, response contains just attributes returned by default*/
                    obj = getConnectorFacade().getObject(getObjectClass(), uid, null);
                    break;// GET

                case SEARCH:
                    Filter fltUid = FilterBuilder.equalTo(AttributeBuilder
                            .build(Uid.NAME, uid.getUidValue()));

                    assertTrue(testMarkMsg + " filterUid is null", fltUid != null);

                    List<ConnectorObject> coObjects = ConnectorHelper.search(
                            getConnectorFacade(), getSupportedObjectClass(),
                            fltUid, null);

                    assertTrue(testMarkMsg + 
                            " Search filter by uid with no OperationOptions failed, expected to return one object, but returned "
                                    + coObjects.size(), coObjects.size() == 1);

                    assertNotNull(testMarkMsg + " Unable to retrieve newly created object",
                            coObjects.get(0));

                    obj = coObjects.get(0);
                    break;// SEARCH

                case SYNC:
                    uid = testSync(uid, token, attrs, oci, testMarkMsg);
                    break;// SYNC
                }//switch

                /*
                 * Check if attribute set contains non-returned by default
                 * Attributes. This is specific for AttributeTests
                 */
                if (!apiOp.equals(ApiOperations.SYNC)) {
                    assertNotNull("Unable to retrieve newly created object", obj);
                    // obj is null for sync tests
                    checkAttributes(obj, oci, apiOp);
                }

            } finally {
                if (uid != null) {
                    // cleanup test data
                    ConnectorHelper.deleteObject(getConnectorFacade(), getSupportedObjectClass(), uid,
                            false, getOperationOptionsByOp(DeleteApiOp.class));
                }
            }
        } else {
            LOG
                    .info("----------------------------------------------------------------------------------------");
            LOG
                    .info(
                            "Skipping test ''testReturnedByDefault'' for object class ''{0}''.",
                            getObjectClass());
            LOG
                    .info("----------------------------------------------------------------------------------------");
        }
    }

    /** Main checking of "no returned by default" attributes 
     * @param testName 
     * @param apiOp */
    private void checkAttributes(ConnectorObject obj, ObjectClassInfo oci, ApiOperations apiOp) {
        // Check if attribute set contains non-returned by default
        // Attributes.
        for (Attribute attr : obj.getAttributes()) {
            String msg = String
                    .format(
                            "[testReturnedByDefault / %s]Attribute %s returned. However it is _not_ returned by default.",
                            apiOp, attr.getName());
            /*
             * this is a hack that skips control of UID, as it is presently 
             * non returned by default, however it is automatically returned.
             * see discussion in Issue mailing list -- Issue #334
             * future TODO: after joining UID to schema, erase the condition.
             */
            if (!attr.getName().equals(Uid.NAME)) {
                assertTrue(msg, ConnectorHelper.isReturnedByDefault(oci, attr));
            }
        }
    }

    /**
     * test sync
     * 
     * @param token
     *            initialized token
     * @param attrs
     *            newly created attributes
     * @param uid
     *            the newly created object
     * @param oci
     *            object class info
     * @param testMarkMsg test marker
     * @return the updated Uid
     */
    private Uid testSync(Uid uid, SyncToken token,
            Set<Attribute> attrs, ObjectClassInfo oci, String testMarkMsg) {
        List<SyncDelta> deltas = null;
        String msg = null;

        /*
         * CREATE: (was handled in the calling method, result of create is in
         * param uid, cleanup is also in caller method.)
         */

        if (SyncApiOpTests.canSyncAfterOp(CreateApiOp.class)) {
            // sync after create
            deltas = ConnectorHelper.sync(getConnectorFacade(),
                    getObjectClass(), token,
                    null);

            // check that returned one delta
            msg = "%s Sync should have returned one sync delta after creation of one object, but returned: %d";
            assertTrue(String.format(msg, testMarkMsg, deltas.size()), deltas.size() == 1);

            // check delta
            ConnectorHelper.checkSyncDelta(getObjectClassInfo(), deltas.get(0),
                    uid, attrs, SyncDeltaType.CREATE_OR_UPDATE, false);

            /*
             * check the attributes inside delta This is specific for
             * AttributeTests
             */
            ConnectorObject obj = deltas.get(0).getObject();
            checkAttributes(obj, oci, ApiOperations.SYNC);

            token = deltas.get(0).getToken();
        }

        /* UPDATE: */

        if (ConnectorHelper.operationSupported(getConnectorFacade(),
                UpdateApiOp.class)
                && SyncApiOpTests.canSyncAfterOp(UpdateApiOp.class)) {

            Set<Attribute> replaceAttributes = ConnectorHelper
                    .getUpdateableAttributes(getDataProvider(),
                            getObjectClassInfo(), getTestName(),
                            SyncApiOpTests.MODIFIED, 0, false, false);

            // update only in case there is something to update
            if (replaceAttributes.size() > 0) {
                replaceAttributes.add(uid);

                assertTrue(testMarkMsg + " no update attributes were found",
                        (replaceAttributes.size() > 0));
                Uid newUid = getConnectorFacade().update(
                        UpdateApiOp.Type.REPLACE, getSupportedObjectClass(),
                        replaceAttributes,
                        null);

                // Update change of Uid must be propagated to
                // replaceAttributes
                if (!newUid.equals(uid)) {
                    replaceAttributes.remove(uid);
                    replaceAttributes.add(newUid);
                    uid = newUid;
                }

                // sync after update
                deltas = ConnectorHelper.sync(getConnectorFacade(),
                        getObjectClass(), token,
                        null);

                // check that returned one delta
                msg = "%s Sync should have returned one sync delta after update of one object, but returned: %d";
                assertTrue(String.format(msg, testMarkMsg, deltas.size()),
                        deltas.size() == 1);

                // check delta
                ConnectorHelper.checkSyncDelta(getObjectClassInfo(), deltas
                        .get(0), uid, replaceAttributes,
                        SyncDeltaType.CREATE_OR_UPDATE, false);

                /*
                 * check the attributes inside delta This is specific for
                 * AttributeTests
                 */
                ConnectorObject obj = deltas.get(0).getObject();
                checkAttributes(obj, oci, ApiOperations.SYNC);

                token = deltas.get(0).getToken();
            }
        }

        /* DELETE: */

        if (SyncApiOpTests.canSyncAfterOp(DeleteApiOp.class)) {
            // delete object
            getConnectorFacade().delete(getObjectClass(), uid,
                    null);

            // sync after delete
            deltas = ConnectorHelper.sync(getConnectorFacade(),
                    getObjectClass(), token,
                    null);

            // check that returned one delta
            msg = "%s Sync should have returned one sync delta after delete of one object, but returned: %d";
            assertTrue(String.format(msg, testMarkMsg, deltas.size()), deltas.size() == 1);

            // check delta
            ConnectorHelper.checkSyncDelta(getObjectClassInfo(), deltas.get(0),
                    uid, null, SyncDeltaType.DELETE, false);

            /*
             * check the attributes inside delta This is specific for
             * AttributeTests
             */
            ConnectorObject obj = deltas.get(0).getObject();
            checkAttributes(obj, oci, ApiOperations.SYNC);
        }
        return uid;
    }
}// end of class AttributeTests

/** helper inner class for passing the type of tested operations */
enum ApiOperations {
    SEARCH(SearchApiOp.class), GET(GetApiOp.class), SYNC(SyncApiOp.class);
    private final String s;
    private final Class<? extends APIOperation> clazz;

    private ApiOperations(Class<? extends APIOperation> c) {
        this.s = c.getName();
        this.clazz = c;
    }

    @Override
    public String toString() {
        return s;
    }

    public Class<? extends APIOperation> getClazz() {
        return clazz;
    }
}// end of enum ApiOperations