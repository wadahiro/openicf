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
package org.identityconnectors.framework.common.objects;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.CaseInsensitiveMap;
import org.identityconnectors.common.CollectionUtil;


/**
 * Utility methods to retrieve values from instances of {@link AttributeInfo}.
 */
public final class AttributeInfoUtil {

    /**
     * Transform a <code>Collection</code> of {@link AttributeInfo} instances into
     * a {@link Map}. The key to each element in the map is the <i>name</i> of
     * an <code>AttributeInfo</code>. The value of each element in the map is the
     * <code>AttributeInfo</code> instance with that name.
     * 
     * @param attributes
     *            set of AttributeInfo to transform to a map.
     * @return a map of string and AttributeInfo.
     * @throws NullPointerException
     *             iff the parameter <strong>attributes</strong> is
     *             <strong>null</strong>.
     */
    public static Map<String, AttributeInfo> toMap(
            Collection<? extends AttributeInfo> attributes) {
        Map<String, AttributeInfo> ret = new CaseInsensitiveMap<AttributeInfo>();
        for (AttributeInfo attr : attributes) {
            ret.put(attr.getName(), attr);
        }
        return ret;
    }

    /**
     * Find the {@link AttributeInfo} of the given name in the {@link Set}.
     * 
     * @param name
     *            {@link AttributeInfo}'s name to search for.
     * @param attrs
     *            {@link Set} of AttributeInfo to search.
     * @return {@link AttributeInfo} with the specified otherwise <code>null</code>.
     */
    public static AttributeInfo find(String name, Set<AttributeInfo> attrs) {
        Assertions.nullCheck(name, "name");
        Set<AttributeInfo> set = CollectionUtil.nullAsEmpty(attrs);
        for (AttributeInfo attr : set) {
            if (attr.is(name)) {
                return attr;
            }
        }
        return null;
    }

}
