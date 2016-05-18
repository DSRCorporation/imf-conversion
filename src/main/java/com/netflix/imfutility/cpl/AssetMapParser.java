package com.netflix.imfutility.cpl;

import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.imf.assetmap.AssetMapType;
import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.io.File;

/**
 * Parses ASSETMAP.xml and creates a {@link AssetMap} with location of each asset.
 */
public class AssetMapParser {

    private static final String XSD_ASSETMAP_XSD = "xsd/imf/asset-map.xsd";
    private static final String ASSETMAP_PACKAGE = "com.netflix.imfutility.xsd.imf.assetmap";

    public AssetMap parse(String assetMapXml) throws XmlParsingException {
        AssetMap result = new AssetMap();

        AssetMapType assetmap = XmlParser.parse(new File(assetMapXml), XSD_ASSETMAP_XSD, ASSETMAP_PACKAGE, AssetMapType.class);
        for (AssetType asset : assetmap.getAssetList().getAsset()) {
            UUID uuid = UUID.create(asset.getId());
            //per st0429-9:2014 Section 6.4, <ChunkList> shall contain one <Chunk> element only
            if (asset.getChunkList() == null || (asset.getChunkList().getChunk().size() != 1)) {
                throw new RuntimeException(String.format(
                        "'%s' must have exactly one chunk for asset '%s'",
                        assetMapXml, uuid.toString()));
            }
            result.addAsset(uuid, asset.getChunkList().getChunk().get(0).getPath());
        }

        return result;
    }


}
