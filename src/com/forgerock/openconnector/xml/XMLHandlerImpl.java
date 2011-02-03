package com.forgerock.openconnector.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQItem;
import javax.xml.xquery.XQItemType;
import javax.xml.xquery.XQPreparedExpression;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class XMLHandlerImpl implements XMLHandler { 

    /** 
     * Setup logging for the {@link XMLHandlerImpl}.
     */
    private static final Log log = Log.getLog(XMLHandlerImpl.class);
    private String filePath;
    private Document document;
    private Schema schema;

    public XMLHandlerImpl(String filePath, Schema schema) {
        checkNull(filePath);
        checkEmpty(filePath);
        this.filePath = filePath;
        this.schema = schema;
        buildDocument();
    }

    public String getFilePath() {
        return this.filePath;
    }

    private void createDocument() {
        Element root = new Element("OpenICFContainer"); // TODO: add final static field

        document = new Document(root);

    }

    private void loadDocument(File xmlFile) {
        SAXBuilder builder = new SAXBuilder();
        try {
            document = builder.build(xmlFile);
        } catch (JDOMException ex) {
            Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
                Logger.getLogger(XMLHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    // creating the Document-object
    private void buildDocument() {
        final String method = "buildDocument";
        log.info("Entry {0}", method);

        File xmlFile = new File(filePath);

        if (!xmlFile.exists()) {

            createDocument();
        }
        else {
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

    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes) throws AlreadyExistsException {
        // TODO: How to check if this object is supported?
        ObjectClassInfo objInfo = schema.findObjectClassInfo(objClass.getObjectClassValue());
        Set<AttributeInfo> objAttributes = objInfo.getAttributeInfo();
        Map<String, Attribute> attributesMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
        Name name = AttributeUtil.getNameFromAttributes(attributes);

        if (entryExists(objClass, name)) {
            throw new AlreadyExistsException(); // TODO: Add exception message
        }

        // Create "root" element
        Element root = new Element(objClass.getObjectClassValue());

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

            root.addContent(child);
        }

        document.getRootElement().addContent(root);

        serialize();

        return new Uid(name.getNameValue());
    }

    public Uid update(ObjectClass objClass, Uid uid, Set<Attribute> replaceAttributes) throws UnknownUidException {

        // TODO: Check if field exists in the schema
        ObjectClassInfo objInfo = schema.findObjectClassInfo(objClass.getObjectClassValue());
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
        }
        else
            throw new IllegalArgumentException("..."); // TODO: Add exception message if object does not exist


        serialize();

        return uid;
    }


    public Element getEntry(ObjectClass objClass, Name name) {
        Element result = null;

        try {
            result = (Element)XPath.selectSingleNode(document, "/OpenICFContainer/" + objClass.getObjectClassValue() + "[__NAME__='" + name.getNameValue() + "']");
        } catch (JDOMException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex); // TODO: Change to framework logger
        }

        return result;
    }

    public void delete(final ObjectClass objClass, final Uid uid) throws UnknownUidException {
        
        Name name = new Name(uid.getUidValue());

        if (entryExists(objClass, name)) {
            document.getRootElement().removeContent(getEntry(objClass, name));
        }
        else
            throw new IllegalArgumentException("..."); // TODO: Add message for exception

        serialize();
    }

    public Collection<ConnectorObject> search(String q) {
        List<ConnectorObject> hits = null;
        try {
            String query = "for $x in doc(\"test-sample2.xml\")/OpenICFContainer/__ACCOUNT__ where $x/firstname='Jan Eirik' return $x";
            XQDataSource datasource = new SaxonXQDataSource();
            XQConnection connection = datasource.getConnection();
            XQPreparedExpression expression = connection.prepareExpression(query);
            XQResultSequence result = expression.executeQuery();
            while (result.next()) {
                XQItem item = result.getItem();
                Node node = item.getNode();
                NodeList nl = node.getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    Node tmp = nl.item(i);
                    System.out.println(tmp.getNodeName() + " containts " + tmp.getTextContent());
                }
            }
//            System.out.println("RESULTS: " + results);


            
        } catch (XQException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hits;
    }

    private boolean entryExists(ObjectClass objClass, Name name) {
        if (getEntry(objClass, name) != null)
            return true;

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
}