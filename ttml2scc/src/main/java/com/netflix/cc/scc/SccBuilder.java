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
package com.netflix.cc.scc;

import com.netflix.imfutility.ttmltostl.ttml.Caption;
import com.netflix.imfutility.ttmltostl.ttml.Time;
import com.netflix.imfutility.ttmltostl.ttml.TimedTextObject;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class represents the .SCC subtitle format
 * <br><br>
 * Copyright (c) 2012 J. David Requejo <br>
 * j[dot]david[dot]requejo[at] Gmail
 * <br><br>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <br><br>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 * <br><br>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author J. David Requejo
 *
 */
public class SccBuilder {

    /**
     * Builds SCC closed caption from Timed Text Object, only 29.97 nonDrop frame rate format.
     *
     * @param tto timed text object instance
     * @return SCC closed captions as array of lines/strings
     */
    public String[] build(TimedTextObject tto) {

        //first we check if the TimedTextObject had been built, otherwise...
        if (!tto.isBuilt()) {
            return null;
        }

        //we will write the lines in an ArrayList
        int index = 0;
        //the minimum size of the file is double the number of captions since lines are double spaced.
        ArrayList<String> file = new ArrayList<>(20 + 2 * tto.getCaptions().size());

        //first we add the header
        file.add(index++, "Scenarist_SCC V1.0\n");

        //line is to store the information to add to the file
        String line;
        //to store information about the captions
        Caption oldC;
        Caption newC = new Caption();
        newC.setEnd(new Time("h:mm:ss.cs", "0:00:00.00"));

        //Next we iterate over the captions
        Iterator<Caption> itrC = tto.getCaptions().values().iterator();
        while (itrC.hasNext()) {
            line = "";
            oldC = newC;
            newC = itrC.next();
            //if old caption ends after new caption starts
            if (oldC.getEnd().getMseconds() > newC.getStart().getMseconds()) {
                //captions overlap
                newC.setContent(newC.getContent() + "<br />" + oldC.getContent());
                //we add the time to the new line, and clear old caption so both can now appear
                newC.getStart().setMseconds(newC.getStart().getMseconds() - (int) (1000 / 29.97));
                //we correct the frame delay (8080 8080)
                line += newC.getStart().getTime("hh:mm:ss:ff/29.97") + "\t942c 942c ";
                newC.getStart().setMseconds(newC.getStart().getMseconds() + (int) (1000 / 29.97));
                //we clear the buffer and start new pop-on caption
                line += "94ae 94ae 9420 9420 ";

            } else if (oldC.getEnd().getMseconds() < newC.getStart().getMseconds()) {
                //we clear the screen for new caption
                line += oldC.getEnd().getTime("hh:mm:ss:ff/29.97") + "\t942c 942c\n\n";
                //we add the time to the new line, we clear buffer and start new caption
                newC.getStart().setMseconds(newC.getStart().getMseconds() - (int) (1000 / 29.97));
                //we correct the frame delay (8080 8080)
                line += newC.getStart().getTime("hh:mm:ss:ff/29.97") + "\t94ae 94ae 9420 9420 ";
                newC.getStart().setMseconds(newC.getStart().getMseconds() + (int) (1000 / 29.97));
            } else {
                //we add the time to the new line, we clear screen and buffer and start new caption
                newC.getStart().setMseconds(newC.getStart().getMseconds() - (int) (1000 / 29.97));
                //we correct the frame delay (8080 8080)
                line += newC.getStart().getTime("hh:mm:ss:ff/29.97") + "\t942c 942c 94ae 94ae 9420 9420 ";
                newC.getStart().setMseconds(newC.getStart().getMseconds() + (int) (1000 / 29.97));
            }

            //we add the coded caption text along with any styles to the off-screen buffer
            line += codeText(newC);
            //lastly we display the caption
            line += "8080 8080 942f 942f\n";

            //we add it to the "file"
            file.add(index++, line);

        }

        //an empty line is added
        file.add(index++, "");

        return file.toArray(new String[0]);
    }

