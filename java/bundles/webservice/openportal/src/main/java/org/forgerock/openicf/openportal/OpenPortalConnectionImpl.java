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

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collection;
import java.util.Set;

/**
 * Class to represent a OpenPortal Connection
 *
 * @author $author$
 * @version $Revision$ $Date$
 */
public class OpenPortalConnectionImpl implements OpenPortalConnection {

    private OpenPortalConfiguration _configuration;

    public OpenPortalConnectionImpl(OpenPortalConfiguration configuration) {
        _configuration = configuration;
    }

    public Uid create(ObjectClass objectClass, Set<Attribute> attributes) {
        return null;
    }

    public Uid update(ObjectClass objectClass, Uid uid, Set<Attribute> replaceAttributes) {
        return null;
    }

    public void delete(ObjectClass objectClass, Uid uid) {
    }

    public Collection<ConnectorObject> serach(String query, ObjectClass objectClass) {
        return null;
    }

    public Uid authenticate(String userName, GuardedString password) {
        return null;
    }

    /**
     * If internal connection is not usable, throw IllegalStateException
     */
    public void test() {
        //implementation
    }

    /**
     * Release internal resources
     */
    public void dispose() {
        //implementation
    }

    public Schema schema() {
        return null;
    }

}
