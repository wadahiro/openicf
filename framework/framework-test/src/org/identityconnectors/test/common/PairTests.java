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
package org.identityconnectors.test.common;

import java.util.HashSet;
import java.util.Set;

import org.identityconnectors.common.Pair;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the pair object.
 */
public class PairTests {

    @Test
    public void equals() {
        Pair<String, String> a = new Pair<String, String>("a", "b");
        Pair<String, String> b = new Pair<String, String>("a", "b");
        Assert.assertTrue(a.equals(b));
        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(b.equals(null));
        Assert.assertFalse(a.equals("f"));
    }

    @Test
    public void hash() {
        Set<Pair<Integer, Integer>> set = new HashSet<Pair<Integer,Integer>>();
        for (int i=0; i<20; i++) {
            Pair<Integer, Integer> pair = new Pair<Integer, Integer>(i, i+1);
            Pair<Integer, Integer> tst = new Pair<Integer, Integer>(i, i+1);
            set.add(pair);
            Assert.assertTrue(set.contains(tst));
        }
        // check that each pair is unique..
        Assert.assertEquals(20, set.size());
    }
}
