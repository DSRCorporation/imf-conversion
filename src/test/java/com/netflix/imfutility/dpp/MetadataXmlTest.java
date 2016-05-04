package com.netflix.imfutility.dpp;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit tests for DPP metadata.xml processing.
 */
public class MetadataXmlTest {
    @org.junit.Test
    public void generateEmptyXml() throws Exception {
        //create a temp file
        File temp = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        assertTrue("Temporary file cannot be deleted.", temp.delete());

        //try to generate Dpp metadata.xml
        MetadataXml.GenerateEmptyXml(temp.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated metadata.xml is zero size.", temp.length() > 0);

        //remove temp file;
        temp.delete();
    }
}
