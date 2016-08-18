/*
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.netflix.imfutility.itunes.image;

import org.apache.commons.math3.fraction.BigFraction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Validator to check image size, aspect ratio, color model and format.
 */
public class ImageValidator {
    private static final int JPEG_START_BYTES = 0xffd8ffe0;
    private final File file;
    private BufferedImage image;

    private String imageType;

    public ImageValidator(File file, String imageType) {
        this.file = file;
        this.imageType = imageType;
        ensureReadImage();
    }

    private void ensureReadImage() throws ImageValidationException {
        if (image != null) {
            return;
        }

        try {
            image = ImageIO.read(file);
            if (image == null) {
                throw new ImageValidationException(String.format(
                        "Can't read %s image from file %s", imageType, file.getName()));
            }
        } catch (IOException e) {
            throw new ImageValidationException(String.format(
                    "Can't read %s image from file %s", imageType, file.getName()), e);
        }
    }

    public void validateSize(Integer width, Integer height) throws ImageValidationException {
        if ((width != null && image.getWidth() < width) || (height != null && image.getHeight() < height)) {
            throw new ImageValidationException(String.format(
                    "%s image must be at least %4d x%4d", imageType, width, height));
        }
    }

    public void validateAspectRatio(BigFraction aspectRatio) throws ImageValidationException {
        if (!Objects.equals(aspectRatio, getImageAspectRatio())) {
            throw new ImageValidationException(
                    String.format(
                            "%s image must have %s aspect ratio", imageType, aspectRatio));
        }
    }

    public void validateRGBColorSpace() throws ImageValidationException {
        if (!image.getColorModel().getColorSpace().isCS_sRGB()) {
            throw new ImageValidationException(String.format(
                    "%s image must be RGB", imageType));
        }
    }

    public void validateJpeg() throws ImageValidationException {
        if (!isJpeg()) {
            throw new ImageValidationException(String.format(
                    "%s image must be JPG", imageType));
        }
    }

    private BigFraction getImageAspectRatio() {
        return new BigFraction(image.getWidth()).divide(image.getHeight());
    }

    private boolean isJpeg() {
        try (DataInputStream ins = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            return ins.readInt() == JPEG_START_BYTES;
        } catch (IOException e) {
            throw new ImageValidationException(String.format(
                    "Can't read file %s", file.getName()), e);
        }
    }
}
