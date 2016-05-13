package com.netflix.imfutility.util.conversion.executor;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A fake process used in tests.
 * It logs when it's started and finished.
 */
public class FakeProcess extends Process {

    private final TestExecutorLogger executorLogger;
    private final ExternalProcess.ExternalProcessInfo processInfo;

    public FakeProcess(TestExecutorLogger executorLogger, ExternalProcess.ExternalProcessInfo processInfo) {
        this.executorLogger = executorLogger;
        this.processInfo = processInfo;

        executorLogger.startProcess(processInfo);
    }

    @Override
    public OutputStream getOutputStream() {
        return new NullOutputStream() {

            @Override
            public void close() throws IOException {
                super.close();
                executorLogger.finishProcess(processInfo);
            }

        };
    }

    @Override
    public InputStream getInputStream() {
        return new NullInputStream(100);
    }

    @Override
    public InputStream getErrorStream() {
        return new NullInputStream(100);
    }

    @Override
    public int waitFor() throws InterruptedException {
        executorLogger.finishProcess(processInfo);
        return 0;
    }

    @Override
    public int exitValue() {
        return 0;
    }

    @Override
    public void destroy() {

    }
}
