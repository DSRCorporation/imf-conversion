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
package com.netflix.imfutility.validation;

import com.netflix.imflibrary.IMFErrorLogger.IMFErrors.ErrorLevels;
import com.netflix.imflibrary.utils.ErrorLogger;
import com.netflix.imfutility.generated.validation.ErrorType;
import com.netflix.imfutility.generated.validation.Errors;
import com.netflix.imfutility.generated.validation.LevelType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.List;

/**
 * Prints all errors in a simple XML form.
 *
 */
public class ImfErrorXmlPresenter implements IImfErrorPresenter {

    @Override
    public void printErrors(List<ErrorLogger.ErrorObject> errorObjs, String workingDir, String fileName) {
        File file = new File(workingDir, fileName);
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Errors.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Errors errors = new Errors();
            for (ErrorLogger.ErrorObject errorObj : errorObjs) {
                ErrorType error = new ErrorType();
                error.setValue(getErrorMsg(errorObj));
                error.setLevel(getErrorLevel(errorObj));
                errors.getError().add(error);
            }

            jaxbMarshaller.marshal(errors, file);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private String getErrorMsg(ErrorLogger.ErrorObject errorObj) {
        String errorLevel = errorObj.getErrorLevel().toString();
        String errorCode = errorObj.getErrorCode().toString();
        String errorDescr = errorObj.getErrorDescription();
        if (errorDescr == null) {
            errorDescr = "<no description>";
        }
        return String.format("%s: %s: %s", errorLevel, errorCode, errorDescr);
    }

    private LevelType getErrorLevel(ErrorLogger.ErrorObject errorObj) {
        if (!(errorObj.getErrorLevel() instanceof ErrorLevels)) {
            throw new RuntimeException("ErrorLevels enum is expected as error level");
        }
        switch ((ErrorLevels) errorObj.getErrorLevel()) {
            case FATAL:
                return LevelType.FATAL;
            case NON_FATAL:
                return LevelType.NON_FATAL;
            case WARNING:
                return LevelType.WARNING;
            default:
                throw new RuntimeException("Unknown error level " + errorObj.getErrorLevel().toString());
        }
    }
}
