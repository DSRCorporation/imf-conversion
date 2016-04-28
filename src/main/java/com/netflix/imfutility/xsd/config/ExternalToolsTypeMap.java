package com.netflix.imfutility.xsd.config;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.util.HashMap;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(ExternalToolsTypeMapAdapter.class)
public class ExternalToolsTypeMap<String, ToolType> extends HashMap<String, ToolType> {
}
