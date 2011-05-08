
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
 *         &lt;element name="contactHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="groupHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "contactHandle",
    "groupHandle"
})
@XmlRootElement(name = "removeMemberFromGroup")
public class RemoveMemberFromGroup {

    protected int sid;
    @XmlElement(required = true)
    protected String contactHandle;
    @XmlElement(required = true)
    protected String groupHandle;

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
     * Gets the value of the contactHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactHandle() {
        return contactHandle;
    }

    /**
     * Sets the value of the contactHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactHandle(String value) {
        this.contactHandle = value;
    }

    /**
     * Gets the value of the groupHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupHandle() {
        return groupHandle;
    }

    /**
     * Sets the value of the groupHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupHandle(String value) {
        this.groupHandle = value;
    }

}
