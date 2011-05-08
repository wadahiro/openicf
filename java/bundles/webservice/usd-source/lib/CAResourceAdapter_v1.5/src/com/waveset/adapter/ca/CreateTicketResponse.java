
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
 *         &lt;element name="createTicketReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newTicketHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newTicketNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="returnUserData" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="returnApplicationData" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createTicketReturn",
    "newTicketHandle",
    "newTicketNumber",
    "returnUserData",
    "returnApplicationData"
})
@XmlRootElement(name = "createTicketResponse")
public class CreateTicketResponse {

    @XmlElement(required = true)
    protected String createTicketReturn;
    @XmlElement(required = true)
    protected String newTicketHandle;
    @XmlElement(required = true)
    protected String newTicketNumber;
    @XmlElement(required = true)
    protected String returnUserData;
    @XmlElement(required = true)
    protected String returnApplicationData;

    /**
     * Gets the value of the createTicketReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateTicketReturn() {
        return createTicketReturn;
    }

    /**
     * Sets the value of the createTicketReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateTicketReturn(String value) {
        this.createTicketReturn = value;
    }

    /**
     * Gets the value of the newTicketHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewTicketHandle() {
        return newTicketHandle;
    }

    /**
     * Sets the value of the newTicketHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewTicketHandle(String value) {
        this.newTicketHandle = value;
    }

    /**
     * Gets the value of the newTicketNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewTicketNumber() {
        return newTicketNumber;
    }

    /**
     * Sets the value of the newTicketNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewTicketNumber(String value) {
        this.newTicketNumber = value;
    }

    /**
     * Gets the value of the returnUserData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnUserData() {
        return returnUserData;
    }

    /**
     * Sets the value of the returnUserData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnUserData(String value) {
        this.returnUserData = value;
    }

    /**
     * Gets the value of the returnApplicationData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnApplicationData() {
        return returnApplicationData;
    }

    /**
     * Sets the value of the returnApplicationData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnApplicationData(String value) {
        this.returnApplicationData = value;
    }

}
