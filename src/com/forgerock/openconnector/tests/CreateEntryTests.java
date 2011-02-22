package com.forgerock.openconnector.tests;

import com.forgerock.openconnector.xml.*;
import com.forgerock.openconnector.xsdparser.SchemaParser;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

public class CreateEntryTests {

    // Object types
    public static final String ACCOUNT_TYPE = "__ACCOUNT__";
    public static final String GROUP_TYPE =  "__GROUP__";
    public static final String OPEN_ICF_CONTAINER_TYPE = "OpenICFContainer";

    // ICF attribute fields
    public static final String ATTR_UID = Uid.NAME;
    public static final String ATTR_NAME = Name.NAME;
    public static final String ATTR_PASSWORD = "__PASSWORD__";
    public static final String ATTR_LAST_LOGIN_DATE = "__LAST_LOGIN_DATE__";
    public static final String ATTR_DESCRIPTION = "__DESCRIPTION__";
    public static final String ATTR_DISABLE_DATE = "__DISABLE_DATE__";
    public static final String ATTR_ENABLE_DATE = "__ENABLE_DATE__";
    public static final String ATTR_ENABLE = "__ENABLE__";
    public static final String ATTR_GROUPS = "__GROUPS__";
    public static final String ATTR_SHORT_NAME = "__SHORT_NAME__";

    // Account attribute fields
    public static final String ATTR_ACCOUNT_FIRST_NAME = "firstname";
    public static final String ATTR_ACCOUNT_LAST_NAME = "lastname";
    public static final String ATTR_ACCOUNT_EMAIL = "email";
    public static final String ATTR_ACCOUNT_ADDRESS = "address";
    public static final String ATTR_ACCOUNT_EMPLOYEE_NUMBER = "employee-number";
    public static final String ATTR_ACCOUNT_EMPLOYEE_TYPE = "employee-type";
    public static final String ATTR_ACCOUNT_SECRET_ANSWER = "password-secret-answer";
    public static final String ATTR_ACCOUNT_IS_DELETED = "is-deleted";
    public static final String ATTR_ACCOUNT_PHOTO = "jpegPhoto";
    public static final String ATTR_ACCOUNT_LAST_LOGOFF_DATE = "last-logoff";
    public static final String ATTR_ACCOUNT_CREATED_TIMESTAMP = "account-created-timestamp";
    public static final String ATTR_ACCOUNT_MS_EMPLOYED = "ms-employed";
    public static final String ATTR_ACCOUNT_FIRST_LETTER_LAST_NAME = "lastname-first-letter";
    public static final String ATTR_ACCOUNT_GENDER = "gender";
    public static final String ATTR_ACCOUNT_HOURLY_WAGE = "hourly-wage";
    public static final String ATTR_ACCOUNT_OVERTIME_COMISSION = "overtime-commission";
    public static final String ATTR_ACCOUNT_AVERAGE_WAGE = "avg-wage";
    public static final String ATTR_ACCOUNT_OFFICE_SQUARE_FEET = "office-square-feet";
    public static final String ATTR_ACCOUNT_AGE = "age";
    public static final String ATTR_ACCOUNT_YEARS_EMPLOYED = "years-employed";
    public static final String ATTR_ACCOUNT_SIXTH_SENSE = "has-sixth-sense";
    public static final String ATTR_ACCOUNT_PERMANTENT_EMPLOYEE = "permanent-employee";
    public static final String ATTR_ACCOUNT_YEARLY_WAGE = "yearly-wage";
    public static final String ATTR_ACCOUNT_MAX_STORAGE = "max-storage";
    public static final String ATTR_ACCOUNT_USER_CERTIFICATE = "userCertificate";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Test filepaths
    private static final String XML_FILE_PATH = "test/xml_store/test.xml";
    private static final String XSD_FILE_PATH = "test/xml_store/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4.xsd";

    private static XMLHandler handler;

    @Before
    public void init() {
        XMLConfiguration config = new XMLConfiguration();
        config.setXmlFilePath(XML_FILE_PATH);
        config.setXsdFilePath(XSD_FILE_PATH);
        SchemaParser parser = new SchemaParser(XMLConnector.class, config.getXsdFilePath());
        handler = new XMLHandlerImpl(config, parser.parseSchema(), parser.getXsdSchema());
    }

    @After
    public void destroy() {
        File xmlFile = new File(XML_FILE_PATH);

        if (xmlFile.exists())
            xmlFile.delete();
    }

    @Test
    public void WithNonSupportedTypeShouldThrowException() {
        final String objectType = "NonExistingObject";
        final String expectedErrorMessage = objectType + " is not supported.";
        
        ObjectClass objClass = new ObjectClass(objectType);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);
    
