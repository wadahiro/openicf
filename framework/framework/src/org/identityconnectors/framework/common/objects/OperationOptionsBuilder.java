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
import java.util.HashMap;
import java.util.Map;

import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.serializer.ObjectSerializerFactory;


/**
 * Builder for {@link OperationOptions}.
 */
public final class OperationOptionsBuilder {
    private final Map<String, Object> _options = new HashMap<String, Object>();

    /**
     * Create a builder with an empty set of options.
     */
    public OperationOptionsBuilder() {

    }

    /**
     * Sets a given option and a value for that option.
     * 
     * @param name
     *            The name of the option
     * @param value
     *            The value of the option. Must be one of the types that we can
     *            serialize. See {@link ObjectSerializerFactory} for a list of
     *            supported types.
     */
    public void setOption(String name, Object value) {
        Assertions.blankCheck(name, "name");
        // don't validate value here - we do that in
        // the constructor of OperationOptions - that's
        // really the only place we can truly enforce this
        _options.put(name, value);
    }

    /**
     * Sets the {@link OperationOptions#OP_ATTRIBUTES_TO_GET} option.
     * 
     * @param attrNames
     *            list of {@link Attribute} names.
     */
    public void setAttributesToGet(String... attrNames) {
        Assertions.nullCheck(attrNames, "attrNames");
        // don't validate value here - we do that in
        // the constructor of OperationOptions - that's
        // really the only place we can truly enforce this
        _options.put(OperationOptions.OP_ATTRIBUTES_TO_GET, attrNames);
    }

    /**
     * Sets the {@link OperationOptions#OP_ATTRIBUTES_TO_GET} option.
     * 
     * @param attrNames
     *            list of {@link Attribute} names.
     */
    public void setAttributesToGet(Collection<String> attrNames) {
        Assertions.nullCheck(attrNames, "attrNames");
        // don't validate value here - we do that in
        // the constructor of OperationOptions - that's
        // really the only place we can truly enforce this
        String[] attrs = new String[attrNames.size()];
        attrs = attrNames.toArray(attrs);
        _options.put(OperationOptions.OP_ATTRIBUTES_TO_GET, attrs);
    }

    /**
	 * Set the run with password option.
	 */
	public void setRunWithPassword(GuardedString password) {
		Assertions.nullCheck(password, "password");
		_options.put(OperationOptions.OP_RUN_WITH_PASSWORD, password);
	}
    
    /**
	 * Set the run as user option.
	 */
    public void setRunAsUser(String user) {
        Assertions.nullCheck(user, "user");
        _options.put(OperationOptions.OP_RUN_AS_USER, user);
    }

    /**
     * Returns a mutable reference of the options map.
     * 
     * @return A mutable reference of the options map.
     */
    public Map<String, Object> getOptions() {
        // might as well be mutable since it's the builder and
        // we don't want to deep copy anyway
        return _options;
    }

    /**
     * Creates the <code>OperationOptions</code>.
     * 
     * @return The newly-created <code>OperationOptions</code>
     */
    public OperationOptions build() {
        return new OperationOptions(_options);
    }
}
