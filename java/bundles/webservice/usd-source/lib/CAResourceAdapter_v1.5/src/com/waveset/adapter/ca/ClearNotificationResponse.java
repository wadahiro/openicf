
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
 *         &lt;element name="clearNotificationReturn" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "clearNotificationReturn"
})
@XmlRootElement(name = "clearNotificationResponse")
public class ClearNotificationResponse {

    protected int clearNotificationReturn;

    /**
     * Gets the value of the clearNotificationReturn property.
     * 
     */
    public int getClearNotificationReturn() {
        return clearNotificationReturn;
    }

    /**
     * Sets the value of the clearNotificationReturn property.
     * 
     */
    public void setClearNotificationReturn(int value) {
        this.clearNotificationReturn = value;
    }

}
