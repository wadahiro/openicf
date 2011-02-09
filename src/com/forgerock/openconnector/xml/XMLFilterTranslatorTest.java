/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.xml.query.FunctionQuery;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryBuilder;
import java.util.Collection;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EndsWithFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.GreaterThanOrEqualFilter;
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

    private IQuery equalsQueryFirstname;
    private IQuery equalsQueryLastname;

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
        xmlHandler = new XMLHandlerImpl("test-sample1.xml", null, null);
        ft = new XMLFilterTranslator();

        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("Jan Eirik");
        EqualsFilter filter = new EqualsFilter(attrBld.build());
        equalsQueryFirstname = ft.createEqualsExpression(filter, false);

        attrBld = new AttributeBuilder();
        attrBld.setName("lastname");
        attrBld.addValue("Hallstensen");
        EqualsFilter filter2 = new EqualsFilter(attrBld.build());
        equalsQueryLastname = ft.createEqualsExpression(filter2, false);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void searchForExistingShouldReturnSizeLargerThanZero() {
        QueryBuilder qb = new QueryBuilder(equalsQueryFirstname, ObjectClass.ACCOUNT);
        System.out.println(qb.toString());
        Collection<ConnectorObject> hits = xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT);
        assertEquals(1, hits.size());
    }

    @Test
    public void searchForExistingWTwoParamsShouldReturnSizeLargerThanZero() {
        IQuery andQuery = ft.createAndExpression(equalsQueryFirstname, equalsQueryLastname);
        QueryBuilder qb = new QueryBuilder(andQuery, ObjectClass.ACCOUNT);

        System.out.println(qb.toString());

        Collection<ConnectorObject> hits = xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT);
        assertEquals(1, hits.size());
    }

    @Test
    public void searchForTwoExistingNamesShouldReturnSizeOfTwo() {
        // make firstname filter for second part of OR
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("Jørgen");
        EqualsFilter filter = new EqualsFilter(attrBld.build());
        IQuery rightHand = ft.createEqualsExpression(filter, false);

        // make is-enabled filter for second part of OR
        attrBld = new AttributeBuilder();
        attrBld.setName("is-deleted");
        attrBld.addValue("true");
        filter = new EqualsFilter(attrBld.build());
        IQuery isEnabled = ft.createEqualsExpression(filter, false);

        // make ms-employed-filter
        attrBld = new AttributeBuilder();
        attrBld.setName("ms-employed");
        attrBld.addValue("999999");
//        GreaterThanFilter gtFilter = new GreaterThanFilter(attrBld.build());
//        IQuery msEmployed = ft.createGreaterThanExpression(gtFilter, false);
        GreaterThanOrEqualFilter gtoreqFilter = new GreaterThanOrEqualFilter(attrBld.build());
        IQuery msEmployed = ft.createGreaterThanOrEqualExpression(gtoreqFilter, false);

        // chaining
        rightHand = ft.createAndExpression(rightHand, isEnabled);
        rightHand = ft.createAndExpression(rightHand, msEmployed);
        

        IQuery orQuery = ft.createOrExpression(equalsQueryFirstname, rightHand);
        QueryBuilder qb = new QueryBuilder(orQuery, ObjectClass.ACCOUNT);
        System.out.println(qb);

        Collection<ConnectorObject> hits = xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT);

        assertEquals(2, hits.size());
    }

    @Test
    public void testFunctionQuery() {
        String fn = "fn:matches";
        String [] args = {"$x/firstname", "'123'"};
        String expected = "fn:matches($x/firstname, '123')";
        FunctionQuery fq = new FunctionQuery(args, fn);
        assertEquals(expected, fq.getExpression());
    }

    @Test
    public void testSearchWithContainsFunctionQuery() {
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("rgen");
        ContainsFilter filter = new ContainsFilter(attrBld.build());
        IQuery query = ft.createContainsExpression(filter, true);
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
        IQuery query = ft.createStartsWithExpression(filter, true);
        QueryBuilder builder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        System.out.println(builder.toString());
        assertEquals(2, hits);
    }

    @Test
    public void testSearchWithEndswithFunction() {
        AttributeBuilder attrBld = new AttributeBuilder();
        attrBld.setName("firstname");
        attrBld.addValue("n");
        EndsWithFilter filter = new EndsWithFilter(attrBld.build());
        IQuery query = ft.createEndsWithExpression(filter, true);
        QueryBuilder builder = new QueryBuilder(query, ObjectClass.ACCOUNT);
        int hits = xmlHandler.search(builder.toString(), ObjectClass.ACCOUNT).size();
        System.out.println(builder.toString());
        assertEquals(1, hits);
    }
    
    


//    @Test
//    public void searchForAllShouldReturnSizeLargerThanZero() {
//        QueryBuilder qb = new QueryBuilder(null, ObjectClass.ACCOUNT);
//        assertTrue(xmlHandler.search(qb.toString(), ObjectClass.ACCOUNT).size() > 0);
//    }

}