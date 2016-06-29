package com.netflix.imfutility.xsd.config;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom map for generated {@link ExternalToolsType}.
 */
@XmlJavaTypeAdapter(ExternalToolsTypeMapAdapter.class)
public class ExternalToolsTypeMap<String, ToolType> {

    private final Map<String, ToolType> map = new HashMap<>();

    public Map<String, ToolType> getMap() {
        return map;
    }
}
