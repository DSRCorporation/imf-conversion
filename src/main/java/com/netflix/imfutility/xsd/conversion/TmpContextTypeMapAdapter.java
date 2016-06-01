package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Maps generated {@link TmpContextType} to {@link TmpContextTypeMap}.
 */
public class TmpContextTypeMapAdapter extends XmlAdapter<TmpContextType, TmpContextTypeMap<String, TmpParamType>> {

    @Override
    public TmpContextTypeMap<String, TmpParamType> unmarshal(TmpContextType tmpContextTypes) throws Exception {
        TmpContextTypeMap<String, TmpParamType> map = new TmpContextTypeMap<>();
        for (TmpParamType pt : tmpContextTypes.getParam()) {
            map.getMap().put(pt.getId(), pt);
        }
        return map;
    }

    @Override
    public TmpContextType marshal(TmpContextTypeMap<String, TmpParamType> map) throws Exception {
        TmpContextType tmpContextType = new TmpContextType();
        for (Map.Entry<String, TmpParamType> entry : map.getMap().entrySet()) {
            tmpContextType.getParam().add(entry.getValue());
        }
        return tmpContextType;
    }
}
