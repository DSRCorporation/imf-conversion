package com.netflix.imfutility.dpp;

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
    public void tearDown() throws Exception {
        //remove temp file;
        metadataXml.delete();
    }

    /**
     * Tests empty test-metadata.xml generation.
     *
     * @throws Exception
     */
    @Test
    public void generateEmptyXml() throws Exception {
        //try to generate Dpp test-metadata.xml
        MetadataXmlProvider.generateEmptyXml(metadataXml.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated test-metadata.xml is zero size.", metadataXml.length() > 0);
    }

}
