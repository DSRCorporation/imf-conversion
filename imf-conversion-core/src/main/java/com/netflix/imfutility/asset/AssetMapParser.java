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

import com.netflix.imfutility.ConversionException;
import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.generated.imf.assetmap.AssetMapType;
import com.netflix.imfutility.generated.imf.assetmap.AssetType;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;

import java.io.File;
import java.io.FileNotFoundException;

import static com.netflix.imfutility.CoreConstants.ASSETMAP_PACKAGE;
import static com.netflix.imfutility.CoreConstants.XSD_ASSETMAP_XSD;

/**
 * Parses ASSETMAP.xml and creates a {@link AssetMap} with location of each asset. Asset map contains full absolute paths.
 */
public class AssetMapParser {

    /**
     * Parses the given assetmap.xml and fills {@link AssetMap}. The result Asset map contains full absolute paths for each UUID.
     *
     * @param impDirectory the IMP directory
     * @param assetMapFile a full path to ASSETMAP.xml
     * @return am  Asset map instance containing full absolute paths for each UUID.
     * @throws XmlParsingException   if input is not a valid XML or it doesn't pass XSD validation
     * @throws FileNotFoundException if the input path doesn't define a file.
     */
    public final AssetMap parse(File impDirectory, File assetMapFile) throws XmlParsingException, FileNotFoundException {
        if (!assetMapFile.isFile()) {
            throw new FileNotFoundException(String.format("Invalid ASSETMAP file: '%s' not found", assetMapFile.getAbsolutePath()));
        }

        AssetMap result = new AssetMap();

        AssetMapType assetmap = XmlParser.parse(assetMapFile, new String[]{XSD_ASSETMAP_XSD}, ASSETMAP_PACKAGE, AssetMapType.class);
        for (AssetType asset : assetmap.getAssetList().getAsset()) {
            UUID uuid = UUID.create(asset.getId());
            //per st0429-9:2014 Section 6.4, <ChunkList> shall contain one <Chunk> element only
            if (asset.getChunkList() == null || (asset.getChunkList().getChunk().size() != 1)) {
                throw new ConversionException(String.format(
                        "'%s' must have exactly one chunk for asset '%s'",
                        uuid.toString(), assetMapFile.getAbsolutePath()));
            }

            String assetPath = asset.getChunkList().getChunk().get(0).getPath();

            // we should add a full absolute path
            String assetFullPath;
            File assetAbsoluteFile = new File(assetPath);
            File assetRelativeFile = new File(impDirectory, assetPath);
            if (assetAbsoluteFile.isFile()) {
                // assetmap.xml contains full path?
                assetFullPath = assetAbsoluteFile.getAbsolutePath();
            } else if (assetRelativeFile.isFile()) {
                // assetmap.xml contains a path relative to IMP folder?
                assetFullPath = assetRelativeFile.getAbsolutePath();
            } else {
                throw new ConversionException(String.format(
                        "'%s' must point to an existing file: '%s'", uuid.toString(), assetPath));
            }

            result.addAsset(uuid, assetFullPath);
        }

        return result;
    }


}
