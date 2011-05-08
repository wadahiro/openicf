
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
 *         &lt;element name="getKDListPerAttmntReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "getKDListPerAttmntReturn"
})
@XmlRootElement(name = "getKDListPerAttmntResponse")
public class GetKDListPerAttmntResponse {

    @XmlElement(required = true)
    protected String getKDListPerAttmntReturn;

    /**
     * Gets the value of the getKDListPerAttmntReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetKDListPerAttmntReturn() {
        return getKDListPerAttmntReturn;
    }

    /**
     * Sets the value of the getKDListPerAttmntReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetKDListPerAttmntReturn(String value) {
        this.getKDListPerAttmntReturn = value;
    }

}
