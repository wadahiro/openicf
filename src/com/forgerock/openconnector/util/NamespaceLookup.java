package com.forgerock.openconnector.util;

import com.forgerock.openconnector.xml.*;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import java.util.HashMap;
import java.util.Map;


public enum NamespaceLookup {

    INSTANCE;

    private Map<String, String> namespaceMap = new HashMap<String, String>();

    public void initialize(XSSchema schema) {
        Map<String,XSElementDecl> map = schema.getElementDecls();
        for (String s : map.keySet()) {
            namespaceMap.put(s, XMLHandlerImpl.ICF_NAMESPACE_PREFIX);
        }
    }

    public String getNamespace(String attrName) {
        return namespaceMap.get(attrName);
    }
}
