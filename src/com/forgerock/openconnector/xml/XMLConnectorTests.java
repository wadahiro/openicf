/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;


import com.forgerock.openconnector.util.XmlHandlerUtil;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import com.sun.org.apache.xerces.internal.parsers.XMLParser;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.common.Assertions;
import org.junit.*;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationalAttributeInfos;
import org.identityconnectors.framework.common.objects.PredefinedAttributeInfos;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLConnectorTests {

    private static XMLConnector xmlConnector;
    private static XMLConfiguration xmlConfig;

    @BeforeClass
    public static void setUp() {
        xmlConfig = new XMLConfiguration();
        xmlConfig.setXmlFilePath("test/xml_store/testXmlConnector.xml");
        xmlConfig.setXsdFilePath("test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd");
        
        xmlConnector = new XMLConnector();
    }

    @AfterClass
    public static void tearDown() {
        xmlConfig = null;
        xmlConnector = null;
    }

    @Test
    public void callInitShouldNotCastException(){
        xmlConnector.init(xmlConfig);
    }

    @Test
    public void callTestShouldNotCastException(){
        xmlConnector.test();
    }

    @Test(expected=NullPointerException.class)
    public void callTestShouldCastNullPointerException(){
        XMLConnector xmlCon = new XMLConnector();
        xmlCon.test();
    }

    @Test(expected=IllegalArgumentException.class)
    public void callTestShouldCastIllegalArgumentException(){
        XMLConfiguration conf = new XMLConfiguration();
        
        conf.setXmlFilePath("404.xml");
        conf.setXsdFilePath("test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd");
        conf.setXsdIcfFilePath("404.xml");

        XMLConnector xmlCon = new XMLConnector();
        xmlCon.init(conf);
        
        xmlCon.test();
    }
  
}
