//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.11 at 01:51:42 PM MSK 
//


package org.smpte_ra.reg._395._2014;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.smpte_ra.reg._2003._2012.Boolean;
import org.smpte_ra.reg._2003._2012.UTF8String;


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
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}LensZoomActualFocalLength" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}LensAttributes" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}IrisFNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}FocusRingPosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}ZoomRingPosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}IrisRingPosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}FocusPositionFromFrontLensVertex" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}IrisTNumber" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}FocusPositionFromImagePlane" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}OpticalExtenderMagnification" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}MacroSetting" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}LensZoom35mmStillCameraEquivalent" minOccurs="0"/>
 *         &lt;element ref="{http://www.smpte-ra.org/reg/335/2012}InstanceID" minOccurs="0"/>
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
@XmlRootElement(name = "LensUnitAcquisitionMetadata")
public class LensUnitAcquisitionMetadata {

    @XmlElement(name = "LensZoomActualFocalLength", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected BigDecimal lensZoomActualFocalLength;
    @XmlElement(name = "LensAttributes", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected UTF8String lensAttributes;
    @XmlElement(name = "IrisFNumber", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String irisFNumber;
    @XmlElement(name = "FocusRingPosition", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String focusRingPosition;
    @XmlElement(name = "ZoomRingPosition", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String zoomRingPosition;
    @XmlElement(name = "IrisRingPosition", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String irisRingPosition;
    @XmlElement(name = "FocusPositionFromFrontLensVertex", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected BigDecimal focusPositionFromFrontLensVertex;
    @XmlElement(name = "IrisTNumber", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String irisTNumber;
    @XmlElement(name = "FocusPositionFromImagePlane", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected BigDecimal focusPositionFromImagePlane;
    @XmlElement(name = "OpticalExtenderMagnification", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anySimpleType")
    protected String opticalExtenderMagnification;
    @XmlElement(name = "MacroSetting", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "token")
    protected Boolean macroSetting;
    @XmlElement(name = "LensZoom35mmStillCameraEquivalent", namespace = "http://www.smpte-ra.org/reg/335/2012")
    protected BigDecimal lensZoom35MmStillCameraEquivalent;
    @XmlElement(name = "InstanceID", namespace = "http://www.smpte-ra.org/reg/335/2012")
    @XmlSchemaType(name = "anyURI")
    protected String instanceID;
    @XmlAttribute(name = "path", namespace = "http://sandflow.com/ns/SMPTEST2001-1/baseline")
    protected String path;

    /**
     * Gets the value of the lensZoomActualFocalLength property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLensZoomActualFocalLength() {
        return lensZoomActualFocalLength;
    }

    /**
     * Sets the value of the lensZoomActualFocalLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLensZoomActualFocalLength(BigDecimal value) {
        this.lensZoomActualFocalLength = value;
    }

    /**
     * Gets the value of the lensAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link UTF8String }
     *     
     */
    public UTF8String getLensAttributes() {
        return lensAttributes;
    }

    /**
     * Sets the value of the lensAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link UTF8String }
     *     
     */
    public void setLensAttributes(UTF8String value) {
        this.lensAttributes = value;
    }

    /**
     * Gets the value of the irisFNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIrisFNumber() {
        return irisFNumber;
    }

    /**
     * Sets the value of the irisFNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIrisFNumber(String value) {
        this.irisFNumber = value;
    }

    /**
     * Gets the value of the focusRingPosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFocusRingPosition() {
        return focusRingPosition;
    }

    /**
     * Sets the value of the focusRingPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFocusRingPosition(String value) {
        this.focusRingPosition = value;
    }

    /**
     * Gets the value of the zoomRingPosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZoomRingPosition() {
        return zoomRingPosition;
    }

    /**
     * Sets the value of the zoomRingPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZoomRingPosition(String value) {
        this.zoomRingPosition = value;
    }

    /**
     * Gets the value of the irisRingPosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIrisRingPosition() {
        return irisRingPosition;
    }

    /**
     * Sets the value of the irisRingPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIrisRingPosition(String value) {
        this.irisRingPosition = value;
    }

    /**
     * Gets the value of the focusPositionFromFrontLensVertex property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFocusPositionFromFrontLensVertex() {
        return focusPositionFromFrontLensVertex;
    }

    /**
     * Sets the value of the focusPositionFromFrontLensVertex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFocusPositionFromFrontLensVertex(BigDecimal value) {
        this.focusPositionFromFrontLensVertex = value;
    }

    /**
     * Gets the value of the irisTNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIrisTNumber() {
        return irisTNumber;
    }

    /**
     * Sets the value of the irisTNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIrisTNumber(String value) {
        this.irisTNumber = value;
    }

    /**
     * Gets the value of the focusPositionFromImagePlane property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getFocusPositionFromImagePlane() {
        return focusPositionFromImagePlane;
    }

    /**
     * Sets the value of the focusPositionFromImagePlane property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setFocusPositionFromImagePlane(BigDecimal value) {
        this.focusPositionFromImagePlane = value;
    }

    /**
     * Gets the value of the opticalExtenderMagnification property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOpticalExtenderMagnification() {
        return opticalExtenderMagnification;
    }

    /**
     * Sets the value of the opticalExtenderMagnification property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpticalExtenderMagnification(String value) {
        this.opticalExtenderMagnification = value;
    }

    /**
     * Gets the value of the macroSetting property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getMacroSetting() {
        return macroSetting;
    }

    /**
     * Sets the value of the macroSetting property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMacroSetting(Boolean value) {
        this.macroSetting = value;
    }

    /**
     * Gets the value of the lensZoom35MmStillCameraEquivalent property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLensZoom35MmStillCameraEquivalent() {
        return lensZoom35MmStillCameraEquivalent;
    }

    /**
     * Sets the value of the lensZoom35MmStillCameraEquivalent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLensZoom35MmStillCameraEquivalent(BigDecimal value) {
        this.lensZoom35MmStillCameraEquivalent = value;
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
