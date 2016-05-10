package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom map for generated {@link TmpContextType}.
 */
@XmlJavaTypeAdapter(TmpContextTypeMapAdapter.class)
public class TmpContextTypeMap<String, ParamType> {

    private final Map<String, ParamType> map = new HashMap<>();

    public Map<String, ParamType> getMap() {
        return map;
    }

}
