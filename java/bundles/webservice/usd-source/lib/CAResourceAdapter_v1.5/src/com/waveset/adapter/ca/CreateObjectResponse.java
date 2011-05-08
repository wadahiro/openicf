
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
 *         &lt;element name="createObjectResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createObjectResult",
    "newHandle"
})
@XmlRootElement(name = "createObjectResponse")
public class CreateObjectResponse {

    @XmlElement(required = true)
    protected String createObjectResult;
    @XmlElement(required = true)
    protected String newHandle;

    /**
     * Gets the value of the createObjectResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateObjectResult() {
        return createObjectResult;
    }

    /**
     * Sets the value of the createObjectResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateObjectResult(String value) {
        this.createObjectResult = value;
    }

    /**
     * Gets the value of the newHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewHandle() {
        return newHandle;
    }

    /**
     * Sets the value of the newHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewHandle(String value) {
        this.newHandle = value;
    }

}
