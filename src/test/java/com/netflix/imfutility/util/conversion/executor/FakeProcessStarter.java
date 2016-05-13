package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.executor.ProcessStarter;

import java.io.IOException;
import java.util.List;

/**
 * A process starter used in tests. It starts {@link FakeProcess} instead of a real one.
 */
public class FakeProcessStarter extends ProcessStarter {

    private TestExecutorLogger executorLogger;

    public FakeProcessStarter(TestExecutorLogger executorLogger) {
        this.executorLogger = executorLogger;
    }

    @Override
    public Process startProcess(ExternalProcess.ExternalProcessInfo processInfo, List<String> execAndParams, String workingDir) throws IOException {
        return new FakeProcess(executorLogger, processInfo);
    }

}
