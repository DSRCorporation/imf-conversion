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
package com.netflix.imfutility.ttmltostl.stl;

import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.CD;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.CPN;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.RD;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.TNB;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.TNS;

/**
 * Default implementation of EBU STL GSI block building.
 */
public class DefaultGsiStrategy extends AbstractStlStrategy implements IGsiStrategy {

    @Override
    public String getCharset() {
        if ("850".equals(CPN.getStringValue())) {
            return "Cp850";
        } else if ("437".equals(CPN.getStringValue())) {
            return "Cp437";
        } else if ("860".equals(CPN.getStringValue())) {
            return "Cp860";
        } else if ("863".equals(CPN.getStringValue())) {
            return "Cp863";
        } else if ("865".equals(CPN.getStringValue())) {
            return "Cp865";
        }
        throw new RuntimeException("Can not get GSI block charset. Unknown CPN value: " + CPN.getStringValue());
    }

    @Override
    public void fillAttributes(TimedTextObject tto) {
        // CD and RD
        String currentDate = getCurrentDate();
        CD.setValue(currentDate);
        RD.setValue(currentDate);

        // TNS
        TNS.setValue(tto.getCaptions().size());
    }

    @Override
    public void fillTtiAttributes(byte[] ttiBlocks) {
        // TNB
        int ttiBlocksCount = ttiBlocks.length / ITtiStrategy.TTI_BLOCK_SIZE;
        TNB.setValue(ttiBlocksCount);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyMMdd").format(new Date());
    }

    @Override
    public byte[] build(TimedTextObject tto) throws IOException {
        byte[] result = new byte[GSI_BLOCK_SIZE];
        int lastPos = 0;
        for (GsiAttribute gsiAttribute : GsiAttribute.values()) {
            byte[] value = gsiAttribute.getValue(getCharset());
            if (value.length != gsiAttribute.getBytesAllocated()) {
                throw new RuntimeException(
                        String.format("GSI attribute %s length (%d) is not equal to the expected one (%d)",
                                gsiAttribute.name(), value.length, gsiAttribute.getBytesAllocated()));
            }

            System.arraycopy(value, 0, result, lastPos, value.length);
            lastPos += value.length;
        }

        return result;
    }
}