    /* PRIVATEMETHODS */
    /**
     * INCOMPLETE METHOD: does not tab to correct position or applies styles.
     */
    private String codeText(Caption newC) {
        String toReturn = "";

        String[] lines = newC.getContent().split("<br />");

        int i = 0;
        int tab;
        //max 32 chars
        if (lines[i].length() > 32) {
            lines[i] = lines[i].substring(0, 32);
        }
        // we calculate tabs to center the text
        tab = (32 - lines[i].length()) / 2;

        //we position the cursor with a preamble code
        //the row should be chosen according to how many lines left...
        toReturn += "1340 1340 ";
        //we tab over to the correct spot
        if (tab % 4 != 0) {
            //TODO: tab code should go here
        }

        //we add the caption style using midrow codes
        //we code the caption text
        toReturn += codeChar(lines[i].toCharArray());

        if (lines.length > 1) {
            //and next line
            i++;

            //max 32 chars
            if (lines[i].length() > 32) {
                lines[i] = lines[i].substring(0, 32);
            }
            // we calculate tabs to center the text
            tab = (32 - lines[i].length()) / 2;

            //we position the cursor with a preamble code
            //the row should be chosen according to how many lines left...
            toReturn += "13e0 13e0 ";
            //we tab over to the correct spot
            if (tab % 4 != 0) {
                //TODO: tab code should go here
            }

            //we add the caption style using midrow codes
            //we code the caption text
            toReturn += codeChar(lines[i].toCharArray());

            if (lines.length > 2) {
                //and next line
                i++;

                //max 32 chars
                if (lines[i].length() > 32) {
                    lines[i] = lines[i].substring(0, 32);
                }
                // we calculate tabs to center the text
                tab = (32 - lines[i].length()) / 2;

                //we position the cursor with a preamble code
                toReturn += "9440 9440 ";
                //we tab over to the correct spot
                if (tab % 4 != 0) {
                    //TODO: tab code should go here
                }
                //we add the caption style using midrow codes

                //we code the caption text
                toReturn += codeChar(lines[i].toCharArray());

                if (lines.length > 3) {
                    //and next line
                    i++;

                    //max 32 chars
                    if (lines[i].length() > 32) {
                        lines[i] = lines[i].substring(0, 32);
                    }
                    // we calculate tabs to center the text
                    tab = (32 - lines[i].length()) / 2;

                    //we position the cursor with a preamble code
                    toReturn += "94e0 94e0 ";
                    //we tab over to the correct spot
                    if (tab % 4 != 0) {
                        //TODO: tab code should go here
                    }
                    //we add the caption style using midrow codes

                    //we code the caption text
                    toReturn += codeChar(lines[i].toCharArray());

                }
            }
        }

        return toReturn;
    }

