/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.forgerock.openicf.usd.ca.uds;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author developer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", propOrder = {
    "attrName",
    "attrValue"
})
public class UDSAttribute {

    @XmlElement(name = "AttrName", required = true)
    private String attrName;
    @XmlAttribute(name = "DataType")
    protected String dataType;
    @XmlElement(name = "AttrValue", required=true, nillable=true)
    private String attrValue;

    public UDSAttribute() {
    }

    public UDSAttribute(String Name, Object value) {
        if (null == Name) {
            throw new InstantiationError("UDSAttribute can not be created without valid AttrName");
        }
        if (Name.contains(":")) {
            this.attrName = Name.substring(Name.indexOf(":") + 1);
        } else {
            this.attrName = Name;
        }
        if (null == value) {
            this.attrValue = "";
        } else if (value instanceof Integer) {
            this.attrValue = value.toString();
            this.dataType = "2001";
        } else if (value instanceof String) {
            this.attrValue = value.toString();
            this.dataType = "2002";
        } else if (value instanceof Date) {
            this.attrValue = Long.toString(((Date)value).getTime());
            this.dataType = "2004";
        } else {
            this.attrValue = value.toString();
        }
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String AttrName) {
        this.attrName = AttrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String AttrValue) {
        this.attrValue = AttrValue;
    }

    /**
     * Gets the value of the dataType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the value of the dataType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDataType(String value) {
        this.dataType = value;
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if ((other == null)) {
            return false;
        }
        if (!(other instanceof UDSAttribute)) {
            return false;
        }
        UDSAttribute castOther = (UDSAttribute) other;
        return (this.getAttrName().equals(castOther.getAttrName()));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (getAttrName() == null ? 0 : this.getAttrName().hashCode());
        return result;
    }
}
