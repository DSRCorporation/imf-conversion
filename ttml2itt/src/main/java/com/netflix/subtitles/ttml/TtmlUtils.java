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
package com.netflix.subtitles.ttml;

import com.netflix.imfutility.resources.ResourceHelper;
import com.netflix.imfutility.util.ConversionHelper;
import static com.netflix.subtitles.TtmlConverterConstants.STYLE_FIELD;
import static com.netflix.subtitles.TtmlConverterConstants.TTML_PACKAGES;
import static com.netflix.subtitles.TtmlConverterConstants.XSLT2_TRANSFORMER_IMPLEMENTATION;
import com.netflix.subtitles.exception.ConvertException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.w3.ns.ttml.BodyEltype;
import org.w3.ns.ttml.DivEltype;
import org.w3.ns.ttml.ObjectFactory;
import org.w3.ns.ttml.PEltype;
import org.w3.ns.ttml.TtEltype;

/**
 * Incapsulate time reduce functionality for timed texts objects.
 */
public final class TtmlUtils {

    /**
     * Reduce timed objects according to start and end and normalize timeExpressions according to virtual track times.
     * <p></p>
     * dur attributes will be converted to end attributes and all time expression will be in SMPTE time code format. If
     * frameRate and frameRateMultiplier are not specified then default 30 and 1000 1001 values will be used.
     * <p></p>
     * region, span and set &lt;timeExpression&gt; will be ignored. Nested div elements are not supported too.
     *
     * @param tt root timed object that will be reduced by times
     * @param offsetMS virtual track time offset in millis
     * @param startMS ttml content start time in millis
     * @param endMS ttml content end time in millis
     */
    public static void reduceAccordingSegment(TtEltype tt, long offsetMS, long startMS, long endMS) {
        TtmlTimeConverter ttConverter = new TtmlTimeConverter(tt);
        long totalBegin = ttConverter.parseTimeExpression(tt.getBody().getBegin());

        // remove body timeExpressions
        tt.getBody().setBegin(null);
        tt.getBody().setEnd(null);
        tt.getBody().setDur(null);

        Iterator<DivEltype> divIt = tt.getBody().getDiv().iterator();
        while (divIt.hasNext()) {
            DivEltype div = divIt.next();

            totalBegin += ttConverter.parseTimeExpression(div.getBegin());

            // remove div timeExpressions
            div.setBegin(null);
            div.setEnd(null);
            div.setDur(null);

            // remove nested divs and filter p according interval [startMS; endMS]
            Iterator blockIt = div.getBlockClass().iterator(); // p or div
            while (blockIt.hasNext()) {
                Object blockClass = blockIt.next();
                if (!(blockClass instanceof PEltype)) {
                    blockIt.remove();
                    continue;
                }

                PEltype p = (PEltype) blockClass;
                long pBegin = totalBegin + ttConverter.parseTimeExpression(p.getBegin());
                long pEnd = totalBegin + getEnd(ttConverter, p.getBegin(), p.getEnd(), p.getDur());
                if (pEnd < startMS || pBegin > endMS) { // remove not matched
                    blockIt.remove();
                    continue;
                }

                if (pBegin < startMS) {
                    pBegin = startMS;
                }
                if (pEnd > endMS) {
                    pEnd = endMS;
                }

                // set p timeExpression according to a virtual track times
                p.setBegin(ConversionHelper.msToSmpteTimecode(offsetMS + pBegin - startMS, ttConverter.getUnitsInSec()));
                p.setEnd(ConversionHelper.msToSmpteTimecode(offsetMS + pEnd - startMS, ttConverter.getUnitsInSec()));
                p.setDur(null);
            }

            if (div.getBlockClass().isEmpty()) {
                divIt.remove();
            }
        }
    }

