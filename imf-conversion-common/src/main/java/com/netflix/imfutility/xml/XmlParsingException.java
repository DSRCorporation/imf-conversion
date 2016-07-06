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
package com.netflix.imfutility.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alexandr on 5/10/2016.
 * <p>
 * An Exception class to wrap xml parsing errors.
 */
public class XmlParsingException extends Exception {

    private List<String> errors;

    public XmlParsingException(Exception e, List<String> errors) {
        super(e);
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }

        if (this.errors.isEmpty()) {
            this.errors.add(e.getLocalizedMessage());
        }
    }

    public XmlParsingException(List<String> errors) {
        super();
        this.errors = errors;
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
    }

    /**
     * Returns all parsing errors occurred during loading and validating of xml file.
     *
     * @return a collection with all found errors.
     */
    public List<String> getErrors() {
        return this.errors;
    }

    @Override
    public String getMessage() {
        return errors.stream().collect(Collectors.joining("\n", "\n[", "]"));
    }

}
