
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
 *         &lt;element name="getGroupMemberListValuesReturn" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "getGroupMemberListValuesReturn"
})
@XmlRootElement(name = "getGroupMemberListValuesResponse")
public class GetGroupMemberListValuesResponse {

    @XmlElement(required = true)
    protected String getGroupMemberListValuesReturn;

    /**
     * Gets the value of the getGroupMemberListValuesReturn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetGroupMemberListValuesReturn() {
        return getGroupMemberListValuesReturn;
    }

    /**
     * Sets the value of the getGroupMemberListValuesReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetGroupMemberListValuesReturn(String value) {
        this.getGroupMemberListValuesReturn = value;
    }

}
