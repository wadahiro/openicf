
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
 *         &lt;element name="docId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="rating" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="multiplier" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ticketPerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="onTicketAccept" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="solveUserProblem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isDefault" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "docId",
    "rating",
    "multiplier",
    "ticketPerId",
    "onTicketAccept",
    "solveUserProblem",
    "isDefault"
})
@XmlRootElement(name = "rateDocument")
public class RateDocument {

    protected int sid;
    protected int docId;
    protected int rating;
    protected int multiplier;
    @XmlElement(required = true)
    protected String ticketPerId;
    protected boolean onTicketAccept;
    protected boolean solveUserProblem;
    protected boolean isDefault;

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
     * Gets the value of the docId property.
     * 
     */
    public int getDocId() {
        return docId;
    }

    /**
     * Sets the value of the docId property.
     * 
     */
    public void setDocId(int value) {
        this.docId = value;
    }

    /**
     * Gets the value of the rating property.
     * 
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the value of the rating property.
     * 
     */
    public void setRating(int value) {
        this.rating = value;
    }

    /**
     * Gets the value of the multiplier property.
     * 
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * Sets the value of the multiplier property.
     * 
     */
    public void setMultiplier(int value) {
        this.multiplier = value;
    }

    /**
     * Gets the value of the ticketPerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTicketPerId() {
        return ticketPerId;
    }

    /**
     * Sets the value of the ticketPerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTicketPerId(String value) {
        this.ticketPerId = value;
    }

    /**
     * Gets the value of the onTicketAccept property.
     * 
     */
    public boolean isOnTicketAccept() {
        return onTicketAccept;
    }

    /**
     * Sets the value of the onTicketAccept property.
     * 
     */
    public void setOnTicketAccept(boolean value) {
        this.onTicketAccept = value;
    }

    /**
     * Gets the value of the solveUserProblem property.
     * 
     */
    public boolean isSolveUserProblem() {
        return solveUserProblem;
    }

    /**
     * Sets the value of the solveUserProblem property.
     * 
     */
    public void setSolveUserProblem(boolean value) {
        this.solveUserProblem = value;
    }

    /**
     * Gets the value of the isDefault property.
     * 
     */
    public boolean isIsDefault() {
        return isDefault;
    }

    /**
     * Sets the value of the isDefault property.
     * 
     */
    public void setIsDefault(boolean value) {
        this.isDefault = value;
    }

}
