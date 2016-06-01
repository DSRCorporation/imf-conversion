package com.netflix.imfutility;

import com.netflix.imfutility.dpp.DppFormatBuilder;

/**
 * The main class.
 * <ul>
 * <li>Invokes command line parsing logic</li>
 * <li>Invokes an appropriate builder depending on the inout format and mode</li>
 * </ul>
 */
public class ImfUtility {

    public static void main(String... args) {
        //TODO: add command line parameter processing logic
        String configXml = ClassLoader.getSystemClassLoader().getResource("xml/config.xml").getPath();
        String imp = ClassLoader.getSystemClassLoader().getResource("xml/IMP-test").getPath();
        String cplXml = "CPL-test.xml";
        String metadataXml = ClassLoader.getSystemClassLoader().getResource("xml/metadata.xml").getPath();

        new DppFormatBuilder(configXml, metadataXml).build(imp, cplXml);
    }

}
