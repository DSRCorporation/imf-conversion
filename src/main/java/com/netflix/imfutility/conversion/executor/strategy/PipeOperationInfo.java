package com.netflix.imfutility.conversion.executor.strategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Information about all conversion operations to be executed in a pipe.
 */
public class PipeOperationInfo {

    private final List<List<OperationInfo>> cycleOperations = new ArrayList<>();
    private final List<OperationInfo> tailOperations = new ArrayList<>();

    public List<List<OperationInfo>> getCycleOperations() {
        return cycleOperations;
    }

    public void addCycleOperation(List<OperationInfo> cycleHeadOperations) {
        cycleOperations.add(cycleHeadOperations);
    }

    public void addCycleOperation(OperationInfo cycleHeadOperation) {
        List<OperationInfo> cycleHeadOperations = new ArrayList<>();
        cycleHeadOperations.add(cycleHeadOperation);
        cycleOperations.add(cycleHeadOperations);
    }


    public void addTailOperation(OperationInfo tailOperation) {
        tailOperations.add(tailOperation);
    }

    public void addTailOperations(Collection<OperationInfo> operations) {
        this.tailOperations.addAll(operations);
    }


    public List<OperationInfo> getTailOperations() {
        return tailOperations;
    }
}
