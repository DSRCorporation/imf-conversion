package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom map for generated {@link TmpContextType}.
 */
@XmlJavaTypeAdapter(TmpContextTypeMapAdapter.class)
public class TmpContextTypeMap<String, TmpParamType> {

    private final Map<String, TmpParamType> map = new HashMap<>();

    public Map<String, TmpParamType> getMap() {
        return map;
    }

}
