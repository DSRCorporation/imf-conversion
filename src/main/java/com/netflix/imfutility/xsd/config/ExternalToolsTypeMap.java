package com.netflix.imfutility.xsd.config;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(ExternalToolsTypeMapAdapter.class)
public class ExternalToolsTypeMap<String, ToolType> {

    private Map<String, ToolType> map = new HashMap<>();

    public Map<String, ToolType> getMap() {
        return map;
    }
}
