
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
 *         &lt;element name="removeAttachmentReturn" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "removeAttachmentReturn"
})
@XmlRootElement(name = "removeAttachmentResponse")
public class RemoveAttachmentResponse {

    protected int removeAttachmentReturn;

    /**
     * Gets the value of the removeAttachmentReturn property.
     * 
     */
    public int getRemoveAttachmentReturn() {
        return removeAttachmentReturn;
    }

    /**
     * Sets the value of the removeAttachmentReturn property.
     * 
     */
    public void setRemoveAttachmentReturn(int value) {
        this.removeAttachmentReturn = value;
    }

}
