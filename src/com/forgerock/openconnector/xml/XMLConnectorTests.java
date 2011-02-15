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

    private final static String NAME = "Name";

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

    @Test
    public void schemaShouldReturnSchema(){
        assertNotNull(xmlConnector.schema());
    }

    @Test
    public void creatFilterTranslatorShouldReturnNewXmlFilterTranslator(){
        assertNotNull(xmlConnector.createFilterTranslator(ObjectClass.ACCOUNT, null));
    }

    @Test
    public void executeQueryShouldNotCastException(){
        
    }

    @Test
    public void createAccountShouldReturnUid(){
        Uid uid = xmlConnector.create(ObjectClass.ACCOUNT, creatAttributes(), null);
        assertNotNull(uid);
    }

    @Test(expected=AlreadyExistsException.class)
    public void createAccountWithSameNameShouldCastAlreadyExistsException(){
        Uid uid = xmlConnector.create(ObjectClass.ACCOUNT, creatAttributes(), null);
        assertNotNull(uid);
    }

    @Test
    public void updateAccountShouldReturnUid(){
        Set<Attribute> set = creatAttributes();

        set.add(AttributeBuilder.build("email","mailadress@company.org"));
        set.add(AttributeBuilder.build("__ENABLE__", true));

        Uid uid = xmlConnector.update(ObjectClass.ACCOUNT, new Uid(NAME), set, null);

        assertNotNull(uid);
    }

    @Test
    public void deleteAccountShouldNotCastException(){
        xmlConnector.delete(ObjectClass.ACCOUNT, new Uid(NAME), null);
    }

    private Set<Attribute> creatAttributes(){
        Set<Attribute> set = new HashSet<Attribute>();

        set.add(AttributeBuilder.build("__NAME__", NAME));

        char[] chars = {'A', 'B', 'C', 'D'};
        set.add(AttributeBuilder.buildPassword(new GuardedString(chars)));
        set.add(AttributeBuilder.build("address", "Adressroad 12"));
        return set;
    }
}
