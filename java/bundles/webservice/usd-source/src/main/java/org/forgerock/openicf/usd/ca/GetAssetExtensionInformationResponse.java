
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
    "getAssetExtInfoResult",
    "extensionHandle",
    "extensionName"
})
@XmlRootElement(name = "getAssetExtensionInformationResponse")
public class GetAssetExtensionInformationResponse {

    @XmlElement(required = true)
    protected String getAssetExtInfoResult;
    @XmlElement(required = true)
    protected String extensionHandle;
    @XmlElement(required = true)
    protected String extensionName;

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
