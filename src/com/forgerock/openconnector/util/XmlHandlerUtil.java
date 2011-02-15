/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.util;

import com.forgerock.openconnector.xml.*;
import java.util.EnumSet;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.PredefinedAttributeInfos;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.spi.operations.AuthenticateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.ScriptOnConnectorOp;
import org.identityconnectors.framework.spi.operations.ScriptOnResourceOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.SyncOp;
import org.identityconnectors.framework.spi.operations.TestOp;

/**
 *
 * @author slogum
 */
public class XmlHandlerUtil {


    // List of all supported classes in the framework
    public static final String STRING = "String";
    public static final String INT_PRIMITIVE = "int";
    public static final String INTEGER = "Integer";
    public static final String LONG_PRIMITIVE = "long";
    public static final String LONG = "Long";
    public static final String BOOLEAN_PRIMITIVE = "boolean";
    public static final String BOOLEAN = "Boolean";
    public static final String DOUBLE_PRIMITIVE = "double";
    public static final String DOUBLE = "Double";
    public static final String FLOAT_PRIMITIVE = "float";
    public static final String FLOAT = "Float";
    public static final String CHAR_PRIMITIVE = "char";
    public static final String CHARACTER = "Character";
    public static final String BIG_INTEGER = "BigInteger";
    public static final String BIG_DECIMAL = "BigDecimal";
    public static final String GUARDED_STRING = "GuardedString";
    public static final String GUARDED_BYTE_ARRAY = "GuardedByteArray";
    public static final String BYTE_ARRAY = "byte[]";

    // List of all supported operations
    public static final String CREATE = "CREATE";
    public static final String AUTHENTICATE = "AUTHENTICATE";
    public static final String DELETE = "DELETE";
    public static final String RESOLVEUSERNAME = "RESOLVEUSERNAME";
    public static final String SCHEMA = "SCHEMA";
    public static final String SCRIPTONCONNECTOR = "SCRIPTONCONNECTOR";
    public static final String SCRIPTONRESOURCE = "SCRIPTONRESOURCE";
    public static final String SEARCH = "SEARCH";
    public static final String SYNC = "SYNC";
    public static final String TEST = "TEST";
    public static final String UPDATEATTRIBUTEVALUES = "UPDATEATTRIBUTEVALUES";
    public static final String UPDATE = "UPDATE";

    // List of all flags
    public static final String NOT_CREATABLE = "NOT_CREATABLE";
    public static final String NOT_UPDATABLE = "NOT_UPDATABLE";
    public static final String NOT_READABLE = "NOT_READABLE";
    public static final String NOT_RETURNED_BY_DEFAULT = "NOT_RETURNED_BY_DEFAULT";



    public static Schema createHardcodedSchema() {
        SchemaBuilder schemaBuilder = new SchemaBuilder(XMLConnector.class);
        Schema schema = null;
        ObjectClassInfoBuilder ocBuilder;

        //ACCOUNT
        ocBuilder = new ObjectClassInfoBuilder();
        ocBuilder.setType(ObjectClass.ACCOUNT_NAME);
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Uid.NAME, String.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("firstname", String.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("lastname", String.class, EnumSet.of(Flags.REQUIRED)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("email", String.class, EnumSet.of(Flags.MULTIVALUED)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("address", String.class, EnumSet.of(Flags.MULTIVALUED)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("employee-number", String.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("employee-type", String.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("__PASSWORD__", org.identityconnectors.common.security.GuardedString.class, EnumSet.of(Flags.NOT_READABLE, Flags.NOT_RETURNED_BY_DEFAULT)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("secret-pin", org.identityconnectors.common.security.GuardedString.class, EnumSet.of(Flags.NOT_READABLE, Flags.NOT_RETURNED_BY_DEFAULT)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("password-secret-answer", org.identityconnectors.common.security.GuardedByteArray.class, EnumSet.of(Flags.NOT_RETURNED_BY_DEFAULT)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("is-deleted", boolean.class, EnumSet.of(Flags.NOT_CREATABLE, Flags.NOT_UPDATEABLE)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("jpegPhoto", byte[].class, EnumSet.of(Flags.NOT_RETURNED_BY_DEFAULT)));
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.LAST_LOGIN_DATE);
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("last-logoff", java.math.BigInteger.class, EnumSet.of(Flags.NOT_CREATABLE, Flags.NOT_UPDATEABLE)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("account-created-timestamp", long.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("ms-employed", java.lang.Long.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("lastname-first-letter", char.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("gender", java.lang.Character.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("hourly-wage", double.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("overtime-commission", java.lang.Double.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("avg-wage", float.class, EnumSet.of(Flags.NOT_CREATABLE)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("office-square-feet", java.lang.Float.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("age", int.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("years-employed", java.lang.Integer.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("has-sixth-sense", boolean.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("permanent-employee", java.lang.Boolean.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("yearly-wage", java.math.BigInteger.class, EnumSet.of(Flags.NOT_READABLE, Flags.NOT_RETURNED_BY_DEFAULT)));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("max-storage", java.math.BigDecimal.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("userCertificate", byte[].class, EnumSet.of(Flags.NOT_RETURNED_BY_DEFAULT)));
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("__DISABLE_DATE__", long.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("__ENABLE_DATE__", long.class));
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("__ENABLE__", boolean.class));
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.GROUPS);
        schemaBuilder.defineObjectClass(ocBuilder.build());
        
        // GROUP
        ocBuilder = new ObjectClassInfoBuilder();
        ocBuilder.setType(ObjectClass.GROUP_NAME);
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.SHORT_NAME);
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("OpenICFContainer"));
        ObjectClassInfo objectClassInfo = ocBuilder.build();
        schemaBuilder.defineObjectClass(objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(AuthenticateOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(SchemaOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(ScriptOnConnectorOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(ScriptOnResourceOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(SearchOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(SyncOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(TestOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(DeleteOp.class, objectClassInfo);

        
        // ORGANIZATIONUNIT
        ocBuilder = new ObjectClassInfoBuilder();
        ocBuilder.setType("OrganizationUnit");
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
        ocBuilder.addAttributeInfo(PredefinedAttributeInfos.SHORT_NAME);
        ocBuilder.addAttributeInfo(AttributeInfoBuilder.build("OpenICFContainer"));
        objectClassInfo = ocBuilder.build();
        schemaBuilder.defineObjectClass(objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(AuthenticateOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(SchemaOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(ScriptOnConnectorOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(ScriptOnResourceOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(SearchOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(SyncOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(TestOp.class, objectClassInfo);
        schemaBuilder.removeSupportedObjectClass(DeleteOp.class, objectClassInfo);
        
        schema = schemaBuilder.build();
        return schema;
    }
}
