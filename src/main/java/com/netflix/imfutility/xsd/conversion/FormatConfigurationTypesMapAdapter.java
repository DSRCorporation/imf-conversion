package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

/**
 * Created by Alexander on 4/25/2016.
 */
public class FormatConfigurationTypesMapAdapter extends XmlAdapter<FormatConfigurationTypes, FormatConfigurationTypesMap<String, FormatConfigurationType>> {

    @Override
    public FormatConfigurationTypesMap<String, FormatConfigurationType> unmarshal(FormatConfigurationTypes formatConfigurations) throws Exception {
        FormatConfigurationTypesMap<String, FormatConfigurationType> map = new FormatConfigurationTypesMap();
        for (FormatConfigurationType fct : formatConfigurations.getFormatConfiguration()) {
            map.put(fct.getName(), fct);
        }
        return map;
    }

    @Override
    public FormatConfigurationTypes marshal(FormatConfigurationTypesMap<String, FormatConfigurationType> map) throws Exception {
        FormatConfigurationTypes formatTypes = new FormatConfigurationTypes();
        for (Map.Entry<String, FormatConfigurationType> entry : map.entrySet()) {
            formatTypes.getFormatConfiguration().add(entry.getValue());
        }
        return formatTypes;
    }
}