    /**
     * INCOMPLETE METHOD, does not consider special or extended chars.
     */
    private String codeChar(char[] chars) {
        String toReturn = "";

        int i;
        for (i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case ' ':
                    toReturn += "20";
                    break;
                case '!':
                    toReturn += "a1";
                    break;
                case '"':
                    toReturn += "a2";
                    break;
                case '#':
                    toReturn += "23";
                    break;
                case '$':
                    toReturn += "a4";
                    break;
                case '%':
                    toReturn += "25";
                    break;
                case '&':
                    toReturn += "26";
                    break;
                case '\'':
                    toReturn += "a7";
                    break;
                case '(':
                    toReturn += "a8";
                    break;
                case ')':
                    toReturn += "29";
                    break;
                case 'á':
                    toReturn += "2a";
                    break;
                case '+':
                    toReturn += "ab";
                    break;
                case ',':
                    toReturn += "2c";
                    break;
                case '-':
                    toReturn += "ad";
                    break;
                case '.':
                    toReturn += "ae";
                    break;
                case '/':
                    toReturn += "2f";
                    break;
                case '0':
                    toReturn += "b0";
                    break;
                case '1':
                    toReturn += "31";
                    break;
                case '2':
                    toReturn += "32";
                    break;
                case '3':
                    toReturn += "b3";
                    break;
                case '4':
                    toReturn += "34";
                    break;
                case '5':
                    toReturn += "b5";
                    break;
                case '6':
                    toReturn += "b6";
                    break;
                case '7':
                    toReturn += "37";
                    break;
                case '8':
                    toReturn += "38";
                    break;
                case '9':
                    toReturn += "b9";
                    break;
                case ':':
                    toReturn += "ba";
                    break;
                case ';':
                    toReturn += "3b";
                    break;
                case '<':
                    toReturn += "bc";
                    break;
                case '=':
                    toReturn += "3d";
                    break;
                case '>':
                    toReturn += "3e";
                    break;
                case '?':
                    toReturn += "bf";
                    break;
                case '@':
                    toReturn += "40";
                    break;
                case 'A':
                    toReturn += "c1";
                    break;
                case 'B':
                    toReturn += "c2";
                    break;
                case 'C':
                    toReturn += "43";
                    break;
                case 'D':
                    toReturn += "c4";
                    break;
                case 'E':
                    toReturn += "45";
                    break;
                case 'F':
                    toReturn += "46";
                    break;
                case 'G':
                    toReturn += "c7";
                    break;
                case 'H':
                    toReturn += "c8";
                    break;
                case 'I':
                    toReturn += "49";
                    break;
                case 'J':
                    toReturn += "4a";
                    break;
                case 'K':
                    toReturn += "cb";
                    break;
                case 'L':
                    toReturn += "4c";
                    break;
                case 'M':
                    toReturn += "cd";
                    break;
                case 'N':
                    toReturn += "ce";
                    break;
                case 'O':
                    toReturn += "4f";
                    break;
                case 'P':
                    toReturn += "d0";
                    break;
                case 'Q':
                    toReturn += "51";
                    break;
                case 'R':
                    toReturn += "52";
                    break;
                case 'S':
                    toReturn += "d3";
                    break;
                case 'T':
                    toReturn += "54";
                    break;
                case 'U':
                    toReturn += "d5";
                    break;
                case 'V':
                    toReturn += "d6";
                    break;
                case 'W':
                    toReturn += "57";
                    break;
                case 'X':
                    toReturn += "58";
                    break;
                case 'Y':
                    toReturn += "d9";
                    break;
                case 'Z':
                    toReturn += "da";
                    break;
                case '[':
                    toReturn += "5b";
                    break;
                case 'é':
                    toReturn += "dc";
                    break;
                case ']':
                    toReturn += "5d";
                    break;
                case 'í':
                    toReturn += "5e";
                    break;
                case 'ó':
                    toReturn += "df";
                    break;
                case 'ú':
                    toReturn += "e0";
                    break;
                case 'a':
                    toReturn += "61";
                    break;
                case 'b':
                    toReturn += "62";
                    break;
                case 'c':
                    toReturn += "e3";
                    break;
                case 'd':
                    toReturn += "64";
                    break;
                case 'e':
                    toReturn += "e5";
                    break;
                case 'f':
                    toReturn += "e6";
                    break;
                case 'g':
                    toReturn += "67";
                    break;
                case 'h':
                    toReturn += "68";
                    break;
                case 'i':
                    toReturn += "e9";
                    break;
                case 'j':
                    toReturn += "ea";
                    break;
                case 'k':
                    toReturn += "6b";
                    break;
                case 'l':
                    toReturn += "ec";
                    break;
                case 'm':
                    toReturn += "6d";
                    break;
                case 'n':
                    toReturn += "6e";
                    break;
                case 'o':
                    toReturn += "ef";
                    break;
                case 'p':
                    toReturn += "70";
                    break;
                case 'q':
                    toReturn += "f1";
                    break;
                case 'r':
                    toReturn += "f2";
                    break;
                case 's':
                    toReturn += "73";
                    break;
                case 't':
                    toReturn += "f4";
                    break;
                case 'u':
                    toReturn += "75";
                    break;
                case 'v':
                    toReturn += "76";
                    break;
                case 'w':
                    toReturn += "f7";
                    break;
                case 'x':
                    toReturn += "f8";
                    break;
                case 'y':
                    toReturn += "79";
                    break;
                case 'z':
                    toReturn += "7a";
                    break;
                case 'ç':
                    toReturn += "fb";
                    break;
                case '÷':
                    toReturn += "7c";
                    break;
                case 'Ñ':
                    toReturn += "fd";
                    break;
                case 'ñ':
                    toReturn += "fe";
                    break;
                case '|':
                    toReturn += "7f";
                    break;

                default:
                    //error
                    //it happens for strange chars, since it is not complete, they are replaced by spaces
                    toReturn += "7f";
                    break;
            }
            if (Integer.remainderUnsigned(i, 2) == 1) {
                toReturn += " ";
            }
        }
        if (Integer.remainderUnsigned(i, 2) == 1) {
            toReturn += "80 ";
        }

        return toReturn;
    }
}
