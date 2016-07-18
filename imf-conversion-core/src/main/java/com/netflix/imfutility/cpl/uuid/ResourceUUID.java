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
package com.netflix.imfutility.cpl.uuid;

/**
 * Resource UUID.
 */
public class ResourceUUID extends UUID {

    public static ResourceUUID create(String uuid, long repeat) {
        return new ResourceUUID(uuid, repeat);
    }

    private final long repeat;

    protected ResourceUUID(String uuid, long repeat) {
        super(uuid);
        this.repeat = repeat;
    }

    public long getRepeat() {
        return repeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ResourceUUID that = (ResourceUUID) o;

        return repeat == that.repeat;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (repeat ^ (repeat >>> 32));
        return result;
    }
}
