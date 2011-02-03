/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author slogum
 */
public class XMLHandlerTests {

    private XMLHandlerImpl xmlHandler;
    private static final String filePath = "testusers.xml";

    private File testFile;

    @Before
    public void setUp() {
        
        xmlHandler = new XMLHandlerImpl(filePath, null);
        testFile = new File("testusers.xml");

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
        assertEquals(filePath, xmlHandler.getFilePath());
    }

    @Test(expected=IllegalArgumentException.class)
    public void blankStringInConstructorShouldThrowException() {
        XMLHandlerImpl xmlHandlerBlankConstr = new XMLHandlerImpl("", null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullInConstructorShouldThrowException() {
        XMLHandlerImpl xmlHandlerNullConstr = new XMLHandlerImpl(null, null);
    }
}