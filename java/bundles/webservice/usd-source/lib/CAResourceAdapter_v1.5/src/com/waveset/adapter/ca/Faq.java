
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
 *         &lt;element name="categoryIds" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resultSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="propertyList" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descending" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="whereClause" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="maxDocIDs" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "categoryIds",
    "resultSize",
    "propertyList",
    "sortBy",
    "descending",
    "whereClause",
    "maxDocIDs"
})
@XmlRootElement(name = "faq")
public class Faq {

    protected int sid;
    @XmlElement(required = true)
    protected String categoryIds;
    protected int resultSize;
    @XmlElement(required = true)
    protected String propertyList;
    @XmlElement(required = true)
    protected String sortBy;
    protected boolean descending;
    @XmlElement(required = true)
    protected String whereClause;
    protected int maxDocIDs;

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
     * Gets the value of the categoryIds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryIds() {
        return categoryIds;
    }

    /**
     * Sets the value of the categoryIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryIds(String value) {
        this.categoryIds = value;
    }

    /**
     * Gets the value of the resultSize property.
     * 
     */
    public int getResultSize() {
        return resultSize;
    }

    /**
     * Sets the value of the resultSize property.
     * 
     */
    public void setResultSize(int value) {
        this.resultSize = value;
    }

    /**
     * Gets the value of the propertyList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPropertyList() {
        return propertyList;
    }

    /**
     * Sets the value of the propertyList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPropertyList(String value) {
        this.propertyList = value;
    }

    /**
     * Gets the value of the sortBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Sets the value of the sortBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortBy(String value) {
        this.sortBy = value;
    }

    /**
     * Gets the value of the descending property.
     * 
     */
    public boolean isDescending() {
        return descending;
    }

    /**
     * Sets the value of the descending property.
     * 
     */
    public void setDescending(boolean value) {
        this.descending = value;
    }

    /**
     * Gets the value of the whereClause property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhereClause() {
        return whereClause;
    }

    /**
     * Sets the value of the whereClause property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhereClause(String value) {
        this.whereClause = value;
    }

    /**
     * Gets the value of the maxDocIDs property.
     * 
     */
    public int getMaxDocIDs() {
        return maxDocIDs;
    }

    /**
     * Sets the value of the maxDocIDs property.
     * 
     */
    public void setMaxDocIDs(int value) {
        this.maxDocIDs = value;
    }

}
