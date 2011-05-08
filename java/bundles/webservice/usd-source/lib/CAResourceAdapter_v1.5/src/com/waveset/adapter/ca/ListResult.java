
package com.waveset.adapter.ca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listHandle" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="listLength" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListResult", propOrder = {
    "listHandle",
    "listLength"
})
public class ListResult {

    protected int listHandle;
    protected int listLength;

    /**
     * Gets the value of the listHandle property.
     * 
     */
    public int getListHandle() {
        return listHandle;
    }

    /**
     * Sets the value of the listHandle property.
     * 
     */
    public void setListHandle(int value) {
        this.listHandle = value;
    }

    /**
     * Gets the value of the listLength property.
     * 
     */
    public int getListLength() {
        return listLength;
    }

    /**
     * Sets the value of the listLength property.
     * 
     */
    public void setListLength(int value) {
        this.listLength = value;
    }

}
