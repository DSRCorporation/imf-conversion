//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.08.11 at 01:51:42 PM MSK 
//


package ch.ebu.ebucore.smpte.class13.type;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ch.ebu.ebucore.smpte.class13.group.TechnicalAttributeRational;


/**
 * <p>Java class for technicalAttributeRationalStrongReference complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="technicalAttributeRationalStrongReference">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.ebu.ch/ebucore/smpte/class13/group}technicalAttributeRational"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "technicalAttributeRationalStrongReference", propOrder = {
    "technicalAttributeRational"
})
public class TechnicalAttributeRationalStrongReference {

    @XmlElement(namespace = "http://www.ebu.ch/ebucore/smpte/class13/group")
    protected TechnicalAttributeRational technicalAttributeRational;

    /**
     * Gets the value of the technicalAttributeRational property.
     * 
     * @return
     *     possible object is
     *     {@link TechnicalAttributeRational }
     *     
     */
    public TechnicalAttributeRational getTechnicalAttributeRational() {
        return technicalAttributeRational;
    }

    /**
     * Sets the value of the technicalAttributeRational property.
     * 
     * @param value
     *     allowed object is
     *     {@link TechnicalAttributeRational }
     *     
     */
    public void setTechnicalAttributeRational(TechnicalAttributeRational value) {
        this.technicalAttributeRational = value;
    }

}
