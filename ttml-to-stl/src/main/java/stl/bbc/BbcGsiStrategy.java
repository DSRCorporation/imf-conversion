package stl.bbc;

import com.netflix.imfutility.xsd.dpp.metadata.Dpp;
import com.netflix.imfutility.xsd.dpp.metadata.TimecodeType;
import stl.DefaultGsiStrategy;
import ttml.TimedTextObject;
import xml.XmlParser;
import xml.XmlParsingException;

import java.io.File;

import static stl.GsiAttribute.*;

/**
 * Created by Alexander on 6/23/2016.
 */
public class BbcGsiStrategy extends DefaultGsiStrategy {

    private static final String METADATA_XML_SCHEME = "metadata.xsd";
    private static final String METADATA_PACKAGE = "com.netflix.imfutility.xsd.dpp.metadata";


    private final Dpp metadata;

    public BbcGsiStrategy(String metadataXml) throws XmlParsingException {
        this.metadata = XmlParser.parse(new File(metadataXml), METADATA_XML_SCHEME, METADATA_PACKAGE, Dpp.class);
    }

    @Override
    public void fillAttributes(TimedTextObject tto, byte[] ttiBlocks) {
        super.fillAttributes(tto, ttiBlocks);

        // OPT
        if (metadata.getEditorial().getProgrammeTitle() != null) {
            OPT.setValue(metadata.getEditorial().getProgrammeTitle());
        } else {
            OPT.fillEmptyValue();
        }

        // OET
        if (metadata.getEditorial().getEpisodeTitleNumber() != null) {
            OET.setValue(metadata.getEditorial().getEpisodeTitleNumber());
        } else {
            OET.fillEmptyValue();
        }

        // TCP
        fillTcp();

        // TCF
        TCF.setValue(tto.captions.get(tto.captions.firstKey()).start.getTime("hhmmssff/25"));

        // PUB
        if (metadata.getEditorial().getOriginator() != null) {
            PUB.setValue(metadata.getEditorial().getOriginator());
        } else {
            PUB.fillEmptyValue();
        }

        // EN
        if (metadata.getEditorial().getDistributor() != null) {
            EN.setValue(metadata.getEditorial().getDistributor());
        } else {
            EN.fillEmptyValue();
        }

        // ECD
        if (metadata.getTechnical().getContactInformation() != null
                && metadata.getTechnical().getContactInformation().getContactEmail() != null) {
            ECD.setValue(metadata.getTechnical().getContactInformation().getContactEmail());
        } else {
            ECD.fillEmptyValue();
        }
    }

    private void fillTcp() {
        TCP.fillEmptyValue();

        if (metadata.getTechnical().getTimecodes() == null) {
            return;
        }
        if (metadata.getTechnical().getTimecodes().getParts() == null) {
            return;
        }
        if (metadata.getTechnical().getTimecodes().getParts().getPart() == null) {
            return;
        }
        if (metadata.getTechnical().getTimecodes().getParts().getPart().isEmpty()) {
            return;
        }

        TimecodeType tc = metadata.getTechnical().getTimecodes().getParts().getPart().get(0).getPartSOM();

        String[] parts = tc.getValue().split("[:;]");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() == 1) {
                sb.append("0");
                sb.append(parts[i]);
            } else if (parts[i].length() > 2) {
                sb.append(parts[i].substring(parts[i].length() - 2, parts[i].length()));
            } else {
                sb.append(parts[i]);
            }
        }

        TCP.setValue(sb.toString());
    }

}
