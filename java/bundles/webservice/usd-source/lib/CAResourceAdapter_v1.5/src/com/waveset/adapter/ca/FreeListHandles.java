
package com.waveset.adapter.ca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="handles" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfInt"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "sid",
    "handles"
})
@XmlRootElement(name = "freeListHandles")
public class FreeListHandles {

    protected int sid;
    @XmlElement(required = true)
    protected ArrayOfInt handles;

    /**
     * Gets the value of the sid property.
     * 
     */
    public int getSid() {
        return sid;
    }

    /**
     * Sets the value of the sid property.
     * 
     */
    public void setSid(int value) {
        this.sid = value;
    }

    /**
     * Gets the value of the handles property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfInt }
     *     
     */
    public ArrayOfInt getHandles() {
        return handles;
    }

    /**
     * Sets the value of the handles property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfInt }
     *     
     */
    public void setHandles(ArrayOfInt value) {
        this.handles = value;
    }

}
