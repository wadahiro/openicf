
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
 *         &lt;element name="createIssueReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newIssueHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newIssueNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createIssueReturn",
    "newIssueHandle",
    "newIssueNumber"
})
@XmlRootElement(name = "createIssueResponse")
public class CreateIssueResponse {

    @XmlElement(required = true)
    protected String createIssueReturn;
    @XmlElement(required = true)
    protected String newIssueHandle;
    @XmlElement(required = true)
    protected String newIssueNumber;

    /**
     * Gets the value of the createIssueReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateIssueReturn() {
        return createIssueReturn;
    }

    /**
     * Sets the value of the createIssueReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateIssueReturn(String value) {
        this.createIssueReturn = value;
    }

    /**
     * Gets the value of the newIssueHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewIssueHandle() {
        return newIssueHandle;
    }

    /**
     * Sets the value of the newIssueHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewIssueHandle(String value) {
        this.newIssueHandle = value;
    }

    /**
     * Gets the value of the newIssueNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewIssueNumber() {
        return newIssueNumber;
    }

    /**
     * Sets the value of the newIssueNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewIssueNumber(String value) {
        this.newIssueNumber = value;
    }

}
