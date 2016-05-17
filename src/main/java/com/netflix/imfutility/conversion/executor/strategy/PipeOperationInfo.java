package com.netflix.imfutility.conversion.executor.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Information about all conversion operations to be executed in a pipe.
 */
public class PipeOperationInfo {

    private final List<OperationInfo> cycleOperations = new ArrayList<>();
    private final List<OperationInfo> tailOperations = new ArrayList<>();

    public List<OperationInfo> getCycleOperations() {
        return cycleOperations;
    }

    public List<OperationInfo> getTailOperations() {
        return tailOperations;
    }
}
