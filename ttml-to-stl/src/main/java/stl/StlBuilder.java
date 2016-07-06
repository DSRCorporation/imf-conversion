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

import java.io.IOException;

/**
 * Created by Alexander on 6/23/2016.
 */
public class StlBuilder {

    public byte[] build(TimedTextObject tto, IGsiStrategy gsiStrategy, ITtiStrategy ttiStrategy) throws IOException {
        //first we check if the TimedTextObject had been built, otherwise...
        if (!tto.built) {
            return null;
        }

        // build tti
        byte[] tti = ttiStrategy.build(tto);

        // build gsi
        gsiStrategy.fillAttributes(tto, tti);
        byte[] gsi = gsiStrategy.build(tto);


        // build result
        byte[] result = new byte[gsi.length + tti.length];
        System.arraycopy(gsi, 0, result, 0, gsi.length);
        System.arraycopy(tti, 0, result, gsi.length, tti.length);
        return result;
    }

}
