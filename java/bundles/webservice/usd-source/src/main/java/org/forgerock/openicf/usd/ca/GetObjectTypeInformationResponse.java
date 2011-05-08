
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
 *         &lt;element name="getObjectTypeInformationReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "getObjectTypeInformationReturn"
})
@XmlRootElement(name = "getObjectTypeInformationResponse")
public class GetObjectTypeInformationResponse {

    @XmlElement(required = true)
    protected String getObjectTypeInformationReturn;

    /**
     * Gets the value of the getObjectTypeInformationReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetObjectTypeInformationReturn() {
        return getObjectTypeInformationReturn;
    }

    /**
     * Sets the value of the getObjectTypeInformationReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetObjectTypeInformationReturn(String value) {
        this.getObjectTypeInformationReturn = value;
    }

}
