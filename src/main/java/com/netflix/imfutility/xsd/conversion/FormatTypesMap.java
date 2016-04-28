package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(FormatTypesMapAdapter.class)
public class FormatTypesMap<String, FormatType> extends HashMap<String, FormatType> {
}
