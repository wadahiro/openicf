
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
 *         &lt;element name="createRequestReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newRequestHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newRequestNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createRequestReturn",
    "newRequestHandle",
    "newRequestNumber"
})
@XmlRootElement(name = "createRequestResponse")
public class CreateRequestResponse {

    @XmlElement(required = true)
    protected String createRequestReturn;
    @XmlElement(required = true)
    protected String newRequestHandle;
    @XmlElement(required = true)
    protected String newRequestNumber;

    /**
     * Gets the value of the createRequestReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateRequestReturn() {
        return createRequestReturn;
    }

    /**
     * Sets the value of the createRequestReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateRequestReturn(String value) {
        this.createRequestReturn = value;
    }

    /**
     * Gets the value of the newRequestHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewRequestHandle() {
        return newRequestHandle;
    }

    /**
     * Sets the value of the newRequestHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewRequestHandle(String value) {
        this.newRequestHandle = value;
    }

    /**
     * Gets the value of the newRequestNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewRequestNumber() {
        return newRequestNumber;
    }

    /**
     * Sets the value of the newRequestNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewRequestNumber(String value) {
        this.newRequestNumber = value;
    }

}
