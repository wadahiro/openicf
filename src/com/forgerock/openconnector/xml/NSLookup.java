package com.forgerock.openconnector.xml;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import java.util.HashMap;
import java.util.Map;


public enum NSLookup {

    INSTANCE;

    private Map<String, String> nsFieldMap = new HashMap<String, String>();

    public void initialize(XSSchema schema) {
        Map<String,XSElementDecl> map = schema.getElementDecls();
        for (String s : map.keySet()) {
            nsFieldMap.put(s, XMLHandlerImpl.ICF_NAMESPACE_PREFIX);
        }
    }

    public String getNamespace(String attrName) {
        return nsFieldMap.get(attrName);
    }
}
