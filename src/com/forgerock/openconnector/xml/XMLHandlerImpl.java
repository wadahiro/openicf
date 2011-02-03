package com.forgerock.openconnector.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Uid;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


public class XMLHandlerImpl implements XMLHandler {

    /** 
     * Setup logging for the {@link XMLHandlerImpl}.
     */
    private static final Log log = Log.getLog(XMLHandlerImpl.class);
    private final String filePath;
    private Document document;

    public XMLHandlerImpl(String filePath) {
        checkNull(filePath);
        checkEmpty(filePath);
        this.filePath = filePath;
        buildDocument();
    }

    public String getFilePath() {
        return this.filePath;
    }

    // creating the Document-object
    private void buildDocument() {
        final String method = "buildDocument";
        log.info("Entry {0}", method);

        SAXBuilder builder = new SAXBuilder();
        try {
            File xmlFile = loadXmlFile();
            this.document = builder.build(xmlFile);
        } catch (JDOMException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        log.info("Exit {0}", method);
    }

    private File loadXmlFile() {
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
    }

    private void createNewXmlFile() {
        Element root = new Element("objects");
        this.document = new Document(root);
        writeDocumentToXmlFile();
    }

    private void writeDocumentToXmlFile() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(this.document, fos);
            // TODO: Should throw exception?
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void create(Object obj) throws AlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void update(Object obj) throws UnknownUidException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(Uid uid) throws UnknownUidException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object search(String query) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void serialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
