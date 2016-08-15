//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.11 at 01:51:42 PM MSK 
//


package org.smpte_ra.reg._395._2014._13._1.aaf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.smpte_ra.reg._2003._2012.MetaDefinitionStrongReferenceSet;
import org.smpte_ra.reg._2003._2012.UTF16String;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}PreferredPrefix" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}ExtensionDescription" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}InstanceID" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}SymbolSpaceURI"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}ExtensionSchemeID"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}MetaDefinitions" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute ref="{http://sandflow.com/ns/SMPTEST2001-1/baseline}path"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ExtensionScheme")
public class ExtensionScheme {

    @XmlElement(name = "PreferredPrefix", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected UTF16String preferredPrefix;
    @XmlElement(name = "ExtensionDescription", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected UTF16String extensionDescription;
    @XmlElement(name = "InstanceID", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anyURI")
    protected String instanceID;
    @XmlElement(name = "SymbolSpaceURI", namespace = "http://www.smpte-ra.org/reg/335/2012", required = true)
    protected UTF16String symbolSpaceURI;
    @XmlElement(name = "ExtensionSchemeID", namespace = "http://www.smpte-ra.org/reg/335/2012", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String extensionSchemeID;
    @XmlElement(name = "MetaDefinitions", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected MetaDefinitionStrongReferenceSet metaDefinitions;
    @XmlAttribute(name = "path", namespace = "http://sandflow.com/ns/SMPTEST2001-1/baseline")
    protected String path;

    /**
     * Gets the value of the preferredPrefix property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getPreferredPrefix() {
        return preferredPrefix;
    }

    /**
     * Sets the value of the preferredPrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setPreferredPrefix(UTF16String value) {
        this.preferredPrefix = value;
    }

    /**
     * Gets the value of the extensionDescription property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getExtensionDescription() {
        return extensionDescription;
    }

    /**
     * Sets the value of the extensionDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setExtensionDescription(UTF16String value) {
        this.extensionDescription = value;
    }

    /**
     * Gets the value of the instanceID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceID() {
        return instanceID;
    }

    /**
     * Sets the value of the instanceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceID(String value) {
        this.instanceID = value;
    }

    /**
     * Gets the value of the symbolSpaceURI property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getSymbolSpaceURI() {
        return symbolSpaceURI;
    }

    /**
     * Sets the value of the symbolSpaceURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setSymbolSpaceURI(UTF16String value) {
        this.symbolSpaceURI = value;
    }

    /**
     * Gets the value of the extensionSchemeID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtensionSchemeID() {
        return extensionSchemeID;
    }

    /**
     * Sets the value of the extensionSchemeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtensionSchemeID(String value) {
        this.extensionSchemeID = value;
    }

    /**
     * Gets the value of the metaDefinitions property.
     * 
     * @return
     *     possible object is
     *     {@link MetaDefinitionStrongReferenceSet }
     *     
     */
    public MetaDefinitionStrongReferenceSet getMetaDefinitions() {
        return metaDefinitions;
    }

    /**
     * Sets the value of the metaDefinitions property.
     * 
     * @param value
     *     allowed object is
     *     {@link MetaDefinitionStrongReferenceSet }
     *     
     */
    public void setMetaDefinitions(MetaDefinitionStrongReferenceSet value) {
        this.metaDefinitions = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

}