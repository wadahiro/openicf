
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
 *         &lt;element name="problem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resultSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="properties" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sortBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descending" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="relatedCategories" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="searchType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="matchType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="searchField" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="categoryPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "problem",
    "resultSize",
    "properties",
    "sortBy",
    "descending",
    "relatedCategories",
    "searchType",
    "matchType",
    "searchField",
    "categoryPath",
    "whereClause",
    "maxDocIDs"
})
@XmlRootElement(name = "search")
public class Search {

    protected int sid;
    @XmlElement(required = true)
    protected String problem;
    protected int resultSize;
    @XmlElement(required = true)
    protected String properties;
    @XmlElement(required = true)
    protected String sortBy;
    protected boolean descending;
    protected boolean relatedCategories;
    protected int searchType;
    protected int matchType;
    protected int searchField;
    @XmlElement(required = true)
    protected String categoryPath;
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
     * Gets the value of the problem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProblem() {
        return problem;
    }

    /**
     * Sets the value of the problem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProblem(String value) {
        this.problem = value;
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
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProperties(String value) {
        this.properties = value;
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
     * Gets the value of the relatedCategories property.
     * 
     */
    public boolean isRelatedCategories() {
        return relatedCategories;
    }

    /**
     * Sets the value of the relatedCategories property.
     * 
     */
    public void setRelatedCategories(boolean value) {
        this.relatedCategories = value;
    }

    /**
     * Gets the value of the searchType property.
     * 
     */
    public int getSearchType() {
        return searchType;
    }

    /**
     * Sets the value of the searchType property.
     * 
     */
    public void setSearchType(int value) {
        this.searchType = value;
    }

    /**
     * Gets the value of the matchType property.
     * 
     */
    public int getMatchType() {
        return matchType;
    }

    /**
     * Sets the value of the matchType property.
     * 
     */
    public void setMatchType(int value) {
        this.matchType = value;
    }

    /**
     * Gets the value of the searchField property.
     * 
     */
    public int getSearchField() {
        return searchField;
    }

    /**
     * Sets the value of the searchField property.
     * 
     */
    public void setSearchField(int value) {
        this.searchField = value;
    }

    /**
     * Gets the value of the categoryPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryPath() {
        return categoryPath;
    }

    /**
     * Sets the value of the categoryPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryPath(String value) {
        this.categoryPath = value;
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
