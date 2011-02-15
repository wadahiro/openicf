
package com.forgerock.openconnector.xsdparser;


import com.forgerock.openconnector.xml.XMLConnector;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class SchemaParserTests {
    SchemaParser parser;


    @Before
    public void setUp(){
        parser = new SchemaParser(XMLConnector.class, "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd");
    }

    @After
    public void tearDown(){
        parser = null;
    }

    @Test
    public void newSchemaParserShouldReturnNewSchemaParser(){
        assertNotNull(parser);
    }

    @Test(expected= ConnectorIOException.class)
    public void newSchemaParserWithInvalidFilePathShouldReturnConnectorIOException(){
        SchemaParser parserTest = new SchemaParser(XMLConnector.class, "test/xml_store/404");
    }

    @Test(expected= IllegalArgumentException.class)
    public void newSchemaParserWithEmptyFilePathShouldReturnIllegalArgumentException(){
        SchemaParser parserTest = new SchemaParser(XMLConnector.class, "");
    }
    
    @Test(expected= NullPointerException.class)
    public void newSchemaParserWithNullClassShouldReturnNullPointerException(){
        SchemaParser parserTest = new SchemaParser(null, "test/xml_store/404");
    }

    @Test
    public void getXsdSchemaShouldReturnXsdSchemaSet(){
        assertNotNull(parser.getXsdSchema());
    }

    @Test
    public void parseSchemaShouldReturnSchema(){
        assertNotNull(parser.parseSchema());
    }
}
