/*
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
package com.netflix.imfutility.itunes.metadata.film.wrap;


import com.apple.itunes.importer.film.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import java.util.List;
import java.util.Objects;

/**
 * Common helper wrapper for generated metadata JAXB classes.
 *
 * @param <T> elem type to wrap.
 */
public abstract class ElemWrapper<T> {
    protected final ObjectFactory objectFactory = new ObjectFactory();

    protected final JAXBContext context;
    protected final T inner;

    public ElemWrapper(JAXBContext context, T inner) {
        this.context = context;
        this.inner = inner;
    }

    public T getInner() {
        return inner;
    }

    protected <V> V getElemValueByName(List<?> list, String name, Class<V> clazz) {
        JAXBIntrospector introspector = context.createJAXBIntrospector();

        return list.stream()
                .filter(introspector::isElement)
                .filter(obj -> Objects.equals(introspector.getElementName(obj).getLocalPart(), name))
                .map(JAXBIntrospector::getValue)
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }

    protected <V> V getElemValueByClass(List<?> list, Class<V> clazz) {
        return list.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}
