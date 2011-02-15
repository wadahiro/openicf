/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author slogum
 */
public class XMLConfigurationTests {
    
    private XMLConfiguration config;

    @Before
    public void connectionSetUp() {
       config = new XMLConfiguration();
    }

    @Test
    public void shouldGetXmlFilepathFromConfiguration() {
        config.setXmlFilePath("users.xml");
        assertEquals("users.xml", config.getXmlFilePath());
    }
    @Test
    public void shouldGetXsdRIFilepathFromConfiguration() {
        config.setXsdRIFilePath("xsdRi.xsd");
        assertEquals("xsdRi.xsd", config.getXsdRIFilePath());
    }
    @Test
    public void shouldGetXsdFilepathFromConfiguration() {
        config.setXsdFilePath("xsdTest.xsd");
        assertEquals("xsdTest.xsd", config.getXsdFilePath());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValidatingWithNullFilepath() {
        config.setXmlFilePath(null);
        config.validate();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValidatingWithBlankFilepath() {
        config.setXmlFilePath("");
        config.validate();
    }
}