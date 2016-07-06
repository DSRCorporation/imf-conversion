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
package com.netflix.imfutility.asset;

import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.util.ImpUtils;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static junit.framework.TestCase.assertEquals;

/**
 * <ul>
 * <li>Tests the ASSETMAP.xml can be parsed and mapped to Java model successfully.</li>
 * <li>Tests the XSD validation is applied to the ASSETMAP.xml and an exception is thrown is validation doesn't pass.</li>
 * </ul>
 */
public class AssetMapParserTest {

    @Test
    public void testParseCorrectConfig() throws Exception {
        AssetMap assetMap = new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getCorrectAssetmap());

        // the values as in ASSETMAP.xml
        // assetMap must contain full paths!
        assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:559452f0-9b31-4df7-a9c0-6b16d43bd8b0")));
        assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio_2.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:559452f0-9b31-4df7-a9c0-6b16d43bd8b1")));
        assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:6a64f1c5-629d-43be-befc-bebafed2e946")));
        assertEquals(ImpUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_2.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:6a64f1c5-629d-43be-befc-bebafed2e947")));
        assertEquals(ImpUtils.getAbsolutePath("CPL.xml"),
                assetMap.getAsset(UUID.create("urn:uuid:6f548f17-48c5-452a-94ea-9bb58c6c5b5b")));
        assertEquals(ImpUtils.getAbsolutePath("PKL.xml"),
                assetMap.getAsset(UUID.create("urn:uuid:805f2969-0356-4e70-88a0-8d9f724681d7")));

    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getBrokenXmlAssetmap());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        new AssetMapParser().parse(ImpUtils.getImpFolder(), ImpUtils.getInvalidXsdAssetmap());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new AssetMapParser().parse(ImpUtils.getImpFolder(), new File("invalid-path"));
    }

}
