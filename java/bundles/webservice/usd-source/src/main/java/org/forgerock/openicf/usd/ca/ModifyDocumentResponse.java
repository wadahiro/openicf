
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
 *         &lt;element name="modifyDocumentReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "modifyDocumentReturn"
})
@XmlRootElement(name = "modifyDocumentResponse")
public class ModifyDocumentResponse {

    @XmlElement(required = true)
    protected String modifyDocumentReturn;

    /**
     * Gets the value of the modifyDocumentReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModifyDocumentReturn() {
        return modifyDocumentReturn;
    }

    /**
     * Sets the value of the modifyDocumentReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifyDocumentReturn(String value) {
        this.modifyDocumentReturn = value;
    }

}
