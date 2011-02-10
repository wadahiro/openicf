/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author slogum
 */
public class XMLHandlerTests {

    private XMLHandler xmlHandler;
    private static final String filePath = "test-sample2.xml";

    private File testFile;
    private Collection<ConnectorObject> hits;

    private ConnectorObject existingUsrConObj;

    @Before
    public void setUp() {
        
        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath(filePath);
        config.setXsdFilePath("test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd");
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());

        xmlHandler = new XMLHandlerImpl(config.getXmlFilePath(), parser.parseSchema(), parser.getXsdSchema());
        testFile = new File("testusers.xml");
        System.out.println(testFile.getAbsolutePath());

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(testFile));

            pw.println("<objects></objects>");
            
        } catch (IOException ex) {
            Logger.getLogger(XMLHandlerTests.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }

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

    

    @After
    public void tearDown() {
        testFile.delete();
    }

//    @Test
//    public void getXmlFilePathShouldReturnConstructorInput() {
////        assertEquals(filePath, xmlHandler.getFilePath());
//    }

//    @Test(expected=IllegalArgumentException.class)
//    public void blankStringInConstructorShouldThrowException() {
//        XMLHandlerImpl xmlHandlerBlankConstr = new XMLHandlerImpl("", null);
//    }

//    @Test(expected=IllegalArgumentException.class)
//    public void nullInConstructorShouldThrowException() {
//        XMLHandlerImpl xmlHandlerNullConstr = new XMLHandlerImpl(null, null, null);
//    }

    @Test
    public void emptySearchQueryShouldReturnNull() {
        String query = "";
        Collection<ConnectorObject> hits = xmlHandler.search(query, null);
        assertNull(hits);
    }

//    @Test
//    public void searchForExistingAccountsFirstnameShouldNotReturnZeroHits() {
//        String query = "for $x in /OpenICFContainer/__ACCOUNT__ where $x/firstname='Jan Eirik' return $x";
//        System.out.println(query);
//        hits = xmlHandler.search(query, ObjectClass.ACCOUNT);
//        assertTrue(hits.size() > 0);
//    }

//    @Test
//    public void searchForTwoExistingAccountsFirstnameShouldReturnSizeOfTwo() {
//        String query = "declare namespace icf = \"http://openidm.forgerock.com/xml/ns/public/resource/openicf/resource-schema-1.xsd\"; declare namespace " + XMLHandlerImpl.RI_NAMESPACE_PREFIX + " = \"http://openidm.forgerock.com/xml/ns/public/resource/instances/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4\"; for $x in /OpenICFContainer/__ACCOUNT__ where $x/r:firstname = 'Jørgen' return $x";
//        System.out.println("QUERY FROM TWOEXSISTING: " + query);
//
//        hits = xmlHandler.search(query, ObjectClass.ACCOUNT);
//        assertEquals(2, hits.size());
//    }

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
}
