
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
 *         &lt;element name="parentHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="childHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "parentHandle",
    "childHandle"
})
@XmlRootElement(name = "createAssetParentChildRelationship")
public class CreateAssetParentChildRelationship {

    protected int sid;
    @XmlElement(required = true)
    protected String parentHandle;
    @XmlElement(required = true)
    protected String childHandle;

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
     * Gets the value of the parentHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentHandle() {
        return parentHandle;
    }

    /**
     * Sets the value of the parentHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentHandle(String value) {
        this.parentHandle = value;
    }

    /**
     * Gets the value of the childHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildHandle() {
        return childHandle;
    }

    /**
     * Sets the value of the childHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildHandle(String value) {
        this.childHandle = value;
    }

}
