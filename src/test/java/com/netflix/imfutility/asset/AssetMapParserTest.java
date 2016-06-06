package com.netflix.imfutility.asset;

import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.util.AssetmapUtils;
import com.netflix.imfutility.xml.XmlParsingException;
import org.junit.Test;

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
        AssetMap assetMap = new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getCorrectAssetmap());

        // the values as in ASSETMAP.xml
        // assetMap must contain full paths!
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:559452f0-9b31-4df7-a9c0-6b16d43bd8b0")));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_Audio_2.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:559452f0-9b31-4df7-a9c0-6b16d43bd8b1")));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:6a64f1c5-629d-43be-befc-bebafed2e946")));
        assertEquals(AssetmapUtils.getAbsolutePath("Chimera50_FTR_C_EN_XG-NR_20_4K_20150622_OV_2.mxf"),
                assetMap.getAsset(UUID.create("urn:uuid:6a64f1c5-629d-43be-befc-bebafed2e947")));
        assertEquals(AssetmapUtils.getAbsolutePath("CPL.xml"),
                assetMap.getAsset(UUID.create("urn:uuid:6f548f17-48c5-452a-94ea-9bb58c6c5b5b")));
        assertEquals(AssetmapUtils.getAbsolutePath("PKL.xml"),
                assetMap.getAsset(UUID.create("urn:uuid:805f2969-0356-4e70-88a0-8d9f724681d7")));

    }

    @Test(expected = XmlParsingException.class)
    public void testParseBrokenXml() throws Exception {
        new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getBrokenXmlAssetmap());
    }

    @Test(expected = XmlParsingException.class)
    public void testParseInvalidXsd() throws Exception {
        new AssetMapParser().parse(AssetmapUtils.getImpFolder(), AssetmapUtils.getInvalidXsdAssetmap());
    }

    @Test(expected = FileNotFoundException.class)
    public void testParseInvalidFilePath() throws Exception {
        new AssetMapParser().parse(AssetmapUtils.getImpFolder(), "C:/invalid-path");
    }

}
