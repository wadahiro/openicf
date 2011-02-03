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
import net.sf.saxon.query.*;


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

            /*try {
                log.info("XML file was not found. Creating a new xml file on the path: {0}", filePath);
                xmlFile.createNewFile();
            } catch (IOException ex) {
                log.error(ex, "Failed creating XML file: {0}", filePath);
                throw new ConnectorIOException("Failed creating XML file: " + filePath, ex);
            }*/
        }
        else {
            loadDocument(xmlFile);
        }

        log.info("Exit {0}", method);
    }

    /*private File loadXmlFile() {
        final String method = "getXmlFile";
        log.info("Entry {0}", method);

        File xmlFile = new File(filePath);

        // creates a new file if it doesnt already exist
        if (!xmlFile.exists()) {
            log.info("XML file was not found. Creating a new xml file on the path: {0}", filePath);

            createNewXmlFile();
        }

        log.info("Exit {0}", method);

        return xmlFile;
    }*/

    /*private void createNewXmlFile() {
        Element root = new Element("objects");
        this.document = new Document(root);
        writeDocumentToXmlFile();
    }*/

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

    public Uid update(Object obj) throws UnknownUidException {

        /*if (entryExists(objClass, name)) {
            throw new AlreadyExistsException(); // TODO: Add exception message
        }*/


        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(Uid uid) throws UnknownUidException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    public Collection<ConnectorObject> search(String query) {
//        List<ConnectorObject> hits = null;
//        if (!query.isEmpty()) {
//            try {
//                XPath xPath = XPath.newInstance(query);
//                hits = xPath.selectNodes(document);
//            } catch (JDOMException ex) {
//                Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return hits;
//    }

    public Collection<ConnectorObject> search(String query) {
        List<ConnectorObject> hits = null;

        

        return hits;
    }

    private boolean entryExists(ObjectClass objClass, Name name) {
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
