/*
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
package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.executor.strategy.OperationInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Contains logs of all conversion operations that have been started and finished by a {@link FakeProcess}.
 */
public class TestExecutorLogger {

    private final List<String> processes = new ArrayList<>();
    private Iterator<String> iterator = null;


    public void startProcess(ExternalProcess.ExternalProcessInfo processInfo) {
        processes.add(String.format("START: %s %s", processInfo.toString(), processInfo.getOutputRedirect().toString()));
    }

    public void finishProcess(ExternalProcess.ExternalProcessInfo processInfo) {
        processes.add(String.format("FINISH: %s %s", processInfo.toString(), processInfo.getOutputRedirect().toString()));
    }

    public void skipOperation(OperationInfo operationInfo) {
        processes.add(String.format("SKIPPED: %s", operationInfo.toString()));
    }

    public int getProcessCount() {
        return processes.size();
    }

    public String getNext() {
        if (iterator == null) {
            iterator = processes.iterator();
        }
        assertTrue("There are less executed processes than expected!", iterator.hasNext());
        return iterator.next();
    }

    public boolean hasNext() {
        if (iterator == null) {
            iterator = processes.iterator();
        }
        return iterator.hasNext();
    }

    public String getProcess(int num) {
        return processes.get(num);
    }

    public void reset() {
        processes.clear();
        iterator = null;
    }

    public void assertNextStart(String text, int num) {
        assertEquals("START: External Process " + num + ": " + text, getNext());
    }

    public void assertNextFinish(String text, int num) {
        assertEquals("FINISH: External Process " + num + ": " + text, getNext());
    }

    public void assertSkipped(String text) {
        assertEquals("SKIPPED: " + text, getNext());
    }

}
