
package org.forgerock.openicf.usd.ca;

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
 *         &lt;element name="catId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="getCategoryPaths" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "catId",
    "getCategoryPaths"
})
@XmlRootElement(name = "getCategory")
public class GetCategory {

    protected int sid;
    protected int catId;
    protected boolean getCategoryPaths;

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
     * Gets the value of the catId property.
     * 
     */
    public int getCatId() {
        return catId;
    }

    /**
     * Sets the value of the catId property.
     * 
     */
    public void setCatId(int value) {
        this.catId = value;
    }

    /**
     * Gets the value of the getCategoryPaths property.
     * 
     */
    public boolean isGetCategoryPaths() {
        return getCategoryPaths;
    }

    /**
     * Sets the value of the getCategoryPaths property.
     * 
     */
    public void setGetCategoryPaths(boolean value) {
        this.getCategoryPaths = value;
    }

}
