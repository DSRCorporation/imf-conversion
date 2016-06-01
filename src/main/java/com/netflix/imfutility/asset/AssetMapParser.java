package com.netflix.imfutility.asset;

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import com.netflix.imfutility.xsd.imf.assetmap.AssetMapType;
import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.io.File;
import java.io.FileNotFoundException;

import static com.netflix.imfutility.Constants.ASSETMAP_PACKAGE;
import static com.netflix.imfutility.Constants.XSD_ASSETMAP_XSD;

/**
 * Parses ASSETMAP.xml and creates a {@link AssetMap} with location of each asset.
 */
public class AssetMapParser {

    public AssetMap parse(String assetMapXml) throws XmlParsingException, FileNotFoundException {
        File assetMapFile = new File(assetMapXml);
        if (!assetMapFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid ASSETMAP file: '%s' not found", assetMapFile.getAbsolutePath()));
        }

        AssetMap result = new AssetMap();

        AssetMapType assetmap = XmlParser.parse(assetMapFile, XSD_ASSETMAP_XSD, ASSETMAP_PACKAGE, AssetMapType.class);
        for (AssetType asset : assetmap.getAssetList().getAsset()) {
            UUID uuid = UUID.create(asset.getId());
            //per st0429-9:2014 Section 6.4, <ChunkList> shall contain one <Chunk> element only
            if (asset.getChunkList() == null || (asset.getChunkList().getChunk().size() != 1)) {
                throw new ConversionException(String.format(
                        "'%s' must have exactly one chunk for asset '%s'",
                        assetMapFile.getAbsolutePath(), uuid.toString()));
            }
            result.addAsset(uuid, asset.getChunkList().getChunk().get(0).getPath());
        }

        return result;
    }


}
