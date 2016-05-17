package com.netflix.imfutility.cpl;

import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset Map containing a path to an asset (resource, essence) for each asset UUID.
 */
public class AssetMap {

    private final Map<String, AssetType> assetMap = new HashMap<>();

    public void addAsset(String id, AssetType asset) {
        assetMap.put(id, asset);
    }

    public AssetType getAsset(String id) {
        return assetMap.get(id);
    }

}
