/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
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
    private static final String filePath = "test-sample1.xml";

    private File testFile;
    private Collection<ConnectorObject> hits;

    @Before
    public void setUp() {
        
        xmlHandler = new XMLHandlerImpl(filePath, null);
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
    }

    @After
    public void tearDown() {
        testFile.delete();
    }

    @Test
    public void getXmlFilePathShouldReturnConstructorInput() {
//        assertEquals(filePath, xmlHandler.getFilePath());
    }

    @Test(expected=IllegalArgumentException.class)
    public void blankStringInConstructorShouldThrowException() {
        XMLHandlerImpl xmlHandlerBlankConstr = new XMLHandlerImpl("", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullInConstructorShouldThrowException() {
        XMLHandlerImpl xmlHandlerNullConstr = new XMLHandlerImpl(null, null);
    }

    @Test
    public void emptySearchQueryShouldReturnNull() {
        String query = "";
        Collection<ConnectorObject> hits = xmlHandler.search(query, null);
        assertNull(hits);
    }

    @Test
    public void searchForExistingAccountsFirstnameShouldNotReturnZeroHits() {
        String query = "for $x in doc(\"test-sample2.xml\")/OpenICFContainer/__ACCOUNT__ where $x/firstname='Jan Eirik' return $x";
        hits = xmlHandler.search(query, ObjectClass.ACCOUNT);
        assertTrue(hits.size() > 0);
    }

//    @Test
//    public void searchForTwoExistingAccountsFirstnameShouldReturnSizeOfTwo() {
//        String query = "for $x in doc(\"test-sample2.xml\")/OpenICFContainer/__ACCOUNT__ where $x/substring(firstname, 1, 2) = 'J' return $x";
//        hits = xmlHandler.search(query, null);
//        assertEquals(2, hits.size());
//    }
}