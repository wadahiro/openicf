/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.*;

import org.identityconnectors.common.logging.Log;
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

/**
 * Attempts to test the {@link XMLConnector} with the framework.
 *
 * @author slogum
 * @version 1.0
 * @since 1.0
 */
public class XMLConnectorTests {

    public static final String ATTR_REGISTRY_NAME = "registryName";
    public static final String ATTR_FIRST_NAME = "firstname";
    public static final String ATTR_LAST_NAME = "lastname";
    public static final String ATTR_SSO_USER = "ssoUser";
    public static final String ATTR_PASSWORD_POLICY = "passwordPolicy";
    public static final String ATTR_EXPIRE_PASSWORD = "expirePassword";
    public static final String ATTR_DELETE_FROM_REGISTRY = "deleteFromRegistry";
    public static final String ATTR_SYNC_GSO_CREDENTIALS = "syncGSOCredentials";
    public static final String ATTR_IMPORT_FROM_REGISTRY = "importFromRegistry";
    public static final String ATTR_GROUP_MEMBERS = "members";
    public static final String ATTR_GSO_WEB_CREDENTIALS = "gsoWebCredentials";
    public static final String ATTR_GSO_GROUP_CREDENTIALS = "gsoGroupCredentials";
    public static final String TYPE_GSO_GROUP_RESOURCE = "GSOGroupResource";
    public static final String TYPE_GSO_RESOURCE = "GSOWebResource";
    public static final String TOKEN_GSO_RESOURCE = "|:";

    //set up logging
    private static final Log log = Log.getLog(XMLConnectorTests.class);

    @BeforeClass
    public static void setUp() {
        //Assert.assertNotNull(HOST);
        //Assert.assertNotNull(LOGIN);
        //Assert.assertNotNull(PASSWORD);
        //
        //other setup work to do before running tests
        //
    }

    @AfterClass
    public static void tearDown() {
        //
        //clean up resources
        //
    }

    @Test
    public void exampleTest1() {

        // Create test attributes
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("__NAME__", "namedid"));
        attributes.add(AttributeBuilder.build(ATTR_IMPORT_FROM_REGISTRY, String.valueOf(false)));
        attributes.add(AttributeBuilder.build(ATTR_FIRST_NAME, "Jan Eirik"));
        attributes.add(AttributeBuilder.build(ATTR_LAST_NAME, "Hallstensen"));
        Name name = AttributeUtil.getNameFromAttributes(attributes);

        // Convert attribute set to map
        Map<String, Attribute> attrMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));

        // Get schema
        Schema schema = getSchema();


        ObjectClass c = new ObjectClass("ASD");
        //System.out.println(c.getObjectClassValue());

        // Get object class
        ObjectClassInfo info =  schema.findObjectClassInfo(ObjectClass.ACCOUNT_NAME);

        // Get fields for object class
        Set<AttributeInfo> attr = info.getAttributeInfo();



        // Create "root" element
        Element root = new Element(ObjectClass.ACCOUNT_NAME);

        // Add child elements
        for (AttributeInfo ai : attr) {
            //System.out.println(ai.isRequired());

            Element child = new Element(ai.getName());

            // Add attribute value to field
            if (attrMap.containsKey(ai.getName())) {
                child.setText(AttributeUtil.getStringValue(attrMap.get(ai.getName())));
            }

            root.addContent(child);
        }

        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());

        /*try {
            out.output(root, System.out);

        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    public Schema getSchema() {
        Schema schema;

        SchemaBuilder schemaBuilder = new SchemaBuilder(XMLConnector.class);

            // GSO WEB Resource
            ObjectClassInfoBuilder ocBuilder = new ObjectClassInfoBuilder();
            ocBuilder.setType(TYPE_GSO_RESOURCE);
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
            ObjectClassInfo objectClassInfo = ocBuilder.build();
            schemaBuilder.defineObjectClass(objectClassInfo);
            schemaBuilder.removeSupportedObjectClass(CreateOp.class, objectClassInfo);
            schemaBuilder.removeSupportedObjectClass(UpdateOp.class, objectClassInfo);
            schemaBuilder.removeSupportedObjectClass(DeleteOp.class, objectClassInfo);


            // GSO GROUP Resource
            ocBuilder = new ObjectClassInfoBuilder();
            ocBuilder.setType(TYPE_GSO_GROUP_RESOURCE);
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
            objectClassInfo = ocBuilder.build();
            schemaBuilder.defineObjectClass(objectClassInfo);
            schemaBuilder.removeSupportedObjectClass(CreateOp.class, objectClassInfo);
            schemaBuilder.removeSupportedObjectClass(UpdateOp.class, objectClassInfo);
            schemaBuilder.removeSupportedObjectClass(DeleteOp.class, objectClassInfo);


            // Group
            ocBuilder = new ObjectClassInfoBuilder();
            ocBuilder.setType(ObjectClass.GROUP_NAME);
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
            //ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Uid.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_REGISTRY_NAME, String.class, EnumSet.of(Flags.REQUIRED)));
            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_GROUP_MEMBERS, String.class, EnumSet.of(Flags.MULTIVALUED)));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_IMPORT_FROM_REGISTRY, Boolean.class));
            schemaBuilder.defineObjectClass(ocBuilder.build());


            // Users
            ocBuilder = new ObjectClassInfoBuilder();
            ocBuilder.setType(ObjectClass.ACCOUNT_NAME);
            //The name of the object
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED, Flags.NOT_UPDATEABLE)));
            //User registry name
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_REGISTRY_NAME, String.class, EnumSet.of(Flags.REQUIRED, Flags.NOT_UPDATEABLE)));
            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_FIRST_NAME, String.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_LAST_NAME, String.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.LAST_LOGIN_DATE);
            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.GROUPS);
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_SSO_USER, Boolean.class));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_IMPORT_FROM_REGISTRY, Boolean.class));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_EXPIRE_PASSWORD, Boolean.class));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_PASSWORD_POLICY, Boolean.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_GSO_GROUP_CREDENTIALS, String.class, EnumSet.of(Flags.MULTIVALUED)));
            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_GSO_WEB_CREDENTIALS, String.class, EnumSet.of(Flags.MULTIVALUED)));
            ocBuilder.addAttributeInfo(OperationalAttributeInfos.PASSWORD);
            ocBuilder.addAttributeInfo(OperationalAttributeInfos.ENABLE);
            schemaBuilder.defineObjectClass(ocBuilder.build());
            schema = schemaBuilder.build();

            return schema;
    }

    @Test
    public void exampleTest2() {
        XMLHandler handler = new XMLHandlerImpl("test.xml", getSchema());

        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(AttributeBuilder.build("__NAME__", "namedid"));
        attributes.add(AttributeBuilder.build(ATTR_REGISTRY_NAME, "registry"));
        attributes.add(AttributeBuilder.build(ATTR_IMPORT_FROM_REGISTRY, String.valueOf(false)));
        attributes.add(AttributeBuilder.build(ATTR_FIRST_NAME, "Jan Eirik"));
        attributes.add(AttributeBuilder.build(ATTR_LAST_NAME, "Hallstensen"));


        ObjectClass objClass = new ObjectClass(ObjectClass.ACCOUNT_NAME);

        Uid uid = handler.create(objClass, attributes);
    }
}
