package com.netflix.imfutility;

import com.netflix.imfutility.dpp.DppFormatBuilder;

/**
 * Created by Alexander on 4/22/2016.
 */
public class ImfUtility {

    public static void main(String... args) {
        //TODO: add command line parameter processing logic
        String configXml = ClassLoader.getSystemClassLoader().getResource("xml/config.xml").getPath();
        String conversionXml = ClassLoader.getSystemClassLoader().getResource("xml/conversion.xml").getPath();

        new DppFormatBuilder().build(configXml, conversionXml);
    }

}
