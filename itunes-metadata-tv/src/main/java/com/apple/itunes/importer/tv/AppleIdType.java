//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7-b41 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.01 at 06:27:35 PM MSK 
//


package com.apple.itunes.importer.tv;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for AppleIdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AppleIdType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://apple.com/itunes/importer>FormattedPositiveInteger">
 *       &lt;attribute name="generate" type="{http://apple.com/itunes/importer}FormattedBoolean" />
 *       &lt;attribute name="allow_duplicate_name" type="{http://apple.com/itunes/importer}FormattedBoolean" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppleIdType", propOrder = {
    "value"
})
public class AppleIdType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "generate")
    protected String generate;
    @XmlAttribute(name = "allow_duplicate_name")
    protected String allowDuplicateName;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the generate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerate() {
        return generate;
    }

    /**
     * Sets the value of the generate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerate(String value) {
        this.generate = value;
    }

    /**
     * Gets the value of the allowDuplicateName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllowDuplicateName() {
        return allowDuplicateName;
    }

    /**
     * Sets the value of the allowDuplicateName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllowDuplicateName(String value) {
        this.allowDuplicateName = value;
    }

}
