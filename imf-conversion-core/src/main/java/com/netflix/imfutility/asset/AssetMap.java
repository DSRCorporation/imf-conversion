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
package com.netflix.imfutility.asset;

import com.netflix.imfutility.cpl.uuid.UUID;

import java.util.HashMap;
import java.util.Map;

/**
 * Asset Map containing a path to an asset (resource, essence) for each asset UUID.
 * Asset map contains full absolute paths.
 */
public class AssetMap {

    private final Map<UUID, String> assetMap = new HashMap<>();

    /**
     * Adds asset.
     *
     * @param uuid      asset UUID
     * @param assetPath asset absolute path
     */
    public final void addAsset(UUID uuid, String assetPath) {
        assetMap.put(uuid, assetPath);
    }

    /**
     * Gets asset absolute path by UUID.
     *
     * @param uuid asset UUID
     * @return asset absolute path
     */
    public final String getAsset(UUID uuid) {
        return assetMap.get(uuid);
    }

}
