package com.netflix.imfutility.mediainfo;

/**
 * An exception when it's not possible to obtain resource parameters (media info).
 */
public class MediaInfoException extends Exception {

    public MediaInfoException(String message, String essence) {
        super(String.format("Can not get media information for '%s'. %s", essence, message));
    }
}
