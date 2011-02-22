/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.util.AttributeTypeUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class ConnectorObjectCreator {

    private Map<String, String> attributeClassMap;
    private Map<String, AttributeInfo> attributeInfoMap;
    private ObjectClass objectClass;
    
    private NodeList nodeList;
    private ConnectorObjectBuilder conObjBuilder;


    public ConnectorObjectCreator(HashMap<String, String> attrClasses,
            HashMap<String, AttributeInfo> attrInfos, ObjectClass objClass) {

        this.attributeClassMap = attrClasses;
        this.attributeInfoMap = attrInfos;
        this.objectClass = objClass;
    }

    protected ConnectorObject createConnectorObject(NodeList nodes) {
        nodeList = nodes;
        conObjBuilder = new ConnectorObjectBuilder();

        conObjBuilder.setObjectClass(objectClass);

        addAllAttributesToBuilder();
        

        return conObjBuilder.build();
    }

    // Add all the attributes to the connectorbuilder-object
    private void addAllAttributesToBuilder() {

        boolean hasUid = false;

        // map for storing multivalued attributes
        HashMap<String, ArrayList<String>> multivalues = new HashMap<String, ArrayList<String>>();

        String nameTmp = "";
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node attributeNode = nodeList.item(i);
            if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                Node textNode = attributeNode.getFirstChild();

                if (isTextNode(textNode)) {
                    String attrName = attributeNode.getLocalName();
                    String attrValue = textNode.getNodeValue();

                    if (attrName.equals(Uid.NAME)) {
                        conObjBuilder.setUid(attrValue);
                        hasUid = true;
                    }
                    if (!hasUid && attrName.equals(Name.NAME)) {
                        nameTmp = attrValue;
                    }

                    Attribute attribute = null;
                    AttributeInfo info = attributeInfoMap.get(attrName);

                    if (info.isReadable()) {
                        if (!info.isMultiValued()) {
                            attribute = createAttribute(attrName, attrValue);
                        }
                        else { // collect all multivalues in a map
                            if (multivalues.containsKey(attrName)) {
                                multivalues.get(attrName).add(attrValue);
                            }
                            else {
                                ArrayList<String> values = new ArrayList<String>();
                                values.add(attrValue);
                                multivalues.put(attrName, values);
                            }
                        }
                    }
                    if (attribute != null) {
                        conObjBuilder.addAttribute(attribute);
                    }
                }
            }
        }

        // set __NAME__ attribute as UID if no UID was wound
        if (!hasUid) {
            conObjBuilder.setUid(nameTmp);
        }

        //add multivalued attributes
        for (String s : multivalues.keySet()) {
            Attribute attribute = createMultivaluedAttribute(s, multivalues.get(s));
            if (attribute != null)
                conObjBuilder.addAttribute(attribute);
        }
    }

    private Attribute createAttribute(String attributeName, String attributeValue) {
        AttributeBuilder attrBuilder = new AttributeBuilder();
        attrBuilder.setName(attributeName);

        // check if attrInfo has the attributes object-type
        if (attributeClassMap.containsKey(attributeName)) {
            String javaclass = attributeClassMap.get(attributeName);
            Object value = AttributeTypeUtil.createInstantiatedObject(attributeValue, javaclass);
            attrBuilder.addValue(value);
            Attribute result = attrBuilder.build();
            return result;
        }
        return null;
    }

    private Attribute createMultivaluedAttribute(String attributeName, ArrayList<String> attributeValues) {
        AttributeBuilder attrBuilder = new AttributeBuilder();
        attrBuilder.setName(attributeName);

        if (attributeClassMap.containsKey(attributeName)) {
            String javaclass = attributeClassMap.get(attributeName);
            for (String attrValue : attributeValues) {
                Object value = AttributeTypeUtil.createInstantiatedObject(attrValue, javaclass);
                attrBuilder.addValue(value);
            }
            Attribute result = attrBuilder.build();
            return result;
        }
        return null;
    }

    // see if an attribute-node has text-content
    private boolean isTextNode(Node node) {
        return node != null && node.getNodeType() == Node.TEXT_NODE;
    }

}
