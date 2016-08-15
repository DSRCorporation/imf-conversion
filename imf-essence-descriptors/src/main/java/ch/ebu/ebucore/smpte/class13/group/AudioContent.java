//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.11 at 01:51:42 PM MSK 
//


package ch.ebu.ebucore.smpte.class13.group;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import ch.ebu.ebucore.smpte.class13.type.IDRefStrongReferenceSet;
import ch.ebu.ebucore.smpte.class13.type.LoudnessMetadataStrongReference;
import org.smpte_ra.reg._2003._2012.ApplicationPluginObjectStrongReferenceSet;
import org.smpte_ra.reg._2003._2012.Boolean;
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
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/element}audioContentId" minOccurs="0"/>
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/element}audioContentName" minOccurs="0"/>
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/element}audioContentDialogueIndicator" minOccurs="0"/>
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/element}audioContentLanguageCode" minOccurs="0"/>
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/element}audioContentAudioObjectIDRefObjects" minOccurs="0"/>
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/element}audioContentLoudnessMetadataObject" minOccurs="0"/>
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
@XmlRootElement(name = "audioContent")
public class AudioContent {

    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/element")
    protected UTF16String audioContentId;
    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/element")
    protected UTF16String audioContentName;
    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/element")
    @XmlSchemaType(name = "token")
    protected Boolean audioContentDialogueIndicator;
    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/element")
    protected UTF16String audioContentLanguageCode;
    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/element")
    protected IDRefStrongReferenceSet audioContentAudioObjectIDRefObjects;
    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/element")
    protected LoudnessMetadataStrongReference audioContentLoudnessMetadataObject;
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
     * Gets the value of the audioContentId property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getAudioContentId() {
        return audioContentId;
    }

    /**
     * Sets the value of the audioContentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setAudioContentId(UTF16String value) {
        this.audioContentId = value;
    }

    /**
     * Gets the value of the audioContentName property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getAudioContentName() {
        return audioContentName;
    }

    /**
     * Sets the value of the audioContentName property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setAudioContentName(UTF16String value) {
        this.audioContentName = value;
    }

    /**
     * Gets the value of the audioContentDialogueIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getAudioContentDialogueIndicator() {
        return audioContentDialogueIndicator;
    }

    /**
     * Sets the value of the audioContentDialogueIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAudioContentDialogueIndicator(Boolean value) {
        this.audioContentDialogueIndicator = value;
    }

    /**
     * Gets the value of the audioContentLanguageCode property.
     * 
     * @return
     *     possible object is
     *     {@link UTF16String }
     *     
     */
    public UTF16String getAudioContentLanguageCode() {
        return audioContentLanguageCode;
    }

    /**
     * Sets the value of the audioContentLanguageCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF16String }
     *     
     */
    public void setAudioContentLanguageCode(UTF16String value) {
        this.audioContentLanguageCode = value;
    }

    /**
     * Gets the value of the audioContentAudioObjectIDRefObjects property.
     * 
     * @return
     *     possible object is
     *     {@link IDRefStrongReferenceSet }
     *     
     */
    public IDRefStrongReferenceSet getAudioContentAudioObjectIDRefObjects() {
        return audioContentAudioObjectIDRefObjects;
    }

    /**
     * Sets the value of the audioContentAudioObjectIDRefObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link IDRefStrongReferenceSet }
     *     
     */
    public void setAudioContentAudioObjectIDRefObjects(IDRefStrongReferenceSet value) {
        this.audioContentAudioObjectIDRefObjects = value;
    }

    /**
     * Gets the value of the audioContentLoudnessMetadataObject property.
     * 
     * @return
     *     possible object is
     *     {@link LoudnessMetadataStrongReference }
     *     
     */
    public LoudnessMetadataStrongReference getAudioContentLoudnessMetadataObject() {
        return audioContentLoudnessMetadataObject;
    }

    /**
     * Sets the value of the audioContentLoudnessMetadataObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoudnessMetadataStrongReference }
     *     
     */
    public void setAudioContentLoudnessMetadataObject(LoudnessMetadataStrongReference value) {
        this.audioContentLoudnessMetadataObject = value;
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