
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
        config.setXsdIcfFilePath("xsdRi.xsd");
        assertEquals("xsdRi.xsd", config.getXsdIcfFilePath());
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