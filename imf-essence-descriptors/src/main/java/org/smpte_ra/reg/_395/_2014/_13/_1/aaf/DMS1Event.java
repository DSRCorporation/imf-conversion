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
import org.smpte_ra.reg._2003._2012.ApplicationPluginObjectStrongReferenceSet;
import org.smpte_ra.reg._2003._2012.DescriptiveObjectStrongReferenceSet;
import org.smpte_ra.reg._2003._2012.ISO7;
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
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}PublicationObjects" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}EventStartDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}EventEndDateTime" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}EventIndication" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}EventAnnotationObjects" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}ThesaurusName" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}LinkedDescriptiveObjectPluginID" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}InstanceID" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}ObjectClass" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}ApplicationPlugInObjects" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}LinkedGenerationID" minOccurs="0"/>
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
@XmlRootElement(name = "DMS1Event")
public class DMS1Event {

    @XmlElement(name = "PublicationObjects", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected DescriptiveObjectStrongReferenceSet publicationObjects;
    @XmlElement(name = "EventStartDateTime", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected ISO7 eventStartDateTime;
    @XmlElement(name = "EventEndDateTime", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected ISO7 eventEndDateTime;
    @XmlElement(name = "EventIndication", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected UTF16String eventIndication;
    @XmlElement(name = "EventAnnotationObjects", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected DescriptiveObjectStrongReferenceSet eventAnnotationObjects;
    @XmlElement(name = "ThesaurusName", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected UTF16String thesaurusName;
    @XmlElement(name = "LinkedDescriptiveObjectPluginID", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String linkedDescriptiveObjectPluginID;
    @XmlElement(name = "InstanceID", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anyURI")
    protected String instanceID;
    @XmlElement(name = "ObjectClass", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String objectClass;
    @XmlElement(name = "ApplicationPlugInObjects", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected ApplicationPluginObjectStrongReferenceSet applicationPlugInObjects;
    @XmlElement(name = "LinkedGenerationID", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anyURI")
    protected String linkedGenerationID;
    @XmlAttribute(name = "path", namespace = "http://sandflow.com/ns/SMPTEST2001-1/baseline")
    protected String path;

    /**
     * Gets the value of the publicationObjects property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptiveObjectStrongReferenceSet }
     *     
     */
    public DescriptiveObjectStrongReferenceSet getPublicationObjects() {
        return publicationObjects;
    }

    /**
     * Sets the value of the publicationObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptiveObjectStrongReferenceSet }
     *     
     */
    public void setPublicationObjects(DescriptiveObjectStrongReferenceSet value) {
        this.publicationObjects = value;
    }

    /**
     * Gets the value of the eventStartDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link ISO7 }
     *     
     */
    public ISO7 getEventStartDateTime() {
        return eventStartDateTime;
    }

    /**
     * Sets the value of the eventStartDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ISO7 }
     *     
     */
    public void setEventStartDateTime(ISO7 value) {
        this.eventStartDateTime = value;
    }

    /**
     * Gets the value of the eventEndDateTime property.
     * 
     * @return
     *     possible object is
     *     {@link ISO7 }
     *     
     */
    public ISO7 getEventEndDateTime() {
        return eventEndDateTime;
    }

    /**
     * Sets the value of the eventEndDateTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link ISO7 }
     *     
     */
    public void setEventEndDateTime(ISO7 value) {
        this.eventEndDateTime = value;
    }

    /**
     * Gets the value of the eventIndication property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getEventIndication() {
        return eventIndication;
    }

    /**
     * Sets the value of the eventIndication property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setEventIndication(UTF16String value) {
        this.eventIndication = value;
    }

    /**
     * Gets the value of the eventAnnotationObjects property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptiveObjectStrongReferenceSet }
     *     
     */
    public DescriptiveObjectStrongReferenceSet getEventAnnotationObjects() {
        return eventAnnotationObjects;
    }

    /**
     * Sets the value of the eventAnnotationObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptiveObjectStrongReferenceSet }
     *     
     */
    public void setEventAnnotationObjects(DescriptiveObjectStrongReferenceSet value) {
        this.eventAnnotationObjects = value;
    }

    /**
     * Gets the value of the thesaurusName property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getThesaurusName() {
        return thesaurusName;
    }

    /**
     * Sets the value of the thesaurusName property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setThesaurusName(UTF16String value) {
        this.thesaurusName = value;
    }

    /**
     * Gets the value of the linkedDescriptiveObjectPluginID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkedDescriptiveObjectPluginID() {
        return linkedDescriptiveObjectPluginID;
    }

    /**
     * Sets the value of the linkedDescriptiveObjectPluginID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkedDescriptiveObjectPluginID(String value) {
        this.linkedDescriptiveObjectPluginID = value;
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
     * Gets the value of the objectClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectClass() {
        return objectClass;
    }

    /**
     * Sets the value of the objectClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectClass(String value) {
        this.objectClass = value;
    }

    /**
     * Gets the value of the applicationPlugInObjects property.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationPluginObjectStrongReferenceSet }
     *     
     */
    public ApplicationPluginObjectStrongReferenceSet getApplicationPlugInObjects() {
        return applicationPlugInObjects;
    }

    /**
     * Sets the value of the applicationPlugInObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationPluginObjectStrongReferenceSet }
     *     
     */
    public void setApplicationPlugInObjects(ApplicationPluginObjectStrongReferenceSet value) {
        this.applicationPlugInObjects = value;
    }

    /**
     * Gets the value of the linkedGenerationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinkedGenerationID() {
        return linkedGenerationID;
    }

    /**
     * Sets the value of the linkedGenerationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinkedGenerationID(String value) {
        this.linkedGenerationID = value;
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
