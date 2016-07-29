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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Lists all attributes to be present in GSI block.
 * <ul>
 *     <li>Provides a required size for each attribute.</li>
 *     <li>Provides default values for some attributes.</li>
 *     <li>The values of all attributes can be changed in the GSI strategy ({@link IGsiStrategy}).</li>
 *     <li>Some attributes are required to be set/changed by the strategy.</li>
 *     <li>The final method to be called to get the attribute value when building the result GSI header - {@link #getValue(String)}.
 *     It gets correctly encoded value (charset depends on CPN and CCT values). The value has the correct size matching {@link #getBytesAllocated()}.
 *     </li>
 * </ul>
 */
public enum GsiAttribute {

    CPN(3, "850"), // Multilingual
    DFC(8, "STL25.01"), // 25 fps
    DSC(1, "1"), // teletext
    CCT(2, "00"), // Latin
    LC(2, "09"), // English

    OPT(32, false), // must be set by a strategy!
    OET(32, false), // must be set by a strategy!

    TPT(32, true), // optional
    TET(32, true), // optional
    TN(32, true), // optional
    TCD(32, true), // optional
    SLR(16, true), // optional

    CD(6, false), // must be set by a strategy!
    RD(6, false), // must be set by a strategy!

    RN(2, "01"), // revision number

    TNB(5, false), // must be set by a strategy!
    TNS(5, false), // must be set by a strategy!

    TNG(3, 1), // number of subtitle groups
    MNC(2, 58), // max chars per row
    MNR(2, 11), // max number of rows
    TCS(1, "1"), // TC intended for use

    TCP(8, false), // must be set by a strategy!
    TCF(8, false), // must be set by a strategy!

    TND(1, 1), // number of disks
    DSN(1, 1), // disk sequence number
    CO(3, "GBR"), // country of origin

    PUB(32, false), // must be set by a strategy!
    EN(32, false), // must be set by a strategy!

    ECD(32, true), // optional
    Spare(75, true), // optional
    UDA(576, true); // optional


    private int bytesAllocated;
    private byte[] value = null;
    private int intValue;
    private String stringValue;

    GsiAttribute(int bytesAllocated, boolean fillEmpty) {
        this.bytesAllocated = bytesAllocated;
        if (fillEmpty) {
            fillEmptyValue();
        }
    }

    GsiAttribute(int bytesAllocated, String value) {
        this.bytesAllocated = bytesAllocated;
        setValue(value);
    }

    GsiAttribute(int bytesAllocated, int value) {
        this.bytesAllocated = bytesAllocated;
        setValue(value);
    }

    public int getBytesAllocated() {
        return bytesAllocated;
    }

    public byte[] getValue(String charset) throws UnsupportedEncodingException {
        if (this.value != null) {
            return Arrays.copyOf(value, value.length);
        }
        if (this.stringValue != null) {
            byte[] result = getEmptyValue();
            byte[] strBytes = stringValue.getBytes(charset);
            System.arraycopy(strBytes, 0, result, 0, strBytes.length);
            return result;
        }
        throw new RuntimeException("GSI attribute not set: " + name());
    }

    public int getIntValue() {
        return intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    void fillEmptyValue() {
        this.value = getEmptyValue();
    }

    private byte[] getEmptyValue() {
        byte[] v = new byte[getBytesAllocated()];
        Arrays.fill(v, (byte) 0x20);
        return v;
    }

    void setValue(String value) {
        this.stringValue = value;
    }

    void setValue(int value) {
        this.intValue = value;
        String strValue = String.valueOf(value);
        StringBuilder sb = new StringBuilder(strValue);
        while (sb.length() < getBytesAllocated()) {
            sb.insert(0, "0");
        }
        setValue(sb.toString());
    }


}
