/*
 *
 * Copyright (c) 2010 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * $Id$
 */
package org.forgerock.openicf.usd;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.idm.logging.trace.Trace;
import com.sun.idm.logging.trace.TraceManager;
import org.forgerock.openicf.usd.ca.ArrayOfInt;
import org.forgerock.openicf.usd.ca.ArrayOfString;
import org.forgerock.openicf.usd.ca.ListResult;
import org.forgerock.openicf.usd.ca.uds.UDSAttribute;
import org.forgerock.openicf.usd.ca.uds.UDSObject;
import org.forgerock.openicf.usd.ca.USDMessages;
import org.forgerock.openicf.usd.ca.USDWebService;
import org.forgerock.openicf.usd.ca.USDWebServiceSoap;
import org.forgerock.openicf.usd.ca.uds.UDSObjectList;
import com.waveset.msgcat.ErrorMessage;
import com.waveset.msgcat.Severity;
import com.waveset.object.AccountAttributeType;
import com.waveset.object.GenericObject;
import com.waveset.object.ObjectCache;
import com.waveset.object.Resource;
import com.waveset.object.ResourceAttribute;
import com.waveset.object.ResourceInfo;
import com.waveset.object.WSAttribute;
import com.waveset.object.WSUser;
import com.waveset.object.WavesetResult;
import com.waveset.util.EncryptedData;
import com.waveset.util.WavesetException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;
import org.xml.sax.InputSource;

public class CAServicedeskResourceAdapter extends ResourceAdapterBase {

    public static final String code_id = "$Id: CAServicedeskResourceAdapter.java,v 1.5 2010/02/18";
    private static final String CLASS = CAServicedeskResourceAdapter.class.getName();
    private static final Trace TRACE = TraceManager.getTrace(CLASS);
    /**
     * Default Qname
     */
    private static final QName SERVICE_NAME = new QName("http://www.ca.com/UnicenterServicePlus/ServiceDesk", "USD_WebService");
    /**
     * {@link UDSObjectList} and {@link UDSObject}  Unmarshaller
     */
    private static Unmarshaller unmarshaller = null;
    private static Marshaller marshaller = null;
    private boolean connectionIsVerified = false;

    static {
        try {
            JAXBContext jc = JAXBContext.newInstance(UDSObject.class.getPackage().getName());
            marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            unmarshaller = jc.createUnmarshaller();
        } catch (JAXBException ex) {
            TRACE.caught1("STATIC_INITIALISE", ex);
        }
    }

    // Class constructors
    // change-value-here for resource adapter name - no other modifications
    // needed here
    public CAServicedeskResourceAdapter(Resource res, ObjectCache cache) {
        super(res, cache);
    }

