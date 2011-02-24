package com.forgerock.openconnector.tests;

import com.forgerock.openconnector.xml.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.junit.*;
import static org.junit.Assert.*;

public class XMLConnectorTests {

    private static XMLConnector createXmlConnector, updateXmlConnector, deleteXmlConnector, queryXmlConnector;
    private static XMLConfiguration createXmlConfig, updateXmlConfig, deleteXmlConfig, queryXmlConfig;
    private final static String ACCOUNT_FIRST_NAME = "Erwita";
    private final static String GROUP_NAME = "Admin";
    private final static String LAST_NAME = "Lastnamerson";
    private final static String ACCOUNT_NAME = "username";

    private final static String XML_CREATE_FILEPATH = "test/xml_store/testCreateConnector.xml";
    private final static String XML_UPDATE_FILEPATH = "test/xml_store/testUpdateConnector.xml";
    private final static String XML_DELETE_FILEPATH = "test/xml_store/testDeleteConnector.xml";
    private final static String XML_QUERY_FILEPATH = "test/xml_store/testQueryConnector.xml";

    private final static String XSD_SCHEMA_FILEPATH = "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd";


    @BeforeClass
    public static void setUp() {

        createXmlConfig = new XMLConfiguration();
        createXmlConfig.setXmlFilePath(XML_CREATE_FILEPATH);
        createXmlConfig.setXsdFilePath(XSD_SCHEMA_FILEPATH);

        createXmlConnector = new XMLConnector();
        createXmlConnector.init(createXmlConfig);

        updateXmlConfig = new XMLConfiguration();
        updateXmlConfig.setXmlFilePath(XML_UPDATE_FILEPATH);
        updateXmlConfig.setXsdFilePath(XSD_SCHEMA_FILEPATH);

        updateXmlConnector = new XMLConnector();
        updateXmlConnector.init(updateXmlConfig);

        deleteXmlConfig = new XMLConfiguration();
        deleteXmlConfig.setXmlFilePath(XML_DELETE_FILEPATH);
        deleteXmlConfig.setXsdFilePath(XSD_SCHEMA_FILEPATH);

        deleteXmlConnector = new XMLConnector();
        deleteXmlConnector.init(deleteXmlConfig);

        queryXmlConfig = new XMLConfiguration();
        queryXmlConfig.setXmlFilePath(XML_QUERY_FILEPATH);
        queryXmlConfig.setXsdFilePath(XSD_SCHEMA_FILEPATH);

        queryXmlConnector = new XMLConnector();

    }

    @AfterClass
    public static void tearDown() {
        File create = new File(XML_CREATE_FILEPATH);
        File update = new File(XML_UPDATE_FILEPATH);
        File delete = new File(XML_DELETE_FILEPATH);
        
        if(create.exists()){
            create.delete();
        }
        if(update.exists()){
             update.delete();
         }
        if(delete.exists()){
            delete.delete();
        }
         
    }

    @Test
    public void initShouldNotCastExceptionWhenInitiatedWithValidXmlConfig() {
        queryXmlConnector.init(queryXmlConfig);
    }

    @Test(expected=NullPointerException.class)
    public void initShouldCastNullPointerExceptionWhenInitiatedWithNull(){
        XMLConnector xmlCon = new XMLConnector();
        xmlCon.init(null);
    }

    @Test
    public void testShouldNotCastExceptionWhenEverithingIsOK() {
        queryXmlConnector.test();
    }

