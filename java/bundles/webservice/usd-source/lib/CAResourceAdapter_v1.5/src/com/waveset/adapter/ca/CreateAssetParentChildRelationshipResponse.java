
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
 *         &lt;element name="createAssetParentChildRelationshipReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createAssetParentChildRelationshipReturn"
})
@XmlRootElement(name = "createAssetParentChildRelationshipResponse")
public class CreateAssetParentChildRelationshipResponse {

    @XmlElement(required = true)
    protected String createAssetParentChildRelationshipReturn;

    /**
     * Gets the value of the createAssetParentChildRelationshipReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateAssetParentChildRelationshipReturn() {
        return createAssetParentChildRelationshipReturn;
    }

    /**
     * Sets the value of the createAssetParentChildRelationshipReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateAssetParentChildRelationshipReturn(String value) {
        this.createAssetParentChildRelationshipReturn = value;
    }

}
