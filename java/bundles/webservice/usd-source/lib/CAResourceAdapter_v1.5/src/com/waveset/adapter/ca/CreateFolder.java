
package com.waveset.adapter.ca;

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
 *         &lt;element name="sid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="parentFolderId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="repId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="folderType" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="folderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "parentFolderId",
    "repId",
    "folderType",
    "description",
    "folderName"
})
@XmlRootElement(name = "createFolder")
public class CreateFolder {

    protected int sid;
    protected int parentFolderId;
    protected int repId;
    protected int folderType;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String folderName;

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
     * Gets the value of the parentFolderId property.
     * 
     */
    public int getParentFolderId() {
        return parentFolderId;
    }

    /**
     * Sets the value of the parentFolderId property.
     * 
     */
    public void setParentFolderId(int value) {
        this.parentFolderId = value;
    }

    /**
     * Gets the value of the repId property.
     * 
     */
    public int getRepId() {
        return repId;
    }

    /**
     * Sets the value of the repId property.
     * 
     */
    public void setRepId(int value) {
        this.repId = value;
    }

    /**
     * Gets the value of the folderType property.
     * 
     */
    public int getFolderType() {
        return folderType;
    }

    /**
     * Sets the value of the folderType property.
     * 
     */
    public void setFolderType(int value) {
        this.folderType = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the folderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Sets the value of the folderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderName(String value) {
        this.folderName = value;
    }

}
