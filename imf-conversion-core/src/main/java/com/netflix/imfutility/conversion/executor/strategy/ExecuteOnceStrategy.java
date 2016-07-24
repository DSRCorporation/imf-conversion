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
package com.netflix.imfutility.conversion.executor.strategy;

import com.netflix.imfutility.conversion.executor.ExternalProcess;
import com.netflix.imfutility.conversion.executor.OutputRedirect;
import com.netflix.imfutility.conversion.executor.ProcessStarter;
import com.netflix.imfutility.conversion.templateParameter.context.TemplateParameterContextProvider;

import java.io.IOException;

/**
 * Simply starts the conversion operation and waits until it's finished.
 */
public class ExecuteOnceStrategy extends AbstractExecuteStrategy {

    public ExecuteOnceStrategy(TemplateParameterContextProvider contextProvider, ProcessStarter processStarter) {
        super(contextProvider, processStarter);
    }

    public void execute(OperationInfo operationInfo) throws IOException {
        if (operationInfo.isSkip()) {
            skipOperation(operationInfo);
            return;
        }
        ExternalProcess process = startProcess(operationInfo, OutputRedirect.ERR_LOG);
        process.finishWaitFor();
    }

}
