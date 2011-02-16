package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.util.AttrTypeUtil;
import com.forgerock.openconnector.util.GuardedByteArrayAccessor;
import com.forgerock.openconnector.util.NamespaceLookup;
import com.forgerock.openconnector.util.GuardedStringAccessor;
import com.forgerock.openconnector.util.XmlHandlerUtil;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xml.query.XQueryHandler;
import com.forgerock.openconnector.xsdparser.NamespaceType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import java.io.File;
import java.io.FileWriter;
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

import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQResultSequence; 
import org.identityconnectors.common.logging.Log;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfoUtil;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.jdom.Namespace;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHandlerImpl implements XMLHandler {

    /**
     * Setup logging for the {@link XMLHandlerImpl}.
     */
    private static final Log log = Log.getLog(XMLHandlerImpl.class);

    private String filePath;
    private Document document;
    private Schema connSchema; // TODO: Move to config class

    private XSSchema icfSchema;
    private XSSchema riSchema;

    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String ICF_NAMESPACE_PREFIX = "icf";
    public static final String RI_NAMESPACE_PREFIX = "ri";
    public static final String XSI_NAMESPACE_PREFIX = "xsi";

    public static final String ICF_CONTAINER_TAG = "OpenICFContainer";

    // TODO: Use Assertions class for null and blank check
    public XMLHandlerImpl(String filePath, Schema connSchema, XSSchemaSet xsdSchemas) {
        Assertions.nullCheck(filePath, "filePath");
        Assertions.blankCheck(filePath, "filePath");
        this.filePath = filePath;

        this.connSchema = connSchema;
        this.riSchema = xsdSchemas.getSchema(1);
        this.icfSchema = xsdSchemas.getSchema(2);

        NamespaceLookup.INSTANCE.initialize(icfSchema);

        buildDocument();
    }

    public String getFilePath() {
        return this.filePath;
    }

    // TODO: Add schemalocation
    private void createDocument() {
        Element root = new Element(ICF_CONTAINER_TAG);

        root.addNamespaceDeclaration(getNameSpace(NamespaceType.XSI_NAMESPACE));
        root.addNamespaceDeclaration(getNameSpace(NamespaceType.ICF_NAMESPACE));
        root.addNamespaceDeclaration(getNameSpace(NamespaceType.RI_NAMESPACE));
        
        root.setNamespace(getNameSpace(NamespaceType.ICF_NAMESPACE));

        document = new Document(root);
    }
    
    private Namespace getNameSpace(NamespaceType namespaceType) {
        Namespace namespace = null;

        switch (namespaceType) {
            case XSI_NAMESPACE :
                namespace = Namespace.getNamespace(XSI_NAMESPACE_PREFIX, XSI_NAMESPACE);
                break;
            case ICF_NAMESPACE :
                namespace = Namespace.getNamespace(ICF_NAMESPACE_PREFIX, icfSchema.getTargetNamespace());
                break;
            case RI_NAMESPACE :
                namespace = Namespace.getNamespace(RI_NAMESPACE_PREFIX, riSchema.getTargetNamespace());
                break;
        }

        return namespace;
    }

    private void loadDocument(File xmlFile) {
        final String method = "loadDocument";
        log.info("Entry {0}", method);

        SAXBuilder builder = new SAXBuilder();

        try {
            document = builder.build(xmlFile);
        } catch (JDOMException ex) {
            log.info(ex.getMessage());
        } catch (IOException ex) {
            log.info(ex.getMessage());
        }

        log.info("Exit {0}", method);
    }

    // creating the Document-object
    private void buildDocument() {
        final String method = "buildDocument";
        log.info("Entry {0}", method);

        File xmlFile = new File(filePath);

        if (!xmlFile.exists()) {
            createDocument();
        } else {
            loadDocument(xmlFile);
        }
        
        log.info("Exit {0}", method);
    }

    // TODO: Logging
    // TODO: Exceptions
    // TODO: Uid
    // TODO: Check if types is valid?
    // TODO: Check if type and fields is creatable
    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes) {
        final String method = "create";
        log.info("Entry {0}", method);

        XmlHandlerUtil.checkObjectType(objClass, riSchema);

        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
        Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();
        Map<String, Attribute> attributesMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
        Map<String, AttributeInfo> attributeInfoMap = new HashMap<String, AttributeInfo>(AttributeInfoUtil.toMap(objAttributes));
        String uidValue = null;

        if (!attributesMap.containsKey(Name.NAME) || attributesMap.get(Name.NAME).getValue().isEmpty()) {
            throw new IllegalArgumentException(Name.NAME + " must be defined.");
        }

        Name name = AttributeUtil.getNameFromAttributes(attributes);

        if (entryExists(objClass, name)) {
            throw new AlreadyExistsException("Could not create entry. An entry with the " + Uid.NAME + " of " + name.getNameValue() + " already exists.");
        }

        if (attributeInfoMap.containsKey(Uid.NAME))
            uidValue = UUID.randomUUID().toString();
        else
            uidValue = name.getNameValue();

        // Create object type element
        Element root = new Element(objClass.getObjectClassValue());
        root.setNamespace(getNameSpace(NamespaceType.RI_NAMESPACE));

        // Add child elements
        for (AttributeInfo attrInfo : objAttributes) {

            if (attrInfo.isRequired()) {
                if (!attributesMap.containsKey(attrInfo.getName()) || attributesMap.get(attrInfo.getName()).getValue().isEmpty()) {
                    throw new IllegalArgumentException("Missing required field: " + attrInfo.getName());
                }
            }

            if (!attrInfo.isCreateable() && attributesMap.containsKey(attrInfo.getName())) {
                throw new IllegalArgumentException(attrInfo.getName() + " is not creatable.");
            }

            Element child = new Element(attrInfo.getName());
            String elementText = "";

            // Add attribute value to field
            if (attrInfo.getName().equals(Uid.NAME)) {
                elementText = uidValue;
            }
            else if (attributesMap.containsKey(attrInfo.getName())) {
                elementText = AttrTypeUtil.findAttributeValue(attributesMap.get(attrInfo.getName()), attrInfo);
            }

            child.setText(elementText);

            // Set namespace
            if (icfSchema.getElementDecls().containsKey(attrInfo.getName()))
                child.setNamespace(getNameSpace(NamespaceType.ICF_NAMESPACE));
            else
                child.setNamespace(getNameSpace(NamespaceType.RI_NAMESPACE));

            root.addContent(child);

            if (attributesMap.containsKey(attrInfo.getName()))
                attributesMap.remove(attrInfo.getName());
        }

        if (attributesMap.size() > 0) {
            throw new IllegalArgumentException("Entry contains attributes that is not supported: " + attributesMap.toString());
        }

        document.getRootElement().addContent(root);

        serialize();

        log.info("Exit {0}", method);

        return new Uid(uidValue);
    }

    public boolean checkAttributeExists() {
        return false;
    }

    // TODO: Uid
    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes) throws UnknownUidException {
        final String method = "update";
        log.info("Entry {0}", method);

        XmlHandlerUtil.checkObjectType(objClass, riSchema);

        // TODO: Check if field exists in the schema
        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
        Map<String, AttributeInfo> objAttributes = AttributeInfoUtil.toMap(objInfo.getAttributeInfo());

        Name name = new Name(uid.getUidValue());

        if (entryExists(objClass, name)) {

            Element entry = getEntry(objClass, name);

            for (Attribute attribute : replaceAttributes) {

                if (!objAttributes.containsKey(attribute.getName())) {
                    throw new IllegalArgumentException("Data field: " + attribute.getName() + " is not supported.");
                }
                
                // TODO: GuardedString, GuardedByteArray
                if (objAttributes.get(attribute.getName()).isRequired() && AttributeUtil.getStringValue(attribute).isEmpty()) {
                    throw new IllegalArgumentException(attribute.getName() + " is a required field and cannot be empty.");
                }

                // TODO: GuardedString, GuardedByteArray
                entry.getChild(attribute.getName()).setText(AttributeUtil.getStringValue(attribute));
            }
        }
        else {
            throw new IllegalArgumentException("Could not update entry. No entry of type " + objClass.getObjectClassValue() + " with the id " + name.getNameValue() + " found.");
        }

        serialize();

        log.info("Exit {0}", method);

        return uid;
    }

    public Element getEntry(ObjectClass objClass, Name name) {

        XMLFilterTranslator translator = new XMLFilterTranslator();
        AttributeBuilder builder = new AttributeBuilder();
        builder.setName(Name.NAME);
        builder.addValue(name.getNameValue());
        EqualsFilter equals = new EqualsFilter(builder.build());
        IQuery query = translator.createEqualsExpression(equals, false);
        QueryBuilder queryBuilder = new QueryBuilder(query, objClass);

        XQueryHandler xqHandler = null;
        try {
            xqHandler = new XQueryHandler(queryBuilder.toString(), document);
            XQResultSequence results = xqHandler.getResultSequence();

            if (results.next()) {
                org.w3c.dom.Element element = (org.w3c.dom.Element)results.getItem().getNode();

                DOMBuilder oMBuilder = new DOMBuilder();
                return oMBuilder.build(element);
            }
        } catch (XQException ex) {
            log.error(ex.getMessage());
        } 
        finally {
                xqHandler.close();
        }

        return null;
    }

    public void delete(final ObjectClass objClass, final Uid uid) throws UnknownUidException {

        Name name = new Name(uid.getUidValue());

        if (entryExists(objClass, name)) {
            document.getRootElement().removeContent(getEntry(objClass, name));
        } else {
            throw new UnknownUidException("Deleting entry failed. Could not find an entry of type " + objClass.getObjectClassValue() + " with the uid " + name.getNameValue());
        }
        
        serialize();
    }


    public Collection<ConnectorObject> search(String query, ObjectClass objClass) {

        List<ConnectorObject> results = null;

        if (query != null && !query.isEmpty() && objClass != null) {

            ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
            Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();

            // create a map with the attribute-names and what class they are
            HashMap<String, String> attrClasses = new HashMap<String, String>();
            for (AttributeInfo info : objAttributes) {
                attrClasses.put(info.getName(), info.getType().getSimpleName());
            }

            HashMap<String, AttributeInfo> attrInfos =
                    new HashMap<String, AttributeInfo>(AttributeInfoUtil.toMap(objInfo.getAttributeInfo()));

            XQueryHandler xqHandler = null;
            try {
                xqHandler = new XQueryHandler(query, document);
                XQResultSequence queryResult = xqHandler.getResultSequence();

                results = new ArrayList<ConnectorObject>();

                while (queryResult.next()) {
                    
                    NodeList nodes = createNodeList(queryResult.getItem());
                    ConnectorObjectBuilder conObjBuilder = new ConnectorObjectBuilder();
                    conObjBuilder.setObjectClass(objClass);
                    addAllAttributesToBuilder(nodes, conObjBuilder, attrClasses, attrInfos);

                    results.add(conObjBuilder.build());
                }                
            } catch (XQException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                xqHandler.close();
            }
        }
        return results;
    }

    private boolean entryExists(ObjectClass objClass, Name name) {
        if (getEntry(objClass, name) != null) {
            return true;
        }
        return false;
    }
    

    public void serialize() {
        FileWriter writer = null;
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            writer = new FileWriter(new File(filePath));
            outputter.output(document, writer);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex); // TODO: Change to framework logger
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex); // TODO: Change to framework logger
            }
        }
    }

    private NodeList createNodeList(XQItem xqItem) throws XQException {
        Node node = xqItem.getNode();
        return node.getChildNodes();
    }

    // Add all the attributes to the connectorbuilder-object
    private void addAllAttributesToBuilder(NodeList nodeList, ConnectorObjectBuilder coBuilder,
            Map<String, String> classes, Map<String, AttributeInfo> infos) {
        
        boolean hasUid = false;
        HashMap<String, ArrayList<String>> multivalues = new HashMap<String, ArrayList<String>>();
        String nameTmp = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node attributeNode = nodeList.item(i);
            if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {

                Node textNode = attributeNode.getFirstChild();
                
                if (isTextNode(textNode)) {
                    String attrName = attributeNode.getLocalName();
                    String attrValue = textNode.getNodeValue();

                    if (attrName.equals("__UID__")) {
                        coBuilder.setUid(attrValue);
                        hasUid = true;
                    }
                    if (!hasUid && attrName.equals("__NAME__")) {
                        nameTmp = attrValue;
                    }

                    Attribute attribute = null;

                    AttributeInfo info = infos.get(attrName);
                    if (info.isReadable()) {
                        if (!info.isMultiValued()) {
                            // TODO: SJEKK HVORFOR DEN IKKE FINNER MULTIVALUED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            System.out.println("FOUND NONMULTIVALUED: " + attrName);
                            attribute = createAttribute(attrName, attrValue, classes);
                        } else {
                            System.out.println("FOUND MULTIVALUED: " + attrName);
                            if (multivalues.containsKey(attrName)) {
                                multivalues.get(attrName).add(attrValue);
                            } else {
                                ArrayList<String> values = new ArrayList<String>();
                                values.add(attrValue);
                                multivalues.put(attrName, values);
                            }
                        }
                    }

                    if (attribute != null) {
                        coBuilder.addAttribute(attribute);
                    }
                }
            }
        }
        // set __NAME__ attribute as UID if no UID was wound
        if (!hasUid) {
            coBuilder.setUid(nameTmp);
        }

        for (String s : multivalues.keySet()) {
            System.out.println("LOOPING THROUG MULTIVALUED: " + s);
            Attribute result = createMultivaluedAttribute(s, multivalues.get(s), classes);
            if (result != null)
                coBuilder.addAttribute(result);
        }
    }

    // returns an attributed created for the attribute-node
    private Attribute createAttribute(String attrName, String attrValue,
            Map<String, String> classes) {
        
        AttributeBuilder attrBuilder = new AttributeBuilder();
        attrBuilder.setName(attrName);

        // check if attrInfo has the attributes object-type
        if (classes.containsKey(attrName)) {
            String javaclass = classes.get(attrName);
            Object value = AttrTypeUtil.createInstantiatedObject(attrValue, javaclass);
            attrBuilder.addValue(value);
            Attribute result = attrBuilder.build();
            return result;
        }
        return null;
    }

    private Attribute createMultivaluedAttribute(String attrName, ArrayList<String> attrValues, Map<String, String> classes) {
        AttributeBuilder attrBuilder = new AttributeBuilder();
        attrBuilder.setName(attrName);
        if (classes.containsKey(attrName)) {
            String javaclass = classes.get(attrName);
            for (String eachValue : attrValues) {
                Object valueObj = AttrTypeUtil.createInstantiatedObject(eachValue, javaclass);
                attrBuilder.addValue(valueObj);
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
