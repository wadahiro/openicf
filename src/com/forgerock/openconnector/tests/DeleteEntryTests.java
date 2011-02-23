package com.forgerock.openconnector.tests;

import com.forgerock.openconnector.xml.XMLConfiguration;
import com.forgerock.openconnector.xml.XMLConnector;
import com.forgerock.openconnector.xml.XMLHandler;
import com.forgerock.openconnector.xml.XMLHandlerImpl;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Attributes;
import java.io.File;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

public class DeleteEntryTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Test filepaths
    private static final String XML_FILE_PATH = "test/xml_store/test.xml";
    private static final String XSD_FILE_PATH = "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd";

    private static XMLHandlerImpl handler;

    @Before
    public void init() {
        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath(XML_FILE_PATH);
        config.setXsdFilePath(XSD_FILE_PATH);
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        handler = new XMLHandlerImpl(config, parser.parseSchema(), parser.getXsdSchema());
    }

    @After
    public void destroy() {
        File xmlFile = new File(XML_FILE_PATH);

        if (xmlFile.exists())
            xmlFile.delete();
    }

    @Test
    public void withNonExistingUidShouldThrowException() {
        final String uid = "nonexistingUID";
        final String expectedErrorMessage = "Deleting entry failed. Could not find an entry of type " + ObjectClass.ACCOUNT_NAME + " with the uid " + uid;

        thrown.expect(UnknownUidException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.delete(ObjectClass.ACCOUNT, new Uid(uid));
    }

    @Test
    public void shouldRemoveEntryFromDocument() {
        Set<Attribute> attrSet = XmlConnectorTestUtil.getRequiredAccountAttributes();
        ObjectClass objClass = ObjectClass.ACCOUNT;
        Name name = AttributeUtil.getNameFromAttributes(attrSet);

        // Create new account object
        Uid insertedUid = handler.create(objClass, XmlConnectorTestUtil.getRequiredAccountAttributes());

        // Delete account object
        handler.delete(objClass, insertedUid);

        //assertNull(handler.getEntry(objClass, name));
    }
}
