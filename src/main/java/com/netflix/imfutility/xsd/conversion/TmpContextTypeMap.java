package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(TmpContextTypeMapAdapter.class)
public class TmpContextTypeMap<String, ParamType> {

    private Map<String, ParamType> map = new HashMap<>();

    public Map<String, ParamType> getMap() {
        return map;
    }

}
