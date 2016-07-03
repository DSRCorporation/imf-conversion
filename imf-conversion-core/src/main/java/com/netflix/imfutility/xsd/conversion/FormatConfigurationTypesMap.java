package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom map for generated {@link FormatConfigurationTypes}.
 */
@XmlJavaTypeAdapter(FormatConfigurationTypesMapAdapter.class)
public class FormatConfigurationTypesMap<String, FormatConfigurationType> {

    private final Map<String, FormatConfigurationType> map = new HashMap<>();

    public Map<String, FormatConfigurationType> getMap() {
        return map;
    }

}
