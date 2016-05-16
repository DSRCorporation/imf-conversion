package com.netflix.imfutility.cpl;

import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 5/14/2016.
 */
public class AssetMap {

    private Map<String, AssetType> assetMap = new HashMap<>();

    public void addAsset(String id, AssetType asset) {
        assetMap.put(id, asset);
    }

    public AssetType getAsset(String id) {
        return assetMap.get(id);
    }

}
