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

import ttml.Caption;
import ttml.Style;
import ttml.Time;
import ttml.TimedTextObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of EBU STL TTI block building.
 */
public class DefaultTtiStrategy implements ITtiStrategy {

    private List<Caption> captions;
    private List<StlSubtitle> stlSubtitles;

    @Override
    public byte[] build(TimedTextObject tto) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();

        this.captions = new ArrayList<>(tto.captions.values());
        this.stlSubtitles = new ArrayList<>();

        int sn = 0;
        for (int captionNum = 0; captionNum < captions.size(); captionNum++) {
            Caption caption = captions.get(captionNum);

            // 1. split to lines
            String[] lines = splitAndCleanText(caption);

            // 2. apply styles
            byte[] text = applyStyles(caption, lines);

            // 3. split to extension blocks
            byte[][] extensionBlocks = splitToExtensionBlocks(text);

            // 4. create a subtitle object
            StlSubtitle stlSubtitle = new StlSubtitle(captions, caption, captionNum, lines.length, extensionBlocks);
            this.stlSubtitles.add(stlSubtitle);
        }

        //call it when this.stlSubtitles initially populated.
        defineTimesLinesAndCumulativeSubtitlesLines();

        // build result STL
        for (StlSubtitle stlSubtitle : this.stlSubtitles) {
            // 5. create a TTI block for each EB
            for (int ebn = 0; ebn < stlSubtitle.getExtensionBlocks().length; ebn++) {
                byte[] ttiBlock = doBuildTtiBlock(stlSubtitle, sn, ebn);
                result.write(ttiBlock);
                sn++;
            }
        }

        return result.toByteArray();
    }


    private void defineTimesLinesAndCumulativeSubtitlesLines() {
        // Define start/end cumulative subtitles taking into account the there should not be more line than MNR
        int mnr = GsiAttribute.MNR.getIntValue();
        Time previousEndCSTime = null;
        for (int i = 0; i < this.stlSubtitles.size() - 1; i++) {
            StlSubtitle stlSubtitle = this.stlSubtitles.get(i);

            Time endCSTime = stlSubtitle.getEnd();// end time of whole cumulative set.
            int totalCsLines = stlSubtitle.getLinesCount(); // total lines of cumulative set.
            int lastCSindex = i; // the last index of cumulative set.
            for (int j = i + 1; j < this.stlSubtitles.size(); j++) {
                StlSubtitle stlCummulativeSubtitle = this.stlSubtitles.get(j);
                // Check whether it is a cumulative subtitle or not.
                if (endCSTime.getMseconds() > stlCummulativeSubtitle.getStart().getMseconds()) {
                    // check that we fit acceptable number of lines.
                    if (totalCsLines + stlCummulativeSubtitle.getLinesCount() > mnr) {
                        // TODO: add a test when single subtitle has more than mnr lines
                        // TODO: add a test when several subtitles has more than mnr lines in total

                        //set previous CS as the last
                        this.stlSubtitles.get(j - 1).setCumulativeEndFlag(true);
                        //next iteration must start from Start always.
                        //i must point to the last element in the CS
                        lastCSindex = j - 1;
                        break;
                    }
                    stlCummulativeSubtitle.setCumulative(true);
                    totalCsLines += stlCummulativeSubtitle.getLinesCount();
                    //check what end time is bigger.
                    endCSTime = stlCummulativeSubtitle.getEnd().getMseconds() > endCSTime.getMseconds() ? stlCummulativeSubtitle.getEnd() : endCSTime;

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
            int startTTLine = StlSubtitle.BOTTOM_TELETEXT_LINE_TO_USE + StlSubtitle.TELETEXT_LINE_STEP - (totalCsLines * StlSubtitle.TELETEXT_LINE_STEP) ;
            startTTLine = startTTLine < 0 ? 0 : startTTLine;
            for (int e = i; e <= lastCSindex; e++) {
                StlSubtitle csSubtitle = this.stlSubtitles.get(e);
                csSubtitle.setEnd(endCSTime);
                //set Start not less than previous CSs end time
                if (previousEndCSTime != null
                        && previousEndCSTime.getMseconds() > csSubtitle.getStart().getMseconds()) {
                    // TODO: add a test when we brake up CSs and got start time of next CS set less than end time of the CSs
                    csSubtitle.setStart(previousEndCSTime);
                }
                csSubtitle.setLineNum(startTTLine);
                startTTLine += csSubtitle.getLinesCount() * StlSubtitle.TELETEXT_LINE_STEP;
            }

            i = lastCSindex;
            previousEndCSTime = endCSTime;
        }
    }

    private String[] splitAndCleanText(Caption caption) throws IOException {
        ByteArrayOutputStream allText = new ByteArrayOutputStream();

        String[] lines = caption.content.split("\n");

        return lines;
    }

    private byte[] applyStyles(Caption caption, String[] lines) throws IOException {
        ByteArrayOutputStream allText = new ByteArrayOutputStream();

        if (caption.style != null) {
            Style style = caption.style;
            if (style.italic) {
                allText.write((byte) 0x80);
            } else {
                allText.write((byte) 0x81);
            }
            if (style.underline) {
                allText.write((byte) 0x82);
            } else {
                allText.write((byte) 0x83);
            }

            //colors
            String color = style.color.substring(0, 6);
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

        for (int i = 0; i < lines.length; i++) {
            for (char ch : lines[i].toCharArray()) {
                //check it is a supported char, else it is ignored
                if ((ch >= 0x20) && (ch <= 0x7f)) {
                    allText.write((byte) ch);
                }
            }
            if (i < lines.length - 1) {
                allText.write((byte) 0x8A); // end of lines
            }
        }

        return allText.toByteArray();
    }

    private byte[][] splitToExtensionBlocks(byte[] text) throws IOException {
        // how many eb we need
        int textPerBlock = TTI_TEXT_SIZE - 1; // 0x8F terminate
        int ebnBlockCount = 1 + (text.length / (textPerBlock + 1));

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
        //FIXME: f/25 is OK for BBC, but there may be case for:
        // *In the STL30.01 format, the range is 00..29 frames (00h..1Dh).
        String[] timeCode = stlSubtitle.getStart().getTime("h:m:s:f/25").split(":");
        result[0] = Byte.parseByte(timeCode[0]);
        result[1] = Byte.parseByte(timeCode[1]);
        result[2] = Byte.parseByte(timeCode[2]);
        result[3] = Byte.parseByte(timeCode[3]);
        return result;
    }

    protected byte[] getTco(StlSubtitle stlSubtitle) {
        byte[] result = new byte[4];
        //FIXME: f/25 is OK for BBC, but there may be case for:
        // *In the STL30.01 format, the range is 00..29 frames (00h..1Dh).
        String[] timeCode = stlSubtitle.getEnd().getTime("h:m:s:f/25").split(":");
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

        if (stlSubtitle.getCaptionStyle().textAlign.contains("left")) {
            return (byte) 0x01;
        }

        if (stlSubtitle.getCaptionStyle().textAlign.contains("right")) {
            return (byte) 0x03;
        }

        return (byte) 0x02; // center
    }

    protected byte getCf(StlSubtitle stlSubtitle) {
        return (byte) 0x00;
    }

}
