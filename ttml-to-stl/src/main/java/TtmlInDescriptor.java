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
/**
 * Created by Alexandr on 5/27/2016.
 */
public class TtmlInDescriptor {

    private String file = null;
    private String startTC = null;
    private String endTC = null;
    private String offsetTC = null;


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getStartTC() {
        return startTC;
    }

    public void setStartTC(String startTC) {
        this.startTC = startTC;
    }

    public String getEndTC() {
        return endTC;
    }

    public void setEndTC(String endTC) {
        this.endTC = endTC;
    }

    public String getOffsetTC() {
        return offsetTC;
    }

    public void setOffsetTC(String offsetTC) {
        this.offsetTC = offsetTC;
    }

}