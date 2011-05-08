
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
 *         &lt;element name="attmntFolderLinkCountReturn" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "attmntFolderLinkCountReturn"
})
@XmlRootElement(name = "attmntFolderLinkCountResponse")
public class AttmntFolderLinkCountResponse {

    protected int attmntFolderLinkCountReturn;

    /**
     * Gets the value of the attmntFolderLinkCountReturn property.
     * 
     */
    public int getAttmntFolderLinkCountReturn() {
        return attmntFolderLinkCountReturn;
    }

    /**
     * Sets the value of the attmntFolderLinkCountReturn property.
     * 
     */
    public void setAttmntFolderLinkCountReturn(int value) {
        this.attmntFolderLinkCountReturn = value;
    }

}
