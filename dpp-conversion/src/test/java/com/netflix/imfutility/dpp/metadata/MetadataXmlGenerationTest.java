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
package com.netflix.imfutility.dpp.metadata;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Unit tests for generation of DPP test-metadata.xml.
 */
public class MetadataXmlGenerationTest {

    private File metadataXml;

    @Before
    public void setUp() throws Exception {
        //create and delete a temp file
        metadataXml = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        metadataXml.deleteOnExit();
        assertTrue("Temporary file cannot be deleted.", metadataXml.delete());
    }

    @After
    public void tearDown() {
        //remove temp file;
        assertTrue("Temporary file cannot be deleted.", metadataXml.delete());
    }

    /**
     * Tests empty test-metadata.xml generation.
     *
     *
     */
    @Test
    public void generateEmptyXml() {
        //try to generate Dpp test-metadata.xml
        MetadataXmlCreator.generateEmptyXml(metadataXml.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated test-metadata.xml is zero size.", metadataXml.length() > 0);
    }

}
