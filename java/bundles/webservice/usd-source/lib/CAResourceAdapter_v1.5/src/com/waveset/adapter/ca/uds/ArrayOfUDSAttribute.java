/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.waveset.adapter.ca.uds;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author laszlohordos
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attributes", propOrder = {
    "attribute"
})
public class ArrayOfUDSAttribute {

    @XmlElement(name = "Attribute", nillable = true)
    protected List<UDSAttribute> attribute;

    /**
     * Gets the value of the domain property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDomain().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     *
     *
     */
    public List<UDSAttribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<UDSAttribute>();
        }
        return this.attribute;
    }
}
