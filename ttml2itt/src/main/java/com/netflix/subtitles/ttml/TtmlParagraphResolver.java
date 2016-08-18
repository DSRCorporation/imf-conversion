/*
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
package com.netflix.subtitles.ttml;

import com.netflix.imfutility.util.ConversionHelper;
import com.netflix.imfutility.xml.XmlParser;
import com.netflix.imfutility.xml.XmlParsingException;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3.ns.ttml.DivEltype;
import org.w3.ns.ttml.PEltype;
import org.w3.ns.ttml.TtEltype;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Stream;

import static com.netflix.subtitles.TtmlConverterConstants.TTML_PACKAGES;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_SCHEMA;

/**
 * Utility class to resolve paragraphs &lt;p&gt; in accordance with iTT format.
 */
public final class TtmlParagraphResolver {

    private TtmlParagraphResolver() {
    }

    public static void resolveTimeOverlaps(File ttml) throws FileNotFoundException, XmlParsingException {
        resolveTimeOverlaps(XmlParser.parse(ttml, new String[]{TTML_SCHEMA}, TTML_PACKAGES, TtEltype.class));
    }

    public static void resolveTimeOverlaps(TtEltype ttEl) {
        Optional<DivEltype> div = ttEl.getBody().getDiv().stream().findFirst();
        if (!div.isPresent()) {
            //TODO: throw exception
        }

        paragraphsStream(div.get());
    }

    private void resolveTimeOverlap(String startTimecode, Queue<String> messages) {

    }

    private static BigFraction getFrameRate(TtEltype ttEl) {
        return ConversionHelper.parseEditRate(ttEl.getFrameRateMultiplier()).multiply(ttEl.getFrameRate());
    }

    private static Stream<PEltype> paragraphsStream(DivEltype div) {
        return div.getBlockClass().stream()
                .filter(PEltype.class::isInstance)
                .map(PEltype.class::cast);
    }
}
