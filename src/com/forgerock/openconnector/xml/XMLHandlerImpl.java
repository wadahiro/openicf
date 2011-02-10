package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.xsdparser.NamespaceType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource; 
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
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
import org.jdom.xpath.XPath;
import net.sf.saxon.xqj.SaxonXQDataSource; 
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfoUtil;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;
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
        checkNull(filePath);
        checkEmpty(filePath);
        this.filePath = filePath;

        this.connSchema = connSchema;
        this.riSchema = xsdSchemas.getSchema(1);
        this.icfSchema = xsdSchemas.getSchema(2);


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
        SAXBuilder builder = new SAXBuilder();

        try {
            document = builder.build(xmlFile);
        } catch (JDOMException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex); // TODO: Change to framework logger
        } catch (IOException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex); // TODO: Change to framework logger
        }
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

    private void checkNull(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("Filepath can't be null");
        }
    }

    private void checkEmpty(String filePath) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("Filepath can't be empty");
        }
    }

    // TODO: Uid
    // TODO: Check if all fields exists
    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes) throws AlreadyExistsException {
        
        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
        Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();
        Map<String, Attribute> attributesMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
        Name name = AttributeUtil.getNameFromAttributes(attributes);

        if (!riSchema.getElementDecls().containsKey(objClass.getObjectClassValue())) {
            throw new IllegalArgumentException();
        }

        if (entryExists(objClass, name)) {
            log.info("Already exists");
            throw new AlreadyExistsException(); // TODO: Add exception message
        }
        else
            log.info("Not existing");

        // Create object type element
        Element root = new Element(objClass.getObjectClassValue());
        root.setNamespace(getNameSpace(NamespaceType.RI_NAMESPACE));
        

        // Add child elements
        for (AttributeInfo attrInfo : objAttributes) {

            if (attrInfo.isRequired()) {
                if (!attributesMap.containsKey(attrInfo.getName()) || attributesMap.get(attrInfo.getName()).getValue().isEmpty()) {
                    throw new IllegalArgumentException("Missing required field: " + attrInfo.getName()); // TODO: Add exception message
                }
            }

            Element child = new Element(attrInfo.getName());

            // Add attribute value to field
            if (attributesMap.containsKey(attrInfo.getName())) {
                child.setText(AttributeUtil.getStringValue(attributesMap.get(attrInfo.getName())));
            }

            if (icfSchema.getElementDecls().containsKey(attrInfo.getName()))
                child.setNamespace(getNameSpace(NamespaceType.ICF_NAMESPACE));
            else
                child.setNamespace(getNameSpace(NamespaceType.RI_NAMESPACE));
            
            root.addContent(child);
        }

        document.getRootElement().addContent(root);

        serialize();

        return new Uid(name.getNameValue());
    }

    // TODO: Uid
    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes) throws UnknownUidException {

        // TODO: Check if field exists in the schema
        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(objClass.getObjectClassValue());
        Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();


        Name name = new Name(uid.getUidValue());

        if (entryExists(objClass, name)) {

            Element entry = getEntry(objClass, name);

            for (Attribute attribute : replaceAttributes) {

                Element entryChild = entry.getChild(attribute.getName());

                if (entryChild == null) {
                    throw new IllegalArgumentException("Data field: " + attribute.getName() + " is not supported.");
                }

                entry.getChild(attribute.getName()).setText(AttributeUtil.getStringValue(attribute));
            }

            System.out.println(entry.getChild("firstname").getText());
        } else {
            throw new IllegalArgumentException("..."); // TODO: Add exception message if object does not exist
        }

        serialize();

        return uid;
    }

    // TODO: Change to use search method
    public Element getEntry(ObjectClass objClass, Name name) {
        Element result = null;
        
        try {
            result = (Element) XPath.selectSingleNode(document, "OpenICFContainer/" + objClass.getObjectClassValue() + "[__NAME__='" + name.getNameValue() + "']");
        } catch (JDOMException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex); // TODO: Change to framework logger
        }

        return result;
    }

    public void delete(final ObjectClass objClass, final Uid uid) throws UnknownUidException {

        Name name = new Name(uid.getUidValue());

        if (entryExists(objClass, name)) {
            document.getRootElement().removeContent(getEntry(objClass, name));
        } else {
            throw new IllegalArgumentException("..."); // TODO: Add message for exception
        }
        
        serialize();
    }

    //TODO:
    /*
     * se litt mer på hvordan man bygger objectinfo-objektet
     * refaktorer koden
     * sett uid riktig
     * close connections
     *
     * */
    public Collection<ConnectorObject> search(String query, ObjectClass objClass) { // TODO: remove exception

        ObjectClassInfo objInfo = connSchema.findObjectClassInfo(ObjectClass.ACCOUNT_NAME);
        Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();

        HashMap<String, String> attrInfo = new HashMap<String, String>();
        for (AttributeInfo info : objAttributes) {
            attrInfo.put(info.getName(), info.getType().getSimpleName());
        }
        
        List<ConnectorObject> hits = null;

        if (query != null && !query.isEmpty()) {
            try {
                XQDataSource datasource = new SaxonXQDataSource();
                XQConnection connection = datasource.getConnection();
                XQExpression xqexpression = connection.createExpression();
                DOMOutputter dOMOutputter = new DOMOutputter();
                org.w3c.dom.Document w3cDoc = dOMOutputter.output(document);
                xqexpression.bindNode(XQConstants.CONTEXT_ITEM, w3cDoc, null);
                XQResultSequence result = xqexpression.executeQuery(query);

                hits = new ArrayList<ConnectorObject>();
                
                while (result.next()) {
                    ConnectorObject connectorObject = createConnectorObject(result.getItem(), objClass, attrInfo);
                    hits.add(connectorObject);
                }
                
            } catch (JDOMException ex) {
                Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XQException ex) {
                Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return hits;
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

    private ConnectorObject createConnectorObject(XQItem xqItem, ObjectClass objClass, Map<String, String> attrInfo) throws XQException {
        Node node = xqItem.getNode();
        NodeList nodeList = node.getChildNodes();
        ConnectorObjectBuilder conObjBuilder = new ConnectorObjectBuilder();
        conObjBuilder.setObjectClass(objClass);
        Set<Attribute> attrs = createAttributeList(nodeList, attrInfo);
        conObjBuilder.addAttributes(attrs);
        conObjBuilder.setUid("???"); // TODO: What to add for UID ?
        return conObjBuilder.build();
    }

    private Set<Attribute> createAttributeList(NodeList nodeList, Map<String, String> attrInfo) {
        Set<Attribute> attrs = new HashSet<Attribute>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node attributeElement = nodeList.item(i);
            if (elementHasTextContent(attributeElement)) {
                Node textNode = attributeElement.getFirstChild();
                Attribute attribute = createAttribute(attributeElement, textNode, attrInfo);
                System.out.println("Attribute: " + attribute);
                attrs.add(attribute);
            }
        }
        return attrs;
    }

    private Attribute createAttribute(Node attributeElement, Node textNode, Map<String, String> attrInfo) {
        AttributeBuilder builder = new AttributeBuilder();
        
        String name = attributeElement.getNodeName();
        String value = textNode.getNodeValue();

        builder.setName(name);

        if (attrInfo.containsKey(name)) {

            String javaclass = attrInfo.get(name);

            if (javaclass.equals("String")) {
                String s = new String(value);
                builder.addValue(s);
            }
            else if (javaclass.equals("int")) {
                int i = new Integer(value);
                builder.addValue(i);
            }
            else if (javaclass.equals("Integer")) {
                Integer i = new Integer(value);
                builder.addValue(i);
            } 
            else if (javaclass.equals("Long")) {
                Long l = new Long(value);
                builder.addValue(l);
            }
            else if (javaclass.equals("long")) {
                long l = new Long(value);
                builder.addValue(l);
            }
            else if (javaclass.equals("Boolean")) {
                Boolean b = new Boolean(value);
                builder.addValue(b);
            }
            else if (javaclass.equals("boolean")) {
                boolean b = new Boolean(value);
                builder.addValue(b);
            }
            else if (javaclass.equals("Double")) {
                Double d = new Double(value);
                builder.addValue(d);
            }
            else if (javaclass.equals("double")) {
                double d = new Double(value);
                builder.addValue(d);
            }
            else if (javaclass.equals("Float")) {
                Float f = new Float(value);
                builder.addValue(f);
            }
            else if (javaclass.equals("float")) {
                float f = new Float(value);
                builder.addValue(f);
            }
            else if (javaclass.equals("Character")) {
                Character c = value.charAt(0);
                builder.addValue(c);
            }
            else if (javaclass.equals("char")) {
                char c = value.charAt(0);
                builder.addValue(c);
            }
            else if (javaclass.equals("BigInteger")) {
                BigInteger bi = new BigInteger(value);
                builder.addValue(bi);
            }
            else if (javaclass.equals("BigDecimal")) {
                BigDecimal bd = new BigDecimal(value);
                builder.addValue(bd);
            }
            else if (javaclass.equals("GuardedString")) {
                GuardedString gs = new GuardedString(value.toCharArray());
                builder.addValue(gs);
            }
            else if (javaclass.equals("GuardedByteArray")) { // ???
                GuardedByteArray gb = new GuardedByteArray(value.getBytes());
                builder.addValue(gb);
            }
            else if (javaclass.equals("byte[]")) {
                byte[] b = value.getBytes();
                builder.addValue(b);
            }
        }
        
        return builder.build();
    }

    private boolean elementHasTextContent(Node node) {
        Node child = node.getFirstChild();
        if (child != null) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                return true;
            }
        }
        return false;
    }
    
}
