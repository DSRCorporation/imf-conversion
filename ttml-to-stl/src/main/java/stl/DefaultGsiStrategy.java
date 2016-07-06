/**
 * Copyright (C) 2016 Netflix, Inc.
 *
 *     This file is part of IMF Conversion Utility.
 *
 *     IMF Conversion Utility is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     IMF Conversion Utility is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with IMF Conversion Utility.  If not, see <http://www.gnu.org/licenses/>.
 */
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
