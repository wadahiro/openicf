
package com.waveset.adapter.ca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="resultSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="descending" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "resultSize",
    "descending"
})
@XmlRootElement(name = "getQuestionsAsked")
public class GetQuestionsAsked {

    protected int sid;
    protected int resultSize;
    protected boolean descending;

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

}