    @Test(expected = NullPointerException.class)
    public void testShouldCastNullPointerExceptionWhenRequiredFiledsAreNotSet() {
        XMLConnector xmlCon = new XMLConnector();
        xmlCon.test();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldCastIllegalArgumentExceptionWhenGivenFilpathsIsWrong() {
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
        assertNotNull(queryXmlConnector.schema());
    }

    @Test
    public void creatFilterTranslatorShouldReturnNewXmlFilterTranslatorWhenGivenValidParameters() {
        assertNotNull(queryXmlConnector.createFilterTranslator(ObjectClass.ACCOUNT, null));
    }

    @Test
    public void getConfigurationShouldReturnConfiguration() {
        assertNotNull(queryXmlConnector.getConfiguration());
    }

    @Test
    public void executeNullQueryShouldReturnOneResult() {
        TestResultsHandler r = new TestResultsHandler();
        queryXmlConnector.executeQuery(ObjectClass.ACCOUNT, null, r, null);
        assertEquals(1, r.getSumResults());
    }

    @Test
    public void executeLastNameQueryShouldReturnOneResult() {
        XMLFilterTranslator f = (XMLFilterTranslator) queryXmlConnector.createFilterTranslator(ObjectClass.ACCOUNT, null);
        
        EqualsFilter ef = new EqualsFilter(AttributeBuilder.build("lastname", LAST_NAME));
        TestResultsHandler r = new TestResultsHandler();

        queryXmlConnector.executeQuery(ObjectClass.ACCOUNT, f.createEqualsExpression(ef, false), r, null);
    
        assertEquals(1, r.getSumResults());
    }

    @Test(expected=NullPointerException.class)
    public void executeQueryWithNullShouldCastNullPointerException(){
        queryXmlConnector.executeQuery(null, null, null, null);
    }

    @Test
    public void createAccountShouldReturnUidWhenGivedValidParameters() {
        Uid uid = createXmlConnector.create(ObjectClass.ACCOUNT, createAttributesAccount(), null);
        assertNotNull(uid);
    }

    @Test(expected = AlreadyExistsException.class)
    public void createAccountWithSameNameShouldCastAlreadyExistsException() {
        createXmlConnector.create(ObjectClass.ACCOUNT, createAttributesAccount(), null);
    }

    @Test
    public void createGroupShouldReturnUidWhenGivenValidParameters() {
        Uid uid = createXmlConnector.create(ObjectClass.GROUP, createAttributesGroup(), null);
        assertNotNull(uid);
    }

    @Test(expected = AlreadyExistsException.class)
    public void createGroupWithSameNameShouldCastAlreadyExistsException() {
        createXmlConnector.create(ObjectClass.GROUP, createAttributesGroup(), null);
    }

    @Test(expected = NullPointerException.class)
    public void createEntityWithNullAttributesShouldCastNullPointerException() {
        createXmlConnector.create(null, null, null);
    }

    @Test
    public void updateAccountShouldReturnUidWhenGivenValidParameters() {
        updateXmlConnector.create(ObjectClass.ACCOUNT, createAttributesAccount(), null);

        Set<Attribute> attributes = createAttributesAccount();

        attributes.add(AttributeBuilder.build("email", "mailadress1@company.org","mailadress2@company.org","mailadress3@company.org"));
  

        Uid uid = updateXmlConnector.update(ObjectClass.ACCOUNT, new Uid(ACCOUNT_FIRST_NAME), attributes, null);

        assertNotNull(uid);
    }

    @Test
    public void updateGroupShouldReturnUidWhenGivedValidParameters() {
        updateXmlConnector.create(ObjectClass.GROUP, createAttributesGroup(), null);
        Set<Attribute> attributes = new HashSet<Attribute>();

        attributes.add(AttributeBuilder.build("__DESCRIPTION__", "this is description updated"));
        attributes.add(AttributeBuilder.build("__SHORT_NAME__", "tidu"));

        Uid uid = updateXmlConnector.update(ObjectClass.GROUP, new Uid(GROUP_NAME), attributes, null);
        assertNotNull(uid);
    }

    @Test(expected = NullPointerException.class)
    public void updateEntryWithNullShouldCastNullPointerException() {
        updateXmlConnector.update(null, null, null, null);
    }

    @Test
    public void deleteAccountQueryShouldReturnZero() {
        deleteXmlConnector.create(ObjectClass.ACCOUNT, createAttributesAccount(), null);

        deleteXmlConnector.delete(ObjectClass.ACCOUNT, new Uid(ACCOUNT_FIRST_NAME), null);

        TestResultsHandler r = new TestResultsHandler();
        deleteXmlConnector.executeQuery(ObjectClass.ACCOUNT, null, r, null);
        assertEquals(0, r.getSumResults());
    }

    @Test
    public void deleteGroupQueryShouldReturnZero() {
        deleteXmlConnector.create(ObjectClass.GROUP, createAttributesGroup(), null);

        deleteXmlConnector.delete(ObjectClass.GROUP, new Uid(GROUP_NAME), null);

        TestResultsHandler r = new TestResultsHandler();
        deleteXmlConnector.executeQuery(ObjectClass.GROUP, null, r, null);
        assertEquals(0, r.getSumResults());
    }

    @Test(expected = NullPointerException.class)
    public void deleteAccountWithNullShouldCastNullPointerException() {
        deleteXmlConnector.delete(null, null, null);
    }

    @Test
    public void authenticateShouldReturnUid(){
        char[] chars = {'A', 'B', 'C', 'D'};
        Uid uid = queryXmlConnector.authenticate(ObjectClass.ACCOUNT, ACCOUNT_NAME , new GuardedString(chars), null);

        assertEquals("e1c0a42b-5c6e-4fe9-b319-4c3aea94a5ee", uid.getUidValue());
        
    }

    private Set<Attribute> createAttributesAccount() {
        Set<Attribute> set = new HashSet<Attribute>();

        set.add(AttributeBuilder.build("__NAME__", ACCOUNT_FIRST_NAME));
        set.add(AttributeBuilder.build("lastname", LAST_NAME));

        char[] chars = {'A', 'B', 'C', 'D'};
        set.add(AttributeBuilder.buildPassword(new GuardedString(chars)));
        set.add(AttributeBuilder.build("address", "Adressroad 12"));
        return set;
    }

    

    private Set<Attribute> createAttributesGroup() {
        Set<Attribute> attributes = new HashSet<Attribute>();

        attributes.add(AttributeBuilder.build("__NAME__", GROUP_NAME));
        attributes.add(AttributeBuilder.build("__DESCRIPTION__", "The almighty"));
        attributes.add(AttributeBuilder.build("__SHORT_NAME__", "tid"));

        return attributes;
    }

    private class TestResultsHandler implements ResultsHandler {

        int sumResults = 0;

        public boolean handle(ConnectorObject arg0) {
            sumResults++;
            return true;
        }

        public int getSumResults() {
            return sumResults;
        }
    }
}
