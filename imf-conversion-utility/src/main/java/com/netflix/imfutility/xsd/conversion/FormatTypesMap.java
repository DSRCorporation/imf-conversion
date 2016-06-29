package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom map for generated {@link FormatTypes}.
 */
@XmlJavaTypeAdapter(FormatTypesMapAdapter.class)
public class FormatTypesMap<String, FormatType> {

    private final Map<String, FormatType> map = new HashMap<>();

    public Map<String, FormatType> getMap() {
        return map;
    }

}
