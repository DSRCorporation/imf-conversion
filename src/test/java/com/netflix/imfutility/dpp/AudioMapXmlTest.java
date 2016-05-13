package com.netflix.imfutility.dpp;

import com.netflix.imfutility.xsd.dpp.audiomap.AudioMap;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alexandr on 5/12/2016.
 */
public class AudioMapXmlTest {

    /**
     * Tests sample audiomap.xml generation.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void generateSampleXml() throws Exception {
        //create a temp file
        File temp = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        assertTrue("Temporary file cannot be deleted.", temp.delete());

        //try to generate sample audiomap.xml
        AudioMapXml.GenerateSampleXml(temp.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated audiomap.xml is zero size.", temp.length() > 0);

        //remove temp file;
        temp.delete();
    }

    /**
     * Tests audiomap.xml loading and validation.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void loadAndValidateXml() throws Exception {
        //create a temp file
        File temp = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        assertTrue("Temporary file cannot be deleted.", temp.delete());

        //try to generate sample audiomap.xml
        AudioMapXml.GenerateSampleXml(temp.getAbsolutePath());

        //check it is not empty
        assertTrue("Generated audiomap.xml is zero size.", temp.length() > 0);

        //Try to load and validate
        AudioMap audioMap = AudioMapXml.loadAudioMapXml(temp);

        //remove temp file;
        temp.delete();
    }
}
