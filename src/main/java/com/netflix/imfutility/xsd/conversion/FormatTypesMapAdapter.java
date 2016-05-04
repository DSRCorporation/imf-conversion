package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Maps generated {@link FormatTypes} to {@link FormatTypesMap}.
 */
public class FormatTypesMapAdapter extends XmlAdapter<FormatTypes, FormatTypesMap<String, FormatType>> {

    @Override
    public FormatTypesMap<String, FormatType> unmarshal(FormatTypes formatTypes) throws Exception {
        FormatTypesMap<String, FormatType> map = new FormatTypesMap<>();
        for (FormatType ft : formatTypes.getFormat()) {
            map.getMap().put(ft.getName(), ft);
        }
        return map;
    }

    @Override
    public FormatTypes marshal(FormatTypesMap<String, FormatType> map) throws Exception {
        FormatTypes formatTypes = new FormatTypes();
        for (Map.Entry<String, FormatType> entry : map.getMap().entrySet()) {
            formatTypes.getFormat().add(entry.getValue());
        }
        return formatTypes;
    }
}
