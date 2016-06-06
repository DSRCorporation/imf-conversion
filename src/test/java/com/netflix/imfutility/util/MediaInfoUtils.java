package com.netflix.imfutility.util;

/**
 * Test utility for mediaInfo.xml.
 */
public final class MediaInfoUtils {

    private MediaInfoUtils() {
    }

    public static String getCorrectMediaInfoAudio() {
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio.xml").getPath();
    }

    public static String getCorrectMediaInfoAudio2() {
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoAudio2.xml").getPath();
    }


    public static String getCorrectMediaInfoVideo() {
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoVideo.xml").getPath();
    }

    public static String getCorrectMediaInfoVideo2() {
        return ClassLoader.getSystemClassLoader().getResource("xml/mediaInfoVideo2.xml").getPath();
    }

    public static String getBrokenXmlMediaInfoAudio() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-mediaInfoAudio.xml").getPath();
    }

    public static String getBrokenXmlMediaInfoVideo() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/broken-xml-mediaInfoVideo.xml").getPath();
    }

    public static String getInvalidXsdMediaInfoAudio() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-mediaInfoAudio.xml").getPath();
    }

    public static String getInvalidXsdMediaInfoVideo() {
        return ClassLoader.getSystemClassLoader().getResource("xml/invalid/invalid-xsd-mediaInfoVideo.xml").getPath();
    }

}