        handler.create(objClass, null);
    }

    @Test
    public void withoutNameAttributeDefinedShouldThrowException() {
        final String expectedErrorMessage = Name.NAME + " must be defined.";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.create(ObjectClass.ACCOUNT, null);
    }

    @Test
    public void containingAttributesFlaggedAsNonCreateableShouldThrowException() {
        final String expectedErrorMessage = ATTR_ACCOUNT_IS_DELETED + " is not a creatable field.";

        Set<Attribute> attrSet = getRequiredAttributes();
        attrSet.add(AttributeBuilder.build(ATTR_ACCOUNT_IS_DELETED, true));
        
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.create(ObjectClass.ACCOUNT, attrSet);
    }
    
    @Test
    public void withMissingRequiredFieldShouldThrowException() {
        final String expectedErrorMessage = "Missing required field: " + ATTR_PASSWORD;

        Map<String, Attribute> requiredMap = convertToAttributeMap(getRequiredAttributes());
        requiredMap.remove(ATTR_PASSWORD);

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.create(ObjectClass.ACCOUNT, convertToAttributeSet(requiredMap));
    }
    
    @Test
    public void withBlankRequiredFieldShouldThrowException() {
        final String expectedErrorMessage = "Parameter '" + ATTR_PASSWORD + "' must not be blank.";

        Map<String, Attribute> requiredMap = convertToAttributeMap(getRequiredAttributes());
        requiredMap.remove(ATTR_PASSWORD);
        requiredMap.put(ATTR_PASSWORD, AttributeBuilder.build(ATTR_PASSWORD, new GuardedString(new String("").toCharArray())));

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.create(ObjectClass.ACCOUNT, convertToAttributeSet(requiredMap));
    }

    @Test
    public void withIllegalAttributeTypeShouldThrowException() {
        final String expectedErrorMessage = ATTR_ACCOUNT_FIRST_NAME + " contains invalid type. Value(s) should be of type java.lang.String";

        Set<Attribute> attrSet = getRequiredAttributes();

        // Expected type is String
        attrSet.add(AttributeBuilder.build(ATTR_ACCOUNT_FIRST_NAME, 20.0));

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.create(ObjectClass.ACCOUNT, attrSet);
    }

    @Test
    public void shouldReturnUid() {
        Uid insertedUid = handler.create(ObjectClass.ACCOUNT, getRequiredAttributes());
        assertNotNull(insertedUid.getUidValue());
    }

    @Test
    public void shouldReturnNameAsUidWhenUidIsNotImplementedInSchema() {
        Set<Attribute> attrSet = new HashSet<Attribute>();
        attrSet.add(AttributeBuilder.build(ATTR_NAME, "test-group"));
        attrSet.add(AttributeBuilder.build(ATTR_DESCRIPTION, "Rulers of the world!"));
        attrSet.add(AttributeBuilder.build(ATTR_SHORT_NAME, "test"));

        Uid insertedUid = handler.create(ObjectClass.GROUP, attrSet);
        assertEquals(insertedUid.getUidValue(), AttributeUtil.getNameFromAttributes(attrSet).getNameValue());
    }

    @Test
    public void shouldReturnRandomGeneratedUidWhenUidIsImplementedInSchema() {
        Name name = AttributeUtil.getNameFromAttributes(getRequiredAttributes());
        Set<Attribute> attrSet = getRequiredAttributes();

        Uid uid = handler.create(ObjectClass.ACCOUNT, attrSet);
        assertNotSame(uid, name.getNameValue());
    }

    @Test
    public void withExistingIdShouldThrowException() {
        final String uid = AttributeUtil.getNameFromAttributes(getRequiredAttributes()).getNameValue();
        final String expectedErrorMessage = "Could not create entry. An entry with the " + Uid.NAME + " of " + uid + " already exists.";
        handler.create(ObjectClass.ACCOUNT, getRequiredAttributes());

        thrown.expect(AlreadyExistsException.class);
        thrown.expectMessage(expectedErrorMessage);

        handler.create(ObjectClass.ACCOUNT, getRequiredAttributes());
    }

    private Set<Attribute> getRequiredAttributes() {
        Set<Attribute> requiredAttrSet = new HashSet<Attribute>();
        requiredAttrSet.add(AttributeBuilder.build(ATTR_NAME, "vaderUID"));
        requiredAttrSet.add(AttributeBuilder.build(ATTR_PASSWORD, new GuardedString(new String("secret").toCharArray())));
        requiredAttrSet.add(AttributeBuilder.build(ATTR_ACCOUNT_LAST_NAME, "Vader"));

        return requiredAttrSet;
    }

    private Map<String, Attribute> convertToAttributeMap(Set<Attribute> attrSet) {
        return new HashMap<String, Attribute>(AttributeUtil.toMap(attrSet));
    }

    private Set<Attribute> convertToAttributeSet(Map<String, Attribute> attrMap) {
        return new HashSet<Attribute>(attrMap.values());
    }
}