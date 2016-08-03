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
 * An entity representing Time in TTML.
 */
public class Time {

    // in an integer we can store 24 days worth of milliseconds, no need for a long
    private int mseconds;

    /**
     * Constructor to create a time object.
     *
     * @param format supported formats: "hh:mm:ss,ms", "h:mm:ss.cs" and "h:m:s:f/fps"
     * @param value  string in the correct format
     */
    protected Time(String format, String value) {
        if (format.equalsIgnoreCase("hh:mm:ss,ms")) {
            // this type of format:  01:02:22,501 (used in .SRT)
            int h, m, s, ms;
            h = Integer.parseInt(value.substring(0, 2));
            m = Integer.parseInt(value.substring(3, 5));
            s = Integer.parseInt(value.substring(6, 8));
            ms = Integer.parseInt(value.substring(9, 12));

            setMseconds(ms + s * 1000 + m * 60000 + h * 3600000);

        } else if (format.equalsIgnoreCase("h:mm:ss.cs")) {
            // this type of format:  1:02:22.51 (used in .ASS/.SSA)
            int h, m, s, cs;
            String[] hms = value.split(":");
            h = Integer.parseInt(hms[0]);
            m = Integer.parseInt(hms[1]);
            s = Integer.parseInt(hms[2].substring(0, 2));
            cs = Integer.parseInt(hms[2].substring(3, 5));

            setMseconds(cs * 10 + s * 1000 + m * 60000 + h * 3600000);
        } else if (format.equalsIgnoreCase("h:m:s:f/fps")) {
            int h, m, s, f;
            float fps;
            String[] args = value.split("/");
            fps = Float.parseFloat(args[1]);
            args = args[0].split(":");
            h = Integer.parseInt(args[0]);
            m = Integer.parseInt(args[1]);
            s = Integer.parseInt(args[2]);
            f = Integer.parseInt(args[3]);

            setMseconds((int) (f * 1000 / fps) + s * 1000 + m * 60000 + h * 3600000);
        }
    }

    public Time(int mseconds) {
        this.setMseconds(mseconds);
    }

    public int getMseconds() {
        return mseconds;
    }

    public void setMseconds(int mseconds) {
        this.mseconds = mseconds;
    }

    /* METHODS */

    /**
     * Method to return a formatted value of the time stored.
     *
     * @param format supported formats: "hh:mm:ss,ms", "h:mm:ss.cs" and "hhmmssff/fps"
     * @return formatted time in a string
     */
    public String getTime(String format) {
        if (format.equalsIgnoreCase("hh:mm:ss,ms")) {
            // this type of format:  01:02:22,501 (used in .SRT)
            return getTime1(format);
        } else if (format.equalsIgnoreCase("h:mm:ss.cs")) {
            // this type of format:  1:02:22.51 (used in .ASS/.SSA)
            return getTime2(format);
        } else if (format.startsWith("hhmmssff/")) {
            //this format is used in EBU's STL
            return getTime3(format);
        } else if (format.startsWith("h:m:s:f/")) {
            //this format is used in EBU's STL
            return getTime4(format);
        } else if (format.startsWith("hh:mm:ss:ff/")) {
            //this format is used in SCC
            return getTime5(format);
        }

        throw new RuntimeException("Unsupported format " + format);
    }

    private String getTime1(String format) {
        StringBuilder time = new StringBuilder();
        String aux;

        // this type of format:  01:02:22,501 (used in .SRT)
        int h, m, s, ms;
        h = getMseconds() / 3600000;
        aux = String.valueOf(h);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        m = (getMseconds() / 60000) % 60;
        aux = String.valueOf(m);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        s = (getMseconds() / 1000) % 60;
        aux = String.valueOf(s);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(',');
        ms = getMseconds() % 1000;
        aux = String.valueOf(ms);
        if (aux.length() == 1) {
            time.append("00");
        } else if (aux.length() == 2) {
            time.append('0');
        }
        time.append(aux);

        return time.toString();
    }

    private String getTime2(String format) {
        StringBuilder time = new StringBuilder();
        String aux;

        int h, m, s, cs;
        h = getMseconds() / 3600000;
        aux = String.valueOf(h);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        m = (getMseconds() / 60000) % 60;
        aux = String.valueOf(m);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        s = (getMseconds() / 1000) % 60;
        aux = String.valueOf(s);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append('.');
        cs = (getMseconds() / 10) % 100;
        aux = String.valueOf(cs);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);

        return time.toString();
    }

    private String getTime3(String format) {
        StringBuilder time = new StringBuilder();
        String aux;

        //this format is used in EBU's STL
        int h, m, s, f;
        float fps;
        String[] args = format.split("/");
        fps = Float.parseFloat(args[1]);
        //now we concatenate time
        h = getMseconds() / 3600000;
        aux = String.valueOf(h);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        m = (getMseconds() / 60000) % 60;
        aux = String.valueOf(m);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        s = (getMseconds() / 1000) % 60;
        aux = String.valueOf(s);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        f = (getMseconds() % 1000) * (int) fps / 1000;
        aux = String.valueOf(f);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);

        return time.toString();
    }


    private String getTime4(String format) {
        StringBuilder time = new StringBuilder();
        String aux;

        //this format is used in EBU's STL
        int h, m, s, f;
        float fps;
        String[] args = format.split("/");
        fps = Float.parseFloat(args[1]);
        //now we concatenate time
        h = getMseconds() / 3600000;
        aux = String.valueOf(h);
        //if (aux.length()==1) time.append('0');
        time.append(aux);
        time.append(':');
        m = (getMseconds() / 60000) % 60;
        aux = String.valueOf(m);
        //if (aux.length()==1) time.append('0');
        time.append(aux);
        time.append(':');
        s = (getMseconds() / 1000) % 60;
        aux = String.valueOf(s);
        //if (aux.length()==1) time.append('0');
        time.append(aux);
        time.append(':');
        f = (getMseconds() % 1000) * (int) fps / 1000;
        aux = String.valueOf(f);
        //if (aux.length()==1) time.append('0');
        time.append(aux);

        return time.toString();
    }

    private String getTime5(String format) {
        StringBuilder time = new StringBuilder();
        String aux;

        int h, m, s, f;
        float fps;
        String[] args = format.split("/");
        fps = Float.parseFloat(args[1]);
        //now we concatenate time
        h = getMseconds() / 3600000;
        aux = String.valueOf(h);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        m = (getMseconds() / 60000) % 60;
        aux = String.valueOf(m);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        s = (getMseconds() / 1000) % 60;
        aux = String.valueOf(s);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);
        time.append(':');
        f = (getMseconds() % 1000) * (int) fps / 1000;
        aux = String.valueOf(f);
        if (aux.length() == 1) {
            time.append('0');
        }
        time.append(aux);

        return time.toString();
    }

    /**
     * ASS/SSA time format.
     * @return ASS/SSA time format.
     */
    @Override
    public String toString() {
        return getTime("h:mm:ss.cs");
    }

}
