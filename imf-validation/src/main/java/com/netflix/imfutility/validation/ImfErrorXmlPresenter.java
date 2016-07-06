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
