package com.netflix.imfutility.inputparameters;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.ImfUtility;

import java.io.File;

/**
 * Defines executables for default tools.
 */
public class DefaultTools implements IDefaultTools {

    @Override
    public String getImfValidationTool() {
        return String.format("java -jar %s",
                new File(getCurrentLocation(), Constants.IMF_VALIDATION_PATH).getAbsolutePath());
    }

    protected File getCurrentLocation() {
        return new File(ImfUtility.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
    }
}
