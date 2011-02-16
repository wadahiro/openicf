package com.forgerock.openconnector.xml;

import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.*;
import static org.junit.Assert.*;

public class XMLConnectorTests {

    private static XMLConnector xmlConnector;
    private static XMLConfiguration xmlConfig;
    private final static String ACCOUNT_NAME = "Name";
    private final static String GROUP_NAME = "This is name";

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
    public void initShouldNotCastException() {
        xmlConnector.init(xmlConfig);
    }

    @Test
    public void testShouldNotCastException() {
        xmlConnector.test();
    }

    @Test(expected = NullPointerException.class)
    public void testShouldCastNullPointerException() {
        XMLConnector xmlCon = new XMLConnector();
        xmlCon.test();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldCastIllegalArgumentException() {
        XMLConfiguration conf = new XMLConfiguration();

        conf.setXmlFilePath("404.xml");
        conf.setXsdFilePath("test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd");
        conf.setXsdIcfFilePath("404.xml");

        XMLConnector xmlCon = new XMLConnector();
        xmlCon.init(conf);

        xmlCon.test();
    }

    @Test
    public void schemaShouldReturnSchema() {
        assertNotNull(xmlConnector.schema());
    }

    @Test
    public void creatFilterTranslatorShouldReturnNewXmlFilterTranslator() {
        assertNotNull(xmlConnector.createFilterTranslator(ObjectClass.ACCOUNT, null));
    }

    @Test
    public void getConfigurationShouldReturnConfiguration() {
        assertNotNull(xmlConnector.getConfiguration());
    }

    @Test
    public void executeQueryShouldNotCastException() {
    }

    @Test
    public void createAccountShouldReturnUid() {
        Uid uid = xmlConnector.create(ObjectClass.ACCOUNT, createAttributesAccount(), null);
        assertNotNull(uid);
    }

    @Test(expected = AlreadyExistsException.class)
    public void createAccountWithSameNameShouldCastAlreadyExistsException() {
        xmlConnector.create(ObjectClass.ACCOUNT, createAttributesAccount(), null);
    }

    @Test
    public void createGroupShouldReturnUid() {
        Uid uid = xmlConnector.create(ObjectClass.GROUP, createAttributesGroup(), null);
        assertNotNull(uid);
    }

    @Test(expected = AlreadyExistsException.class)
    public void createGroupWithSameNameShouldCastAlreadyExistsException() {
        xmlConnector.create(ObjectClass.GROUP, createAttributesGroup(), null);
    }

    @Test(expected = NullPointerException.class)
    public void createEntityWithNullAttributesShouldCastNullPointerException() {
        xmlConnector.create(null, null, null);
    }

    @Test
    public void updateAccountShouldReturnUid() {
        Set<Attribute> attributes = createAttributesAccount();

        attributes.add(AttributeBuilder.build("email", "mailadress@company.org"));
        attributes.add(AttributeBuilder.build("__ENABLE__", true));

        Uid uid = xmlConnector.update(ObjectClass.ACCOUNT, new Uid(ACCOUNT_NAME), attributes, null);

        assertNotNull(uid);
    }

    @Test
    public void updateGroupShouldReturnUid() {
        Set<Attribute> attributes = new HashSet<Attribute>();

        attributes.add(AttributeBuilder.build("__DESCRIPTION__", "this is description updated"));
        attributes.add(AttributeBuilder.build("__SHORT_NAME__", "tidu"));

        Uid uid = xmlConnector.update(ObjectClass.GROUP, new Uid(GROUP_NAME), attributes, null);
        assertNotNull(uid);
    }

    @Test(expected = NullPointerException.class)
    public void updateEntryWithNullShouldCastNullPointerException() {
        xmlConnector.update(null, null, null, null);
    }

    @Test
    public void deleteAccountShouldNotCastException() {
        xmlConnector.delete(ObjectClass.ACCOUNT, new Uid(ACCOUNT_NAME), null);
    }

    @Test
    public void deleteGroupShouldNotCastException() {
        xmlConnector.delete(ObjectClass.GROUP, new Uid(GROUP_NAME), null);
    }

    @Test(expected = NullPointerException.class)
    public void deleteAccountWithNullShouldCastNullPointerException() {
        xmlConnector.delete(null, null, null);
    }

    private Set<Attribute> createAttributesAccount() {
        Set<Attribute> set = new HashSet<Attribute>();

        set.add(AttributeBuilder.build("__NAME__", ACCOUNT_NAME));

        char[] chars = {'A', 'B', 'C', 'D'};
        set.add(AttributeBuilder.buildPassword(new GuardedString(chars)));
        set.add(AttributeBuilder.build("address", "Adressroad 12"));
        return set;
    }

    private Set<Attribute> createAttributesGroup() {
        Set<Attribute> attributes = new HashSet<Attribute>();

        attributes.add(AttributeBuilder.build("__NAME__", GROUP_NAME));
        attributes.add(AttributeBuilder.build("__DESCRIPTION__", "this is description"));
        attributes.add(AttributeBuilder.build("__SHORT_NAME__", "tid"));

        return attributes;
    }
}
