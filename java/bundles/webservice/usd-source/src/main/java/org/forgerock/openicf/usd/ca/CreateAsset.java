
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
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attrVals" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfString"/>
 *         &lt;element name="attributes" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfString"/>
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
    "sid",
    "attrVals",
    "attributes",
    "createAssetResult",
    "newAssetHandle",
    "newExtensionHandle",
    "newExtensionName"
})
@XmlRootElement(name = "createAsset")
public class CreateAsset {

    protected int sid;
    @XmlElement(required = true)
    protected ArrayOfString attrVals;
    @XmlElement(required = true)
    protected ArrayOfString attributes;
    @XmlElement(required = true)
    protected String createAssetResult;
    @XmlElement(required = true)
    protected String newAssetHandle;
    @XmlElement(required = true)
    protected String newExtensionHandle;
    @XmlElement(required = true)
    protected String newExtensionName;

    /**
     * Gets the value of the sid property.
     * 
     */
    public int getSid() {
        return sid;
    }

    /**
     * Sets the value of the sid property.
     * 
     */
    public void setSid(int value) {
        this.sid = value;
    }

    /**
     * Gets the value of the attrVals property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getAttrVals() {
        return attrVals;
    }

    /**
     * Sets the value of the attrVals property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setAttrVals(ArrayOfString value) {
        this.attrVals = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setAttributes(ArrayOfString value) {
        this.attributes = value;
    }

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
