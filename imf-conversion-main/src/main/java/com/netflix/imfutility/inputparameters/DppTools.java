package com.netflix.imfutility.inputparameters;

import com.netflix.imfutility.Constants;
import com.netflix.imfutility.dpp.inputparameters.IDppDefaultTools;

import java.io.File;

/**
 * Defines executables for DPP-related default tools.
 */
public class DppTools extends DefaultTools implements IDppDefaultTools {
    @Override
    public String getTtmlToStlTool() {
        return String.format("java -jar %s",
                new File(getCurrentLocation(), Constants.TTML_TO_STL_PATH).getAbsolutePath());

    }
}
