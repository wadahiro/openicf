
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
 *         &lt;element name="assetHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributes" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfString"/>
 *         &lt;element name="getAssetExtInfoResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extensionHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="extensionName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "assetHandle",
    "attributes",
    "getAssetExtInfoResult",
    "extensionHandle",
    "extensionName"
})
@XmlRootElement(name = "getAssetExtensionInformation")
public class GetAssetExtensionInformation {

    protected int sid;
    @XmlElement(required = true)
    protected String assetHandle;
    @XmlElement(required = true)
    protected ArrayOfString attributes;
    @XmlElement(required = true)
    protected String getAssetExtInfoResult;
    @XmlElement(required = true)
    protected String extensionHandle;
    @XmlElement(required = true)
    protected String extensionName;

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
     * Gets the value of the assetHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetHandle() {
        return assetHandle;
    }

    /**
     * Sets the value of the assetHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetHandle(String value) {
        this.assetHandle = value;
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
     * Gets the value of the getAssetExtInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetAssetExtInfoResult() {
        return getAssetExtInfoResult;
    }

    /**
     * Sets the value of the getAssetExtInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetAssetExtInfoResult(String value) {
        this.getAssetExtInfoResult = value;
    }

    /**
     * Gets the value of the extensionHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionHandle() {
        return extensionHandle;
    }

    /**
     * Sets the value of the extensionHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionHandle(String value) {
        this.extensionHandle = value;
    }

    /**
     * Gets the value of the extensionName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionName() {
        return extensionName;
    }

    /**
     * Sets the value of the extensionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionName(String value) {
        this.extensionName = value;
    }

}
