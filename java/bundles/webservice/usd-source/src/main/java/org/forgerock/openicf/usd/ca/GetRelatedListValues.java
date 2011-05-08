
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
 *         &lt;element name="objectHandle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="listName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numToFetch" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attributes" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ArrayOfString"/>
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
    "sid",
    "objectHandle",
    "listName",
    "numToFetch",
    "attributes",
    "getRelatedListValuesResult",
    "numRowsFound"
})
@XmlRootElement(name = "getRelatedListValues")
public class GetRelatedListValues {

    protected int sid;
    @XmlElement(required = true)
    protected String objectHandle;
    @XmlElement(required = true)
    protected String listName;
    protected int numToFetch;
    @XmlElement(required = true)
    protected ArrayOfString attributes;
    @XmlElement(required = true)
    protected String getRelatedListValuesResult;
    protected int numRowsFound;

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
     * Gets the value of the objectHandle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectHandle() {
        return objectHandle;
    }

    /**
     * Sets the value of the objectHandle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectHandle(String value) {
        this.objectHandle = value;
    }

    /**
     * Gets the value of the listName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListName() {
        return listName;
    }

    /**
     * Sets the value of the listName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListName(String value) {
        this.listName = value;
    }

    /**
     * Gets the value of the numToFetch property.
     * 
     */
    public int getNumToFetch() {
        return numToFetch;
    }

    /**
     * Sets the value of the numToFetch property.
     * 
     */
    public void setNumToFetch(int value) {
        this.numToFetch = value;
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
