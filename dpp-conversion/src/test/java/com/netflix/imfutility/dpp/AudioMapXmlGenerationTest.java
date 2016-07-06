package com.netflix.imfutility.dpp;

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
    public void tearDown() throws Exception {
        //remove temp file;
        assertTrue("Temporary file cannot be deleted.", audiomapXml.delete());
    }

    /**
     * Tests sample audiomap.xml generation.
     *
     * @throws Exception
     */
    @Test
    public void generateSampleXml() throws Exception {
        //try to generate sample audiomap.xml
        AudioMapXmlProvider.generateSampleXml(audiomapXml.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated audiomap.xml is zero size.", audiomapXml.length() > 0);
    }

}
