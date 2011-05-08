
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
 *         &lt;element name="createChangeOrderReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newChangeHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newChangeNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createChangeOrderReturn",
    "newChangeHandle",
    "newChangeNumber"
})
@XmlRootElement(name = "createChangeOrderResponse")
public class CreateChangeOrderResponse {

    @XmlElement(required = true)
    protected String createChangeOrderReturn;
    @XmlElement(required = true)
    protected String newChangeHandle;
    @XmlElement(required = true)
    protected String newChangeNumber;

    /**
     * Gets the value of the createChangeOrderReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateChangeOrderReturn() {
        return createChangeOrderReturn;
    }

    /**
     * Sets the value of the createChangeOrderReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateChangeOrderReturn(String value) {
        this.createChangeOrderReturn = value;
    }

    /**
     * Gets the value of the newChangeHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewChangeHandle() {
        return newChangeHandle;
    }

    /**
     * Sets the value of the newChangeHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewChangeHandle(String value) {
        this.newChangeHandle = value;
    }

    /**
     * Gets the value of the newChangeNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewChangeNumber() {
        return newChangeNumber;
    }

    /**
     * Sets the value of the newChangeNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewChangeNumber(String value) {
        this.newChangeNumber = value;
    }

}
