package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(FormatTypesMapAdapter.class)
public class FormatTypesMap<String, FormatType> {

    private Map<String, FormatType> map = new HashMap<>();

    public Map<String, FormatType> getMap() {
        return map;
    }

}