    /**
     * Moves all style refs from boby and div to corresponding p element for easiest merging.
     *
     * @param tt root timed object
     */
    public static void moveStyleRefToP(TtEltype tt) {
        Set<Object> styles = new HashSet<>(tt.getBody().getStyle());

        // set style to null, workaround for JAXB objects else style list will be just empty (invalid ttml)
        BodyEltype body = tt.getBody();
        body.getStyle().clear();
        setStyleListToNull(body);

        tt.getBody().getDiv().stream()
                .peek((div) -> {
                    styles.addAll(div.getStyle());
                    div.getStyle().clear();
                    setStyleListToNull(div);
                })
                .flatMap((div) -> div.getBlockClass().stream())
                .filter((o) -> o instanceof PEltype)
                .map((o) -> (PEltype) o)
                .forEachOrdered((p) -> {
                    styles.addAll(p.getStyle());
                    p.getStyle().clear();
                    p.getStyle().addAll(styles);
                });
    }

    /**
     * Creates specific for TTML document JAXB context object.
     *
     * @return specific for TTML document JAXB context object
     * @throws JAXBException
     */
    public static JAXBContext createTtmlJaxbContext() throws JAXBException {
        return JAXBContext.newInstance(TTML_PACKAGES);
    }

    /**
     * Creates specific for TTML document transformer.
     *
     * @param xslt xsl transformation file/resource
     * @return specific for TTML document transformer
     */
    public static Transformer createTtmlTransformer(String xslt) {
        TransformerFactory tf = TransformerFactory.newInstance(XSLT2_TRANSFORMER_IMPLEMENTATION, null);
        InputStream transformationStream = ResourceHelper.getResourceInputStream(xslt);
        if (transformationStream == null) {
            throw new ConvertException(String.format("The transformation file is absent: %s", xslt));
        }

        try {
            return tf.newTransformer(new StreamSource(transformationStream));
        } catch (TransformerConfigurationException e) {
            throw new ConvertException(e);
        }
    }

    /**
     * Does TTML document transformation to another TTML document.
     *
     * @param tt source TTML document root element
     * @param transformer transformer
     * @return TTML document after transformation
     */
    public static TtEltype transformTtmlDocument(TtEltype tt, Transformer transformer) {
        JAXBElement<TtEltype> ttJaxb = new ObjectFactory().createTt(tt);
        try {
            JAXBContext jaxbc = createTtmlJaxbContext();
            JAXBSource source = new JAXBSource(jaxbc, ttJaxb);
            JAXBResult result = new JAXBResult(jaxbc);

            // transform
            transformer.transform(source, result);

            return (TtEltype) ((JAXBElement<TtEltype>) result.getResult()).getValue();
        } catch (JAXBException | TransformerException e) {
            throw new ConvertException(e);
        }
    }

    /**
     * Sets NULL value of style references list for div and body element.
     *
     * @param obj div or body element
     */
    private static void setStyleListToNull(Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField(STYLE_FIELD);
            field.setAccessible(true);
            field.set(obj, null);
        } catch (Exception e) {
            // ignore exceptions
        }
    }

    /**
     * Gets correct value of ending point of a temporal interval.
     * <p><p/>
     * Note: if both end and dur attributes are specified in the element then ending point is equal to the lesser of the
     * value of the dur attribute and the difference between the value of the end attribute and the element's begin
     * time.
     *
     * @param begin begin point of a temporal interval
     * @param end ending point of a temporal interval
     * @param dur duration of a temporal interval
     * @return correct value of ending point of a temporal interval
     */
    private static long getEnd(TtmlTimeConverter ttConverter, String begin, String end, String dur) {
        long b = (begin == null || begin.isEmpty()) ? 0 : ttConverter.parseTimeExpression(begin);
        long e = (end == null || end.isEmpty()) ? Long.MAX_VALUE : ttConverter.parseTimeExpression(end);
        long d = (dur == null || dur.isEmpty()) ? Long.MAX_VALUE - b : ttConverter.parseTimeExpression(dur);

        return Math.min(b + d, e);
    }

    /**
     * Constructor.
     */
    private TtmlUtils() {
    }
}
