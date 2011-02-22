
package com.forgerock.openconnector.tests;

import com.forgerock.openconnector.xml.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class XMLConfigurationTests {
    
    private XMLConfiguration config;

    @Before
    public void connectionSetUp() {
       config = new XMLConfiguration();
    }

    @Test
    public void getXmlFilepathFromConfiguration() {
        config.setXmlFilePath("users.xml");
        assertEquals("users.xml", config.getXmlFilePath());
    }
    @Test
    public void getXsdRIFilepathFromConfiguration() {
        config.setXsdIcfFilePath("xsdRi.xsd");
        assertEquals("xsdRi.xsd", config.getXsdIcfFilePath());
    }
    @Test
    public void getXsdFilepathFromConfiguration() {
        config.setXsdFilePath("xsdTest.xsd");
        assertEquals("xsdTest.xsd", config.getXsdFilePath());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowIllegalArguemntExceptionWhenValidatingWithNullFilepath() {
        config.setXmlFilePath(null);
        config.validate();
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenValidatingWithBlankFilepath() {
        config.setXmlFilePath("");
        config.validate();
    }
}