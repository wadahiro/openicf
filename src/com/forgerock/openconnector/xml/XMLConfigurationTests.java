/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;

import junit.framework.Assert;
import org.identityconnectors.common.logging.Log;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author slogum
 */
public class XMLConfigurationTests {

    //set up logging
    private static final Log log = Log.getLog(XMLConfigurationTests.class);

    private XMLConfiguration config;

    @Before
    public void connectionSetUp() {
       config = new XMLConfiguration();
    }

    @Test
    public void should_get_xml_filepath_from_configuration() {
        config.setXmlFilePath("users.xml");
        Assert.assertEquals("users.xml", config.getXmlFilePath());
    }

    @Test(expected=IllegalArgumentException.class)
    public void should_throw_exception_when_validating_with_null_filepath() {
        config.setXmlFilePath(null);
        config.validate();
    }

    @Test(expected=IllegalArgumentException.class)
    public void should_throw_exception_when_validating_with_blank_filepath() {
        config.setXmlFilePath("");
        config.validate();
    }
}