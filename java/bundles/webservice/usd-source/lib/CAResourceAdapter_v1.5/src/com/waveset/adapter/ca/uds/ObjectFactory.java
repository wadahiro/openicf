/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.waveset.adapter.ca.uds;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.statoilhydro.cims.sao.pims
     *
     */
    public ObjectFactory() {
    }

    public UDSAttribute createUDSAttribute() {
        return new UDSAttribute();
    }

    public UDSObject createUDSObject() {
        return new UDSObject();
    }

    public UDSObjectList createUDSObjectList() {
        return new UDSObjectList();
    }
}
