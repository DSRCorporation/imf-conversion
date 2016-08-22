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
import com.netflix.subtitles.exception.ConvertException;
import com.netflix.subtitles.exception.ParseException;
import com.netflix.subtitles.util.SplitUtils;
import com.netflix.subtitles.util.SplitUtils.Slice;
import com.netflix.subtitles.util.SplitUtils.SliceBuilder;
import org.apache.commons.math3.fraction.BigFraction;
import org.w3.ns.ttml.DivEltype;
import org.w3.ns.ttml.PEltype;
import org.w3.ns.ttml.TtEltype;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.netflix.subtitles.TtmlConverterConstants.TTML_PACKAGES;

/**
 * Class to resolve TTML paragraphs &lt;p&gt; in accordance with iTT format.
 */
public class TtmlParagraphResolver {

    private final TtEltype tt;
    private final BigFraction frameRate;

    public TtmlParagraphResolver(TtEltype tt) {
        this.tt = tt;
        this.frameRate = ConversionHelper.parseEditRate(tt.getFrameRateMultiplier()).multiply(tt.getFrameRate());
    }

    /**
     * Resolve time overlaps.
     * Method uses {@link SplitUtils} for split initial &lt;p&gt;s on slices.
     */
    public void resolveTimeOverlaps() {
        DivEltype div = tt.getBody().getDiv().stream()
                .findFirst()
                .orElseThrow(() -> new ConvertException("At least one <div> must be defined."));

        List<PEltype> sliced = split(pStream(div.getBlockClass()))
                .map(this::merge)
                .collect(Collectors.toList());
        div.getBlockClass().clear();
        div.getBlockClass().addAll(sliced);
    }

    private Stream<Slice<PEltype>> split(Stream<PEltype> pStream) {
        SliceBuilder<PEltype> builder = new SliceBuilder<PEltype>()
                .setBeginGetter(p -> ConversionHelper.smpteTimecodeToMilliSeconds(p.getBegin(), frameRate))
                .setEndGetter(p -> ConversionHelper.smpteTimecodeToMilliSeconds(p.getEnd(), frameRate));

        return SplitUtils.split(pStream
                .map(builder::build)
                .collect(Collectors.toList())).stream();
    }

    private PEltype merge(Slice<PEltype> slice) {
        PEltype p = copy(slice.getContents().stream()
                .findFirst()
                .orElseThrow(() -> new ConvertException("At least one <p> must exist for content.")));
        p.setBegin(ConversionHelper.msToSmpteTimecode(slice.getBegin(), frameRate));
        p.setEnd(ConversionHelper.msToSmpteTimecode(slice.getEnd(), frameRate));
        p.getContent().clear();
        p.getContent().addAll(mergeContent(slice.getContents()));
        return p;
    }

    private List<Serializable> mergeContent(Collection<PEltype> pEls) {
        return pEls.stream()
                .flatMap(p -> p.getContent().stream())
                .collect(Collectors.toList());
    }

    private Stream<PEltype> pStream(List<Object> blockClass) {
        return blockClass.stream()
                .filter(PEltype.class::isInstance)
                .map(PEltype.class::cast);
    }

    private PEltype copy(PEltype p) {
        List<Object> styles = new ArrayList<>(p.getStyle());
        Object region = p.getRegion();

        p.getStyle().clear();
        p.setRegion(null);

        PEltype targetP = deepCopy(p, PEltype.class, TTML_PACKAGES);

        p.getStyle().addAll(styles);
        p.setRegion(region);

        targetP.getStyle().addAll(styles);
        targetP.setRegion(region);

        return targetP;
    }

    private static <T> T deepCopy(T object, Class<T> clazz, String packages) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(packages);

            //  create marshaller which disable validation step
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setEventHandler(event -> true);

            //  create unmarshaller which disable validation step
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setEventHandler(event -> true);

            JAXBElement<T> contentObject = new JAXBElement<>(new QName(clazz.getName()), clazz, object);
            return unmarshaller.unmarshal(new JAXBSource(marshaller, contentObject), clazz).getValue();
        } catch (JAXBException e) {
            throw new ParseException("Time overlaps in <p> cannot be resolved.", e);
        }
    }
}
