/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.forgerock.openicf.usd.ca.uds;


import org.forgerock.openicf.usd.ca.ArrayOfString;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author developer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "handle",
    "attributes"
})
@XmlRootElement(name = "UDSObject", namespace = "")
public class UDSObject {

    @XmlElement(name = "Handle", required = true)
    private String handle = null;
    @XmlElement(name = "Attributes", required = true)
    private ArrayOfUDSAttribute attributes = null;

    public UDSObject() {    
    }

    public UDSObject(String handler) {        
        this.handle = handler;
    }

    public ArrayOfUDSAttribute getAttributes() {
        if (null == attributes) {
            attributes = new ArrayOfUDSAttribute();
        }
        return attributes;
    }

    public void setAttributes(ArrayOfUDSAttribute attributes) {
        this.attributes = attributes;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public UDSAttribute getAttribute(String attrName) {
        UDSAttribute result = null;
        if (attrName.contains(":")) {
            attrName = attrName.substring(attrName.indexOf(":") + 1);
        }
        for (UDSAttribute attr : getAttributes().getAttribute()) {
            if (attrName.equals(attr.getAttrName())) {
                result = attr;
                break;
            }
        }
        return result;
    }

    public boolean addAttribute(UDSAttribute attribute) {
        if (null != attribute && !attribute.getAttrName().contains(":")) {
            getAttributes().getAttribute().add(attribute);
            return true;
        }
        return false;
    }

    public ArrayOfString getStringArray() {
        ArrayOfString valueList = new ArrayOfString();
        for (UDSAttribute attribute : getAttributes().getAttribute()) {
            valueList.getString().add(attribute.getAttrName());
            valueList.getString().add(attribute.getAttrValue());
        }
        return valueList;
    }
}
