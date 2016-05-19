package com.netflix.imfutility.asset;

import com.netflix.imfutility.cpl.uuid.UUID;
import com.netflix.imfutility.xsd.imf.assetmap.AssetType;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset Map containing a path to an asset (resource, essence) for each asset UUID.
 */
public class AssetMap {

    private final Map<UUID, String> assetMap = new HashMap<>();

    public void addAsset(UUID uuid, String assetPath) {
        assetMap.put(uuid, assetPath);
    }

    public String getAsset(UUID uuid) {
        return assetMap.get(uuid);
    }

}
