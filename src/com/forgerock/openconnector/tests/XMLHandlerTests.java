/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.tests;

import com.forgerock.openconnector.xml.*;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author slogum
 */
public class XMLHandlerTests {

    private XMLHandler xmlHandler;
    private static final String XML_FILE_PATH = "test/xml_store/filterTranslatorTests.xml";
    private static final String XSD_FILE_PATH = "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd";
    private File testFile;
    private Collection<ConnectorObject> hits;
    private ConnectorObject existingUsrConObj;
    private final static String ACCOUNT_NAME = "Erwita";
    private final static String GROUP_NAME = "Admin";
    private final static String LAST_NAME = "Lastnamerson";

    @Before
    public void setUp() {

        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath(XML_FILE_PATH);
        config.setXsdFilePath(XSD_FILE_PATH);
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());

        xmlHandler = new XMLHandlerImpl(config, parser.parseSchema(), parser.getXsdSchema());

        // INITIALIZE QUERY FOR TESTING
        XMLFilterTranslator ft = new XMLFilterTranslator();
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("Jørgen");
        EqualsFilter filter = new EqualsFilter(attrBld.build());
        IQuery query = ft.createEqualsExpression(filter, false);
        QueryBuilder qBuilder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        ArrayList<ConnectorObject> hits = (ArrayList<ConnectorObject>) xmlHandler.search(qBuilder.toString(), ObjectClass.ACCOUNT);
        existingUsrConObj = hits.get(0);
    }

    @Test
    public void emptySearchQueryShouldReturnSizeZero() {
        String query = "";
        Collection<ConnectorObject> hits = xmlHandler.search(query, null);
        assertEquals(0, hits.size());
    }

    @Test
    public void testReturntypeForFirstname() {
        Attribute attribute = existingUsrConObj.getAttributeByName("firstname");
        List<Object> values = attribute.getValue();
        Object val = values.get(0);
        assertTrue(val instanceof String);
    }

    @Test
    public void testReturntypeForYearsEmployed() {
        Attribute attribute = existingUsrConObj.getAttributeByName("years-employed");
        List<Object> values = attribute.getValue();
        Object val = values.get(0);
        assertTrue(val instanceof Integer);
    }

    @Test
    public void testReturntypeForOvertimeCommission() {
        Attribute attribute = existingUsrConObj.getAttributeByName("overtime-commission");
        List<Object> values = attribute.getValue();
        Object val = values.get(0);
        assertTrue(val instanceof Double);
    }

    @Test
    public void testReturntypeForPermanentEmployee() {
        Attribute attribute = existingUsrConObj.getAttributeByName("permanent-employee");
        List<Object> values = attribute.getValue();
        Object val = values.get(0);
        assertTrue(val instanceof Boolean);
    }

    @Test
    public void testReadingNOT_READABLEfield() {
        Attribute attribute = existingUsrConObj.getAttributeByName("secret-pin");
        assertNull(attribute);

        // testing readable field
        attribute = existingUsrConObj.getAttributeByName("years-employed");
        assertNotNull(attribute);
    }

    @Test
    public void testReadingMULTIVALUED() {
        Attribute attribute = existingUsrConObj.getAttributeByName("email");
        int values = attribute.getValue().size();
        assertEquals(3, values);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updatingNotSupportedAttributeShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("notSupported", "arg"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updatingNotUpdatableFieldShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("is-deleted", "true"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNotCreatableFieldShouldThrowException() {
        Set<Attribute> requiredAttributes = createRequiredAttributesAccount();
        requiredAttributes.add(AttributeBuilder.build("last-logoff", new BigInteger("123")));
        xmlHandler.create (ObjectClass.ACCOUNT, requiredAttributes);
    }

//    @Test
//    public void updatingWithNotValidTypeShouldThrowException() {
//        Set<Attribute> newAttributes = new HashSet();
//        float f = 10;
//
//        int i = 1;
//       Object o = i;
//        System.out.println("TYPE OF LIST OBJECT: " + o.getClass());
//
//        newAttributes.add(AttributeBuilder.build("avg-wage", f));
//        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
//    }

    @Test
    public void shouldNotGetNotReadableField() {
        Attribute attribute = existingUsrConObj.getAttributeByName("yearly-wage");
        assertNull(attribute);
    }

    @Test(expected=IllegalArgumentException.class)
    public void updateWithBlankValueForRequiredShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("lastname", ""));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test(expected=IllegalArgumentException.class)
    public void updateWithNullValueForRequiredShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("lastname"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test(expected=IllegalArgumentException.class)
    public void updateRequiredFieldWithNoValuesShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("lastname"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test
    public void updatingExistingAttributeWithNoValuesShouldNotThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("employee-type"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test(expected=IllegalArgumentException.class)
    public void updateAttributeWithWrongObjectTypeShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("ms-employed", "1234"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAttributeWithWrongObjectTypeShouldThrowException() {
        Set<Attribute> requiredAttributes = createRequiredAttributesAccount();
        requiredAttributes.add(AttributeBuilder.build("ms-employed", "1234"));
        xmlHandler.create (ObjectClass.ACCOUNT, requiredAttributes);
    }

    @Test(expected=UnknownUidException.class)
    public void updateNoneExistingShouldThrowException() {
        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build("ms-employed", "1234"));
        xmlHandler.update(ObjectClass.ACCOUNT, new Uid("nonexisting"), newAttributes);
    }

    @Test(expected=UnknownUidException.class)
    public void deleteNoneExisitingUserShouldThrowException() {
        xmlHandler.delete(ObjectClass.ACCOUNT, new Uid("nonexisting"));
    }

    private Set<Attribute> createRequiredAttributesAccount() {
        Set<Attribute> set = new HashSet<Attribute>();

        set.add(AttributeBuilder.build("__NAME__", ACCOUNT_NAME));
        set.add(AttributeBuilder.build("lastname", LAST_NAME));

        char[] chars = {'A', 'B', 'C', 'D'};
        set.add(AttributeBuilder.buildPassword(new GuardedString(chars)));
        set.add(AttributeBuilder.build("address", "Adressroad 12"));
        return set;
    }
}
