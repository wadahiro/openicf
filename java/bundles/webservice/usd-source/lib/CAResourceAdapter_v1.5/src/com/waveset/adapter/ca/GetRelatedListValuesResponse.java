
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
 *         &lt;element name="getRelatedListValuesResult" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numRowsFound" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "getRelatedListValuesResult",
    "numRowsFound"
})
@XmlRootElement(name = "getRelatedListValuesResponse")
public class GetRelatedListValuesResponse {

    @XmlElement(required = true)
    protected String getRelatedListValuesResult;
    protected int numRowsFound;

    /**
     * Gets the value of the getRelatedListValuesResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetRelatedListValuesResult() {
        return getRelatedListValuesResult;
    }

    /**
     * Sets the value of the getRelatedListValuesResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetRelatedListValuesResult(String value) {
        this.getRelatedListValuesResult = value;
    }

    /**
     * Gets the value of the numRowsFound property.
     * 
     */
    public int getNumRowsFound() {
        return numRowsFound;
    }

    /**
     * Sets the value of the numRowsFound property.
     * 
     */
    public void setNumRowsFound(int value) {
        this.numRowsFound = value;
    }

}
