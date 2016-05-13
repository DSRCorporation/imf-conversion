package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.executor.ExternalProcess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Contains logs of all conversion operations that have been started and finished by a {@link FakeProcess}.
 */
public class TestExecutorLogger {

    private List<String> processes = new ArrayList<>();
    private Iterator<String> iterator = null;


    public void startProcess(ExternalProcess.ExternalProcessInfo processInfo) {
        processes.add("START: " + processInfo.toString());
    }

    public void finishProcess(ExternalProcess.ExternalProcessInfo processInfo) {
        processes.add("FINISH: " + processInfo.toString());
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
        return iterator.hasNext();
    }

    public String getProcess(int num) {
        return processes.get(num);
    }

    public void reset() {
        processes.clear();
        iterator = null;
    }

}
