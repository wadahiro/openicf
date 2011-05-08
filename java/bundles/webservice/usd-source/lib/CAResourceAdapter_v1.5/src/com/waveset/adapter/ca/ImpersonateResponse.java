
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
 *         &lt;element name="impersonateReturn" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "impersonateReturn"
})
@XmlRootElement(name = "impersonateResponse")
public class ImpersonateResponse {

    protected int impersonateReturn;

    /**
     * Gets the value of the impersonateReturn property.
     * 
     */
    public int getImpersonateReturn() {
        return impersonateReturn;
    }

    /**
     * Sets the value of the impersonateReturn property.
     * 
     */
    public void setImpersonateReturn(int value) {
        this.impersonateReturn = value;
    }

}
