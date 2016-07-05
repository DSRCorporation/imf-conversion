package com.netflix.imfutility.dpp.inputparameters;

import com.netflix.imfutility.inputparameters.IDefaultTools;

/**
 * Default executables distributed with the utility for DPP format.
 */
public interface IDppDefaultTools extends IDefaultTools {

    /**
     * Default TTML to EBU STL subtitles conversion executable (ttml-to-stl project).
     *
     * @return default TTML to EBU STL subtitles conversion executable (ttml-to-stl project) as 'java -jar path-to-jar' form
     */
    String getTtmlToStlTool();

}
