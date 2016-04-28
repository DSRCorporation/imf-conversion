package com.netflix.imfutility.xsd.conversion;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;

/**
 * Created by Alexander on 4/25/2016.
 */
@XmlJavaTypeAdapter(TmpContextTypeMapAdapter.class)
public class TmpContextTypeMap<String, ParamType> extends HashMap<String, ParamType> {
}
