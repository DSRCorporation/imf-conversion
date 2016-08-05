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
package com.netflix.imfutility.ttmltostl;

import com.netflix.imfutility.dpp.MetadataXmlProvider;
import com.netflix.imfutility.ttmltostl.util.TtmlTestUtil;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

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

        File xml1 = TtmlTestUtil.getTtml("xml/debate.xml");

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

        Boolean result = new Convert().convertTTML(args);

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

        File xml1 = TtmlTestUtil.getTtml("xml/debate.xml");

        String[] args = {
                "--ttml",
                xml1.getAbsolutePath(),
                "36000000",//offset
                "--metadata",
                tempMetadataXml.getPath(),
                "--outputTTML",
                tempXML.getPath(), //"testoutput.xml",
                "--outputSTL",
                tempSTL.getPath() //"testoutput.stl"
        };

        Boolean result = new Convert().convertTTML(args);

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

        File xml1 = TtmlTestUtil.getTtml("xml/debate.xml");
        File xml2 = TtmlTestUtil.getTtml("xml/prueba_angel.xml");
        File xml3 = TtmlTestUtil.getTtml("xml/prueba_angel2.xml");

        String[] args = {
                "--ttml",
                xml1.getAbsolutePath(),
                "36000000",//offset
                "0",//start
                "45200",//end
                "--ttml",
                xml2.getAbsolutePath(),
                "39661010",//offset
                "6100",//start
                "13040",//end
                "--ttml",
                xml3.getAbsolutePath(),
                "43322020",//offset
                "297000",//start
                "215999000",//end
                "--metadata",
                tempMetadataXml.getPath(),
                "--outputTTML",
                tempXML.getPath(), //"testoutput.xml",
                "--outputSTL",
                tempSTL.getPath() //"testoutput.stl"
        };

        Boolean result = new Convert().convertTTML(args);

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

        Boolean result = new Convert().convertTTML(args);
        assertTrue("Convert.convertTTML didn't fail.", !result);
    }

}