    // CAServicedeskResourceAdapter()
    public CAServicedeskResourceAdapter() {
        super();
    }
    // CAServicedeskResourceAdapter()
    private static Map<String, USDWebService> _serviceCacheMap = new HashMap<String, USDWebService>(1);
    private static Map<String, Integer> _sessionCacheMap = new HashMap<String, Integer>(1);
    private static Map<String, List<USDWebServiceSoap>> _connections = new HashMap<String, List<USDWebServiceSoap>>(1);
    /**
     * Resource attributes are the common names that will be visible in the
     * GUI that will prompt the user for values. For example, the GUI will
     * show "endpoint" and have a box next to it for the user to enter
     * a endpoint name.
     * <p/>
     * The name of the endpoint used to communicate with the resource.
     */
    public static final String RA_ENDPOINT = "EndPoint";
    /**
     * The account name used to connect to the resource.
     */
    public static final String RA_USER = "User";
    public static final String RA_PASSWORD = "Password";
    /**
     * The policy which to use with loginService, it's optional for version 11.
     * default value is "DEFAULT"
     */
    public static final String RA_POLICY = "Policy";
    /**
     * Additional resource attributes that need to be defined.
     */
    public static final String RA_WSDL_URL = "WSDL_URL";
    /**
     * Contact attributes
     */
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /////
    /////   Account Attributes
    /////
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /**
     * As is Simple Integer
     * No Schema
     */
    public static final String AA_UUID_ADMIN_ORG = "admin_org";
    //ERROR public static final String AA_ALIAS = "alias";
    /**
     * Read only
     */
    public static final String AA_ALT_PHONE = "alt_phone";
    /**
     * as is simple String
     */
    public static final String AA_CONTACT_NUM = "contact_num";
    /**
     * as is simple String
     */
    public static final String AA_NOTES = "notes";
    /**
     * ignore
     */
    public static final String AA_UUID_COMPANY = "company";
    /**
     * Required 2307
     * as is integer
     */
    public static final String AA_TYPE = "type";
    /**
     * Read only
     */
    public static final String AA_UUID_ID = "id";
    /**
     * ignore
     */
    public static final String AA_BILLING_CODE = "billing_code";
    /**
     * ignore
     */
    public static final String AA_CREATE_DATE = "creation_date";
    /**
     * ignore
     */
    public static final String AA_CREATE_USER = "creation_user";
    /**
     * Read only
     */
    public static final String AA_DELETE_TIME = "delete_time";
    /**
     * resolve ref
     * dept.name
     */
    public static final String AA_DEPT = "dept:dept.name";
    /**
     * as is simple String
     */
    public static final String AA_EMAIL_ADDRESS = "email_address";
    /**
     * ignore
     */
    //public static final String AA_EXCLUDE_REGISTRATION = "exclude_registration";
    /**
     * Read only
     */
    public static final String AA_FAX_PHONE = "fax_phone";
    /**
     * string
     */
    public static final String AA_FIRST_NAME = "first_name";
    //ERROR public static final String AA_FLOOR_LOCATION = "floor_location";
    public static final String AA_DELETE_FLAG = "delete_flag";
    //ERROR public static final String AA_JOB_FUNCTION = "job_function";
    /**
     * string
     */
    public static final String AA_POSITION = "position";
    /**
     * as is simple String
     * REQUIRED
     */
    public static final String AA_LAST_NAME = "last_name";
    /**
     * ignore
     */
    public static final String AA_LAST_MOD = "last_mod";
    /**
     * Read Only
     */
    public static final String AA_LAST_MOD_BY = "last_mod_by";
    /**
     * Read Only
     */
    public static final String AA_UUID_LOCATION = "location";
    //ERROR public static final String AA_MAIL_STOP = "mail_stop";
    /**
     * as is simple String
     */
    public static final String AA_MIDDLE_NAME = "middle_name";
    /**
     * as is simple String
     */
    public static final String AA_MOBILE_PHONE = "mobile_phone";
    /**
     * object type = org
     * key atribute "name"
     *
     */
    public static final String AA_UUID_ORGANIZATION = "org:organization.name";
    /**
     * ignore
     */
    public static final String AA_PEMAIL_ADDRESS = "pemail_address";
    //public static final String AA_BEEPER_PHONE = "beeper_phone";
    /**
     * Read only
     */
    public static final String AA_PHONE_NUMBER = "phone_number";
    //ERROR public static final String AA_ROOM_LOCATION = "room_location";
    /**
     * String
     */
    public static final String AA_UUID_SUPERVISOR_CONTACT = "cnt:supervisor_contact_uuid.userid";
    /**
     * accountId => short_name
     */
    public static final String AA_USERID = "userid";
    /**
     * Read Only
     */
    public static final String AA_VERSION_NUMBER = "version_number";
    /**
     * Required default 2405
     * as is Integer
     */
    public static final String AA_ACCESS_TYPE = "access_type";
    /**
     * As is Integer full ignore
     */
    public static final String AA_AVAILABLE = "available";
    public static final String AA_NOTIFY_METHOD1 = "notify_method1";
    public static final String AA_NOTIFY_METHOD2 = "notify_method2";
    public static final String AA_NOTIFY_METHOD3 = "notify_method3";
    public static final String AA_NOTIFY_METHOD4 = "notify_method4";
    /**
     * ignore
     */
    public static final String AA_DOMAIN = "domain";
    /**
    All these attributes are not defined

    public static final String AA_EMAIL_SERVICE = "c_email_service";
    public static final String AA_UUID_NX_REF1 = "c_nx_ref_1";
    public static final String AA_UUID_NX_REF2 = "c_nx_ref_2";
    public static final String AA_UUID_NX_REF3 = "c_nx_ref_3";
    public static final String AA_NXSTRING1 = "c_nx_string1";
    public static final String AA_NXSTRING2 = "c_nx_string2";
    public static final String AA_NXSTRING3 = "c_nx_string3";
    public static final String AA_NXSTRING4 = "c_nx_string4";
    public static final String AA_NXSTRING5 = "c_nx_string5";
    public static final String AA_NXSTRING6 = "c_nx_string6";
    public static final String AA_UUID_PARENT = "c_parent";
    public static final String AA_VAL_REQ = "c_val_req";
     */
    /**
     * ignored
     */
    public static final String AA_SCHEDULE = "schedule";
    /**
     * ignored
     */
    public static final String AA_SERVICE_TYPE = "service_type";
    /**
     * ignore
     */
    public static final String AA_TIMEZONE = "timezone";
    /**
     * ignore
     */
    public static final String AA_UUID_VENDOR = "vendor";
    /**
     * ignore
     */
    public static final String AA_NOTIFY_WS1 = "notify_ws1";
    public static final String AA_NOTIFY_WS2 = "notify_ws2";
    public static final String AA_NOTIFY_WS3 = "notify_ws3";
    public static final String AA_NOTIFY_WS4 = "notify_ws4";
    public static final String AA_GLOBAL_QUEUE_ID = "global_queue_id";
    /**
     * as is String
     */
    public static final String AA_LDAP_DN = "ldap_dn";
    /**
     * object type = nr
     * custom attribute is "name"
     * read only
     */
    public static final String AA_CUSTOM_ZARBEIDPLASS = "nr:zarbeidplass.name";
    /**
     * String normal
     */
    public static final String AA_CUSTOM_ZTILKNYTNING = "ztilknytning";
    /**
     * 
     * String AS IS
     */
    public static final String AA_CUSTOM_ZSTILLINGSANDEL = "zstillingsandel";
    /**
     * Pass thru authentication variables.
     * Pass thru authentication is a process where a user or administrator
     * can provide an account name and password which will be sent to the
     * resource to be validated.
     * The variables below are used to define the names of the various
     * pieces of the Lighthouse implementation.
     */
    public static final String LOGIN_USER = "CAServicedesk_login_user";
    public static final String LOGIN_PASSWORD = "CAServicedesk_login_password";
    public static final String DISPLAY_USER = "User Id:";
    public static final String DISPLAY_PASSWORD = "Password";
    public static final String RESOURCE_NAME = "CAServicedesk";
    public static final String RESOURCE_TYPE = "CAServicedesk";
    public static final String OBJECT_TYPE_CONTACT = "cnt";
    public static final String OBJECT_TYPE_CONTACT_METHOD = "cmth";
    private static final String CA_ACTIVE = "actbool:4551";
    private static final String CA_INCATIVE = "actbool:4552";
    private static final String CA_TRUE = "bool:200";
    private static final String CA_FALSE = "bool:201";
    // change-value-here for the xml definition for the resource.
    /**
     * The XML definition of a resource needs to contain the resource name
     * and all attributes the resource will have. There are several sections
     * to the resource prototype xml
     * ResourceAttributes: These attributes define the resource and will be
     * used by the waveset UI to configure the resource. Some of the
     * keywords for ResourceAttributes are:
     * type - currently we only support string types
     * multi - specifies if multiple values can be accepted for the
     * attribute. If true, a multi-line box will be displayed.
     * <p/>
     * AccountAttributes: These attributes define the default user schema map
     * for basic user attributes. The schema maps waveset user attribute
     * names to resource attribute names. The waveset attribute names
     * may be in common among several resource adapters.
     * ObjectRef types defined in AccountAttributes should only be used
     * for the special types:
     * accountId, firstname, lastname, fullname, email, or password
     * <p/>
     * Template: Defines how the accountname for the user will be built.
     * The account names are commonly of 2 forms. One form is just the
     * accountId. The other is a complete distinguished name of the
     * user in the form: cn=accountId,ou=sub-org,ou=org,o=company
     * <p/>
     * LoginConfigEntry: Defines the values for support of Pass Thru
     * Authentication for this resource.
     * Make sure you replace the value of displayName with the appropriate
     * message key or string.
     */
    static final String prototypeXml =
            "<Resource name='" + RESOURCE_NAME + "'\n" +
            "          class='" + CLASS + "'\n" +
            "          typeString='" + RESOURCE_TYPE + "'\n" +
            "          typeDisplayString='" + RESOURCE_TYPE + "'>\n" +
            "  <ResourceAttributes>\n" +
            "    <ResourceAttribute name='" + RA_WSDL_URL + "'\n" +
            "                       displayName='" + USDMessages.RESATTR_WSDL_URL + "'\n" +
            "                       type='string'\n" +
            "                       multi='false'\n" +
            "                       description='" + USDMessages.RESATTR_WSDL_URL_HELP + "'/>\n" +
            "    <ResourceAttribute name='" + RA_USER + "'\n" +
            "                       displayName='" + USDMessages.RESATTR_USER + "'\n" +
            "                       type='string'\n" +
            "                       multi='false'\n" +
            "                       description='" + USDMessages.RESATTR_USER_HELP + "'/>\n" +
            "    <ResourceAttribute name='" + RA_PASSWORD + "'\n" +
            "                       displayName='" + USDMessages.RESATTR_PASSWORD + "'\n" +
            "                       type='encrypted'\n" +
            "                       multi='false'\n" +
            "                       description='" + USDMessages.RESATTR_PASSWORD_HELP + "'/>\n" +
            "    <ResourceAttribute name='" + RA_ENDPOINT + "'\n" +
            "                       displayName='" + USDMessages.RESATTR_ENDPOINT + "'\n" +
            "                       type='string'\n" +
            "                       multi='false'\n" +
            "                       description='" + USDMessages.RESATTR_ENDPOINT_HELP + "'/>\n" +
            "    <ResourceAttribute name='" + RA_POLICY + "'\n" +
            "                       displayName='" + USDMessages.RESATTR_POLICY + "'\n" +
            "                       type='string'\n" +
            "                       multi='false'\n" +
            "                       description='" + USDMessages.RESATTR_POLICY_HELP + "'/>\n" +
            "    <ResourceAttribute name='" + RA_BLOCKCOUNT + "'\n" +
            "                       displayName='" + RAMessages.RESATTR_BLOCKCOUNT + "'\n" +
            "                       type='string'\n" +
            "                       multi='false'\n" +
            "                       description='RESATTR_HELP_34'" +
            "                       value='100'/>\n" +
            "  </ResourceAttributes>\n" +
            //
            // Specify the default identity template
            //
            "  <Template>\n" +
            "    <AttrDef name='accountId'\n" +
            "             type='string' />\n" +
            "  </Template>\n" +
            //
            // Specify account attributes and default schema mappings
            //
            "  <AccountAttributeTypes>\n" +
            "    <AccountAttributeType name='accountId'\n" +
            "                          mapName='" + AA_USERID + "'\n" +
            "                          mapType='string'\n" +
            "                          required='true'>\n" +
            "      <AttributeDefinitionRef>\n" +
            "        <ObjectRef type='AttributeDefinition'\n" +
            "                   name='accountId'/>\n" +
            "      </AttributeDefinitionRef>\n" +
            "    </AccountAttributeType>\n" +
            "    <AccountAttributeType name='" + AA_UUID_ADMIN_ORG + "'\n" +
            "                          mapName='" + AA_UUID_ADMIN_ORG + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_ALT_PHONE + "'\n" +
            "                          mapName='" + AA_ALT_PHONE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTES + "'\n" +
            "                          mapName='" + AA_NOTES + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_COMPANY + "'\n" +
            "                          mapName='" + AA_UUID_COMPANY + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_TYPE + "'\n" +
            "                          mapName='" + AA_TYPE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_ID + "'\n" +
            "                          mapName='" + AA_UUID_ID + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_BILLING_CODE + "'\n" +
            "                          mapName='" + AA_BILLING_CODE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_CREATE_DATE + "'\n" +
            "                          mapName='" + AA_CREATE_DATE + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_CREATE_USER + "'\n" +
            "                          mapName='" + AA_CREATE_USER + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_DELETE_TIME + "'\n" +
            "                          mapName='" + AA_DELETE_TIME + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='dept'\n" +
            "                          mapName='" + AA_DEPT + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_EMAIL_ADDRESS + "'\n" +
            "                          mapName='" + AA_EMAIL_ADDRESS + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_FAX_PHONE + "'\n" +
            "                          mapName='" + AA_FAX_PHONE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_FIRST_NAME + "'\n" +
            "                          mapName='" + AA_FIRST_NAME + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            //            "    <AccountAttributeType name='" + AA_DELETE_FLAG + "'\n" +
            //            "                          mapName='" + AA_DELETE_FLAG + "'\n" +
            //            "                          mapType='string'\n" +
            //            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_POSITION + "'\n" +
            "                          mapName='" + AA_POSITION + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_LAST_NAME + "'\n" +
            "                          mapName='" + AA_LAST_NAME + "'\n" +
            "                          mapType='string'\n" +
            "                          required='true'/>\n" +
            "    <AccountAttributeType name='" + AA_LAST_MOD + "'\n" +
            "                          mapName='" + AA_LAST_MOD + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_LAST_MOD_BY + "'\n" +
            "                          mapName='" + AA_LAST_MOD_BY + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_LOCATION + "'\n" +
            "                          mapName='" + AA_UUID_LOCATION + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_MIDDLE_NAME + "'\n" +
            "                          mapName='" + AA_MIDDLE_NAME + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_MOBILE_PHONE + "'\n" +
            "                          mapName='" + AA_MOBILE_PHONE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='organozation'\n" +
            "                          mapName='" + AA_UUID_ORGANIZATION + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_PEMAIL_ADDRESS + "'\n" +
            "                          mapName='" + AA_PEMAIL_ADDRESS + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_CUSTOM_ZSTILLINGSANDEL + "'\n" +
            "                          mapName='" + AA_CUSTOM_ZSTILLINGSANDEL + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_PHONE_NUMBER + "'\n" +
            "                          mapName='" + AA_PHONE_NUMBER + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_CUSTOM_ZTILKNYTNING + "'\n" +
            "                          mapName='" + AA_CUSTOM_ZTILKNYTNING + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='supervisor'\n" +
            "                          mapName='" + AA_UUID_SUPERVISOR_CONTACT + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_CONTACT_NUM + "'\n" +
            "                          mapName='" + AA_CONTACT_NUM + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_VERSION_NUMBER + "'\n" +
            "                          mapName='" + AA_VERSION_NUMBER + "'\n" +
            "                          mapType='string'\n" +
            "                          readOnly='true'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_ACCESS_TYPE + "'\n" +
            "                          mapName='" + AA_ACCESS_TYPE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_AVAILABLE + "'\n" +
            "                          mapName='" + AA_AVAILABLE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_METHOD1 + "'\n" +
            "                          mapName='" + AA_NOTIFY_METHOD1 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_METHOD2 + "'\n" +
            "                          mapName='" + AA_NOTIFY_METHOD2 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_METHOD3 + "'\n" +
            "                          mapName='" + AA_NOTIFY_METHOD3 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_METHOD4 + "'\n" +
            "                          mapName='" + AA_NOTIFY_METHOD4 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_DOMAIN + "'\n" +
            "                          mapName='" + AA_DOMAIN + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            /*
            "    <AccountAttributeType name='" + AA_ALIAS + "'\n" +
            "                          mapName='" + AA_ALIAS + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_FLOOR_LOCATION + "'\n" +
            "                          mapName='" + AA_FLOOR_LOCATION + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_JOB_FUNCTION + "'\n" +
            "                          mapName='" + AA_JOB_FUNCTION + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_MAIL_STOP + "'\n" +
            "                          mapName='" + AA_MAIL_STOP + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_EXCLUDE_REGISTRATION + "'\n" +
            "                          mapName='" + AA_EXCLUDE_REGISTRATION + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_EMAIL_SERVICE + "'\n" +
            "                          mapName='" + AA_EMAIL_SERVICE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_NX_REF1 + "'\n" +
            "                          mapName='" + AA_UUID_NX_REF1 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_NX_REF2 + "'\n" +
            "                          mapName='" + AA_UUID_NX_REF2 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_NX_REF3 + "'\n" +
            "                          mapName='" + AA_UUID_NX_REF3 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NXSTRING1 + "'\n" +
            "                          mapName='" + AA_NXSTRING1 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NXSTRING2 + "'\n" +
            "                          mapName='" + AA_NXSTRING2 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NXSTRING3 + "'\n" +
            "                          mapName='" + AA_NXSTRING3 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NXSTRING4 + "'\n" +
            "                          mapName='" + AA_NXSTRING4 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NXSTRING5 + "'\n" +
            "                          mapName='" + AA_NXSTRING5 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NXSTRING6 + "'\n" +
            "                          mapName='" + AA_NXSTRING6 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_PARENT + "'\n" +
            "                          mapName='" + AA_UUID_PARENT + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
             */
            "    <AccountAttributeType name='" + AA_SCHEDULE + "'\n" +
            "                          mapName='" + AA_SCHEDULE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_SERVICE_TYPE + "'\n" +
            "                          mapName='" + AA_SERVICE_TYPE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_TIMEZONE + "'\n" +
            "                          mapName='" + AA_TIMEZONE + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='zarbeidplass'\n" +
            "                          mapName='" + AA_CUSTOM_ZARBEIDPLASS + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_UUID_VENDOR + "'\n" +
            "                          mapName='" + AA_UUID_VENDOR + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_WS1 + "'\n" +
            "                          mapName='" + AA_NOTIFY_WS1 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_WS2 + "'\n" +
            "                          mapName='" + AA_NOTIFY_WS2 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_WS3 + "'\n" +
            "                          mapName='" + AA_NOTIFY_WS3 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_NOTIFY_WS4 + "'\n" +
            "                          mapName='" + AA_NOTIFY_WS4 + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_GLOBAL_QUEUE_ID + "'\n" +
            "                          mapName='" + AA_GLOBAL_QUEUE_ID + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    <AccountAttributeType name='" + AA_LDAP_DN + "'\n" +
            "                          mapName='" + AA_LDAP_DN + "'\n" +
            "                          mapType='string'\n" +
            "                          required='false'/>\n" +
            "    </AccountAttributeTypes>\n" +
            "</Resource>\n";

