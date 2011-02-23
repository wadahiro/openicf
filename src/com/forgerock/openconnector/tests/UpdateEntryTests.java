/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.tests;

import com.forgerock.openconnector.xml.XMLConfiguration;
import com.forgerock.openconnector.xml.XMLConnector;
import com.forgerock.openconnector.xml.XMLFilterTranslator;
import com.forgerock.openconnector.xml.XMLHandler;
import com.forgerock.openconnector.xml.XMLHandlerImpl;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

/**
 *
 * @author janeirik
 */
public class UpdateEntryTests {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Test filepaths
    private static final String XML_FILE_PATH = "test/xml_store/filterTranslatorTests.xml";
    private static final String XSD_FILE_PATH = "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd";

    private static XMLHandler handler;

    @Before
    public void init() {
        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath(XML_FILE_PATH);
        config.setXsdFilePath(XSD_FILE_PATH);
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        handler = new XMLHandlerImpl(config, parser.parseSchema(), parser.getXsdSchema());
    }

    @Test
    public void withNotSupportedAttributeShouldThrowException() {
        final String notSupportedAttribute = "notSupported";
        final String expectedErrorMessage = "Data field: " + notSupportedAttribute + " is not supported.";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build(notSupportedAttribute, "arg"));
        handler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test
    public void withNotUpdatableFieldShouldThrowException() {
        final String attributeName = "is-deleted";
        final String expectedErrorMessage = attributeName + " is not updatable.";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build(attributeName, "true"));
        handler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test
    public void withBlankValueForRequiredShouldThrowException() {
        final String attributeName = "lastname";
        final String expectedErrorMessage = "Parameter '" + attributeName + "' must not be blank.";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build(attributeName, ""));
        handler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test
    public void withNoValuesForRequiredShouldThrowException() {
        final String attributeName = "lastname";
        final String expectedErrorMessage = "No values provided for required attribute: " + attributeName;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build(attributeName));
        handler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test
    public void withExistingAttributeWithNoValuesShouldNotThrowException() {
        final String attributeName = "employee-type";
        final String uidValue = "JANEIRIK";
        Set<Attribute> attrWithValue = new HashSet();
        attrWithValue.add(AttributeBuilder.build(attributeName, "test-type"));
        handler.update(ObjectClass.ACCOUNT, new Uid(uidValue), attrWithValue);

        Set<Attribute> attrWithoutValue = new HashSet();
        attrWithoutValue.add(AttributeBuilder.build(attributeName));
        handler.update(ObjectClass.ACCOUNT, new Uid(uidValue), attrWithoutValue);

        EqualsFilter eqFilter = new EqualsFilter(AttributeBuilder.build(Uid.NAME, uidValue));
        XMLFilterTranslator ft = new XMLFilterTranslator();
        IQuery equalsQuery =  ft.createEqualsExpression(eqFilter, false);
        QueryBuilder builder = new QueryBuilder(equalsQuery, ObjectClass.ACCOUNT);
        List<ConnectorObject> qResults = (List) handler.search(builder.toString(), ObjectClass.ACCOUNT);
        ConnectorObject obj = qResults.get(0);
        assertNull(obj.getAttributeByName(attributeName));
    }

    @Test
    public void withWrongObjectTypeShouldThrowException() {
        final String attributeName = "ms-employed";
        final String expectedErrorMessage = attributeName + " contains values of illegal type";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        Set<Attribute> newAttributes = new HashSet();
        newAttributes.add(AttributeBuilder.build(attributeName, "1234"));
        handler.update(ObjectClass.ACCOUNT, new Uid("JANEIRIK"), newAttributes);
    }

    @Test
    public void thatDoesNotExistinShouldThrowException() {
        final ObjectClass objClass = ObjectClass.ACCOUNT;
        final String uid = "nonexisting";
        final String expectedErrorMessage = "Could not update entry. No entry of type " + 
                objClass.getObjectClassValue() + " with the id " + uid + " found.";

        thrown.expect(UnknownUidException.class);
        thrown.expectMessage(expectedErrorMessage);

        Set<Attribute> newAttributes = new HashSet();
        handler.update(objClass, new Uid("nonexisting"), newAttributes);
    }
}
