package com.netflix.imfutility.cpl;

import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.imf.assetmap.AssetMapType;
import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.io.File;

/**
 * Created by Alexander on 5/15/2016.
 */
public class AssetMapParser {

    private static final String XSD_ASSETMAP_XSD = "xsd/imf/asset-map.xsd";
    private static final String ASSETMAP_PACKAGE = "com.netflix.imfutility.xsd.imf.assetmap";

    public AssetMap parse(String assetMapXml) throws XmlParsingException {
        AssetMap result = new AssetMap();

        AssetMapType assetmap = XmlParser.parse(new File(assetMapXml), XSD_ASSETMAP_XSD, ASSETMAP_PACKAGE, AssetMapType.class);
        for (AssetType asset : assetmap.getAssetList().getAsset()) {
            String id = asset.getId();
            if (asset.getChunkList() == null || asset.getChunkList().getChunk().isEmpty()) {
                throw new RuntimeException(String.format(
                        "'%s' must have at least one chunk for asset '%s'",
                        assetMapXml, id));
            }
            result.addAsset(id, asset);
        }

        return result;
    }


}
