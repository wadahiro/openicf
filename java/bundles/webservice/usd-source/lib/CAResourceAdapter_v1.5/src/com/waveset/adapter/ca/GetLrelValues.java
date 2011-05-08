
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
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="contextObject" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="lrelName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="startIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="endIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attributes" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfString"/>
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
    "contextObject",
    "lrelName",
    "startIndex",
    "endIndex",
    "attributes"
})
@XmlRootElement(name = "getLrelValues")
public class GetLrelValues {

    protected int sid;
    @XmlElement(required = true)
    protected String contextObject;
    @XmlElement(required = true)
    protected String lrelName;
    protected int startIndex;
    protected int endIndex;
    @XmlElement(required = true)
    protected ArrayOfString attributes;

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
     * Gets the value of the contextObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextObject() {
        return contextObject;
    }

    /**
     * Sets the value of the contextObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextObject(String value) {
        this.contextObject = value;
    }

    /**
     * Gets the value of the lrelName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLrelName() {
        return lrelName;
    }

    /**
     * Sets the value of the lrelName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLrelName(String value) {
        this.lrelName = value;
    }

    /**
     * Gets the value of the startIndex property.
     * 
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * Sets the value of the startIndex property.
     * 
     */
    public void setStartIndex(int value) {
        this.startIndex = value;
    }

    /**
     * Gets the value of the endIndex property.
     * 
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * Sets the value of the endIndex property.
     * 
     */
    public void setEndIndex(int value) {
        this.endIndex = value;
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

}
