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
package com.netflix.imfutility.ttmltostl.ttml;

/**
 * An entity defining TTML styles.
 */
public class Style {

    private static int styleCounter;

    /* ATTRIBUTES */
    private String iD;
    private String font;
    private String fontSize;
    private String color;
    private String backgroundColor;
    private String textAlign = "";

    private boolean italic;
    private boolean bold;
    private boolean underline;

    /**
     * Constructor that receives a String to use a its identifier.
     *
     * @param styleName = identifier of this style
     */
    protected Style(String styleName) {
        this.setiD(styleName);
    }

    /**
     * Constructor that receives a String with the new styleName and a style to copy.
     *
     * @param styleName a style name
     * @param style a style
     */
    protected Style(String styleName, Style style) {
        this.setiD(styleName);
        this.setFont(style.getFont());
        this.setFontSize(style.getFontSize());
        this.setColor(style.getColor());
        this.setBackgroundColor(style.getBackgroundColor());
        this.setTextAlign(style.getTextAlign());
        this.setItalic(style.isItalic());
        this.setUnderline(style.isUnderline());
        this.setBold(style.isBold());
    }

    /* METHODS */

    /**
     * To get the string containing the hex value to put into color or background color.
     *
     * @param format supported: "name", "&HBBGGRR", "&HAABBGGRR", "decimalCodedBBGGRR", "decimalCodedAABBGGRR"
     * @param value RRGGBBAA string
     * @return a color string
     */
    protected static String getRGBValue(String format, String value) {
        String color = null;
        if (format.equalsIgnoreCase("name")) {
            //standard color format from W3C
            switch (value) {
                case "transparent":
                    color = "00000000";
                    break;
                case "black":
                    color = "000000ff";
                    break;
                case "silver":
                    color = "c0c0c0ff";
                    break;
                case "gray":
                    color = "808080ff";
                    break;
                case "white":
                    color = "ffffffff";
                    break;
                case "maroon":
                    color = "800000ff";
                    break;
                case "red":
                    color = "ff0000ff";
                    break;
                case "purple":
                    color = "800080ff";
                    break;
                case "fuchsia":
                    color = "ff00ffff";
                    break;
                case "magenta":
                    color = "ff00ffff ";
                    break;
                case "green":
                    color = "008000ff";
                    break;
                case "lime":
                    color = "00ff00ff";
                    break;
                case "olive":
                    color = "808000ff";
                    break;
                case "yellow":
                    color = "ffff00ff";
                    break;
                case "navy":
                    color = "000080ff";
                    break;
                case "blue":
                    color = "0000ffff";
                    break;
                case "teal":
                    color = "008080ff";
                    break;
                case "aqua":
                    color = "00ffffff";
                    break;
                case "cyan":
                    color = "00ffffff ";
                    break;
                default:
                    // nothing
            }
        } else if (format.equalsIgnoreCase("&HBBGGRR")) {
            //hex format from SSA
            StringBuilder sb = new StringBuilder();
            sb.append(value.substring(6));
            sb.append(value.substring(4, 5));
            sb.append(value.substring(2, 3));
            sb.append("ff");
            color = sb.toString();
        } else if (format.equalsIgnoreCase("&HAABBGGRR")) {
            //hex format from ASS
            StringBuilder sb = new StringBuilder();
            sb.append(value.substring(8));
            sb.append(value.substring(6, 7));
            sb.append(value.substring(4, 5));
            sb.append(value.substring(2, 3));
            color = sb.toString();
        } else if (format.equalsIgnoreCase("decimalCodedBBGGRR")) {
            //normal format from SSA
            color = Integer.toHexString(Integer.parseInt(value));
            //any missing 0s are filled in
            while (color.length() < 6) {
                color = "0" + color;
            }
            //order is reversed
            color = color.substring(4) + color.substring(2, 4) + color.substring(0, 2) + "ff";
        } else if (format.equalsIgnoreCase("decimalCodedAABBGGRR")) {
            //normal format from ASS
            color = Long.toHexString(Long.parseLong(value));
            //any missing 0s are filled in
            while (color.length() < 8) {
                color = "0" + color;
            }
            //order is reversed
            color = color.substring(6) + color.substring(4, 6) + color.substring(2, 4) + color.substring(0, 2);
        }
        return color;
    }

    protected static String defaultID() {
        return "default" + styleCounter++;
    }

    @Override
    public String toString() {
        return "Style{"
                + "id='" + getiD() + '\''
                + ", font='" + getFont() + '\''
                + ", fontSize='" + getFontSize() + '\''
                + ", color='" + getColor() + '\''
                + ", backgroundColor='" + getBackgroundColor() + '\''
                + ", textAlign='" + getTextAlign() + '\''
                + ", italic=" + isItalic()
                + ", bold=" + isBold()
                + ", underline=" + isUnderline()
                + '}';
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Colors are stored as 8 chars long RGBA.
     */
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }
}
