package com.netflix.imfutility.cpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 5/15/2016.
 */
public class SequenceMap {

    private final Map<String, SequenceInfo> sequenceMap = new HashMap<>();

    public void addAsset(String id, SequenceInfo asset) {
        sequenceMap.put(id, asset);
    }

    public SequenceInfo getSequenceInfo(String id) {
        return sequenceMap.get(id);
    }

}
