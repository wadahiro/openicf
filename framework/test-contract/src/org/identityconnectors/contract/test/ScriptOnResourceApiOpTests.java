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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.contract.exceptions.ObjectNotFoundException;
import org.identityconnectors.framework.api.operations.APIOperation;
import org.identityconnectors.framework.api.operations.ScriptOnResourceApiOp;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.ScriptContext;
import org.junit.Test;


/**
 * Contract test of {@link ScriptOnResourceApiOp} operation.
 * 
 * @author Zdenek Louzensky
 */
public class ScriptOnResourceApiOpTests extends AbstractSimpleTest {

    /**
     * Logging..
     */
    private static final Log LOG = Log.getLog(ScriptOnResourceApiOpTests.class);
    
    private static final String TEST_NAME="ScriptOnResource";
    private static final String LANGUAGE_PROP_PREFIX = "language." + TEST_NAME;
    private static final String SCRIPT_PROP_PREFIX = "script." + TEST_NAME;
    private static final String ARGUMENTS_PROP_PREFIX = "arguments." + TEST_NAME;
    private static final String RESULT_PROP_PREFIX = "result." + TEST_NAME;

    /**
     * Returns 
     */
    public Class<? extends APIOperation> getAPIOperation() {
        return ScriptOnResourceApiOp.class;
    }

    
    /**
     * Tests running a script with correct values from property file.
     */
    @Test
    public void testRunScript() {
        // run test only in case operation is supported
        if (ConnectorHelper.operationSupported(getConnectorFacade(), getAPIOperation())) {
            try {
                // get test properties - optional
                // if a property is not found test is skipped
                String language = (String) getDataProvider().getTestSuiteAttribute(
                        String.class.getName(), LANGUAGE_PROP_PREFIX);
                String script = (String) getDataProvider().getTestSuiteAttribute(
                        String.class.getName(), SCRIPT_PROP_PREFIX);
                Map<String, Object> arguments = (Map<String, Object>) getDataProvider()
                        .getTestSuiteAttribute(Map.class.getName(), ARGUMENTS_PROP_PREFIX);
                Object expResult = getDataProvider().getTestSuiteAttribute(Object.class.getName(),
                        RESULT_PROP_PREFIX);

                // run the script
                Object result = getConnectorFacade().runScriptOnResource(
                        new ScriptContext(language, script, arguments),
                        getOperationOptionsByOp(ScriptOnResourceApiOp.class));

                // check that returned result was expected
                final String MSG = "Script result was unexpected, expected: '%s', returned: '%s'.";
                assertEquals(String.format(MSG, expResult, result), expResult, result);
            } catch (ObjectNotFoundException ex) {
                // ok - properties were not provided - test is skipped
                LOG.info("Test properties not set, skipping the test " + TEST_NAME);
            }
        }
    }
    
    /**
     * Tests running a script with unknown language.
     */
    @Test
    public void testRunScriptFailUnknownLanguage() {
        // run test only in case operation is supported
        if (ConnectorHelper.operationSupported(getConnectorFacade(), getAPIOperation())) {
            try {
                getConnectorFacade().runScriptOnResource(
                        new ScriptContext("NONEXISTING LANGUAGE", "script",
                                new HashMap<String, Object>()), null);
                fail("Script language is not supported, should throw an exception.");
            } catch (RuntimeException ex) {
                // expected
            }
        }
    }

    /**
     * Tests running a script with empty script text.
     */
    @Test
    public void testRunScriptFailEmptyScriptText() {
        // run test only in case operation is supported
        if (ConnectorHelper.operationSupported(getConnectorFacade(), getAPIOperation())) {
            try {
                getConnectorFacade().runScriptOnResource(
                        new ScriptContext("LANGUAGE", "", new HashMap<String, Object>()), null);
                fail("Script text is empty and script language is not probably supported, should throw an exception.");
            } catch (RuntimeException ex) {
                // expected
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationOptions getOperationOptionsByOp(Class<? extends APIOperation> clazz) {
        if (clazz.equals(ScriptOnResourceApiOp.class)) {
            OperationOptionsBuilder builder = new OperationOptionsBuilder();
            
            // OperationOptions RUN_AS_USER
            final String user = getStringProperty(OperationOptions.OP_RUN_AS_USER + "." + TEST_NAME);
            if (user != null) {
                LOG.info("Using OperationOptions: ''{0}'' value: ''{1}''.", OperationOptions.OP_RUN_AS_USER, user);
                builder.setRunAsUser(user);
            }

            // OperationOptions RUN_WITH_PASSWORD
            final String password = getStringProperty(OperationOptions.OP_RUN_WITH_PASSWORD + "."
                    + TEST_NAME);
            if (password != null) {
                LOG.info("Using OperationOptions: ''{0}'' value: ''{1}''.", OperationOptions.OP_RUN_WITH_PASSWORD, password);
                builder.setRunWithPassword(new GuardedString(password.toCharArray()));
            }

            return builder.build();
        }

        return super.getOperationOptionsByOp(clazz);
    }

    /**
     * Returns string property value.
     * @param name Property name.
     * @return null in case property definition not found.
     */
    private String getStringProperty(String name) {
        String value = null;
        try {
            value = (String) getDataProvider().getTestSuiteAttribute(String.class.getName(), name);
            LOG.info("Property ''{0}'' value ''{1}''.", name, value);
        } catch (ObjectNotFoundException ex) {
            // ok
        }

        return value;
    }

}
