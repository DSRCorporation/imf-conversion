import org.apache.commons.cli.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to parse command line parameters.
 * <p>
 * Created by Alexandr on 6/2/2016.
 */
public class CmdParametersHelper {

    private Boolean doOuputTTML = false;
    private String outputTTMLFile = null;
    private Boolean doOutputSTL = false;
    private String outputSTLFile = null;
    private List<TtmlInDescriptor> ttmlInDescriptors = new ArrayList<TtmlInDescriptor>();
    private String metadataXml = null;

    public Boolean doOuputTTML() {
        return doOuputTTML;
    }

    public String getOutputTTMLFile() {
        return outputTTMLFile;
    }

    public Boolean doOutputSTL() {
        return doOutputSTL;
    }

    public String getOutputSTLFile() {
        return outputSTLFile;
    }

    public List<TtmlInDescriptor> getTtmlInDescriptors() {
        return ttmlInDescriptors;
    }

    public String getMetadataXml() {
        return metadataXml;
    }

    public Boolean parseCmdOptions(String[] args) {

        Option help = Option.builder()
                .longOpt("help")
                .desc("Print this message")
                .build();

        Option ttmlFile = Option.builder()
                .longOpt("ttml")
                .desc("Input TTML file parameters:"
                        + "\n<file> - The TTML file path."
                        + "\n<offsetTC> - Offset timecode to shift captions of the TTML file."
                        + "\n<startTC> - Start timecode to get captions of the TTML file."
                        + "\n<endTC> - End timecode to get captions of the TTML file.")
                .numberOfArgs(4)
                .optionalArg(true)
                .argName("file> <offsetTC> <startTC> <endTC")
                .build();

        Option outputSTL = Option.builder()
                .longOpt("outputSTL")
                .desc("Output EBU STL file")
                .hasArg()
                .argName("outputSTL")
                .build();

        Option outputTTML = Option.builder()
                .longOpt("outputTTML")
                .desc("Output TTML file")
                .hasArg()
                .argName("outputTTML")
                .build();

        Option metadataOpt = Option.builder()
                .longOpt("metadata")
                .desc("DPP Metadata.xml")
                .hasArg()
                .argName("metadata")
                .build();

        Options options = new Options();
        options.addOption(help);
        options.addOption(ttmlFile);
        options.addOption(outputSTL);
        options.addOption(outputTTML);
        options.addOption(metadataOpt);

        // create the parser
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        Boolean parsingError = false;
        //Reset ttmlInDescriptors
        ttmlInDescriptors = new ArrayList<TtmlInDescriptor>();
        try {
            // parse the command line for --ttml parameters
            line = parser.parse(options, args);
            for (Option option : line.getOptions()) {
                if (option.equals(ttmlFile)) {
                    TtmlInDescriptor ttmlInDescriptor = new TtmlInDescriptor();
                    try {
                        ttmlInDescriptor.setFile(option.getValue(0));
                        ttmlInDescriptor.setOffsetTC(option.getValue(1));
                        ttmlInDescriptor.setStartTC(option.getValue(2));
                        ttmlInDescriptor.setEndTC(option.getValue(3));
                    } catch (IndexOutOfBoundsException e) {
                        //It is error only if don't have file name
                        //For required file it may not be thrown. We will check it later.
                    }

                    if (ttmlInDescriptor.getFile() == null) {
                        System.err.println("--ttml parameter must have at least <file> attribute defined.");
                        parsingError = true;
                    }

                    ttmlInDescriptors.add(ttmlInDescriptor);
                }
            }
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            parsingError = true;
        }

        if ((line != null && line.hasOption(help.getLongOpt()))
                || parsingError) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(120);
            formatter.printHelp("subtitleConverter", options);
            return false;
        }

        if (ttmlInDescriptors.size() == 0) {
            System.err.println("At least one input TTML file must be provided");
            return false;
        }

        doOuputTTML = line.hasOption(outputTTML.getLongOpt());
        if (doOuputTTML) {
            outputTTMLFile = line.getOptionValue(outputTTML.getLongOpt());
        }

        doOutputSTL = line.hasOption(outputSTL.getLongOpt());
        if (doOutputSTL) {
            outputSTLFile = line.getOptionValue(outputSTL.getLongOpt());
            metadataXml = line.getOptionValue(metadataOpt.getLongOpt());
        }

        return true;
    }
}
