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
import com.netflix.imfutility.dpp.MetadataXmlProvider;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Tests for the converter.
 *
 * Created by Alexandr on 6/2/2016.
 */
public class ConvertTest {

    /**
     * Test simple positive case.
     *
     * @throws Exception
     */
    @Test
    public void convertSingleTTML() throws Exception {
        File tempXML = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        tempXML.deleteOnExit();
        assertTrue("Temporary file cannot be deleted.", tempXML.delete());
        File tempSTL = File.createTempFile(UUID.randomUUID().toString(), ".stl");
        assertTrue("Temporary file cannot be deleted.", tempSTL.delete());
        tempSTL.deleteOnExit();
        File tempMetadataXml = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        tempMetadataXml.deleteOnExit();
        MetadataXmlProvider.generateEmptyXml(tempMetadataXml.getAbsolutePath());

        File xml1 = new File(ClassLoader.getSystemClassLoader().getResource("xml/debate.xml").toURI());

        String[] args = {
                "--ttml",
                xml1.getAbsolutePath(),
                "--metadata",
                tempMetadataXml.getPath(),
                "--outputTTML",
                tempXML.getPath(), //"testoutput.xml",
                "--outputSTL",
                tempSTL.getPath() //"testoutput.stl"
        };

        Boolean result = Convert.convertTTML(args);

        assertTrue("Convert.convertTTML returned failure.", result);
        assertTrue("Generated TTML is zero size.", tempXML.length() > 0);
        assertTrue("Generated STL is zero size.", tempSTL.length() > 0);
    }

    /**
     * Test simple positive case.
     *
     * @throws Exception
     */
    @Test
    public void convertSingleTTMLWithOffset() throws Exception {
        File tempXML = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        tempXML.deleteOnExit();
        assertTrue("Temporary file cannot be deleted.", tempXML.delete());
        File tempSTL = File.createTempFile(UUID.randomUUID().toString(), ".stl");
        assertTrue("Temporary file cannot be deleted.", tempSTL.delete());
        tempSTL.deleteOnExit();
        File tempMetadataXml = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        tempMetadataXml.deleteOnExit();
        MetadataXmlProvider.generateEmptyXml(tempMetadataXml.getAbsolutePath());

        File xml1 = new File(ClassLoader.getSystemClassLoader().getResource("xml/debate.xml").toURI());

        String[] args = {
                "--ttml",
                xml1.getAbsolutePath(),
                "10:00:00:00",//offset
                "--metadata",
                tempMetadataXml.getPath(),
                "--outputTTML",
                tempXML.getPath(), //"testoutput.xml",
                "--outputSTL",
                tempSTL.getPath() //"testoutput.stl"
        };

        Boolean result = Convert.convertTTML(args);

        assertTrue("Convert.convertTTML returned failure.", result);
        assertTrue("Generated TTML is zero size.", tempXML.length() > 0);
        assertTrue("Generated STL is zero size.", tempSTL.length() > 0);
    }

    /**
     * Test simple positive case.
     *
     * @throws Exception
     */
    @Test
    public void mergeTTML() throws Exception {

        File tempXML = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        tempXML.deleteOnExit();
        assertTrue("Temporary file cannot be deleted.", tempXML.delete());
        File tempSTL = File.createTempFile(UUID.randomUUID().toString(), ".stl");
        assertTrue("Temporary file cannot be deleted.", tempSTL.delete());
        tempSTL.deleteOnExit();
        File tempMetadataXml = File.createTempFile(UUID.randomUUID().toString(), ".xml");
        tempMetadataXml.deleteOnExit();
        MetadataXmlProvider.generateEmptyXml(tempMetadataXml.getAbsolutePath());

        File xml1 = new File(ClassLoader.getSystemClassLoader().getResource("xml/debate.xml").toURI());
        File xml2 = new File(ClassLoader.getSystemClassLoader().getResource("xml/prueba_angel.xml").toURI());
        File xml3 = new File(ClassLoader.getSystemClassLoader().getResource("xml/prueba_angel2.xml").toURI());

        String[] args = {
                "--ttml",
                xml1.getAbsolutePath(),
                "10:00:00:00",//offset
                "00:00:00:00",//start
                "00:01:45:20",//end
                "--ttml",
                xml2.getAbsolutePath(),
                "11:01:01:01",//offset
                "00:00:6:10",//start
                "00:00:13:04",//end
                "--ttml",
                xml3.getAbsolutePath(),
                "12:02:02:02",//offset
                "00:04:57:00",//start
                "59:59:59:00",//end
                "--metadata",
                tempMetadataXml.getPath(),
                "--outputTTML",
                tempXML.getPath(), //"testoutput.xml",
                "--outputSTL",
                tempSTL.getPath() //"testoutput.stl"
        };

        Boolean result = Convert.convertTTML(args);

        assertTrue("Convert.convertTTML returned failure.", result);
        assertTrue("Generated TTML is zero size.", tempXML.length() > 0);
        assertTrue("Generated STL is zero size.", tempSTL.length() > 0);
    }

    /**
     * Test invalid argument case.
     *
     * @throws Exception
     */
    @Test
    public void invalidArgs() throws Exception {
        String[] args = {
                "--ttml",
        };

        Boolean result = Convert.convertTTML(args);
        assertTrue("Convert.convertTTML didn't fail.", !result);
    }

}