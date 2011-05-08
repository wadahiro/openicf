
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
 *         &lt;element name="deleteCommentReturn" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "deleteCommentReturn"
})
@XmlRootElement(name = "deleteCommentResponse")
public class DeleteCommentResponse {

    protected int deleteCommentReturn;

    /**
     * Gets the value of the deleteCommentReturn property.
     * 
     */
    public int getDeleteCommentReturn() {
        return deleteCommentReturn;
    }

    /**
     * Sets the value of the deleteCommentReturn property.
     * 
     */
    public void setDeleteCommentReturn(int value) {
        this.deleteCommentReturn = value;
    }

}