    // Create prototype of the resource
    // No modifications needed here
    public static Resource staticCreatePrototypeResource()
            throws WavesetException {
        Resource res = new Resource(prototypeXml);

        // If CONTINUE_ON_ERROR is supported, make it disabled by default.
        // An administrator has to turn the feature on to get the behavior.
        res.disableFeature(Features.CONTINUE_ON_ERROR);

        return res;
    }

    public Resource createPrototypeResource() throws WavesetException {
        return staticCreatePrototypeResource();
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Account operations
    //
    //////////////////////////////////////////////////////////////////////
    /**
     * This method will return whether the resource adapter supports case
     * insensitive account Ids or not. If the resource does support allow case
     * insensitive names, then this method should be removed as the default
     * implementation, in ResourceAdapterBase, returns true. If the resource
     * supports case sensitive names, then this method should be implemented to
     * return false.
     */
    @Override
    public boolean supportsCaseInsensitiveAccountIds() {
        return true;
    }

    /**
     * This method indicates to the server that this resource natively supports
     * account enable and disable. If the resource does not natively support
     * account enable and disable, then this method should be removed as the
     * default implementation, in ResourceAdapterBase, returns false.
     */
    @Override
    public boolean supportsAccountDisable() {
        return true;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Utilities
    //
    //////////////////////////////////////////////////////////////////////
    private ArrayOfString getReadableAccountAttributeNames() {
        final String METHOD = "getReadableAccountAttributeNames";
        TRACE.entry1(METHOD);
        AccountAttributeType attrTypes[] = _resource.getAccountAttributeTypes();
        ArrayOfString filteredAttrTypes = new ArrayOfString();
        for (int i = 0; i < attrTypes.length; i++) {
            AccountAttributeType attrType = attrTypes[i];
            if (!attrType.isWriteOnly()) {
                if (attrType.getMapName().contains(":")) {
                    filteredAttrTypes.getString().add(attrType.getMapName().substring(attrType.getMapName().indexOf(":") + 1));
                } else {
                    filteredAttrTypes.getString().add(attrType.getMapName());
                }
            }
        }
        if (TRACE.level4(METHOD)) {
            StringBuilder sb = new StringBuilder("<Attributes>");
            for (String attr : filteredAttrTypes.getString()) {
                sb.append("<" + attr + ">");
            }
            sb.append("</Attributes>");
            TRACE.variable4(METHOD, "attributes", sb.toString());
        }
        TRACE.exit1(METHOD);
        return filteredAttrTypes;
    }

    private synchronized <T> T unmarshal(Class<T> UDSClass, String inputString) throws WavesetException {
        final String METHOD = "unmarshal";
        if (TRACE.level3(METHOD)) {
            TRACE.variable(METHOD, "inputString", inputString);
        }
        if (null != inputString) {
            StringReader localStringReader = new StringReader(inputString);
            InputSource localInputSource = new InputSource(localStringReader);
            try {
                return (T) unmarshaller.unmarshal(localInputSource);
            } catch (JAXBException ex) {
                TRACE.caught2(METHOD, ex);
                throw new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_UNMARSHAL_ERROR, inputString), ex);
            } catch (Exception e) {
                TRACE.caught1(METHOD, e);
                throw new WavesetException(e);
            }
        } else {
            WavesetException ex = new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_UNMARSHAL_NULLINPUT));
            TRACE.throwing2(METHOD, ex);
            throw ex;
        }
    }

    private void changeDisabledStatus(WSUser user, boolean disable, WavesetResult result) throws WavesetException {
        final String METHOD = "changeDisabledStatus";
        TRACE.entry1(METHOD);
        String handle = submitGetHandleForUserid(user);
        if (null != handle) {
            UDSObject update = new UDSObject(handle);
            update.addAttribute(new UDSAttribute(AA_DELETE_FLAG, disable ? CA_INCATIVE : CA_ACTIVE));
            ArrayOfString attributes = new ArrayOfString();
            attributes.getString().add(AA_DELETE_FLAG);
            UDSObject resultObj = submitUpdateObject(update, attributes);
            UDSAttribute deleted = resultObj.getAttribute(AA_DELETE_FLAG);
            boolean _disabled = false;
            if (null != deleted) {
                _disabled = "1".equals(deleted.getAttrValue());
            }
            if (_disabled) {
                setDisabled(user, true);
            } else {
                setDisabled(user, false);
            }
        } else {
            result.addError(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_HANDLE_NOT_FOUND, user.getName()));
        }
        TRACE.exit1(METHOD);
    }

    private UDSAttribute resolveAttributeValue(String attrName, String attrValue) throws WavesetException {
        final String METHOD = "resolveAttributeValue";
        TRACE.entry1(METHOD, attrName);
        String objectType = attrName.substring(0, attrName.indexOf(":"));
        String property = attrName.substring(attrName.indexOf(".") + 1);
        String newAttrName = attrName.substring(attrName.indexOf(":") + 1, attrName.indexOf("."));
        if (null != attrValue && attrValue.length() > 0) {
            ArrayOfString attributes = new ArrayOfString();
            attributes.getString().add("id");
            UDSObjectList queryResult = submitDoSelect(objectType, property + " = '" + attrValue + "'", 2, attributes);
            if (queryResult.getUDSObject().size() != 1) {
                WavesetException ex = new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_ATTRIBUTE_RESOLVE, new Object[]{objectType, property, attrValue, queryResult.getUDSObject().size()}));
                TRACE.throwing2(METHOD, ex);
                throw ex;
            }
            UDSObject o = queryResult.getUDSObject().get(0);
            attrValue = o.getHandle();
        }
        TRACE.exit1(METHOD, newAttrName + "->" + attrValue);
        return new UDSAttribute(newAttrName, attrValue);
    }

    private String getEncryptedResourceAttribute(String name)
            throws WavesetException {
        String str = null;
        if (_resource != null) {
            ResourceAttribute ra = _resource.getResourceAttribute(name);
            if (ra != null) {
                Object value = ra.getValue();
                if (value != null) {
                    if (value instanceof EncryptedData) {
                        EncryptedData ed = (EncryptedData) value;
                        str = ed.decryptToString();
                    } else if (value instanceof String) {
                        EncryptedData ed = new EncryptedData();
                        ed.fromString((String) value);
                        str = ed.decryptToString();
                    } else {
                        str = value.toString();
                    }
                }
            }
        }
        return str;
    }

    /**
     * These are methods implemented in sources to get and set attributes.
     * Since this is a source with a resource, just pass the calls through.
     */
    public Object getAttributeValue(String name) throws WavesetException {
        if ("name".equalsIgnoreCase(name)) {
            return getResource().getName();
        } else {
            return getResource().getResourceAttributeVal(name);
        }
    }

    private String getAccountAttributeBooleanValue(WSUser user, String mapName) {
        String retValue = CA_FALSE;
        AccountAttributeType[] attrTypes = _resource.getAccountAttributeTypes();
        for (int i = 0; i < attrTypes.length; i++) {
            AccountAttributeType a = attrTypes[i];
            String accountMapName = a.getMapName();
            String name = a.getName();
            if (accountMapName.equals(mapName)) {
                WSAttribute wsAttr = user.getWSAttribute(name);
                if (wsAttr != null) {
                    Object value = wsAttr.getValue();
                    if (value != null && value instanceof Boolean) {
                        retValue = ((Boolean) value).booleanValue() ? CA_TRUE : CA_FALSE;
                    }
                }
            }
        }
        return retValue;
    }

    @Override
    protected AccountAttributeType getAttrTypeFromMapName(String mapName, boolean ignoreCase) {
        String METHOD = "getAttrTypeFromMapName";
        TRACE.entry1(METHOD, mapName);
        AccountAttributeType attrType = null;
        AccountAttributeType attrTypes[] = _resource.getAccountAttributeTypes();
        int i = 0;
        do {
            if (i >= attrTypes.length) {
                break;
            }
            String attrName = attrTypes[i].getMapName();
            if (ignoreCase && mapName.equalsIgnoreCase(attrName) || !ignoreCase && mapName.equals(attrName)) {
                attrType = attrTypes[i];
                break;
            }
            if (attrName.indexOf(":") > 0) {
                attrName = attrName.substring(attrName.indexOf(":") + 1);
                if (ignoreCase && mapName.equalsIgnoreCase(attrName) || !ignoreCase && mapName.equals(attrName)) {
                    attrType = attrTypes[i];
                    break;
                }
            }
            i++;
        } while (true);
        TRACE.exit2(METHOD, attrType);
        return attrType;
    }

    /**
     * Map from a resource attribute to a waveset attribute. Return null
     * if there's no mapping.
     */
    protected WSAttribute reverseMapAttr(String attr, Object value) {
        WSAttribute wsAttr = null;
        AccountAttributeType attrType = getAttrTypeFromMapName(attr);
        if (attrType != null) {
            wsAttr = new WSAttribute(attrType.getName(), value,
                    attrType.getSyntax());
        }
        return wsAttr;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Core Operations
    //
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //
    // Get
    //
    //////////////////////////////////////////////////////////////////////
    /**
     * Build the base user object given a PSO (SearchResponse)
     */
    private WSUser buildUser(UDSObject pso) {
        final String method = "buildUser";
        if (TRACE.level1(method)) {
            TRACE.entry1(method);
        }
        WSUser user = null;
        if (null != pso) {
            user = new WSUser();
            ResourceInfo info = new ResourceInfo();
            info.setResource(_resource);
            info.setAccountGUID(pso.getHandle());
            info.setAccountCreated(true);
            user.addResourceInfo(info);
            for (UDSAttribute sourceAttribute : pso.getAttributes().getAttribute()) {
                if (AA_USERID.equals(sourceAttribute.getAttrName())) {
                    info.setAccountID(sourceAttribute.getAttrValue());
                    user.setAccountId(sourceAttribute.getAttrValue());
                } else if (AA_DELETE_FLAG.equals(sourceAttribute.getAttrName())) {
                    info.setDisabled("1".equals(sourceAttribute.getAttrValue()));
                    continue;
                }
                WSAttribute targetAttribute = reverseMapAttr(sourceAttribute.getAttrName(), sourceAttribute.getAttrValue());
                if (null != targetAttribute) {
                    user.setWSAttribute(targetAttribute);
                }
            }
        }
        if (TRACE.level1(method)) {
            TRACE.exit1(method);
        }
        return user;
    }

    @Override
    public WavesetResult testConfiguration() throws WavesetException {
        final String METHOD = "testConfiguration";
        TRACE.entry1(METHOD);
        Pattern simple = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*$");
        Pattern readonly = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*\\.[a-zA-Z_][0-9a-zA-Z_]*$");
        Pattern complex = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*:[a-zA-Z_][0-9a-zA-Z_]*\\.[a-zA-Z_][0-9a-zA-Z_]*$");
        for (String rhlname : ((Map<String, Object>) getSchemaMap()).keySet()) {
            if (!(simple.matcher(rhlname).matches() || readonly.matcher(rhlname).matches() || complex.matcher(rhlname).matches())) {
                WavesetException e = new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_INVALID_RESOURCEATTRIBUTE, new Object[]{rhlname}));
                TRACE.throwing2(METHOD, e);
                e.printStackTrace();
                throw e;
            }
        }
        startConnection();
        TRACE.exit1(METHOD);
        return new WavesetResult();
    }

    /**
     * Retrieve information about an account, and package it as
     * a WSUser object.
     * <p/>
     * Information required to identify the resource account is supplied
     * in another WSUser object.
     * <p/>
     * The returned user object will resemble the source object, but
     * will have its account attribute list filled in.
     * <p/>
     * Returns "null" if there is no account that corresponds to the user.
     */
    @Override
    public WSUser getUser(WSUser user) throws WavesetException {
        final String method = "getUser";
        TRACE.entry1(method);
        startConnection();
        String handler = submitGetHandleForUserid(user);

        // use the user object passed in and find the user's accountId for
        // this resource.
        // getIdentity returns the account name for this user.

        // Exceptions will be thrown if AccountID for the user in the resource
        // info is blank or if the user does not have a waveset account on the
        // resource.
        WSUser newUser = null;
        if (null != handler) {
            UDSObject result = submitGetObjectValues(handler, getReadableAccountAttributeNames());
            newUser = buildUser(result);
        }
        stopConnection();
        TRACE.exit1(method);
        return newUser;
    }

    /**
     * Checks to see if an account can be created. Some of the things that
     * might be checked are as follows:
     * <p/>
     * - can basic connectivity to the resource be established?
     * - Do the account attribute values comply with all (if any) resource
     * specific restrictions or policies that haven't been checked at a
     * higher level?
     * <p/>
     * Currently there are 3 check methods: checkCreateAccount,
     * checkDeleteAccount and checkUpdate account. All three of these methods
     * could be performing similar actions, such as ensuring that the resource
     * is available. These common actions can be moved to a common function
     * such as doBasicCheck() which any/all check methods could call.
     * Then the individual check methods would do additional checks
     * to ensure that user accounts can be added, modified or deleted.
     * <p/>
     * checkCreateAccount is not designed to ensure that the account
     * creation will succeed, only that the likelyhood of success is good.
     * checkCreateAccount does not need to check to see if the account already
     * exists. The provisioner method will follow checkCreateAccount with a
     * getUser call.
     */
    public WavesetResult checkCreateAccount(WSUser user)
            throws WavesetException {

        final String method = "checkCreateAccount";
        TRACE.entry1(method, user.getName());
        WavesetResult result = new WavesetResult();
        startConnection();
        getIdentity(user);
        AccountAttributeType attrTypes[] = _resource.getAccountAttributeTypes();
        boolean createable = false;
        for (int i = 0; i < attrTypes.length; i++) {
            if (AA_LAST_NAME.equals(attrTypes[i].getMapName())) {
                createable = null != user.getAttribute(attrTypes[i].getName());
                break;
            }
        }

        if (!createable) {
            throw new WavesetException("Missing attribute: " + AA_LAST_NAME);
        }

        // add-code-here to check to make sure that the resource can be
        // contacted, that the privileged account name and password being
        // used to create the account is in order, and that account attribute
        // values comply with any resource specific policies (no duplicate ids
        // for example)

        // Common exceptions to use:
        // Invalid arguments use:
        //    throw new com.waveset.util.InvalidArgument("Not a valid "+RA_HOST);
        // Invalid principal account or password use:
        //    throw new WavesetException("Cound not authenticate to the server.");
        // Resource unavailable use:
        //    throw new WavesetException("Could not connect to the server.");

        TRACE.exit1(method);
        return result;
    }

    /**
     * Create a new user account on the resource
     * <p/>
     * This method is called by createAccounts (the multiuser create method).
     * CreateAccounts will open a connection, call realCreate for each user
     * being created, then close the connection.
     */
    @Override
    protected void realCreate(WSUser user, WavesetResult result)
            throws WavesetException {

        final String method = "realCreate";
        TRACE.entry2(method);

        // use the user object passed in and find the user's accountId for
        // this resource.
        // getIdentity returns the account name for this user.
        String identity = getIdentity(user);

        // add-code-here to create the user on the resource

        ResourceInfo resInfo = getResourceInfo(user);
        UDSObject remoteUser = new UDSObject();
        remoteUser.addAttribute(new UDSAttribute(AA_USERID, identity));
        AccountAttributeType attrs[] = getResource().getAccountAttributeTypes();
        for (int i = 0; i < attrs.length; i++) {
            String caAttrName = attrs[i].getMapName();
            if (AA_USERID.equals(caAttrName)) {
                continue;
            }
            String caAttrValue = user.getAttribute(attrs[i].getName());
            if (attrs[i].isRequired() && caAttrValue == null) {
                throw new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_REQUIRED_ATTR_MISSING, attrs[i].getName()));
            }
            if (null != caAttrValue && !attrs[i].isReadOnly()) {
                if (caAttrName.contains(":")) {
                    remoteUser.addAttribute(resolveAttributeValue(caAttrName, caAttrValue));
                } else {
                    remoteUser.addAttribute(new UDSAttribute(caAttrName, caAttrValue));
                }
            }
        }
        remoteUser = submitCreateObject(OBJECT_TYPE_CONTACT, remoteUser, getReadableAccountAttributeNames());
        // Clear the password to indiciate that it was sucessfully applied
        if (remoteUser != null) {
            String handle = remoteUser.getHandle();
            if (null != handle) {
                resInfo.setAccountID(identity);
                resInfo.setAccountGUID(handle);
                resInfo.setAccountCreated(true);
                resInfo.setPassword((EncryptedData) null);
                if (TRACE.level3(method)) {
                    String msg = "Added user '" + identity + "' with handle'" + handle + "'";
                    TRACE.info3(method, msg);
                }
            }

        } else {
            WavesetException e = new WavesetException(new ErrorMessage(Severity.ERROR, RAMessages.SUNIS_USER_CREATE_ERROR));
            TRACE.throwing2(method, e);
            throw e;
        }

        // Common exceptions to use:
        // Missing required attributes use:
        //    throw new WavesetException("Missing required attribute '"+name+"'.");
        // Error adding user use:
        //    throw new WavesetException("An error occurred adding user to resource.");
        // Resource exception messages can also be passed directly back to the user.

        // As a way to indicate that the user password was successfully
        // set on the resource, the password field in the resource info on
        // the user object is set to null.
        //
        // add-code-here to check that the password was set for the user
        // account and then set the resource info password to null.
        // ResourceInfo resInfo = user.getResourceInfo(_resource);
        /* if (resInfo != null) {
        resInfo.setAccountCreated(true);
        // indicate that the password was set
        resInfo.setPassword((EncryptedData) null);
        } */

        if (TRACE.level3(method)) {
            String msg = "Added user '" + identity + "'";
            TRACE.info3(method, msg);
        }
        TRACE.exit2(method);
    }

    public WavesetResult checkUpdateAccount(WSUser user)
            throws WavesetException {
        // Check to see if the account can be updated. See checkCreate for list
        // of things that can be checked.
        // The checkUpdateAccount will take as input a User object.

        // If the account does not exist, throw exception.
        // Common exceptions to use:
        // Invalid arguments use:
        //    throw new com.waveset.util.InvalidArgument("Not a valid "+RA_HOST);
        // Invalid principal account or password use:
        //    throw new WavesetException("Cound not authenticate to the server.");
        // Resource unavailable use:
        //    throw new WavesetException("Could not connect to the server.");

        final String method = "checkUpdateAccount";
        TRACE.entry1(method, user.getName());
        //startConnection();
        WavesetResult result = new WavesetResult();
        TRACE.exit1(method);
        return result;
    }

    @Override
    protected void realUpdate(WSUser user, WavesetResult result)
            throws WavesetException {
        // Update account.
        //
        // Receives as input a User object.
        //
        // updateAccount method needs to modify the individual account
        // attributes and not replace the entire record. The user password will
        // only be present if it is needing to be changed. The password if it
        // does need to be changed will not be found in the user.password
        // variable passed in, it will instead be found on the resource info
        // object password
        //
        final String method = "realUpdate";
        TRACE.entry2(method, user.getName());

        // Use one of the following statements:
        //
        // If the returned identity is needed for the later implementation,
        // use the following:
        //
        // String dn = getIdentity(user);NEW_ACCOUNT_ID
        //
        // Otherwise, use the following to verify validity of user account
        // and to avoid dead store:
        String handle = submitGetHandleForUserid(user);
        ResourceInfo resInfo = getResourceInfo(user);
        // EncryptedData pw = resInfo.getPassword();

        if (TRACE.level2(method)) {
            TRACE.info2(method, "Updating user '" + user + "'");
        }
        if (null != handle) {
            UDSObject remoteUser = new UDSObject(handle);
            AccountAttributeType attrs[] = getResource().getAccountAttributeTypes();
            String newAccountID = user.getAttribute(NEW_ACCOUNT_ID);
            if (null != newAccountID) {
                remoteUser.addAttribute(new UDSAttribute(AA_USERID, newAccountID));
            }
            for (int i = 0; i < attrs.length; i++) {
                String caAttrName = attrs[i].getMapName();
                //TODO: catch the rename event
                if (AA_USERID.equals(caAttrName) || AA_DELETE_FLAG.equals(caAttrName) || AA_UUID_ID.equalsIgnoreCase(caAttrName)) {
                    continue;
                }

                String caAttrValue = user.getAttribute(attrs[i].getName());

                if (null != caAttrValue && !attrs[i].isReadOnly()) {
                    if (caAttrName.contains(":")) {
                        remoteUser.addAttribute(resolveAttributeValue(caAttrName, caAttrValue));
                    } else if (!caAttrName.contains(".")) {
                        remoteUser.addAttribute(new UDSAttribute(caAttrName, caAttrValue));
                    }
                }
            }
            remoteUser = submitUpdateObject(remoteUser, getReadableAccountAttributeNames());

            // Clear the password to indiciate that it was sucessfully applied
            if (remoteUser != null) {
                UDSAttribute nameAttr = remoteUser.getAttribute(AA_USERID);
                if (null != nameAttr) {
                    resInfo.setAccountId(nameAttr.getAttrValue());
                }
                resInfo.setAccountGUID(handle);
                resInfo.setAccountCreated(true);
                if (TRACE.level3(method)) {
                    String msg = "Modified user '" + resInfo.getAccountId() + "' with handle: '" + handle + "'";
                    TRACE.info3(method, msg);
                }
            } else {
                WavesetException e = new WavesetException(new ErrorMessage(Severity.ERROR, RAMessages.SUNIS_USER_UPDATE_ERROR));
                TRACE.throwing2(method, e);
                throw e;
            }

        } else {
            WavesetException e = new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_USER_NOT_EXIST, user.getAccountId()));
            TRACE.throwing2(method, e);
            throw e;
        }

        // add-code-here to update user record

        // As a way to indicate that the user password was successfully
        // set on the resource, the password field in the resource info on
        // the user object is set to null.
        //
        // add-code-here to check that the password was set for the user
        // account and then set the resource info password to null.
        //ResourceInfo resInfo = user.getResourceInfo(_resource);
        if (resInfo != null) {
            // Indicate that the password was set
            resInfo.setPassword((EncryptedData) null);
        }
        TRACE.exit2(method);
    }

    public WavesetResult checkDeleteAccount(WSUser user)
            throws WavesetException {
        // Check to see if the account can be deleted. See checkCreate for list
        // of things that can be checked (with the exception of the account
        // attribute compliance).
        //
        // Receives a User object as input. The user's identity on this resource
        // will be used to identify the account to be deleted.
        //
        // checkDeleteAccount should not fail if account does not exist

        // Common exceptions to use:
        // Invalid arguments use:
        //    throw new com.waveset.util.InvalidArgument("Not a valid "+RA_HOST);
        // Invalid principal account or password use:
        //    throw new WavesetException("Cound not authenticate to the server.");
        // Resource unavailable use:
        //    throw new WavesetException("Could not connect to the server.");

        final String method = "checkDeleteAccount";
        TRACE.entry1(method, user.getName());

        WavesetResult result = new WavesetResult();

        // Use one of the following statements:
        //
        // If the returned identity is needed for the later implementation,
        // use the following:
        //
        // String identity = getIdentity(user);
        //
        // Otherwise, use the following to verify validity of user account
        // and to avoid dead store:
        getIdentity(user);

        TRACE.exit1(method);
        return result;
    }

    @Override
    protected void realEnable(WSUser user, WavesetResult result)
            throws WavesetException {
        // Enable account
        //
        // Receives as input a user object.
        // Result should be an error if account cannot be enabled
        //
        // result.addError(msg) can be used to add detailed info on why
        // the account cannot be enabled.

        final String method = "realEnable";
        TRACE.entry2(method, user.getName());

        // Use one of the following statements:
        //
        // If the returned identity is needed for the later implementation,
        // use the following:
        //
        // String dn = getIdentity(user);
        //
        // Otherwise, use the following to verify validity of user account
        // and to avoid dead store:
        getIdentity(user);
        changeDisabledStatus(user, false, result);
        TRACE.exit2(method);
    }

    @Override
    protected void realDisable(WSUser user, WavesetResult result)
            throws WavesetException {
        // Disable account
        //
        // Receives as input a user object.
        // Result should be an error if account cannot be disabled
        //
        // result.addError(msg) can be used to add detailed info on why
        // the account cannot be disabled.

        final String method = "realDisable";
        TRACE.entry2(method, user.getName());

        // Use one of the following statements:
        //
        // If the returned identity is needed for the later implementation,
        // use the following:
        //
        // String dn = getIdentity(user);
        //
        // Otherwise, use the following to verify validity of user account
        // and to avoid dead store:
        getIdentity(user);
        changeDisabledStatus(user, true, result);

        TRACE.exit2(method);
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Resource Object Methods
    //
    //////////////////////////////////////////////////////////////////////
    /**
     * Returns a list of objects matching the requested objectType and options
     *
     * @param objectType - the name of a valid object class for this specified
     *                   "resId".
     * @param options    - several options can be specified which control the
     *                   behavior of the search.  They include:
     *                   <ol>
     *                   "searchContext" - the value of this option determines
     *                   within what context to perform search
     *                   (ResourceAdapter.RA_SEARCH_CONTEXT). If not specified,
     *                   will attempt to get a value from RA_BASE_CONTEXT. If no
     *                   value, will assume search should be done from logical
     *                   top.
     *                   <li>
     *                   "searchFilter" - optional specification, in LDAP search
     *                   filter format as specified in RFC 1558, of one or more
     *                   object <attr name> <condition> <value> tuples either
     *                   and'ed or or'ed together.  If not specified, a filter
     *                   will be constructed using the specified objectType.
     *                   (ResourceAdapter.SEARCH_FILTER).
     *                   <li>
     *                   "searchScope" - specifies whether the search should be
     *                   done on the current object, only within the context of
     *                   the specified "searchContext", or in all subcontext
     *                   within the specified "searchContext"
     *                   (ResourceAdapter.RA_SEARCH_SCOPE). Valid values are
     *                   "object", "oneLevel", or "subTree" indicates that the
     *                   search should be performed on all sub contexts within
     *                   the specified "searchContext".
     *                   <li>
     *                   "searchTimeLimit" - the timelimit in milliseconds a
     *                    search should not exceed
     *                   (ResourceAdapter.RA_SEARCH_TIME_LIMIT).
     *                   <li>
     *                   "searchAttrsToGet" - the list of objectType specific
     *                   attribute names to get per object
     *                   <li>
     *                   "runAsUser" - user name this request is to be run as.
     *                   If not specified, defaults to resource proxy admin user.
     *                   <li>
     *                   "runAsPassword" - password of runAsUser. Required to
     *                   authenticate with resource in order to run the list
     *                   request as the specified user
     *                   </ol>
     */
    @Override
    public List listObjects(String objectType, Map options)
            throws WavesetException {
        final String method = "listObjects";
        TRACE.entry1(method, objectType);

        List<GenericObject> objects = null;
        GenericObject objOutput = null;

        try {
            // get the search context
            //String searchCtx = (String) options.get(RA_SEARCH_CONTEXT);
            //if (searchCtx == null) {
            //    searchCtx = (String) _resource.getResourceAttributeVal(RA_BASE_CTX);
            //}

            // get the search filter
            String searchFilter = (String) options.get(RA_SEARCH_FILTER);

            // get search attrs to get
            List<String> attrsToGet = (List<String>) options.get(RA_SEARCH_ATTRS_TO_GET);
            ArrayOfString attributes = null;
            if (null != attrsToGet) {
                attributes = new ArrayOfString(attrsToGet);
            }

            if (TRACE.level3(method)) {
                TRACE.list3(method, "attrsToGet", attrsToGet);
                TRACE.variable3(method, "searchFilter", searchFilter);
            }

            // get the requested search scope
            //String searchScope = (String) options.get(RA_SEARCH_SCOPE);

            // get the search time limit
            //Integer timeLimit = (Integer) options.get(RA_SEARCH_TIME_LIMIT);

            // add-code-here to search the resource for the requested object
            // type passing the search filter, search context, search timeout,
            // search scope, and the list of attrs to get specified in the
            // options
            if ("account".equals(objectType)) {
                objectType = OBJECT_TYPE_CONTACT;
            }

            CAObjectIterator objectIterator = new CAObjectIterator(this, objectType, searchFilter, attributes);

            // add-code-here that iterates over the returned list of objects,
            // if any, such that for each object returned by the search matching
            // the specified search criteria ...

            if (objectIterator.hasNext()) {
                objects = new ArrayList<GenericObject>();
            }
            while (objectIterator.hasNext()) {
                // create a new generic object to hold the requested object
                // attr values
                objOutput = new GenericObject();
                UDSObject objInput = objectIterator.next();
                objOutput.setName(objInput.getHandle());
                for (UDSAttribute attr : objInput.getAttributes().getAttribute()) {
                    if (null != attr.getAttrValue()) {
                        objOutput.put(attr.getAttrName(), attr.getAttrValue());
                    }
                }
                objects.add(objOutput);
            }
        } catch (WavesetException w) {
            throw w;
        } catch (Exception e) {
            ErrorMessage errMsg = new ErrorMessage(Severity.ERROR, "Error searching for objects");
            TRACE.info1(method, "Error searching for objects");
            TRACE.caught1(method, e);
            throw new WavesetException(errMsg, e);

        } finally {
            stopConnection();
        }

        TRACE.exit1(method);
        return objects;
    }

    //////////////////////////////////////////////////////////////////////
    //
    // Account Iterator
    //
    //////////////////////////////////////////////////////////////////////
    @Override
    public AccountIterator getAccountIterator() throws WavesetException {
        // Returns an iterator that can be used to iterate over all the
        // accounts on a resource.
        //
        // AccountIterator is used for auto-discovery methods to get bulk user
        // accounts from the resource.
        //
        // NOTE: The WSUser objects returned by the account iterator should
        //       have a ResourceInfo object with the appropriate information
        //       filled out. See the skeleton getUser() implementation.
        //
        // NOTE: If you provide getAccountIterator() and not listAllObjects(),
        // then getAccountIterator is used to create the list of all objects.
        // If you provide listAllObjects and not getAccountIterator(), then
        // the returned list is used along with getUser(WSUser) to implement
        // getAccountIterator().  You can choose to implement either or both
        // methods.  If you implement getAccountIterator(), and wish to support
        // the CONTINUE_ON_ERROR feature, you'll need to intercept the
        // appropriate exceptions and continue.  The implementation of
        // getAccountIterator() in ResourceAdapterBase will check to see if
        // CONTINUE_ON_ERROR is enabled and behave appropriately.
        final String method = "getAccountIterator";
        TRACE.entry1(method);
        AccountIterator acctIter = new CAAccountIterator(this);
        TRACE.exit1(method);
        return acctIter;
    }

    /**
     * Authenticates the user against the resource. Return the authenticated id
     * if authentication succeeds.
     * <p/>
     * The authenticate method is used to verify a user account and password are
     * valid. If the user account name does not exist on the resource, the
     * password does not match, or multiple matches exist, then throw an
     * exception.
     * <p/>
     * If the resource has multiple contexts (for example LDAP or NDS), the
     * authenticate method should not stop when it finds the first match,
     * instead it should continue through the entire list. If it finds more
     * than one match, then an exception should be thrown.
     */
    public WavesetResult authenticate(HashMap loginInfo)
            throws WavesetException {

        WavesetResult result = new WavesetResult();
        String authenticatedId = null;
        final String method = "authenticate";
        TRACE.entry1(method);


        // first get the required authentication info out of loginInfo

        String accountID = (String) loginInfo.get(LOGIN_USER);
        String password = ((EncryptedData) loginInfo.get(LOGIN_PASSWORD)).decryptToString();

        if (accountID == null || accountID.length() < 1 || password == null) {
            throw new WavesetException("Missing required login info.");
        }

        // add-code-here to connect or login to the resource using the accountID
        // and password

        // If authentication succeeds, add the fully qualified identity to the
        // result to be returned
        // result.addResult(Constants.AUTHENTICATED_IDENTITY, accountID);

        // If authentication succeeded, but the user's password was expired, in
        // addition to the identity added above, also add the password expired
        // indicator to the result to be returned
        // result.addResult(Constants.RESOURCE_PASSWORD_EXPIRED, Boolean.TRUE);

        // Common exceptions to use:
        // If authentication fails, meaning username or password not valid use:
        //    throw new WavesetException("Authentication failed for "+uid+".");
        // If there is more than one user account (possibly due to email
        //    login id being used or there was not a full user context given)
        //    and the accounts have the same passowrd use:
        //    throw new WavesetException("More than one user matched and has the same password.");

        TRACE.exit1(method);
        return result;
    }

    protected class CAObjectIterator {

        private CAServicedeskResourceAdapter _adapter;
        private Iterator<UDSObject> _it;
        private ListResult listHandle = null;
        private int startIndex = 0;
        private int _windowSize;
        private ArrayOfString _attributes;

        public CAObjectIterator(CAServicedeskResourceAdapter adapter, String objectType, String whereClause, ArrayOfString attributes) throws WavesetException {
            this._adapter = adapter;
            this._attributes = attributes != null ? attributes : new ArrayOfString();
            this._windowSize = this._adapter.getBlockSize();
            this._adapter.startConnection();
            this.listHandle = this._adapter.submitDoQuery(objectType, whereClause);
            if (this.listHandle.getListLength() > 0) {
                UDSObjectList objectList = this._adapter.submitGetListValues(this.listHandle, this.startIndex, this._windowSize - 1, this._attributes);
                if (null != objectList) {
                    this._it = objectList.getUDSObject().iterator();
                    this.startIndex = this.startIndex + this._windowSize;
                }
            }
        }

        public boolean hasNext() throws WavesetException {
            final String method = "hasNext";
            TRACE.entry1(method);
            boolean hasNext = false;
            if (null != _it) {
                hasNext = _it.hasNext();
                if (!hasNext) {
                    hasNext = startIndex < listHandle.getListLength();
                }
            }
            TRACE.exit1(method, hasNext);
            return hasNext;
        }

        public UDSObject next() {
            final String method = "next";
            TRACE.entry1(method);
            UDSObject pso = null;
            if (_it.hasNext()) {
                pso = _it.next();
            } else {
                try {
                    this._it = null;
                    UDSObjectList objectList = this._adapter.submitGetListValues(this.listHandle, this.startIndex, this._windowSize, this._attributes);
                    if (null != objectList) {
                        this._it = objectList.getUDSObject().iterator();
                        this.startIndex = this.startIndex + this._windowSize;
                        pso = _it.next();
                    }
                } catch (WavesetException e) {
                    TRACE.info1(method, "Error from submitGetListValues() method");
                }
            }
            TRACE.exit1(method);
            return pso;
        }

        public void close() {
            final String METHOD = "close";
            TRACE.entry1(METHOD);
            if (null != listHandle) {
                try {
                    ArrayOfInt handles = new ArrayOfInt();
                    handles.getInteger().add(listHandle.getListHandle());
                    USDWebServiceSoap _endPort = getConnection();
                    _endPort.freeListHandles(getSID(), handles);
                    poolConnection(_endPort);
                    listHandle = null;
                } catch (SOAPFaultException e) {
                    TRACE.caught2(METHOD, e);
                } catch (Exception e) {
                    TRACE.caught1(METHOD, e);
                }
            }
            TRACE.exit1(METHOD);
        }
    }

    protected class CAAccountIterator implements AccountIterator {
        // The adapter we are associated with.      

        private CAObjectIterator _it;
        private ArrayOfString _attributes = new ArrayOfString();

        CAAccountIterator(CAServicedeskResourceAdapter adapter)
                throws WavesetException {
            super();
            this._it = new CAObjectIterator(adapter, OBJECT_TYPE_CONTACT, "", _attributes);
        }

        public boolean hasNext() throws WavesetException {
            return _it.hasNext();
        }

        public WSUser next() {
            final String method = "next";
            if (_trace.level1(CLASS, method));
            TRACE.entry1(method);
            UDSObject pso = _it.next();
            WSUser user = null;
            if (null != pso) {
                user = new WSUser();
                ResourceInfo info = new ResourceInfo();
                info.setResource(_resource);
                info.setAccountGUID(pso.getHandle());
                info.setAccountCreated(true);
                user.addResourceInfo(info);

                for (UDSAttribute sourceAttribute : pso.getAttributes().getAttribute()) {
                    if (AA_USERID.equals(sourceAttribute.getAttrName())) {
                        info.setAccountID(sourceAttribute.getAttrValue());
                        user.setAccountId(sourceAttribute.getAttrValue());
                    }
                    WSAttribute targetAttribute = new WSAttribute(sourceAttribute.getAttrName(), sourceAttribute.getAttrValue());
                    user.setWSAttribute(targetAttribute);
                }
            }
            if (TRACE.level4(method)) {
                TRACE.info4(method, "User: " + user.toXml());
            }
            if (TRACE.level1(method)) {
                TRACE.exit1(method);
            }
            return user;
        }

        public void close() {
            final String METHOD = "close";
            TRACE.entry1(METHOD);
            if (null != _it) {
                _it.close();
            }
            TRACE.exit1(METHOD);
        }
    }

//    private USDWebServiceSoap getConnection() throws WavesetException {
//        final String METHOD = "getConnection";
//        TRACE.entry1(METHOD);
//        String wsdl_url = (String) getRequiredResAttrVal(RA_WSDL_URL);
//        String endPoint = getOptionalStringResAttrVal(RA_ENDPOINT);
//
//        USDWebServiceSoap _endPort = getPooledConnection(wsdl_url, endPoint);
//        if (null == _endPort) {
//            USDWebService ss = getWebService(wsdl_url);
//            if (null == ss) {
//                URL wsdlURL = null;
//                try {
//                    wsdlURL = new URL(wsdl_url);
//                    ss = new USDWebService(wsdlURL, SERVICE_NAME);
//                    setWebService(ss);
//                } catch (Exception e) {
//                    TRACE.caught1(METHOD, e);
//                    throw new WavesetException(e);
//                }
//            }
//
//            if (null != ss) {
//                try {
//
//                    if (null != endPoint) {
//                        _endPort = ss.getUSDWebServiceSoap();
//                        Map<String, Object> ctx = ((BindingProvider) _endPort).getRequestContext();
//                        ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endPoint);
//                    } else {
//                        _endPort = ss.getUSDWebServiceSoap();
//                    }
//                } catch (Exception e) {
//                    TRACE.caught1(METHOD, e);
//                    throw new WavesetException(e);
//                }
//            }
//        }
//        if (_endPort == null) {
//            WavesetException e = new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_START_CONNECTION, new Object[]{wsdl_url}));
//            TRACE.throwing2(METHOD, e);
//            throw e;
//        }
//
//        try {
//            connectionIsVerified = (connectionIsVerified || submitServerStatus(_endPort) || submitLogin(_endPort));
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        }
//        if (!connectionIsVerified) {
//            WavesetException e = new WavesetException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_AUTHENTICATE_CONNECTION, new Object[]{wsdl_url}));
//            TRACE.throwing2(METHOD, e);
//            throw e;
//        }
//        TRACE.exit1(METHOD);
//        return _endPort;
//    }

    private void poolConnection(USDWebServiceSoap c) throws WavesetException {
        String url = (String) getRequiredResAttrVal(RA_WSDL_URL);
        poolConnection(url, getOptionalStringResAttrVal(RA_ENDPOINT), c);
    }

    private static synchronized USDWebServiceSoap getPooledConnection(String wsdl, String url) {
        USDWebServiceSoap c = null;
        List<USDWebServiceSoap> l = null;
        if (null == url) {
            l = _connections.get(wsdl);
        } else {
            l = _connections.get(wsdl + url);
        }
        if (l != null && l.size() > 0) {
            c = l.get(0);
            l.remove(0);
        }
        return c;
    }

    private static synchronized void poolConnection(String wsdl, String url, USDWebServiceSoap c) {
        List<USDWebServiceSoap> l = null;
        if (null == url) {
            l = _connections.get(wsdl);
        } else {
            l = _connections.get(wsdl + url);
        }
        if (l == null) {
            l = new ArrayList<USDWebServiceSoap>(1);
            if (null == url) {
                _connections.put(wsdl, l);
            } else {
                _connections.put(wsdl + url, l);
            }
        }
        l.add(c);
    }

    @Override
    protected void startConnection() throws WavesetException {
    }

    @Override
    protected void stopConnection() throws WavesetException {
    }

    /**
     * Expose features supported by the Resource Adapter.
     * @see com.waveset.adapter.ResourceAdapter.Features
     * @return GenericObject containing Features, as both key and value, which
     * are supported by this resource adapter.
     */
    @Override
    public GenericObject getFeatures() {
        final String METHOD = "getFeatures";
        TRACE.entry1(METHOD);
        GenericObject genObj = super.getFeatures();
        // If account iterators  supports continuing on error, indicate the
        // feature.  If the adapter doesn't provide any getAccountIterator()
        // methods, but uses the defaults in ResourceAdapterBase, the feature
        // can be reported as ResourceAdapterBase checks the setting before
        // throwing the exception.
        //genObj.put(Features.CONTINUE_ON_ERROR, Features.CONTINUE_ON_ERROR);

        /*
        Feature Name                    Enabled in Base?
        Comments
        -------------------------------------------------
        ACCOUNT_CASE_INSENSITIVE_IDS    Yes
        Indicates whether user account names are case-insensitive. Override the supportsCaseInsensitiveAccountIds method with a false value to make account IDs case-sensitive.
        ACCOUNT_CREATE                  Yes
        Indicates whether accounts can be created. Use the remove operation to disable this feature.
        ACCOUNT_DELETE                  Yes
        Indicates whether accounts can be deleted. Use the remove operation to disable this feature.
        ACCOUNT_DISABLE                 No
        Indicates whether accounts can be disabled on the resource. Override the supportsAccountDisable method with a true value to enable this feature.
        ACCOUNT_EXCLUDE                 No
        Determines whether administrative accounts can be excluded from Identity Manager. Override the supportsExcludedAccounts method with a true value to enable this feature.
        ACCOUNT_ENABLE                  No
        Indicates whether accounts can be enabled on the resource. Override the supportsAccountDisable method with a true value if accounts can be enabled on the resource.
        ACCOUNT_EXPIRE_PASSWORD         Yes
        Enabled if the expirePassword Identity Manager User attribute is present in the schema map for the adapter. Use the remove operation to disable this feature.
        ACCOUNT_GUID                    No
        If a GUID is present on the resource, use the put operation to enable this feature.
        ACCOUNT_ITERATOR                Yes
        Indicates whether the adapter uses an account iterator. Use the remove operation to disable this feature.
        ACCOUNT_LIST                    Yes
        Indicates whether the adapter can list accounts. Use the remove operation to disable this feature.
        ACCOUNT_LOGIN                   Yes
        Indicates whether a user can login to an account. Use the remove operation if logins can be disabled.
        ACCOUNT_PASSWORD                Yes
        Indicates whether an account requires a password. Use the remove operation if passwords can be disabled.
        ACCOUNT_RENAME                  No
        Indicates whether an account can be renamed. Use the put operation to enable this feature.
        ACCOUNT_REPORTS_DISABLED        No
        Indicates whether the resource reports if an account is disabled. r Use the put operation to enable this feature.
        ACCOUNT_UNLOCK                  No
        Indicates whether an account can be unlocked. Use the put operation if accounts can be unlocked.
        ACCOUNT_UPDATE                  Yes
        Indicates whether an account can be modified. Use the remove operation if accounts cannot be updated.
        ACCOUNT_USER_PASSWORD_ON_CHANGE No
        Indicates whether the user‚Äôs current password must be specified when changing the password. Use the put operations if the user‚Äôs current password is required.
         */
        //genObj.put   (Features.ACCOUNT_CASE_INSENSITIVE_IDS, Features.ACCOUNT_CASE_INSENSITIVE_IDS);
        //genObj.put   (Features.ACCOUNT_CREATE, Features.ACCOUNT_CREATE);
        genObj.remove(Features.ACCOUNT_DELETE);
        genObj.put(Features.ACCOUNT_DISABLE, Features.ACCOUNT_DISABLE);
        genObj.put(Features.ACCOUNT_ENABLE, Features.ACCOUNT_ENABLE);
        genObj.remove(Features.ACCOUNT_EXPIRE_PASSWORD);
        genObj.put(Features.ACCOUNT_ITERATOR, Features.ACCOUNT_ITERATOR);
        genObj.remove(Features.ACCOUNT_LIST);
        genObj.remove(Features.ACCOUNT_LOGIN);
        genObj.remove(Features.ACCOUNT_PASSWORD);
        genObj.put(Features.ACCOUNT_RENAME, Features.ACCOUNT_RENAME);
        genObj.put(Features.ACCOUNT_REPORTS_DISABLED, Features.ACCOUNT_REPORTS_DISABLED);
        //genObj.put(Features.CONTINUE_ON_ERROR, Features.CONTINUE_ON_ERROR);
        //genObj.remove(Features.ACCOUNT_UNLOCK);
        genObj.put(Features.ACCOUNT_GUID, Features.ACCOUNT_GUID);
        //genObj.remove(Features.ACCOUNT_USER_PASSWORD_ON_CHANGE);
        // In this case we check the trace level before tracing since
        // genObj.toXml() is a potentially expensive call.
        if (TRACE.level3(METHOD)) {
            TRACE.info3(METHOD, "\n" + genObj.toXml());
        }

        TRACE.exit1(METHOD);
        return genObj;
    }

