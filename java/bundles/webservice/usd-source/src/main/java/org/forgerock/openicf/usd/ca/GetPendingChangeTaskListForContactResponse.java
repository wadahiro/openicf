
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
 *         &lt;element name="getPendingChangeTaskListForContactReturn" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ListResult"/>
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
    "getPendingChangeTaskListForContactReturn"
})
@XmlRootElement(name = "getPendingChangeTaskListForContactResponse")
public class GetPendingChangeTaskListForContactResponse {

    @XmlElement(required = true)
    protected ListResult getPendingChangeTaskListForContactReturn;

    /**
     * Gets the value of the getPendingChangeTaskListForContactReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ListResult }
     *     
     */
    public ListResult getGetPendingChangeTaskListForContactReturn() {
        return getPendingChangeTaskListForContactReturn;
    }

    /**
     * Sets the value of the getPendingChangeTaskListForContactReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListResult }
     *     
     */
    public void setGetPendingChangeTaskListForContactReturn(ListResult value) {
        this.getPendingChangeTaskListForContactReturn = value;
    }

}
