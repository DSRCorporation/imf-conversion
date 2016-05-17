package com.netflix.imfutility.cpl;

import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset Map containing a path to an asset (resource, essence) for each asset UUID.
 */
public class AssetMap {

    private final Map<String, String> assetMap = new HashMap<>();

    public void addAsset(String id, String assetPath) {
        assetMap.put(id, assetPath);
    }

    public String getAsset(String id) {
        return assetMap.get(id);
    }

}
