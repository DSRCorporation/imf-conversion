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
package com.netflix.imfutility.dpp.audio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for generation of audiomap.xml.
 */
public class AudioMapXmlGenerationTest {

    private File audiomapXml;

    @Before
    public void setUp() throws Exception {
        //create and delete a temp file
        audiomapXml = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        audiomapXml.deleteOnExit();
        assertTrue("Temporary file cannot be deleted.", audiomapXml.delete());
    }

    @After
    public void tearDown() {
        //remove temp file;
        assertTrue("Temporary file cannot be deleted.", audiomapXml.delete());
    }

    /**
     * Tests sample audiomap.xml generation.
     *
     */
    @Test
    public void generateSampleXml() {
        //try to generate sample audiomap.xml
        AudioMapXmlCreator.generateSampleXml(audiomapXml.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated audiomap.xml is zero size.", audiomapXml.length() > 0);
    }

}
