package com.netflix.imfutility.inputparameters;

/**
 * Created by Alexander on 7/5/2016.
 */
public class FakeDefaultTools implements IDefaultTools {

    @Override
    public String getImfValidationTool() {
        return "java -jar somejar.jar";
    }
}
