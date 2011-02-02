/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Uid;


public interface XMLHandler {

    public void create(Object obj) throws AlreadyExistsException;

    public void update(Object obj) throws UnknownUidException;

    public void delete(Uid uid) throws UnknownUidException;

    public Object search(String query);

    public void serialize();
}
