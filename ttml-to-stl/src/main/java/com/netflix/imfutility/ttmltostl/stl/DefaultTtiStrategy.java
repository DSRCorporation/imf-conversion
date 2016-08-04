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

import com.netflix.imfutility.ttmltostl.ttml.Caption;
import com.netflix.imfutility.ttmltostl.ttml.Style;
import com.netflix.imfutility.ttmltostl.ttml.Time;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.CCT;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.CPN;
import static com.netflix.imfutility.ttmltostl.stl.GsiAttribute.DSC;

/**
 * Default implementation of EBU STL TTI block building.
 */
public class DefaultTtiStrategy extends AbstractStlStrategy implements ITtiStrategy {

    private static final String ISO6937 = "ISO-6937";

    private List<StlSubtitle> stlSubtitles;

    @Override
    public String getCharset() {
        if ("00".equals(CCT.getStringValue())) { // Latin
            return ISO6937; // provided by a 'fr.noop':'charset' project
        } else if ("01".equals(CPN.getStringValue())) { // Latin/Cyrillic
            return "ISO8859_5";
        } else if ("02".equals(CPN.getStringValue())) { // Latin/Arabic
            return "ISO8859_6";
        } else if ("03".equals(CPN.getStringValue())) { // Latin/Greek
            return "ISO8859_7";
        } else if ("04".equals(CPN.getStringValue())) { // Latin/Hebrew
            return "ISO8859_8";
        }
        throw new RuntimeException("Can not get TTI block charset. Unknown CPN value: " + CPN.getStringValue());
    }

    private boolean isTeletext() {
        return "1".equals(DSC.getStringValue());
    }

    private boolean isOpen() {
        return "0".equals(DSC.getStringValue());
    }

    @Override
    public byte[] build(TimedTextObject tto) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        List<Caption> captions = new ArrayList<>(tto.getCaptions().values());
        this.stlSubtitles = new ArrayList<>();

        int sn = 0;
        // 1. prepare caption objects
        for (int captionNum = 0; captionNum < captions.size(); captionNum++) {
            Caption caption = captions.get(captionNum);

            // 1.1 split to lines
            String[] lines = splitAndCleanText(caption);

            // 1.2 apply styles
            byte[] styles = applyStyles(caption);

            // 1.3 encode strings using required charset.
            byte[] text = encode(styles, lines);

            // 1.4 split to extension blocks
            byte[][] extensionBlocks = splitToExtensionBlocks(text);

            // 1.5 create a subtitle object
            StlSubtitle stlSubtitle = new StlSubtitle(caption, lines.length, extensionBlocks);
            this.stlSubtitles.add(stlSubtitle);
        }

        // 2. call it when this.stlSubtitles initially populated.
        defineTimesLinesAndCumulativeSubtitlesLines();

        // 3. build result STL
        for (StlSubtitle stlSubtitle : this.stlSubtitles) {
            // create a TTI block for each EB
            for (int ebn = 0; ebn < stlSubtitle.getExtensionBlocks().length; ebn++) {
                byte[] ttiBlock = doBuildTtiBlock(stlSubtitle, sn, ebn);
                result.write(ttiBlock);
            }
            sn++;
        }

