package com.netflix.imfutility;

import com.netflix.imfutility.util.TemplateParameterContextCreator;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;

/**
 * A base class for IMFUtility tests that creates a working directory within a tmp folder and deletes the created working directory on exit.
 */
public class ImfUtilityTest {

    @BeforeClass
    public static void setupAll() throws IOException {
        // create both working directory and logs folder.
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
        File workingDir = TemplateParameterContextCreator.getWorkingDir();
        if (!workingDir.mkdir()) {
            throw new RuntimeException("Could not create a working dir within tmp folder");
        }

        File logs = new File(TemplateParameterContextCreator.getWorkingDir(), CoreConstants.LOGS_DIR);
        if (!logs.mkdir()) {
            throw new RuntimeException("Could not create a log dir within working dir");
        }
    }

    @AfterClass
    public static void teardownAll() throws IOException {
        FileUtils.deleteDirectory(TemplateParameterContextCreator.getWorkingDir());
    }

}
