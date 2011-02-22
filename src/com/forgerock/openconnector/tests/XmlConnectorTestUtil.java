package com.forgerock.openconnector.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

public class XmlConnectorTestUtil {

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

    public static Set<Attribute> getRequiredAccountAttributes() {
        Set<Attribute> requiredAttrSet = new HashSet<Attribute>();
        requiredAttrSet.add(AttributeBuilder.build(ATTR_NAME, "vaderUID"));
        requiredAttrSet.add(AttributeBuilder.buildPassword(new String("secret").toCharArray()));
        requiredAttrSet.add(AttributeBuilder.build(ATTR_ACCOUNT_LAST_NAME, "Vader"));

        return requiredAttrSet;
    }

    public static Map<String, Attribute> convertToAttributeMap(Set<Attribute> attrSet) {
        return new HashMap<String, Attribute>(AttributeUtil.toMap(attrSet));
    }

    public static Set<Attribute> convertToAttributeSet(Map<String, Attribute> attrMap) {
        return new HashSet<Attribute>(attrMap.values());
    }
}
