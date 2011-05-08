
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
 *         &lt;element name="creator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="contextObject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageTitle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="messageBody" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="notifyLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="notifyees" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfString"/>
 *         &lt;element name="internal" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "creator",
    "contextObject",
    "messageTitle",
    "messageBody",
    "notifyLevel",
    "notifyees",
    "internal"
})
@XmlRootElement(name = "notifyContacts")
public class NotifyContacts {

    protected int sid;
    @XmlElement(required = true)
    protected String creator;
    @XmlElement(required = true)
    protected String contextObject;
    @XmlElement(required = true)
    protected String messageTitle;
    @XmlElement(required = true)
    protected String messageBody;
    protected int notifyLevel;
    @XmlElement(required = true)
    protected ArrayOfString notifyees;
    protected boolean internal;

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
     * Gets the value of the creator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the value of the creator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreator(String value) {
        this.creator = value;
    }

    /**
     * Gets the value of the contextObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextObject() {
        return contextObject;
    }

    /**
     * Sets the value of the contextObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextObject(String value) {
        this.contextObject = value;
    }

    /**
     * Gets the value of the messageTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageTitle() {
        return messageTitle;
    }

    /**
     * Sets the value of the messageTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageTitle(String value) {
        this.messageTitle = value;
    }

    /**
     * Gets the value of the messageBody property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageBody() {
        return messageBody;
    }

    /**
     * Sets the value of the messageBody property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageBody(String value) {
        this.messageBody = value;
    }

    /**
     * Gets the value of the notifyLevel property.
     * 
     */
    public int getNotifyLevel() {
        return notifyLevel;
    }

    /**
     * Sets the value of the notifyLevel property.
     * 
     */
    public void setNotifyLevel(int value) {
        this.notifyLevel = value;
    }

    /**
     * Gets the value of the notifyees property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getNotifyees() {
        return notifyees;
    }

    /**
     * Sets the value of the notifyees property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setNotifyees(ArrayOfString value) {
        this.notifyees = value;
    }

    /**
     * Gets the value of the internal property.
     * 
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * Sets the value of the internal property.
     * 
     */
    public void setInternal(boolean value) {
        this.internal = value;
    }

}
