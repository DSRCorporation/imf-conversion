/**
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

    private volatile boolean closed = false;

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
                if (!closed) {
                    executorLogger.finishProcess(processInfo);
                    closed = true;
                }
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
        if (!closed) {
            executorLogger.finishProcess(processInfo);
            closed = true;
        }
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
