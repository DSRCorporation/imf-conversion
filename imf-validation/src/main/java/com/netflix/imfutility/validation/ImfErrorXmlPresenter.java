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

import com.netflix.imflibrary.utils.ErrorLogger;
import com.netflix.imfutility.generated.validation.Errors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.List;

/**
 * Prints all errors in a simple XML form:
 * <pre>
 * {@code
 * <errors>
 * <error>error1</error>
 * <error>error2</error>
 * ....
 * </errors>}
 * </pre>
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
                errors.getError().add(errorObj.toString());
            }

            jaxbMarshaller.marshal(errors, file);

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
