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
package com.netflix.subtitles;

/**
 * Contains project constants.
 */
public final class TtmlConverterConstants {

    public static final String TTML_PACKAGES =
            "org.w3.ns.ttml:org.w3.ns.ttml_parameter:org.w3.ns.ttml_datatype:org.w3.ns.ttml_metadata";

    public static final String TTML_SCHEMA = "xsd/CR-ttaf1-dfxp-20100223/ttaf1-dfxp.xsd";

    public static final String XSLT2_TRANSFORMER_IMPLEMENTATION = "net.sf.saxon.TransformerFactoryImpl";
    public static final String TTML_TO_ITT_TRANSFORMATION = "xslt/ttml2itt.xsl";
    public static final String REPLACE_STYLE_ID_TRANSFORMATION = "xslt/replace-style-ref.xsl";
    public static final String OLD_STYLE_ID_PARAMETER = "oldStyleId";
    public static final String NEW_STYLE_ID_PARAMETER = "newStyleId";

    public static final String STYLE_FIELD = "style";

    private TtmlConverterConstants() {
    }
}
