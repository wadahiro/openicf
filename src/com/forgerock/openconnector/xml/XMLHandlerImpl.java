package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.util.AttributeTypeUtil;
import com.forgerock.openconnector.util.GuardedStringAccessor;
import com.forgerock.openconnector.util.NamespaceLookup;
import com.forgerock.openconnector.util.XmlHandlerUtil;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xml.query.XQueryHandler;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQResultSequence; 
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.common.Assertions;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfoUtil;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLHandlerImpl implements XMLHandler {

    /**
     * Setup logging for the {@link XMLHandlerImpl}.
     */
    private static final Log log = Log.getLog(XMLHandlerImpl.class);

    private XMLConfiguration config;
    private Document document;
    private Schema connSchema;

    private XSSchema icfSchema;
    private XSSchema riSchema;

    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String ICF_NAMESPACE_PREFIX = "icf";
    public static final String RI_NAMESPACE_PREFIX = "ri";
    public static final String XSI_NAMESPACE_PREFIX = "xsi";

    public static final String ICF_CONTAINER_TAG = "OpenICFContainer";

    public XMLHandlerImpl(XMLConfiguration config, Schema connSchema, XSSchemaSet xsdSchemas) {
        Assertions.nullCheck(config.getXmlFilePath(), "filePath");
        Assertions.blankCheck(config.getXmlFilePath(), "filePath");
        this.config = config;

        this.connSchema = connSchema;
        this.riSchema = xsdSchemas.getSchema(1);
        this.icfSchema = xsdSchemas.getSchema(2);

        NamespaceLookup.INSTANCE.initialize(icfSchema);

        buildDocument();
    }

    private void createDocument() {
        final String method = "createDocument";
        log.info("Entry {0}", method);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        DOMImplementation implementation = builder.getDOMImplementation();
        document = implementation.createDocument(icfSchema.getTargetNamespace(), ICF_CONTAINER_TAG, null);

        Element root = document.getDocumentElement();
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + XSI_NAMESPACE_PREFIX , XSI_NAMESPACE);
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + RI_NAMESPACE_PREFIX, riSchema.getTargetNamespace());
        root.setPrefix(ICF_NAMESPACE_PREFIX);


        if(config.getXsdIcfFilePath() == null){
            root.setAttribute(XSI_NAMESPACE_PREFIX + ":schemaLocation", riSchema.getTargetNamespace() + " " + config.getXsdFilePath());
        }else{
            root.setAttribute(XSI_NAMESPACE_PREFIX + ":schemaLocation", riSchema.getTargetNamespace() + " " + config.getXsdFilePath()  + " " + icfSchema.getTargetNamespace()  + " " + config.getXsdIcfFilePath());
        }

        log.info("Exit {0}", method);
    }

    private void loadDocument(File xmlFile) {
        final String method = "loadDocument";
        log.info("Entry {0}", method);

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            document = docBuilder.parse (xmlFile);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            log.error("IOExceptino while building document: {0}", ex);
        }
        
        log.info("Exit {0}", method);
    }

    // creating the Document-object
    private void buildDocument() {
        final String method = "buildDocument";
        log.info("Entry {0}", method);

        File xmlFile = new File(config.getXmlFilePath());

        if (!xmlFile.exists()) {
            createDocument();
        } else {
            loadDocument(xmlFile);
        }
        
        log.info("Exit {0}", method);
    }

    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes) {
        final String method = "create";
        log.info("Entry {0}", method);

        XmlHandlerUtil.checkObjectType(objClass, riSchema);


        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
        Set<AttributeInfo> objAttributes = null; //objInfo.getAttributeInfo();
        Map<String, AttributeInfo> supportedAttributeInfoMap = null; //new HashMap<String, AttributeInfo>(AttributeInfoUtil.toMap(objAttributes));
        Map<String, Attribute> providedAttributesMap = null;
        String uidValue = null;

        if (attributes != null) {
            objAttributes = objInfo.getAttributeInfo();
            supportedAttributeInfoMap = new HashMap<String, AttributeInfo>(AttributeInfoUtil.toMap(objAttributes));
            providedAttributesMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
        }

        // check if __NAME__ is defined
        if (providedAttributesMap == null || !providedAttributesMap.containsKey(Name.NAME) || providedAttributesMap.get(Name.NAME).getValue().isEmpty()) {
            throw new IllegalArgumentException(Name.NAME + " must be defined.");
        }

        Name name = AttributeUtil.getNameFromAttributes(attributes);

        // check if entry already exists
        if (entryExists(objClass, new Uid(name.getNameValue()), ElementFieldType.BY_NAME)) {
            throw new AlreadyExistsException("Could not create entry. An entry with the " + Uid.NAME + " of " +
                    name.getNameValue() + " already exists.");
        }

        // create or get UID
        if (supportedAttributeInfoMap.containsKey(Uid.NAME)) {
            uidValue = UUID.randomUUID().toString();
        } else {
            uidValue = name.getNameValue();
        }

        // Create object type element
        Element objElement = document.createElementNS(riSchema.getTargetNamespace(), objClass.getObjectClassValue());
        objElement.setPrefix(RI_NAMESPACE_PREFIX);

        // Add child elements
        for (AttributeInfo attributeInfo : objAttributes) {
            
            String attributeName = attributeInfo.getName();

            List<String> values = AttributeTypeUtil.findAttributeValue(providedAttributesMap.get(attributeName), attributeInfo);

            // throw exception if required attribute is not provided
            if (attributeInfo.isRequired()) {
                if (providedAttributesMap.containsKey(attributeName) && !values.isEmpty()) {
                    for (String value : values) {
                        Assertions.blankCheck(value, attributeName);
                    }
                } else {
                    throw new IllegalArgumentException("Missing required field: " + attributeName);
                }
            }

            if(!attributeInfo.isMultiValued() && values.size() > 1){
                throw new IllegalArgumentException("Data field: " + attributeName + " is not multivalued  can not have more than one value");
            }

            if (!supportedAttributeInfoMap.containsKey(attributeName)) {
               continue;
                // throw new IllegalArgumentException("Data field: " + attributeName + " is not supported.");
            }

            if(!attributeInfo.isCreateable() && providedAttributesMap.containsKey(attributeName)){
                throw  new IllegalArgumentException(attributeName + " is not a creatable field.");
            }


            Element childElement = null;

            if (attributeName.equals(Uid.NAME)) {
                childElement = createDomElement(attributeName, uidValue);
                objElement.appendChild(childElement);
            }
            // add provided element 
            else if (providedAttributesMap.containsKey(attributeName)) {
                // check if the provided value is the same as the class defined in schema
                Class expectedClass = attributeInfo.getType();
                if (!valuesAreExpectedClass(expectedClass, providedAttributesMap.get(attributeName).getValue())) {
                    throw new IllegalArgumentException(attributeName + " contains values of illegal type");
                }
                // create elements
                for (String value : values) {
                    childElement = createDomElement(attributeName, value);
                    objElement.appendChild(childElement);
                }
            }
            // create empty element if not provided
            else {
                childElement = createDomElement(attributeName, "");
                objElement.appendChild(childElement);
            }
        }

        document.getDocumentElement().appendChild(objElement);

        serialize();

        log.info("Exit {0}", method);

        return new Uid(uidValue);
    }
    
    private Element createDomElement(String elementName, String value) {
        
        Element element = null;

        if (icfSchema.getElementDecls().containsKey(elementName)) {
            element = document.createElementNS(icfSchema.getTargetNamespace(), elementName);
            element.setPrefix(ICF_NAMESPACE_PREFIX);
            //child.setNamespace(getNameSpace(NamespaceType.ICF_NAMESPACE));
        }
        else {
            element = document.createElementNS(riSchema.getTargetNamespace(), elementName);
            element.setPrefix(RI_NAMESPACE_PREFIX);
            //child.setNamespace(getNameSpace(NamespaceType.RI_NAMESPACE));
        }
        
        element.setTextContent(value);
        return element;
    }

    private String prefixAttributeName(String name) {
        String result = "";
        if (icfSchema.getElementDecls().containsKey(name)) {
            result = ICF_NAMESPACE_PREFIX + ":" + name;
        } else {
            result =  RI_NAMESPACE_PREFIX + ":" + name;
        }
        return result;
    }

    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes) throws UnknownUidException {
        final String method = "update";
        log.info("Entry {0}", method);

        XmlHandlerUtil.checkObjectType(objClass, riSchema);

        // TODO: Check if field exists in the schema
        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
        Map<String, AttributeInfo> objAttributes = AttributeInfoUtil.toMap(objInfo.getAttributeInfo());

        if (entryExists(objClass, uid, ElementFieldType.AUTO)) {

            Element entry = getEntry(objClass, uid, ElementFieldType.AUTO);

            for (Attribute attribute : replaceAttributes) {

                if (!objAttributes.containsKey(attribute.getName())) {
                    throw new IllegalArgumentException("Data field: " + attribute.getName() + " is not supported.");
                }

                AttributeInfo attributeInfo = objAttributes.get(attribute.getName());
                String attributeName = attribute.getName();

                if(!attributeInfo.isUpdateable()){
                    throw new IllegalArgumentException(attributeName + " is not updatable.");
                }

                if (attributeInfo.isRequired()) {
                    List<String> values = AttributeTypeUtil.findAttributeValue(attribute, attributeInfo);
                    if (values.isEmpty()) {
                        throw new IllegalArgumentException("No values provided for required attribute: " + attributeName);
                    }
                    for (String value : values) {
                        Assertions.blankCheck(value, attributeName);
                        Assertions.nullCheck(value, attributeName);
                    }
                }
                
                // check if the provided value is the same as the class defined in schema
                Class expectedClass = attributeInfo.getType();

                if (attribute.getValue() != null) {
                    if (!valuesAreExpectedClass(expectedClass, attribute.getValue())) {
                        throw new IllegalArgumentException(attributeName + " contains values of illegal type");
                    }
                }

                // remove existing nodes from the entry
                removeChildsFromElement(entry, prefixAttributeName(attributeName));

                // add updated nodes to the entry
                List<String> values = AttributeTypeUtil.findAttributeValue(attribute, attributeInfo);

                if(!attributeInfo.isMultiValued() && values.size() > 1){
                    throw new IllegalArgumentException("Data field: " + attributeName + " is not multivalued  can not have more than one value");
                }

                // append empty element if no values is provided
                if (values.isEmpty()) {
                        Element updatedElement = createDomElement(attributeName, "");
                        entry.appendChild(updatedElement);
                } else {
                    for (String value : values) {
                        Element updatedElement = createDomElement(attributeName, value);
                        entry.appendChild(updatedElement);
                    }
                }
            }
        }
        else {
            throw new UnknownUidException("Could not update entry. No entry of type " + objClass.getObjectClassValue() + " with the id " + uid.getUidValue() + " found.");
        }

        serialize();

        log.info("Exit {0}", method);

        return uid;
    }

    private void removeChildsFromElement(Element element, String childName) {
        NodeList oldNodes = element.getElementsByTagName(childName);
        List<Element> elementsToRemove = new ArrayList<Element>();
        for (int i = 0; i < oldNodes.getLength(); i++) {
            elementsToRemove.add((Element) oldNodes.item(i));
        }
        for (Element e : elementsToRemove) {
            element.removeChild(e);
        }
    }

    // TODO: Refactor name of method
    private String getElementFieldTypeName(ObjectClass objClass, ElementFieldType getByFieldType) {
        String elementField = "";

        if (getByFieldType == ElementFieldType.BY_NAME)
            elementField = Name.NAME;
        else if (getByFieldType == ElementFieldType.BY_UID)
            elementField = Uid.NAME;
        else {
           Map<String, AttributeInfo> attrInfoMap = AttributeInfoUtil.toMap(connSchema.findObjectClassInfo(objClass.getObjectClassValue()).getAttributeInfo());
               
            if (attrInfoMap.containsKey(Uid.NAME))
                elementField = Uid.NAME;
            else
                elementField = Name.NAME;
        }

        return elementField;
    }

    private Element getEntry(ObjectClass objClass, Uid uid, ElementFieldType elementIdField) {
        final String method = "getEntry";
        log.info("Entry {0}", method);

        XMLFilterTranslator translator = new XMLFilterTranslator();
        AttributeBuilder builder = new AttributeBuilder();

        String idField = getElementFieldTypeName(objClass, elementIdField);

        builder.setName(idField);
        builder.addValue(uid.getUidValue());

        EqualsFilter equals = new EqualsFilter(builder.build());
        IQuery query = translator.createEqualsExpression(equals, false);
        QueryBuilder queryBuilder = new QueryBuilder(query, objClass);

        XQueryHandler xqHandler = null;

        try {
            xqHandler = new XQueryHandler(queryBuilder.toString(), document);
            XQResultSequence results = xqHandler.getResultSequence();

            if (results.next()) {
                Element element = (Element)results.getItem().getNode();
                return element;
            }
        } catch (XQException ex) {
            log.error(ex.getMessage());
        } 
        finally {
                xqHandler.close();
        }

        log.info("Exit {0}", method);

        return null;
    }

    private boolean entryExists(ObjectClass objClass, Uid uid, ElementFieldType elementIdField) {
        if (getEntry(objClass, uid, elementIdField) != null) {
            return true;
        }
        
        return false;
    }

    public void delete(final ObjectClass objClass, final Uid uid) throws UnknownUidException {
        final String method = "delete";
        log.info("Entry {0}", method);

        XmlHandlerUtil.checkObjectType(objClass, riSchema);

        if (entryExists(objClass, uid, ElementFieldType.AUTO)) {
            document.getDocumentElement().removeChild(getEntry(objClass, uid, ElementFieldType.AUTO));
        } else {
            throw new UnknownUidException("Deleting entry failed. Could not find an entry of type " + objClass.getObjectClassValue() + " with the uid " + uid.getUidValue());
        }
        
        serialize();

        log.info("Exit {0}", method);
    }

    @Override
    public Collection<ConnectorObject> search(String query, ObjectClass objClass) {
        final String method = "delete";
        log.info("Entry {0}", method);

        List<ConnectorObject> results = new ArrayList<ConnectorObject>();

        if (query != null && !query.isEmpty() && objClass != null) {

            ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
            Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();

            // map with the attribute-names and what class they are
            HashMap<String, String> attributeClassMap = new HashMap<String, String>();
            for (AttributeInfo info : objAttributes) {
                attributeClassMap.put(info.getName(), info.getType().getSimpleName());
            }

            // map with the AttributeInfo for each attribute
            HashMap<String, AttributeInfo> attributeInfoMap =
                    new HashMap<String, AttributeInfo>(AttributeInfoUtil.toMap(objInfo.getAttributeInfo()));

            XQueryHandler xqHandler = null;
            try {
                xqHandler = new XQueryHandler(query, document);
                XQResultSequence queryResult = xqHandler.getResultSequence();


                ConnectorObjectCreator conObjCreator =
                    new ConnectorObjectCreator(attributeClassMap, attributeInfoMap, objClass);

                while (queryResult.next()) {           
                    NodeList nodes = createNodeList(queryResult.getItem());
                    ConnectorObject conObj = conObjCreator.createConnectorObject(nodes);
                    results.add(conObj);
                }                
            } catch (XQException ex) {
                log.error("Error while searching: {0}", ex);
                throw new ConnectorException(ex);
            } finally {
                xqHandler.close();
            }
        }
        log.info("Exit {0}", method);
        return results;
    }

    private NodeList createNodeList(XQItem xqItem) throws XQException {
        Node node = xqItem.getNode();
        return node.getChildNodes();
    }

    public void serialize() {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(config.getXmlFilePath()));
            t.transform(source, result);
        } catch (TransformerException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean valuesAreExpectedClass(Class expectedClass, List<Object> values) {
        boolean ok = true;

        if (expectedClass.isPrimitive())
            expectedClass = convertToWrapper(expectedClass.getName());

        for (Object o : values) {            
            if (expectedClass != o.getClass()) {
                ok = false;
            }
        }
        return ok;
    }

    public static final Map<String, Class<?>> primitiveMap = new HashMap<String, Class<?>>();
    static {
        primitiveMap.put("boolean", Boolean.class);
        primitiveMap.put("byte", Byte.class);
        primitiveMap.put("short", Short.class);
        primitiveMap.put("char", Character.class);
        primitiveMap.put("int", Integer.class);
        primitiveMap.put("long", Long.class);
        primitiveMap.put("float", Float.class);
        primitiveMap.put("double", Double.class);
    }

    public static Class convertToWrapper(String name) {

        return primitiveMap.get(name);
    }

    public Uid authenticate(String username, GuardedString password) {

        Uid uid = null;

        Element entry = getEntry(ObjectClass.ACCOUNT, new Uid(username), ElementFieldType.BY_NAME);
        if(entry != null){
            NodeList passwordElements = entry.getElementsByTagName(ICF_NAMESPACE_PREFIX + ":__PASSWORD__");

            String xmlPassword = passwordElements.item(0).getTextContent();

            GuardedStringAccessor accessor = new GuardedStringAccessor();

            password.access(accessor);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(accessor.getArray());

            String userPassword = stringBuilder.toString();

            if(xmlPassword.equals(userPassword)){
                NodeList uidElements = entry.getElementsByTagName(ICF_NAMESPACE_PREFIX + ":__UID__");

                if(uidElements.getLength() >= 1){
                    uid = new Uid(uidElements.item(0).getTextContent());
                }else{
                    NodeList nameElements = entry.getElementsByTagName(ICF_NAMESPACE_PREFIX + ":__NAME__");
                    uid = new Uid(nameElements.item(0).getTextContent());
                }
            }
        }

        return uid;
    }
  
    // TODO: Refactor name of enum
    private enum ElementFieldType { 
        AUTO,
        BY_UID,
        BY_NAME
    }
}