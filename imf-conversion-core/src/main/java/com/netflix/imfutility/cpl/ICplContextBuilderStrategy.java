/**
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
package com.netflix.imfutility.cpl;

import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A strategy to build a CPL context depending on the supported namespace.
 */
public interface ICplContextBuilderStrategy {

    /**
     * Parse a CPL file.
     * @param cplFile a CPL file.
     * @throws XmlParsingException, FileNotFoundException if the input is not a valid XML or it doesn't pass XSD validation
     */
    void parse(File cplFile) throws XmlParsingException, FileNotFoundException;

    /**
     * Builds CPL context.
     */
    void build();

    /**
     * Adds CPL context with information obtained using destination context.
     */
    void buildPostDest();

    /**
     * Gets a composition start timecode as defined in CPL.
     * @return a composition start timecode as defined in CPL or null it it's absent
     */
    String getCompositionTimecodeStart();

    /**
     * Gets a composition timecode rate as defined in CPL.
     * @return a composition timecode rate as defined in CPL or null it it's absent
     */
    BigFraction getCompositionTimecodeRate();

}
