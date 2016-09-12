//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7-b41 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.01 at 06:27:35 PM MSK 
//


package com.apple.itunes.importer.tv;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for Navnode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Navnode">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://apple.com/itunes/importer}vendor_id"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}short_title"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}background_gallery_link"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}gallery_link"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}selection_gallery_link"/>
 *         &lt;element name="locales">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="locale" type="{http://apple.com/itunes/importer}NavnodeLocale" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://apple.com/itunes/importer}colors"/>
 *         &lt;element name="transition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}show_play_all"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}inherit_background_image"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}inherit_background_audio"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}inherit_background_video"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}navnodes"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}preview_label"/>
 *         &lt;element ref="{http://apple.com/itunes/importer}movie_label"/>
 *       &lt;/choice>
 *       &lt;attribute name="type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="cast"/>
 *             &lt;enumeration value="gallery"/>
 *             &lt;enumeration value="menu"/>
 *             &lt;enumeration value="play"/>
 *             &lt;enumeration value="related"/>
 *             &lt;enumeration value="scenes"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="template">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *             &lt;enumeration value="carousel"/>
 *             &lt;enumeration value="cast"/>
 *             &lt;enumeration value="expand_in_parent"/>
 *             &lt;enumeration value="grid"/>
 *             &lt;enumeration value="list_on_left"/>
 *             &lt;enumeration value="list_on_left_with_grid"/>
 *             &lt;enumeration value="list_on_right"/>
 *             &lt;enumeration value="one_up"/>
 *             &lt;enumeration value="rows"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Navnode", propOrder = {
    "vendorIdOrTitleOrShortTitle"
})
public class Navnode {

    @XmlElementRefs({
        @XmlElementRef(name = "title", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "movie_label", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "inherit_background_video", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "locales", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "transition", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "inherit_background_audio", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "background_gallery_link", namespace = "http://apple.com/itunes/importer", type = BackgroundGalleryLink.class, required = false),
        @XmlElementRef(name = "description", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "inherit_background_image", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "preview_label", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "navnodes", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "short_title", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "show_play_all", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "gallery_link", namespace = "http://apple.com/itunes/importer", type = GalleryLink.class, required = false),
        @XmlElementRef(name = "vendor_id", namespace = "http://apple.com/itunes/importer", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "selection_gallery_link", namespace = "http://apple.com/itunes/importer", type = SelectionGalleryLink.class, required = false),
        @XmlElementRef(name = "colors", namespace = "http://apple.com/itunes/importer", type = Colors.class, required = false)
    })
    protected List<Object> vendorIdOrTitleOrShortTitle;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "template")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String template;

    /**
     * Gets the value of the vendorIdOrTitleOrShortTitle property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vendorIdOrTitleOrShortTitle property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVendorIdOrTitleOrShortTitle().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link Navnode.Locales }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link BackgroundGalleryLink }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link NavnodeList }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link GalleryLink }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link SelectionGalleryLink }
     * {@link Colors }
     * 
     * 
     */
    public List<Object> getVendorIdOrTitleOrShortTitle() {
        if (vendorIdOrTitleOrShortTitle == null) {
            vendorIdOrTitleOrShortTitle = new ArrayList<Object>();
        }
        return this.vendorIdOrTitleOrShortTitle;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemplate(String value) {
        this.template = value;
    }


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
     *         &lt;element name="locale" type="{http://apple.com/itunes/importer}NavnodeLocale" maxOccurs="unbounded" minOccurs="0"/>
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
        "locale"
    })
    public static class Locales {

        protected List<NavnodeLocale> locale;

        /**
         * Gets the value of the locale property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the locale property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLocale().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link NavnodeLocale }
         * 
         * 
         */
        public List<NavnodeLocale> getLocale() {
            if (locale == null) {
                locale = new ArrayList<NavnodeLocale>();
            }
            return this.locale;
        }

    }

}