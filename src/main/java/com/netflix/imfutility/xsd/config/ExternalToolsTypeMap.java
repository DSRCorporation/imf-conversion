package com.netflix.imfutility.xsd.config;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(ExternalToolsTypeMapAdapter.class)
public class ExternalToolsTypeMap<String, ToolType> {

    private final Map<String, ToolType> map = new HashMap<>();

    public Map<String, ToolType> getMap() {
        return map;
    }
}
