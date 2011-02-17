/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author janeirik
 */
public class XmlHandlerPartialTests {
    // Account attribute fields
    public static final String ATTR_NAME = "__NAME__";
    public static final String ATTR_PASSWORD = "__PASSWORD__";
    public static final String ATTR_SECRET_ANSWER = "password-secret-answer";
    public static final String ATTR_MS_EMPLOYED = "ms-employed";
    public static final String ATTR_SIXTH_SENSE = "has-sixth-sense";
    public static final String ATTR_OVERTIME_COMISSION = "overtime-commission";
    public static final String ATTR_FIRST_NAME = "firstname";
    public static final String ATTR_LAST_NAME = "lastname";
    public static final String ATTR_PERMANTENT_EMPLOYEE = "permanent-employee";
    public static final String ATTR_FIRST_LETTER_LAST_NAME = "lastname-first-letter";
    public static final String ATTR_YEARS_EMPLOYED = "years-employed";
    public static final String ATTR_IS_DELETED = "is-deleted";

    private static XMLHandler handler;
    private Set<Attribute> attributes;

    private static final String TEST_XMLFILE_PATH = "test.xml";
    private static final String TEST_XSD_PATH = "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd";


    @BeforeClass
    public static void setUp() {
        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath(TEST_XMLFILE_PATH);
        config.setXsdFilePath(TEST_XSD_PATH);
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        handler = new XMLHandlerImpl(config, parser.parseSchema(), parser.getXsdSchema());
    }

    @AfterClass
    public static void tearDown() {

    }

    @Before
    public void init() {
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ATTR_NAME, "vaderUID"));
        attributes.add(AttributeBuilder.build(ATTR_PASSWORD, new GuardedString(new String("secret").toCharArray())));
        attributes.add(AttributeBuilder.build(ATTR_SECRET_ANSWER, new GuardedByteArray(new String("forgerock").getBytes())));
        attributes.add(AttributeBuilder.build(ATTR_MS_EMPLOYED, 999999L));
        attributes.add(AttributeBuilder.build(ATTR_SIXTH_SENSE, true));
        attributes.add(AttributeBuilder.build(ATTR_OVERTIME_COMISSION, 20.20));
        attributes.add(AttributeBuilder.build(ATTR_FIRST_NAME, "Darth"));
        attributes.add(AttributeBuilder.build(ATTR_LAST_NAME, "Vader"));
        attributes.add(AttributeBuilder.build(ATTR_FIRST_LETTER_LAST_NAME, 'V'));
        attributes.add(AttributeBuilder.build(ATTR_PERMANTENT_EMPLOYEE, true));
        attributes.add(AttributeBuilder.build(ATTR_YEARS_EMPLOYED, 200));
    }

    @After
    public void destroy() {
        /*File xmlFile = new File(TEST_XMLFILE_PATH);

        if (xmlFile.exists())
            xmlFile.delete();*/
    }

    /*@Test(expected=IllegalArgumentException.class)
    public void creatingEntryOfNonSupportedTypeShouldThrowException() {
        ObjectClass objClass = new ObjectClass("NonExistingObject");

        handler.create(objClass, null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void creatingEntryContainingAttributesFlaggedAsNonCreateableShouldThrowException() {
        attributes.add(AttributeBuilder.build(ATTR_IS_DELETED, true));
        handler.create(ObjectClass.ACCOUNT, attributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void creatingEntryWithMissingRequiredFieldShouldThrowException() {
        Set<Attribute> emptyAttributes = new HashSet<Attribute>();

        handler.create(ObjectClass.ACCOUNT, emptyAttributes);

    }

    @Test(expected=IllegalArgumentException.class)
    public void creatingEntryWithBlankRequiredFieldShouldThrowException() {
        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);
        Set<Attribute> emptyAttributes = new HashSet<Attribute>();
        emptyAttributes.add(AttributeBuilder.build(ATTR_NAME, ""));

        handler.create(objClass, emptyAttributes);
    }

    @Test(expected=IllegalArgumentException.class)
    public void creatingEntryWithIllegalAttributeTypeShouldThrowException() {
        attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build(ATTR_NAME, "asd"));
        attributes.add(AttributeBuilder.build(ATTR_PASSWORD, new GuardedString(new String("secret").toCharArray())));
        attributes.add(AttributeBuilder.build(ATTR_SECRET_ANSWER, new GuardedByteArray(new String("forgerock").getBytes())));
        attributes.add(AttributeBuilder.build(ATTR_MS_EMPLOYED, 999999));
        attributes.add(AttributeBuilder.build(ATTR_SIXTH_SENSE, true));
        attributes.add(AttributeBuilder.build(ATTR_OVERTIME_COMISSION, 20.20));
        attributes.add(AttributeBuilder.build(ATTR_FIRST_NAME, "Darth"));
        attributes.add(AttributeBuilder.build(ATTR_LAST_NAME, "Vader"));
        attributes.add(AttributeBuilder.build(ATTR_FIRST_LETTER_LAST_NAME, "V"));
        attributes.add(AttributeBuilder.build(ATTR_PERMANTENT_EMPLOYEE, true));
        attributes.add(AttributeBuilder.build(ATTR_YEARS_EMPLOYED, 200));
        attributes.add(AttributeBuilder.build(ATTR_IS_DELETED, "asdsadasd"));

        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);
        handler.create(objClass, attributes);
    }

    @Test()
    public void creatingEntryContainingAttributesNotDefinedShouldThrowException() {

    }*/

    @Test
    public void creatingEntryShouldReturnUid() {
//        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);

        /*Uid insertedUid = null;

        try {
         insertedUid = handler.create(objClass, attributes);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        assertNotNull(insertedUid.getUidValue());*/

//        handler.delete(objClass, new Uid("vaderUID"));

        //handler.serialize();
    }
    
    /*

    @Test(expected=AlreadyExistsException.class)
    public void creatingEntryWithExistingIdShouldThrowException() {
        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);
        handler.create(objClass, attributes);
    }*/

    /*@Test
    public void deletingEntryThatExistShouldNotThrowException() {
        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);
        handler.delete(objClass, new Uid("vaderUID"));
    }

    @Test(expected=UnknownUidException.class)
    public void deletingEntryThatDoesNotExistShouldThrowException() {
        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);
        handler.delete(objClass, new Uid("nonexistingUID"));
    }

    public Schema getTestSchema() {
        return null;
    }*/
}