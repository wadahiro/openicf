
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
 *         &lt;element name="docId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="propertyList" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="relatedDoc" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="getAttmnt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="getHistory" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="getComments" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="getNotiList" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "docId",
    "propertyList",
    "relatedDoc",
    "getAttmnt",
    "getHistory",
    "getComments",
    "getNotiList"
})
@XmlRootElement(name = "getDocument")
public class GetDocument {

    protected int sid;
    protected int docId;
    @XmlElement(required = true)
    protected String propertyList;
    protected boolean relatedDoc;
    protected boolean getAttmnt;
    protected boolean getHistory;
    protected boolean getComments;
    protected boolean getNotiList;

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
     * Gets the value of the docId property.
     * 
     */
    public int getDocId() {
        return docId;
    }

    /**
     * Sets the value of the docId property.
     * 
     */
    public void setDocId(int value) {
        this.docId = value;
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
     * Gets the value of the relatedDoc property.
     * 
     */
    public boolean isRelatedDoc() {
        return relatedDoc;
    }

    /**
     * Sets the value of the relatedDoc property.
     * 
     */
    public void setRelatedDoc(boolean value) {
        this.relatedDoc = value;
    }

    /**
     * Gets the value of the getAttmnt property.
     * 
     */
    public boolean isGetAttmnt() {
        return getAttmnt;
    }

    /**
     * Sets the value of the getAttmnt property.
     * 
     */
    public void setGetAttmnt(boolean value) {
        this.getAttmnt = value;
    }

    /**
     * Gets the value of the getHistory property.
     * 
     */
    public boolean isGetHistory() {
        return getHistory;
    }

    /**
     * Sets the value of the getHistory property.
     * 
     */
    public void setGetHistory(boolean value) {
        this.getHistory = value;
    }

    /**
     * Gets the value of the getComments property.
     * 
     */
    public boolean isGetComments() {
        return getComments;
    }

    /**
     * Sets the value of the getComments property.
     * 
     */
    public void setGetComments(boolean value) {
        this.getComments = value;
    }

    /**
     * Gets the value of the getNotiList property.
     * 
     */
    public boolean isGetNotiList() {
        return getNotiList;
    }

    /**
     * Sets the value of the getNotiList property.
     * 
     */
    public void setGetNotiList(boolean value) {
        this.getNotiList = value;
    }

}
