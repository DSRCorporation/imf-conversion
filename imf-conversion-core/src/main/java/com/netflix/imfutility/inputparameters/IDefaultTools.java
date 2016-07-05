package com.netflix.imfutility.inputparameters;

/**
 * Default executables distributed with the utility.
 */
public interface IDefaultTools {

    /**
     * Default IMF validation executable (Photon).
     *
     * @return default IMF validation executable (Photon) as 'java -jar path-to-jar' form
     */
    String getImfValidationTool();

}
