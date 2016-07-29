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

/**
 * Builds STL caption. Returns GSI and TTI blocks as an array of bytes.
 */
public class StlBuilder {

    public byte[][] build(TimedTextObject tto, IGsiStrategy gsiStrategy, ITtiStrategy ttiStrategy) throws IOException {
        // 1. first we check if the TimedTextObject had been built, otherwise...
        if (!tto.built) {
            return null;
        }

        // 2. fill custom GSI attributes
        gsiStrategy.fillAttributes(tto);

        // 3. build TTI
        byte[] tti = ttiStrategy.build(tto);

        // 4. fill TTI-based GSI attributes
        gsiStrategy.fillTtiAttributes(tti);

        // 5. build GSI
        byte[] gsi = gsiStrategy.build(tto);

        // 6. prepaer result
        byte[][] result = new byte[2][];
        result[0] = gsi;
        result[1] = tti;
        return result;
    }

}
