package stl;

import ttml.TimedTextObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import static stl.GsiAttribute.*;

/**
 * Created by Alexander on 6/24/2016.
 */
public class DefaultGsiStrategy implements IGsiStrategy {

    @Override
    public void fillAttributes(TimedTextObject tto, byte[] ttiBlocks) {
        // CD and RD
        String currentDate = getCurrentDate();
        CD.setValue(currentDate);
        RD.setValue(currentDate);

        // TNB
        int ttiBlocksCount = ttiBlocks.length / ITtiStrategy.TTI_BLOCK_SIZE;
        TNB.setValue(ttiBlocksCount);

        // TNS
        TNS.setValue(tto.captions.size());
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyMMdd").format(new Date());
    }

    @Override
    public byte[] build(TimedTextObject tto) {
        byte[] result = new byte[GSI_BLOCK_SIZE];
        int lastPos = 0;
        for (GsiAttribute gsiAttribute : GsiAttribute.values()) {
            if (gsiAttribute.getValue() == null) {
                throw new RuntimeException("GSI attribute not set: " + gsiAttribute.name());
            }
            if (gsiAttribute.getValue().length != gsiAttribute.getBytesAllocated()) {
                throw new RuntimeException(
                        String.format("GSI attribute %s length (%d) is not equal to the expected one (%d)",
                                gsiAttribute.name(), gsiAttribute.getValue().length, gsiAttribute.getBytesAllocated()));
            }

            System.arraycopy(gsiAttribute.getValue(), 0, result, lastPos, gsiAttribute.getValue().length);
            lastPos += gsiAttribute.getValue().length;
        }

        return result;
    }
}
