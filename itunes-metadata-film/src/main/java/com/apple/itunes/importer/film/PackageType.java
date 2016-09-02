//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7-b41 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.01 at 05:28:13 PM MSK 
//


package com.apple.itunes.importer.film;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for PackageType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PackageType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="metadata_token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comments" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="language" type="{http://apple.com/itunes/importer}LanguageType"/>
 *         &lt;element name="provider" type="{http://apple.com/itunes/importer}ProviderType"/>
 *         &lt;choice>
 *           &lt;element ref="{http://apple.com/itunes/importer}album"/>
 *           &lt;element ref="{http://apple.com/itunes/importer}tracks"/>
 *           &lt;element ref="{http://apple.com/itunes/importer}assets"/>
 *           &lt;element ref="{http://apple.com/itunes/importer}video"/>
 *           &lt;element ref="{http://apple.com/itunes/importer}artist"/>
 *           &lt;element ref="{http://apple.com/itunes/importer}itunes_extra"/>
 *           &lt;element ref="{http://apple.com/itunes/importer}clone_assets"/>
 *         &lt;/choice>
 *       &lt;/choice>
 *       &lt;attribute name="version" use="required" type="{http://apple.com/itunes/importer}PackageVersion" />
 *       &lt;attribute name="generator" type="{http://apple.com/itunes/importer}String255" />
 *       &lt;attribute name="generator_version" type="{http://apple.com/itunes/importer}String255" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageType", propOrder = {
    "comments",
    "assets",
    "video",
    "artist",
    "itunesExtra",
    "cloneAssets",
    "album",
    "language",
    "provider",
    "tracks",
    "metadataToken"
})
public class PackageType {

    protected List<String> comments;
    protected List<AssetList> assets;
    protected List<Video> video;
    protected List<ArtistType> artist;
    @XmlElement(name = "itunes_extra")
    protected List<ItunesExtra> itunesExtra;
    @XmlElement(name = "clone_assets")
    protected List<CloneAssetList> cloneAssets;
    protected List<Album> album;
    protected List<String> language;
    protected List<String> provider;
    protected List<TrackList> tracks;
    @XmlElement(name = "metadata_token")
    protected List<String> metadataToken;
    @XmlAttribute(name = "version", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String version;
    @XmlAttribute(name = "generator")
    protected String generator;
    @XmlAttribute(name = "generator_version")
    protected String generatorVersion;

    /**
     * Gets the value of the comments property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the comments property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getComments().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getComments() {
        if (comments == null) {
            comments = new ArrayList<String>();
        }
        return this.comments;
    }

    /**
     * Gets the value of the assets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssetList }
     * 
     * 
     */
    public List<AssetList> getAssets() {
        if (assets == null) {
            assets = new ArrayList<AssetList>();
        }
        return this.assets;
    }

    /**
     * Gets the value of the video property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the video property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVideo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Video }
     * 
     * 
     */
    public List<Video> getVideo() {
        if (video == null) {
            video = new ArrayList<Video>();
        }
        return this.video;
    }

    /**
     * Gets the value of the artist property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the artist property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArtist().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArtistType }
     * 
     * 
     */
    public List<ArtistType> getArtist() {
        if (artist == null) {
            artist = new ArrayList<ArtistType>();
        }
        return this.artist;
    }

    /**
     * Gets the value of the itunesExtra property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itunesExtra property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItunesExtra().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItunesExtra }
     * 
     * 
     */
    public List<ItunesExtra> getItunesExtra() {
        if (itunesExtra == null) {
            itunesExtra = new ArrayList<ItunesExtra>();
        }
        return this.itunesExtra;
    }

    /**
     * Gets the value of the cloneAssets property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cloneAssets property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCloneAssets().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CloneAssetList }
     * 
     * 
     */
    public List<CloneAssetList> getCloneAssets() {
        if (cloneAssets == null) {
            cloneAssets = new ArrayList<CloneAssetList>();
        }
        return this.cloneAssets;
    }

    /**
     * Gets the value of the album property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the album property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAlbum().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Album }
     * 
     * 
     */
    public List<Album> getAlbum() {
        if (album == null) {
            album = new ArrayList<Album>();
        }
        return this.album;
    }

    /**
     * Gets the value of the language property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the language property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLanguage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLanguage() {
        if (language == null) {
            language = new ArrayList<String>();
        }
        return this.language;
    }

    /**
     * Gets the value of the provider property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the provider property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProvider().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProvider() {
        if (provider == null) {
            provider = new ArrayList<String>();
        }
        return this.provider;
    }

    /**
     * Gets the value of the tracks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tracks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTracks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrackList }
     * 
     * 
     */
    public List<TrackList> getTracks() {
        if (tracks == null) {
            tracks = new ArrayList<TrackList>();
        }
        return this.tracks;
    }

    /**
     * Gets the value of the metadataToken property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metadataToken property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetadataToken().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMetadataToken() {
        if (metadataToken == null) {
            metadataToken = new ArrayList<String>();
        }
        return this.metadataToken;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the generator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerator() {
        return generator;
    }

    /**
     * Sets the value of the generator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerator(String value) {
        this.generator = value;
    }

    /**
     * Gets the value of the generatorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGeneratorVersion() {
        return generatorVersion;
    }

    /**
     * Sets the value of the generatorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGeneratorVersion(String value) {
        this.generatorVersion = value;
    }

}