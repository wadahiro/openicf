
package org.forgerock.openicf.usd.ca;

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
 *         &lt;element name="objectHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setAssignee" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="newAssigneeHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setGroup" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="newGroupHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setOrganization" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="newOrganizationHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="setPriority" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="newPriorityHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "objectHandle",
    "description",
    "setAssignee",
    "newAssigneeHandle",
    "setGroup",
    "newGroupHandle",
    "setOrganization",
    "newOrganizationHandle",
    "setPriority",
    "newPriorityHandle"
})
@XmlRootElement(name = "escalate")
public class Escalate {

    protected int sid;
    @XmlElement(required = true)
    protected String creator;
    @XmlElement(required = true)
    protected String objectHandle;
    @XmlElement(required = true)
    protected String description;
    protected boolean setAssignee;
    @XmlElement(required = true)
    protected String newAssigneeHandle;
    protected boolean setGroup;
    @XmlElement(required = true)
    protected String newGroupHandle;
    protected boolean setOrganization;
    @XmlElement(required = true)
    protected String newOrganizationHandle;
    protected boolean setPriority;
    @XmlElement(required = true)
    protected String newPriorityHandle;

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
     * Gets the value of the objectHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectHandle() {
        return objectHandle;
    }

    /**
     * Sets the value of the objectHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectHandle(String value) {
        this.objectHandle = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the setAssignee property.
     * 
     */
    public boolean isSetAssignee() {
        return setAssignee;
    }

    /**
     * Sets the value of the setAssignee property.
     * 
     */
    public void setSetAssignee(boolean value) {
        this.setAssignee = value;
    }

    /**
     * Gets the value of the newAssigneeHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewAssigneeHandle() {
        return newAssigneeHandle;
    }

    /**
     * Sets the value of the newAssigneeHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewAssigneeHandle(String value) {
        this.newAssigneeHandle = value;
    }

    /**
     * Gets the value of the setGroup property.
     * 
     */
    public boolean isSetGroup() {
        return setGroup;
    }

    /**
     * Sets the value of the setGroup property.
     * 
     */
    public void setSetGroup(boolean value) {
        this.setGroup = value;
    }

    /**
     * Gets the value of the newGroupHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewGroupHandle() {
        return newGroupHandle;
    }

    /**
     * Sets the value of the newGroupHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewGroupHandle(String value) {
        this.newGroupHandle = value;
    }

    /**
     * Gets the value of the setOrganization property.
     * 
     */
    public boolean isSetOrganization() {
        return setOrganization;
    }

    /**
     * Sets the value of the setOrganization property.
     * 
     */
    public void setSetOrganization(boolean value) {
        this.setOrganization = value;
    }

    /**
     * Gets the value of the newOrganizationHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewOrganizationHandle() {
        return newOrganizationHandle;
    }

    /**
     * Sets the value of the newOrganizationHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewOrganizationHandle(String value) {
        this.newOrganizationHandle = value;
    }

    /**
     * Gets the value of the setPriority property.
     * 
     */
    public boolean isSetPriority() {
        return setPriority;
    }

    /**
     * Sets the value of the setPriority property.
     * 
     */
    public void setSetPriority(boolean value) {
        this.setPriority = value;
    }

    /**
     * Gets the value of the newPriorityHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewPriorityHandle() {
        return newPriorityHandle;
    }

    /**
     * Sets the value of the newPriorityHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewPriorityHandle(String value) {
        this.newPriorityHandle = value;
    }

}
