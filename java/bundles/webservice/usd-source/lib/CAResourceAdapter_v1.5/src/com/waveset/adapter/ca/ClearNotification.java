
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
 *         &lt;element name="lrObject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="clearBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "lrObject",
    "clearBy"
})
@XmlRootElement(name = "clearNotification")
public class ClearNotification {

    protected int sid;
    @XmlElement(required = true)
    protected String lrObject;
    @XmlElement(required = true)
    protected String clearBy;

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
     * Gets the value of the lrObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLrObject() {
        return lrObject;
    }

    /**
     * Sets the value of the lrObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLrObject(String value) {
        this.lrObject = value;
    }

    /**
     * Gets the value of the clearBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClearBy() {
        return clearBy;
    }

    /**
     * Sets the value of the clearBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClearBy(String value) {
        this.clearBy = value;
    }

}