        return result.toByteArray();
    }


    private void defineTimesLinesAndCumulativeSubtitlesLines() {
        // Define start/end cumulative subtitles taking into account the there should not be more line than MNR
        int mnr = GsiAttribute.MNR.getIntValue();
        Time previousEndCSTime = null;
        for (int i = 0; i <= this.stlSubtitles.size() - 1; i++) {
            StlSubtitle stlSubtitle = this.stlSubtitles.get(i);

            Time endCSTime = stlSubtitle.getEnd(); // end time of whole cumulative set.
            int totalCsLines = stlSubtitle.getLinesCount(); // total lines of cumulative set.
            int lastCSindex = i; // the last index of cumulative set.
            for (int j = i + 1; j < this.stlSubtitles.size(); j++) {
                StlSubtitle stlCummulativeSubtitle = this.stlSubtitles.get(j);
                // Check whether it is a cumulative subtitle or not.
                if (endCSTime.getMseconds() > stlCummulativeSubtitle.getStart().getMseconds()) {
                    // check that we fit acceptable number of lines.
                    if (totalCsLines + stlCummulativeSubtitle.getLinesCount() > mnr) {
                        //set previous CS as the last, if one before previous was cumulative
                        this.stlSubtitles.get(j - 1).setCumulativeEndFlag(
                                (j - 2) > 0 && this.stlSubtitles.get(j - 2).getCumulative());
                        //next iteration must start from Start always.
                        //i must point to the last element in the CS
                        lastCSindex = j - 1;
                        break;
                    }
                    stlCummulativeSubtitle.setCumulative(true);
                    totalCsLines += stlCummulativeSubtitle.getLinesCount();
                    //check what end time is bigger.
                    endCSTime = stlCummulativeSubtitle.getEnd().getMseconds() > endCSTime.getMseconds()
                            ? stlCummulativeSubtitle.getEnd() : endCSTime;

                    if (j == this.stlSubtitles.size() - 1) {
                        //i must point to the last element in the CS
                        lastCSindex = j;
                        break;
                    }

                } else {
                    //i must point to the last element in the CS
                    lastCSindex = j - 1;
                    break;
                }
            }

            if (lastCSindex != i) {
                //Set start flag
                stlSubtitle.setCumulativeStartFlag(true);
                stlSubtitle.setCumulative(true);
                //Set the end flag
                this.stlSubtitles.get(lastCSindex).setCumulativeEndFlag(true);
                this.stlSubtitles.get(lastCSindex).setCumulative(true);
            }

            //set start and endTime for all CSs
            //set linenumber for each subtitle.
            int startTTLine = StlSubtitle.BOTTOM_TELETEXT_LINE_TO_USE + StlSubtitle.TELETEXT_LINE_STEP
                    - (totalCsLines * StlSubtitle.TELETEXT_LINE_STEP);
            startTTLine = startTTLine <= 0
                    ? StlSubtitle.TOP_TELETEXT_LINE_TO_USE * StlSubtitle.TELETEXT_LINE_STEP
                    : startTTLine;
            for (int e = i; e <= lastCSindex; e++) {
                StlSubtitle csSubtitle = this.stlSubtitles.get(e);
                csSubtitle.setEnd(endCSTime);
                //set Start not less than previous CSs end time
                if (previousEndCSTime != null
                        && previousEndCSTime.getMseconds() > csSubtitle.getStart().getMseconds()) {
                    csSubtitle.setStart(previousEndCSTime);
                }
                csSubtitle.setLineNum(startTTLine);
                startTTLine += csSubtitle.getLinesCount() * StlSubtitle.TELETEXT_LINE_STEP;
            }

            i = lastCSindex;
            previousEndCSTime = endCSTime;
        }
    }

    private String[] splitAndCleanText(Caption caption) {
        return caption.getContent().split("\n");
    }

    private byte[] applyStyles(Caption caption) {
        ByteArrayOutputStream allText = new ByteArrayOutputStream();

        if (caption.getStyle() == null) {
            return allText.toByteArray();
        }
        Style style = caption.getStyle();

        // styles (for open captions only!)
        if (isOpen()) {
            if (style.isItalic()) {
                allText.write((byte) 0x80);
            } else {
                allText.write((byte) 0x81);
            }
            if (style.isUnderline()) {
                allText.write((byte) 0x82);
            } else {
                allText.write((byte) 0x83);
            }
        }

        //colors (for teletext only)
        if (isTeletext()) {
            if (style.getColor() != null) {
                String color = style.getColor().substring(0, 6);
                if (color.equalsIgnoreCase("000000")) {
                    allText.write((byte) 0x00);
                } else if (color.equalsIgnoreCase("0000ff")) {
                    allText.write((byte) 0x04);
                } else if (color.equalsIgnoreCase("00ffff")) {
                    allText.write((byte) 0x06);
                } else if (color.equalsIgnoreCase("00ff00")) {
                    allText.write((byte) 0x02);
                } else if (color.equalsIgnoreCase("ff0000")) {
                    allText.write((byte) 0x01);
                } else if (color.equalsIgnoreCase("ffff00")) {
                    allText.write((byte) 0x03);
                } else if (color.equalsIgnoreCase("ff00ff")) {
                    allText.write((byte) 0x05);
                } else {
                    allText.write((byte) 0x07);
                }
            }
        }

        return allText.toByteArray();
    }

    private byte[] encode(byte[] styles, String[] lines) throws IOException {
        ByteArrayOutputStream allText = new ByteArrayOutputStream();
        allText.write(styles);

        Iso6937Helper iso6937Helper = null;
        if (ISO6937.equals(getCharset())) {
            iso6937Helper = new Iso6937Helper();
        }

        for (int i = 0; i < lines.length; i++) {
            for (byte ch : lines[i].getBytes(getCharset())) {
                // fix ISO6937 (the fr.noop.charset implementation is not full and doesn't match $ sign correctly).
                if (iso6937Helper != null) {
                    Byte fixedCh = iso6937Helper.fixIso6937(ch);
                    if (fixedCh == null) {
                        continue;
                    } else {
                        ch = fixedCh;
                    }
                }
                allText.write(ch);
            }

            if (i < lines.length - 1) {
                allText.write((byte) 0x8A); // end of lines
            }
        }

        return allText.toByteArray();
    }

    private byte[][] splitToExtensionBlocks(byte[] text) {
        // how many eb we need
        int textPerBlock = TTI_TEXT_SIZE - 1; // 0x8F terminate
        int ebnBlockCount = text.length / textPerBlock;
        if (text.length % textPerBlock != 0) {
            ebnBlockCount++;
        }

        // split bytes to ebs
        byte[][] result = new byte[ebnBlockCount][TTI_TEXT_SIZE];
        int curr = 0;
        int ebn = 0;
        while (curr < text.length) {
            int size = Math.min(TTI_TEXT_SIZE - 1, text.length - curr);
            System.arraycopy(text, curr, result[ebn], 0, size);
            for (int i = size; i < TTI_TEXT_SIZE; i++) {
                result[ebn][i] = (byte) 0x8F; // terminate
            }
            ebn++;
            curr += size;
        }

        return result;
    }

    protected byte[] doBuildTtiBlock(StlSubtitle stlSubtitle, int sn, int ebn) {
        byte[] ttiBlock = new byte[TTI_BLOCK_SIZE];

        //SGN
        ttiBlock[0] = 0;

        //SN
        byte[] snValue = getSn(stlSubtitle, sn);
        ttiBlock[1] = snValue[0];
        ttiBlock[2] = snValue[1];

        //EBN
        ttiBlock[3] = getEbn(stlSubtitle, ebn);

        //CS
        ttiBlock[4] = getCs(stlSubtitle);

        //TCI
        byte[] tciValue = getTci(stlSubtitle);
        ttiBlock[5] = tciValue[0];
        ttiBlock[6] = tciValue[1];
        ttiBlock[7] = tciValue[2];
        ttiBlock[8] = tciValue[3];

        //TCO
        byte[] tcoValue = getTco(stlSubtitle);
        ttiBlock[9] = tcoValue[0];
        ttiBlock[10] = tcoValue[1];
        ttiBlock[11] = tcoValue[2];
        ttiBlock[12] = tcoValue[3];

        //VP
        ttiBlock[13] = getVP(stlSubtitle);

        //JC
        ttiBlock[14] = getJc(stlSubtitle);

        //CF
        ttiBlock[15] = 0;

        // TF
        byte[] text = stlSubtitle.getExtensionBlocks()[ebn];
        System.arraycopy(text, 0, ttiBlock, 16, text.length);

        return ttiBlock;
    }

    protected byte[] getSn(StlSubtitle stlSubtitle, int sn) {
        byte[] result = new byte[2];
        result[0] = (byte) (sn % 256);
        result[1] = (byte) (sn / 256);
        return result;
    }

    protected byte getEbn(StlSubtitle stlSubtitle, int ebn) {
        return (ebn == stlSubtitle.getExtensionBlocks().length - 1) ? (byte) 0xFF : (byte) ebn;
    }

    protected byte[] getTci(StlSubtitle stlSubtitle) {
        byte[] result = new byte[4];
        String[] timeCode = stlSubtitle.getStart().getTime("h:m:s:f/" + getFrameRate()).split(":");
        result[0] = Byte.parseByte(timeCode[0]);
        result[1] = Byte.parseByte(timeCode[1]);
        result[2] = Byte.parseByte(timeCode[2]);
        result[3] = Byte.parseByte(timeCode[3]);
        return result;
    }

    protected byte[] getTco(StlSubtitle stlSubtitle) {
        byte[] result = new byte[4];
        String[] timeCode = stlSubtitle.getEnd().getTime("h:m:s:f/" + getFrameRate()).split(":");
        result[0] = Byte.parseByte(timeCode[0]);
        result[1] = Byte.parseByte(timeCode[1]);
        result[2] = Byte.parseByte(timeCode[2]);
        result[3] = Byte.parseByte(timeCode[3]);
        return result;
    }

    protected byte getCs(StlSubtitle stlSubtitle) {

        if (stlSubtitle.getCumulativeStartFlag()) {
            return (byte) 0x01; // first
        } else if (stlSubtitle.getCumulativeEndFlag()) {
            return (byte) 0x03; // last
        } else if (stlSubtitle.getCumulative()) {
            return (byte) 0x02; // intermediate
        }

        return (byte) 0x00; // no cumulative
    }

    protected byte getVP(StlSubtitle stlSubtitle) {
        return (byte) stlSubtitle.getLineNum();
    }

    protected byte getJc(StlSubtitle stlSubtitle) {
        if (stlSubtitle.getCaptionStyle() == null) {
            return (byte) 0x02; // center
        }

        if (stlSubtitle.getCaptionStyle().getTextAlign().contains("left")) {
            return (byte) 0x01;
        }

        if (stlSubtitle.getCaptionStyle().getTextAlign().contains("right")) {
            return (byte) 0x03;
        }

        return (byte) 0x02; // center
    }

    protected byte getCf(StlSubtitle stlSubtitle) {
        return (byte) 0x00;
    }

}
