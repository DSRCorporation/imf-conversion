package com.netflix.imfutility.cpl;

import com.netflix.imfutility.cpl.uuid.SequenceUUID;
import com.netflix.imfutility.cpl.uuid.UUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander on 5/15/2016.
 */
public class SequenceMap {

    private final Map<SequenceUUID, SequenceInfo> sequenceMap = new HashMap<>();
    private final Map<SequenceUUID, List<UUID>> sequenceAssetMap = new HashMap<>();

    public void addSequenceAsset(SequenceUUID seqUuid, UUID assetUuid) {
        List<UUID> assets = sequenceAssetMap.get(seqUuid);
        if (assets == null) {
            assets = new ArrayList<>();
            sequenceAssetMap.put(seqUuid, assets);
        }
        assets.add(assetUuid);
    }

    public List<UUID> getAssets(SequenceUUID seqUuid) {
        return sequenceAssetMap.get(seqUuid);
    }

}