//    private boolean submitServerStatus(USDWebServiceSoap _endPort) throws WavesetException {
//        final String METHOD = "submitServerStatus";
//        TRACE.entry1(METHOD);
//        Integer SID = getSID();
//        boolean accessable = (null != _endPort && null != SID);
//        if (accessable) {
//            try {
//                /* Returns
//                 * The following values apply:
//                 * 1 =Indicates the Service Desk server is not available
//                 * 0= Indicates the Service Desk server it is running
//                 */
//                accessable = 0 == _endPort.serverStatus(SID);
//            } catch (SOAPFaultException e) {
//                TRACE.caught3(METHOD, e);
//                accessable = false;
//            }
//        }
//        TRACE.exit1(METHOD, accessable);
//        return accessable;
//    }
//
//    private boolean submitLogin(USDWebServiceSoap _endPort) throws WavesetException {
//        final String METHOD = "submitLogin";
//        TRACE.entry1(METHOD);
//        String username = (String) getRequiredResAttrVal(RA_USER);
//        String password = getEncryptedResourceAttribute(RA_PASSWORD);
//        String policy = getOptionalStringResAttrVal(RA_POLICY);
//        boolean result = null != _endPort;
//        if (result) {
//            try {
//                Integer SID = null;
//                if (null != policy) {
//                    SID = _endPort.loginService(username, password, policy);
//                } else {
//                    SID = _endPort.login(username, password);
//                }
//                result = setSID(SID);
//            } catch (SOAPFaultException e) {
//                TRACE.caught2(METHOD, e);
//                throw new WavesetException(e);
//            } catch (Exception e) {
//                TRACE.caught1(METHOD, e);
//                throw new WavesetException(e);
//            }
//        }
//        TRACE.exit1(METHOD, result);
//        return result;
//    }
//
//    private UDSObject submitCreateObject(String objectType, UDSObject update, ArrayOfString attributesToReturn) throws WavesetException {
//        final String METHOD = "submitCreateObject";
//        TRACE.entry1(METHOD);
//        UDSObject result = null;
//        USDWebServiceSoap _endPort = getConnection();
//        try {
//            Holder<String> newAttributes = new Holder<String>();
//            Holder<String> handler = new Holder<String>();
//            if (TRACE.level3(METHOD)) {
//                StringBuilder sb = new StringBuilder("Create: ");
//                sb.append(update.getHandle()).append(" attributes\n");
//                for (UDSAttribute attr : update.getAttributes().getAttribute()) {
//                    sb.append("<string>").append(attr.getAttrName()).append("</string>").append("<string>").append(attr.getAttrValue()).append("</string>\n");
//                }
//                TRACE.info3(METHOD, sb.toString());
//            }
//            TRACE.list2(METHOD, "attributesToReturn", attributesToReturn.getString());
//            _endPort.createObject(getSID(), objectType, update.getStringArray(), attributesToReturn, newAttributes, handler);
//            if (null != handler.value) {
//                result = unmarshal(UDSObject.class, newAttributes.value);
//            }
//        } catch (SOAPFaultException e) {
//            TRACE.caught2(METHOD, e);
//            throw new WavesetException(e);
//        } catch (WavesetException e) {
//            throw e;
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        } finally {
//            poolConnection(_endPort);
//        }
//        TRACE.exit1(METHOD);
//        return result;
//    }
//
//    private UDSObject submitUpdateObject(UDSObject update, ArrayOfString attributesToReturn) throws WavesetException {
//        final String METHOD = "submitUpdateObject";
//        TRACE.entry1(METHOD);
//        UDSObject result = null;
//        USDWebServiceSoap _endPort = getConnection();
//        try {
//            if (null != update && null != update.getHandle() && update.getAttributes().getAttribute().size() > 0) {
//                if (TRACE.level3(METHOD)) {
//                    StringBuilder sb = new StringBuilder("Updating: ");
//                    sb.append(update.getHandle()).append(", attributes\n");
//                    for (UDSAttribute attr : update.getAttributes().getAttribute()) {
//                        sb.append("<string>").append(attr.getAttrName()).append("</string>").append("<string>").append(attr.getAttrValue()).append("</string>\n");
//                    }
//                    TRACE.info3(METHOD, sb.toString());
//                }
//                TRACE.list2(METHOD, "attributesToReturn", attributesToReturn.getString());
//                String s = _endPort.updateObject(getSID(), update.getHandle(), update.getStringArray(), attributesToReturn);
//                result = unmarshal(UDSObject.class, s);
//            }
//        } catch (SOAPFaultException e) {
//            TRACE.caught2(METHOD, e);
//            throw new WavesetException(e);
//        } catch (WavesetException e) {
//            throw e;
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        } finally {
//            poolConnection(_endPort);
//        }
//        TRACE.exit1(METHOD);
//        return result;
//    }
//
//    private UDSObject submitGetObjectValues(String handler, ArrayOfString attrVals) throws WavesetException {
//        final String METHOD = "submitGetObjectValues";
//        TRACE.entry1(METHOD);
//        UDSObject result = null;
//        USDWebServiceSoap _endPort = getConnection();
//        try {
//            TRACE.list2(METHOD, "attributesToReturn", attrVals.getString());
//            String s = _endPort.getObjectValues(getSID(), handler, attrVals);
//            result = unmarshal(UDSObject.class, s);
//        } catch (SOAPFaultException e) {
//            TRACE.caught2(METHOD, e);
//            throw new WavesetException(e);
//        } catch (WavesetException e) {
//            throw e;
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        } finally {
//            poolConnection(_endPort);
//        }
//        TRACE.exit1(METHOD);
//        return result;
//    }
//
//    private UDSObjectList submitDoSelect(String objectType, String whereClause, int maxRows, ArrayOfString attributes) throws WavesetException {
//        final String METHOD = "submitDoSelect";
//        TRACE.entry1(METHOD, new Object[]{objectType, whereClause, maxRows, attributes.getString()});
//        UDSObjectList result = null;
//        USDWebServiceSoap _endPort = getConnection();
//        try {
//            if (null == attributes) {
//                attributes = new ArrayOfString();
//            }
//            String s = _endPort.doSelect(getSID(), objectType, whereClause, maxRows, attributes);
//            result = unmarshal(UDSObjectList.class, s);
//        } catch (SOAPFaultException e) {
//            TRACE.caught2(METHOD, e);
//            throw new WavesetException(e);
//        } catch (WavesetException e) {
//            throw e;
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        } finally {
//            poolConnection(_endPort);
//        }
//        TRACE.exit1(METHOD);
//        return result;
//    }
//
//    private ListResult submitDoQuery(String objectType, String whereClause) throws WavesetException {
//        final String METHOD = "submitDoQuery";
//        TRACE.entry1(METHOD, new Object[]{objectType, whereClause});
//        ListResult result = null;
//        USDWebServiceSoap _endPort = getConnection();
//        try {
//            result = _endPort.doQuery(getSID(), objectType, whereClause != null ? whereClause : "");
//        } catch (SOAPFaultException e) {
//            TRACE.caught2(METHOD, e);
//            throw new WavesetException(e);
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        } finally {
//            poolConnection(_endPort);
//        }
//        TRACE.exit1(METHOD);
//        return result;
//    }
//
//    private UDSObjectList submitGetListValues(ListResult listResult, Integer startIndex, Integer numberOfRows, ArrayOfString attributes) throws WavesetException {
//        final String METHOD = "submitGetListValues";
//        TRACE.entry1(METHOD, new Object[]{listResult.getListHandle(), startIndex, numberOfRows});
//        UDSObjectList result = null;
//        USDWebServiceSoap _endPort = getConnection();
//        try {
//            int from = Math.max(0, startIndex);
//            int to = Math.min(listResult.getListLength() - 1, from + numberOfRows);
//            if (from < listResult.getListLength()) {
//                if (null == attributes) {
//                    attributes = new ArrayOfString();
//                }
//                TRACE.list2(METHOD, "attributes", attributes.getString());
//                String s = _endPort.getListValues(getSID(), listResult.getListHandle(), from, to, attributes);
//                result = unmarshal(UDSObjectList.class, s);
//            }
//        } catch (SOAPFaultException e) {
//            TRACE.caught2(METHOD, e);
//            throw new WavesetException(e);
//        } catch (WavesetException e) {
//            throw e;
//        } catch (Exception e) {
//            TRACE.caught1(METHOD, e);
//            throw new WavesetException(e);
//        } finally {
//            poolConnection(_endPort);
//        }
//        TRACE.exit1(METHOD);
//        return result;
//    }
//
//    private String submitGetHandleForUserid(WSUser user) throws WavesetException {
//        final String METHOD = "submitGetHandleForUserid";
//        TRACE.entry1(METHOD);
//        String userHandler = getResourceInfo(user).getAccountGUID();
//        if (null == userHandler) {
//            String userID = getIdentity(user);
//            USDWebServiceSoap _endPort = getConnection();
//            try {
//                if (null == userHandler) {
//                    ArrayOfString attributes = new ArrayOfString();
//                    attributes.getString().add(AA_USERID);
//                    String s = _endPort.doSelect(getSID(), OBJECT_TYPE_CONTACT, String.format("userid like '%s'", userID), 1, attributes);
//                    UDSObjectList result = unmarshal(UDSObjectList.class, s);
//                    if (!result.getUDSObject().isEmpty()) {
//                        UDSObject o = result.getUDSObject().get(0);
//                        userHandler = o.getHandle();
//                    }
//                }
//                //userHandler = _endPort.getHandleForUserid(getSID(), userID);
//            } catch (SOAPFaultException e) {
//                TRACE.caught2(METHOD, e);
//                //Suppress error if user not exists
//                //throw new WavesetException(e);
//            } catch (Exception e) {
//                TRACE.caught1(METHOD, e);
//                throw new WavesetException(e);
//            } finally {
//                poolConnection(_endPort);
//            }
//        }
//        TRACE.exit1(METHOD, userHandler);
//        return userHandler;
//    }
//
//    private synchronized Integer getSID() throws WavesetException {
//        String URL = (String) getRequiredResAttrVal(RA_WSDL_URL);
//        Integer SID = _sessionCacheMap.get(URL);
//        TRACE.variable1("getSID", "SID", (null != SID ? SID.toString() : "none"));
//        return SID;
//    }
//
//    private synchronized boolean setSID(Integer SID) throws WavesetException {
//        final String METHOD = "setSID";
//        TRACE.entry1(METHOD, SID.toString());
//        if (null != SID && SID > 0) {
//            String URL = (String) getRequiredResAttrVal(RA_WSDL_URL);
//            _sessionCacheMap.put(URL, SID);
//            TRACE.exit1(METHOD, SID);
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private synchronized USDWebService getWebService(String URL) throws WavesetException {
//        final String METHOD = "getWebService";
//        TRACE.entry1(METHOD, URL);
//        TRACE.exit1(METHOD, null != _serviceCacheMap.get(URL));
//        return _serviceCacheMap.get(URL);
//    }
//
//    private synchronized void setWebService(USDWebService ss) throws WavesetException {
//        final String METHOD = "setWebService";
//        TRACE.entry1(METHOD, ss != null);
//        if (null != ss) {
//            String URL = (String) getRequiredResAttrVal(RA_WSDL_URL);
//            _serviceCacheMap.put(URL, ss);
//            TRACE.exit1(METHOD);
//
//        }
//        TRACE.exit1(METHOD);
//    }
}
