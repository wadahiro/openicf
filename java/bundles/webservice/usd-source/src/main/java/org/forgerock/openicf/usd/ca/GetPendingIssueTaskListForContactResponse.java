
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
 *         &lt;element name="getPendingIssueTaskListForContactReturn" type="{http://www.ca.com/UnicenterServicePlus/ServiceDesk}ListResult"/>
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
    "getPendingIssueTaskListForContactReturn"
})
@XmlRootElement(name = "getPendingIssueTaskListForContactResponse")
public class GetPendingIssueTaskListForContactResponse {

    @XmlElement(required = true)
    protected ListResult getPendingIssueTaskListForContactReturn;

    /**
     * Gets the value of the getPendingIssueTaskListForContactReturn property.
     * 
     * @return
     *     possible object is
     *     {@link ListResult }
     *     
     */
    public ListResult getGetPendingIssueTaskListForContactReturn() {
        return getPendingIssueTaskListForContactReturn;
    }

    /**
     * Sets the value of the getPendingIssueTaskListForContactReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListResult }
     *     
     */
    public void setGetPendingIssueTaskListForContactReturn(ListResult value) {
        this.getPendingIssueTaskListForContactReturn = value;
    }

}
