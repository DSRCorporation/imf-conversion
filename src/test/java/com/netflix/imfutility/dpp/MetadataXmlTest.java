package com.netflix.imfutility.dpp;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit tests for DPP metadata.xml processing.
 */
public class MetadataXmlTest {
    /**
     * Tests empty metadata.xml generation.
     *
     * @throws Exception
     */
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

    /**
     * Tests metadata.xml transformation into a sef of parameter files for BMXLib tool.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void transformXmlToBmxParameters() throws Exception {
        //create a temp file
        File temp = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        assertTrue("Temporary file cannot be deleted.", temp.delete());

        //try to generate Dpp metadata.xml
        MetadataXml.GenerateEmptyXml(temp.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated metadata.xml is zero size.", temp.length() > 0);

        //get generated temporary files
        Map<MetadataXml.DMFramework, File> dppParameters = MetadataXml.getBmxDppParameters(temp);

        //check
        assertTrue("UKDPP parameters are not generated", dppParameters.get(MetadataXml.DMFramework.UKDPP).length() > 0);
        assertTrue("AS11 parameters are not generated", dppParameters.get(MetadataXml.DMFramework.AS11CORE).length() > 0);
        assertTrue("SEG parameters are not generated", dppParameters.get(MetadataXml.DMFramework.AS11Segmentation).length() > 0);

        //remove temp file;
        temp.delete();
    }
}
