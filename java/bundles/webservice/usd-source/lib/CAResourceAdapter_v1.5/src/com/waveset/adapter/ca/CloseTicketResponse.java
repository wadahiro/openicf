
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
 *         &lt;element name="closeTicketReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "closeTicketReturn"
})
@XmlRootElement(name = "closeTicketResponse")
public class CloseTicketResponse {

    @XmlElement(required = true)
    protected String closeTicketReturn;

    /**
     * Gets the value of the closeTicketReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCloseTicketReturn() {
        return closeTicketReturn;
    }

    /**
     * Sets the value of the closeTicketReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCloseTicketReturn(String value) {
        this.closeTicketReturn = value;
    }

}
