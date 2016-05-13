package com.netflix.imfutility.dpp;

import com.netflix.imfutility.dpp.audiomap.AudioMap;
import com.netflix.imfutility.dpp.metadata.AudioTrackLayoutDmAs11Type;

import java.io.File;
import java.util.LinkedHashMap;
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

    /**
     * Tests that audio map parameter for pan filter is generated properly.
     *
     * @throws Exception
     */
    @org.junit.Test
    public void getAudioMapParameter() throws Exception {
        //create a temp file
        File temp = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        assertTrue("Temporary file cannot be deleted.", temp.delete());
        //try to generate sample audiomap.xml
        AudioMapXml.GenerateSampleXml(temp.getAbsolutePath());
        //check it is not empty
        assertTrue("Generated audiomap.xml is zero size.", temp.length() > 0);
        AudioMap audioMap = AudioMapXml.loadAudioMapXml(temp);

        //Check simple way
        LinkedHashMap<String, Integer> sequencedTracks = new LinkedHashMap<String, Integer>();
        sequencedTracks.put("urn:uuid:38d52c00-68d3-4056-8858-28eeaf3238d3", 2);
        String audioMapParameter = AudioMapPanParameter.getPanParameter(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, audioMap,  sequencedTracks);
        assertTrue("Generated audio map pan parameter is wrong.", "4c|c0=c0|c1=c1|c2=0*c0|c3=0*c0".equals(audioMapParameter));

        //Check with several virtual tracks
        LinkedHashMap<String, Integer> sequencedTracks2 = new LinkedHashMap<String, Integer>();
        sequencedTracks2.put("some track before", 6);
        sequencedTracks2.put("urn:uuid:38d52c00-68d3-4056-8858-28eeaf3238d3", 2);
        String audioMapParameter2 = AudioMapPanParameter.getPanParameter(AudioTrackLayoutDmAs11Type.EBU_R_48_2_A, audioMap,  sequencedTracks2);
        assertTrue("Generated audio map pan parameter is wrong.", "4c|c0=c6|c1=c7|c2=0*c0|c3=0*c0".equals(audioMapParameter2));

        //remove temp files;
        temp.delete();
    }
}
