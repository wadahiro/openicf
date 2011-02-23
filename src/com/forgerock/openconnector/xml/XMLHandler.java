/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import java.util.Collection;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;


public interface XMLHandler {

    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes);

    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes) throws UnknownUidException;

    public void delete(final ObjectClass objClass, final Uid uid) throws UnknownUidException;

    public Collection<ConnectorObject> search(String query, ObjectClass objectClass);

    public void serialize();
}
