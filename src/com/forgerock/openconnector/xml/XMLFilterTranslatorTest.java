/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.xml.query.FunctionQuery;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.util.Collection;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EndsWithFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.GreaterThanFilter;
import org.identityconnectors.framework.common.objects.filter.LessThanFilter;
import org.identityconnectors.framework.common.objects.filter.StartsWithFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author slogum
 */
public class XMLFilterTranslatorTest {

    private XMLHandlerImpl xmlHandler;
    private XMLFilterTranslator ft;

    private IQuery equalsQueryFnJan;
    private IQuery equalsQueryFnJorgen;
    private IQuery equalsQueryLnHallstensen;
    private IQuery gtQueryMs;
    private IQuery ltQueryMs;

    public XMLFilterTranslatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath("test-sample2.xml");
        config.setXsdFilePath("test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd");
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());

        xmlHandler = new XMLHandlerImpl(config.getXmlFilePath(), parser.parseSchema(), parser.getXsdSchema());
        ft = new XMLFilterTranslator();

        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("Jan Eirik");
        EqualsFilter filter = new EqualsFilter(attrBld.build());
        equalsQueryFnJan = ft.createEqualsExpression(filter, false);

        attrBld = new AttributeBuilder();
        attrBld.setName("lastname");
        attrBld.addValue("Hallstensen");
        EqualsFilter filter2 = new EqualsFilter(attrBld.build());
        equalsQueryLnHallstensen = ft.createEqualsExpression(filter2, false);

        attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("Jørgen");
        EqualsFilter filter3 = new EqualsFilter(attrBld.build());
        equalsQueryFnJorgen = ft.createEqualsExpression(filter3, false);

        attrBld = new AttributeBuilder();
        attrBld.setName("ms-employed");
        attrBld.addValue("1");
        GreaterThanFilter gtFilter = new GreaterThanFilter(attrBld.build());
        IQuery gtQueryMs = ft.createGreaterThanExpression(gtFilter, false);


        attrBld = new AttributeBuilder();
        attrBld.setName("ms-employed");
        attrBld.addValue("10000000000");
        LessThanFilter ltFilter = new LessThanFilter(attrBld.build());
        IQuery ltQueryMs = ft.createLessThanExpression(ltFilter, false);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void uniqueExistingShouldReturnSizeOne() {
        QueryBuilder qb = new QueryBuilder(equalsQueryFnJan, ObjectClass.ACCOUNT);
        System.out.println(qb.toString());
        Collection<ConnectorObject> hits = xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT);
        assertEquals(1, hits.size());
    }

    @Test
    public void uniqueExistingWTwoQueriesShouldReturnSizeOne() {
        IQuery andQuery = ft.createAndExpression(equalsQueryFnJan, equalsQueryLnHallstensen);
        QueryBuilder qb = new QueryBuilder(andQuery, ObjectClass.ACCOUNT);

        System.out.println(qb.toString());

        Collection<ConnectorObject> hits = xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT);
        assertEquals(1, hits.size());
    }

    @Test
    public void twoExistingShouldReturnSizeTwo() {
        // make firstname filter for second part of OR
        IQuery orQuery = ft.createOrExpression(equalsQueryFnJan, equalsQueryFnJorgen);
        QueryBuilder qb = new QueryBuilder(orQuery, ObjectClass.ACCOUNT);
        System.out.println(qb);

        Collection<ConnectorObject> hits = xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT);

        assertEquals(2, hits.size());
    }

    @Test
    public void createFunctionQueryWhereNotIsTrue() {
        String fn = "matches";
        String [] args = {"$x/firstname", "'123'"};
        String expected = "fn:not(matches($x/firstname, '123'))";
        FunctionQuery fq = new FunctionQuery(args, fn, true);
        System.out.println("EXPR: " + fq.getExpression());
        assertEquals(expected, fq.getExpression());
    }

    @Test
    public void createFunctionQueryWhereNotIsFalse() {
        String fn = "matches";
        String [] args = {"$x/firstname", "'123'"};
        String expected = "fn:matches($x/firstname, '123')";
        FunctionQuery fq = new FunctionQuery(args, fn, false);
        assertEquals(expected, fq.getExpression());
    }

    @Test
    public void testSearchWithContainsFunctionQuery() {
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("rgen");
        ContainsFilter filter = new ContainsFilter(attrBld.build());
        IQuery query = ft.createContainsExpression(filter, false);
        QueryBuilder builder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        assertEquals(1, hits);
    }

    @Test
    public void testSearchWithStartswithFunction() {
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("J");
        StartsWithFilter filter = new StartsWithFilter(attrBld.build());
        IQuery query = ft.createStartsWithExpression(filter, false);
        QueryBuilder builder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        System.out.println("SEARCHING FOR TWO EXISTING");
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        assertEquals(2, hits);
    }

    @Test
    public void testSearchWithEndswithFunction() {
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("n");
        EndsWithFilter filter = new EndsWithFilter(attrBld.build());
        IQuery query = ft.createEndsWithExpression(filter, false);
        QueryBuilder builder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        assertEquals(1, hits);
    }
    
    @Test
    public void searchForAllShouldReturnSizeLargerThanZero() {
        QueryBuilder qb = new QueryBuilder(null, ObjectClass.ACCOUNT);
        assertTrue(xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT).size() > 0);
    }

    @Test
    public void oneNoneExistantAccountShouldReturnSizeOfOne() {
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("en");
        EndsWithFilter filter = new EndsWithFilter(attrBld.build());
        IQuery query = ft.createEndsWithExpression(filter, true);
        QueryBuilder builder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        System.out.println(builder.toString());
        assertEquals(1, hits);
    }

    @Test
    public void testGTAndLTExpressions() {
        IQuery andQuery = ft.createAndExpression(gtQueryMs, ltQueryMs);
        QueryBuilder builder = new QueryBuilder(andQuery, ObjectClass.ACCOUNT);
        System.out.println("BUILDER: " + builder.toString());
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        System.out.println("HITS: " + hits);
    }

    @Test
    public void testGTOREQAndLTOREQExpressions() {
        
    }
}