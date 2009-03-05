/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.     
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
 * ====================
 */
package org.identityconnectors.ldap;

import java.util.Collections;
import java.util.Set;

/**
 * A static pre-made definition of the native schema. This is needed
 * for backward compatibility with the LDAP resource adapter, which
 * does not read the schema. This class does not return any attributes
 * or object classes. For IDM, they are set during the post-processing.
 *
 * See also {@link ServerNativeSchema}.
 */
public class StaticNativeSchema implements LdapNativeSchema {

    public Set<String> getRequiredAttributes(String ldapClass) {
        return Collections.emptySet();
    }

    public Set<String> getOptionalAttributes(String ldapClass) {
        return Collections.emptySet();
    }

    public Set<String> getSuperiorObjectClasses(String ldapClass) {
        return Collections.emptySet();
    }

    public LdapAttributeType getAttributeDescription(String ldapAttrName) {
        return null;
    }
}
