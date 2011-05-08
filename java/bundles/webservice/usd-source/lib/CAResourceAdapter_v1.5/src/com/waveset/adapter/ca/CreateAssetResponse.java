
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
 *         &lt;element name="createAssetResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newAssetHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newExtensionHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newExtensionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "createAssetResult",
    "newAssetHandle",
    "newExtensionHandle",
    "newExtensionName"
})
@XmlRootElement(name = "createAssetResponse")
public class CreateAssetResponse {

    @XmlElement(required = true)
    protected String createAssetResult;
    @XmlElement(required = true)
    protected String newAssetHandle;
    @XmlElement(required = true)
    protected String newExtensionHandle;
    @XmlElement(required = true)
    protected String newExtensionName;

    /**
     * Gets the value of the createAssetResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateAssetResult() {
        return createAssetResult;
    }

    /**
     * Sets the value of the createAssetResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateAssetResult(String value) {
        this.createAssetResult = value;
    }

    /**
     * Gets the value of the newAssetHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewAssetHandle() {
        return newAssetHandle;
    }

    /**
     * Sets the value of the newAssetHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewAssetHandle(String value) {
        this.newAssetHandle = value;
    }

    /**
     * Gets the value of the newExtensionHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewExtensionHandle() {
        return newExtensionHandle;
    }

    /**
     * Sets the value of the newExtensionHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewExtensionHandle(String value) {
        this.newExtensionHandle = value;
    }

    /**
     * Gets the value of the newExtensionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewExtensionName() {
        return newExtensionName;
    }

    /**
     * Sets the value of the newExtensionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewExtensionName(String value) {
        this.newExtensionName = value;
    }

}
