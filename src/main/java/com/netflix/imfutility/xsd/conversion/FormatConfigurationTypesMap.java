package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(FormatConfigurationTypesMapAdapter.class)
public class FormatConfigurationTypesMap<String, FormatConfigurationType> {

    private Map<String, FormatConfigurationType> map = new HashMap<>();

    public Map<String, FormatConfigurationType> getMap() {
        return map;
    }

}
