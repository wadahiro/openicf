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
package org.identityconnectors.test.framework.impl.api.local.operations;

import static org.identityconnectors.framework.common.objects.AttributeBuilder.build;
import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.impl.api.local.operations.AttributesToGetResultsHandler;
import org.junit.Test;


public class AttributesToGetResultsHandlerTests {
    
    @Test(expected=NullPointerException.class)
    public void testAttrsToGet() throws Exception {
        new TestHandler((String[])null);
    }

    @Test
    public void testReduceAttributes() throws Exception {
        String[] attrsToGet = { "a", "b" };
        TestHandler tst = new TestHandler(attrsToGet);
        Set<Attribute> expected = CollectionUtil.newSet(build("a"), build("b"));
        Set<Attribute> testAttrs = CollectionUtil.newSet(expected);
        testAttrs.add(build("c"));
        Set<Attribute> actual = tst.reduceToAttrsToGet(testAttrs);
        assertEquals(expected, actual);
    }

    @Test
    public void testIgnoreMissing() throws Exception {
        String[] attrsToGet = { "a", "b", "c", "d" };
        TestHandler tst = new TestHandler(attrsToGet);
        Set<Attribute> expected = CollectionUtil.newSet(build("a"), build("b"));
        Set<Attribute> testAttrs = CollectionUtil.newSet(expected);
        testAttrs.add(build("g"));
        Set<Attribute> actual = tst.reduceToAttrsToGet(testAttrs);
        assertEquals(expected, actual);        
    }
    
    @Test
    public void testSimpleSearch() throws Exception {
        
    }
    
    static class TestHandler extends AttributesToGetResultsHandler {
        public TestHandler(String[] attrsToGet) {
            super(attrsToGet);
        }
    }
    
    
}
