
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
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="problem_type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="asset" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="duplication_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "sid",
    "description",
    "problemType",
    "userid",
    "asset",
    "duplicationId",
    "newTicketHandle",
    "newTicketNumber",
    "returnUserData",
    "returnApplicationData"
})
@XmlRootElement(name = "createTicket")
public class CreateTicket {

    protected int sid;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "problem_type", required = true)
    protected String problemType;
    @XmlElement(required = true)
    protected String userid;
    @XmlElement(required = true)
    protected String asset;
    @XmlElement(name = "duplication_id", required = true)
    protected String duplicationId;
    @XmlElement(required = true)
    protected String newTicketHandle;
    @XmlElement(required = true)
    protected String newTicketNumber;
    @XmlElement(required = true)
    protected String returnUserData;
    @XmlElement(required = true)
    protected String returnApplicationData;

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
     * Gets the value of the problemType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblemType() {
        return problemType;
    }

    /**
     * Sets the value of the problemType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblemType(String value) {
        this.problemType = value;
    }

    /**
     * Gets the value of the userid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Sets the value of the userid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserid(String value) {
        this.userid = value;
    }

    /**
     * Gets the value of the asset property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAsset() {
        return asset;
    }

    /**
     * Sets the value of the asset property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAsset(String value) {
        this.asset = value;
    }

    /**
     * Gets the value of the duplicationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDuplicationId() {
        return duplicationId;
    }

    /**
     * Sets the value of the duplicationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDuplicationId(String value) {
        this.duplicationId = value;
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
