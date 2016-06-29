import stl.DefaultTtiStrategy;
import stl.StlBuilder;
import stl.bbc.BbcGsiStrategy;
import ttml.FatalParsingException;
import ttml.FormatTTML;
import ttml.TimedTextFileFormat;
import ttml.TimedTextObject;
import xml.XmlParsingException;

import java.io.*;

/**
 * Class to parametrize and perform the conversion.
 */
public class Convert {


    /**
     * Convert TTML format to STL.
     *
     * @param args see command line help.
     * @return conversion result
     */

    public static Boolean convertTTML(String[] args) {

        //Parse input parameters
        CmdParametersHelper cmdHelper = new CmdParametersHelper();
        if (!cmdHelper.parseCmdOptions(args)) {
            //parsing error occured
            return false;
        }

        try {
            TimedTextFileFormat ttff = new FormatTTML();
            TimedTextObject tto = null;
            //Parse all TTML files
            for (TtmlInDescriptor ttmlInDescriptor : cmdHelper.getTtmlInDescriptors()) {
                System.out.println("Processing input TTML: " + ttmlInDescriptor.getFile());
                File file = new File(ttmlInDescriptor.getFile());
                InputStream is = new FileInputStream(file);
                tto = ttff.parseFile(file.getName(), is, ttmlInDescriptor.getStartTC(),
                        ttmlInDescriptor.getEndTC(), ttmlInDescriptor.getOffsetTC());
            }

            //Generate output
            if (tto != null) {
                if (cmdHelper.doOuputTTML()) {
                    String outputTTMLFile = cmdHelper.getOutputTTMLFile();
                    System.out.println("Generating output TTML: " + outputTTMLFile);
                    IOClass.writeFileTxt(outputTTMLFile, new FormatTTML().toFile(tto));
                }

                if (cmdHelper.doOutputSTL()) {
                    String outputSTLFile = cmdHelper.getOutputSTLFile();
                    System.out.println("Generating output STL: " + outputSTLFile);
                    try (OutputStream output = new BufferedOutputStream(new FileOutputStream(outputSTLFile))) {
                        byte[] stl = new StlBuilder()
                                .build(tto, new BbcGsiStrategy(cmdHelper.getMetadataXml()), new DefaultTtiStrategy());
                        output.write(stl);
                    }
                }

                return true;
            }
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (FatalParsingException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (XmlParsingException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    /**
     * Entry point.
     *
     * @param args
     */
    public static void main(String[] args) {
        if (!convertTTML(args)) {
            System.exit(1);
        }
    }
}
