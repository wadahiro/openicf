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

import java.io.StringReader;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.PredefinedAttributeInfos;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;
import org.identityconnectors.framework.common.objects.OperationalAttributeInfos;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.TestOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.AuthenticateOp;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
import org.forgerock.openicf.usd.ca.ArrayOfString;
import org.forgerock.openicf.usd.ca.ListResult;
import org.forgerock.openicf.usd.ca.USDMessages;
import org.forgerock.openicf.usd.ca.USDWebService;
import org.forgerock.openicf.usd.ca.USDWebServiceSoap;
import org.forgerock.openicf.usd.ca.uds.UDSAttribute;
import org.forgerock.openicf.usd.ca.uds.UDSObject;
import org.forgerock.openicf.usd.ca.uds.UDSObjectList;
import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.PredefinedAttributes;
import org.xml.sax.InputSource;

/**
 * Main implementation of the Tivoli Access Manager Connector
 * 
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */
@ConnectorClass(displayNameKey = "CAServiceDesk", configurationClass = CAServiceDeskConfiguration.class)
public class CAServiceDeskConnector implements PoolableConnector, AuthenticateOp, CreateOp, SchemaOp, TestOp, DeleteOp, UpdateOp, SearchOp<String> {

    /**
     * Setup logging for the {@link CAServiceDeskConnector}.
     */
    private static final Log TRACE = Log.getLog(CAServiceDeskConnector.class);
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
            TRACE.error(ex, "STATIC_INITIALISE");
        }
    }
    private Integer _sessionID = null;
    ////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////
    /**
     * Place holder for the {@link Configuration} passed into the init() method
     * {@link CAServiceDeskConnector#init}.
     */
    private CAServiceDeskConfiguration configuration;
    /**
     * Place holder for the {@link Schema} create in the schema() method
     * {@link CAServiceDeskConnector#schema}.
     */
    private Schema schema;
    // Resource Attributes
//    public static final String ATTR_REGISTRY_NAME = "registryName";
//    public static final String ATTR_FIRST_NAME = "firstname";
//    public static final String ATTR_LAST_NAME = "lastname";
//    public static final String ATTR_SSO_USER = "ssoUser";
//    public static final String ATTR_PASSWORD_POLICY = "passwordPolicy";
//    public static final String ATTR_EXPIRE_PASSWORD = "expirePassword";
//    public static final String ATTR_DELETE_FROM_REGISTRY = "deleteFromRegistry";
//    public static final String ATTR_SYNC_GSO_CREDENTIALS = "syncGSOCredentials";
//    public static final String ATTR_IMPORT_FROM_REGISTRY = "importFromRegistry";
//    public static final String ATTR_GROUP_MEMBERS = "members";
//    public static final String ATTR_GSO_WEB_CREDENTIALS = "gsoWebCredentials";
//    public static final String ATTR_GSO_GROUP_CREDENTIALS = "gsoGroupCredentials";
//    public static final String TYPE_GSO_GROUP_RESOURCE = "GSOGroupResource";
//    public static final String TYPE_GSO_RESOURCE = "GSOWebResource";
//    public static final String TOKEN_GSO_RESOURCE = "|:";

    /**
     * Gets the Configuration context for this connector.
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Callback method to receive the {@link Configuration}.
     * 
     * @see Connector#init
     */
    /**
     * 
     * @see org.identityconnectors.framework.spi.Connector#init(org.identityconnectors.framework.spi.Configuration)
     */
    public void init(Configuration config) {
        this.configuration = (CAServiceDeskConfiguration) config;
        TRACE.info("initialized Connector");
    }

    /**
     * Disposes of the {@link TAMConnector}'s resources.
     * 
     * @see Connector#dispose()
     */
    public void dispose() {
    }

    public void checkAlive() {
    }

    /**
     * {@inheritDoc}
     */
    public Uid create(final ObjectClass objClass, final Set<Attribute> attributes, final OperationOptions options) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public FilterTranslator<String> createFilterTranslator(ObjectClass objClass, OperationOptions options) {
        return new CAServiceDeskFilterTranslator();
    }

    /**
     * {@inheritDoc}
     */
    public void executeQuery(ObjectClass objClass, String query, ResultsHandler handler, OperationOptions options) {
    }

    /**
     * {@inheritDoc}
     */
    public void test() {
        final String METHOD = "testConfiguration";
        TRACE.info(METHOD);
        Pattern simple = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*$");
        Pattern readonly = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*\\.[a-zA-Z_][0-9a-zA-Z_]*$");
        Pattern complex = Pattern.compile("^[a-zA-Z_][0-9a-zA-Z_]*:[a-zA-Z_][0-9a-zA-Z_]*\\.[a-zA-Z_][0-9a-zA-Z_]*$");
        for (String rhlname : ((Map<String, Object>) getSchemaMap()).keySet()) {
            if (!(simple.matcher(rhlname).matches() || readonly.matcher(rhlname).matches() || complex.matcher(rhlname).matches())) {
                ConnectorException e = new ConnectorException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_INVALID_RESOURCEATTRIBUTE, new Object[]{rhlname}));
                TRACE.error(METHOD, e);
                e.printStackTrace();
                throw e;
            }
        }
        startConnection();
        TRACE.info(METHOD);
        throw new ConnectorException();
    }

    /**
     * {@inheritDoc}
     */
    public Uid update(ObjectClass objClass,
            Uid uid,
            Set<Attribute> replaceAttributes,
            OperationOptions options) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final ObjectClass objClass, final Uid uid, final OperationOptions options) {
    }

    /**
     * {@inheritDoc}
     */
    public Schema schema() {
        if (null == schema) {
            SchemaBuilder schemaBuilder = new SchemaBuilder(getClass());

//            // GSO WEB Resource
//            ObjectClassInfoBuilder ocBuilder = new ObjectClassInfoBuilder();
//            ocBuilder.setType(TYPE_GSO_RESOURCE);
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
//            ObjectClassInfo objectClassInfo = ocBuilder.build();
//            schemaBuilder.defineObjectClass(objectClassInfo);
//            schemaBuilder.removeSupportedObjectClass(CreateOp.class, objectClassInfo);
//            schemaBuilder.removeSupportedObjectClass(UpdateOp.class, objectClassInfo);
//            schemaBuilder.removeSupportedObjectClass(DeleteOp.class, objectClassInfo);
//
//
//            // GSO GROUP Resource
//            ocBuilder = new ObjectClassInfoBuilder();
//            ocBuilder.setType(TYPE_GSO_GROUP_RESOURCE);
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
//            objectClassInfo = ocBuilder.build();
//            schemaBuilder.defineObjectClass(objectClassInfo);
//            schemaBuilder.removeSupportedObjectClass(CreateOp.class, objectClassInfo);
//            schemaBuilder.removeSupportedObjectClass(UpdateOp.class, objectClassInfo);
//            schemaBuilder.removeSupportedObjectClass(DeleteOp.class, objectClassInfo);
//
//
//            // Group
//            ocBuilder = new ObjectClassInfoBuilder();
//            ocBuilder.setType(ObjectClass.GROUP_NAME);
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
//            //ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Uid.NAME, String.class, EnumSet.of(Flags.REQUIRED)));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_REGISTRY_NAME, String.class, EnumSet.of(Flags.REQUIRED)));
//            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_GROUP_MEMBERS, String.class, EnumSet.of(Flags.MULTIVALUED)));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_IMPORT_FROM_REGISTRY, Boolean.class));
//            schemaBuilder.defineObjectClass(ocBuilder.build());
//
//
//            // Users
//            ocBuilder = new ObjectClassInfoBuilder();
//            ocBuilder.setType(ObjectClass.ACCOUNT_NAME);
//            //The name of the object
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(Name.NAME, String.class, EnumSet.of(Flags.REQUIRED, Flags.NOT_UPDATEABLE)));
//            //User registry name
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_REGISTRY_NAME, String.class, EnumSet.of(Flags.REQUIRED, Flags.NOT_UPDATEABLE)));
//            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.DESCRIPTION);
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_FIRST_NAME, String.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_LAST_NAME, String.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
//            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.LAST_LOGIN_DATE);
//            ocBuilder.addAttributeInfo(PredefinedAttributeInfos.GROUPS);
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_SSO_USER, Boolean.class));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_IMPORT_FROM_REGISTRY, Boolean.class));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_EXPIRE_PASSWORD, Boolean.class));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_PASSWORD_POLICY, Boolean.class, EnumSet.of(Flags.NOT_UPDATEABLE)));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_GSO_GROUP_CREDENTIALS, String.class, EnumSet.of(Flags.MULTIVALUED)));
//            ocBuilder.addAttributeInfo(AttributeInfoBuilder.build(ATTR_GSO_WEB_CREDENTIALS, String.class, EnumSet.of(Flags.MULTIVALUED)));
//            ocBuilder.addAttributeInfo(OperationalAttributeInfos.PASSWORD);
//            ocBuilder.addAttributeInfo(OperationalAttributeInfos.ENABLE);
//            schemaBuilder.defineObjectClass(ocBuilder.build());
            schema = schemaBuilder.build();
        }
        return schema;
    }

    public Uid authenticate(ObjectClass oc, String user, GuardedString gs, OperationOptions oo) {
        return null;
    }

    private USDWebServiceSoap getConnection() throws ConnectorException {
        USDWebServiceSoap _endPort = null;
        if (null == _endPort) {
            USDWebService ss = null;
            if (null == ss) {
                URL wsdlURL = null;
                try {
                    wsdlURL = new URL(configuration.getUSDWebServiceWSDL());
                    ss = new USDWebService(wsdlURL, SERVICE_NAME);
                } catch (Exception e) {
                    throw new ConnectorException(e);
                }
            }

            if (null != ss) {
                try {
                    if (null != configuration.getUSDWebServiceLocation()) {
                        _endPort = ss.getUSDWebServiceSoap();
                        Map<String, Object> ctx = ((BindingProvider) _endPort).getRequestContext();
                        ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, configuration.getUSDWebServiceLocation());
                    } else {
                        _endPort = ss.getUSDWebServiceSoap();
                    }
                } catch (Exception e) {
                    // @TODO: Fix error message
                    TRACE.error(e, "");
                    throw new ConnectorException(e);
                }
            }
        }
        if (_endPort == null) {
            ConnectorException e = new ConnectorException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_START_CONNECTION, new Object[]{wsdl_url}));
            // @TODO: Fix error message
            TRACE.error(e, "");
            throw e;
        }

        try {
            connectionIsVerified = (connectionIsVerified || submitServerStatus(_endPort) || submitLogin(_endPort));
        } catch (Exception e) {
            // @TODO: Fix error message
            TRACE.error(e, "");
            throw new ConnectorException(e);
        }
        if (!connectionIsVerified) {
            ConnectorException e = new ConnectorException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_AUTHENTICATE_CONNECTION, new Object[]{wsdl_url}));
            // @TODO: Fix error message
            TRACE.error(e, "");
            throw e;
        }
        return _endPort;
    }

    private synchronized <T> T unmarshal(Class<T> UDSClass, String inputString) throws ConnectorException {
        final String METHOD = "unmarshal";
        if (TRACE.isInfo()) {
            TRACE.info("inputString=\"{}\"", inputString);
        }
        if (null != inputString) {
            StringReader localStringReader = new StringReader(inputString);
            InputSource localInputSource = new InputSource(localStringReader);
            try {
                return (T) unmarshaller.unmarshal(localInputSource);
            } catch (JAXBException ex) {
                TRACE.error("JAXB Exception", ex);
                //throw new ConnectorException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_UNMARSHAL_ERROR, inputString), ex);
            } catch (Exception e) {
                TRACE.error(METHOD, e);
                throw new ConnectorException(e);
            }
        } else {
            ConnectorException ex = new ConnectorException(new ErrorMessage(Severity.ERROR, USDMessages.CAS_ERROR_UNMARSHAL_NULLINPUT));
            TRACE.error(METHOD, ex);
            throw ex;
        }
    }

    private void changeDisabledStatus(WSUser user, boolean disable, ConnectorException result) throws ConnectorException {
        TRACE.info("Enter");
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
        TRACE.info("Exit");
    }

    private boolean submitServerStatus(USDWebServiceSoap _endPort) throws ConnectorException {
        final String METHOD = "submitServerStatus";
        TRACE.entry1(METHOD);
        Integer SID = getSID();
        boolean accessable = (null != _endPort && null != SID);
        if (accessable) {
            try {
                /* Returns
                 * The following values apply:
                 * 1 =Indicates the Service Desk server is not available
                 * 0= Indicates the Service Desk server it is running
                 */
                accessable = 0 == _endPort.serverStatus(SID);
            } catch (SOAPFaultException e) {
                TRACE.caught3(METHOD, e);
                accessable = false;
            }
        }
        TRACE.info(METHOD, accessable);
        return accessable;
    }

    private boolean submitLogin(USDWebServiceSoap _endPort) throws ConnectorException {
        final String METHOD = "submitLogin";
        TRACE.entry1(METHOD);
        String username = (String) getRequiredResAttrVal(RA_USER);
        String password = getEncryptedResourceAttribute(RA_PASSWORD);
        String policy = getOptionalStringResAttrVal(RA_POLICY);
        boolean result = null != _endPort;
        if (result) {
            try {
                Integer SID = null;
                if (null != policy) {
                    SID = _endPort.loginService(username, password, policy);
                } else {
                    SID = _endPort.login(username, password);
                }
                result = setSID(SID);
            } catch (SOAPFaultException e) {
                TRACE.caught2(METHOD, e);
                throw new ConnectorException(e);
            } catch (Exception e) {
                TRACE.error(METHOD, e);
                throw new ConnectorException(e);
            }
        }
        TRACE.info(METHOD, result);
        return result;
    }

    private UDSObject submitCreateObject(String objectType, UDSObject update, ArrayOfString attributesToReturn) throws ConnectorException {
        final String METHOD = "submitCreateObject";
        TRACE.entry1(METHOD);
        UDSObject result = null;
        USDWebServiceSoap _endPort = getConnection();
        try {
            Holder<String> newAttributes = new Holder<String>();
            Holder<String> handler = new Holder<String>();
            if (TRACE.level3(METHOD)) {
                StringBuilder sb = new StringBuilder("Create: ");
                sb.append(update.getHandle()).append(" attributes\n");
                for (UDSAttribute attr : update.getAttributes().getAttribute()) {
                    sb.append("<string>").append(attr.getAttrName()).append("</string>").append("<string>").append(attr.getAttrValue()).append("</string>\n");
                }
                TRACE.info3(METHOD, sb.toString());
            }
            TRACE.list2(METHOD, "attributesToReturn", attributesToReturn.getString());
            _endPort.createObject(getSID(), objectType, update.getStringArray(), attributesToReturn, newAttributes, handler);
            if (null != handler.value) {
                result = unmarshal(UDSObject.class, newAttributes.value);
            }
        } catch (SOAPFaultException e) {
            TRACE.caught2(METHOD, e);
            throw new ConnectorException(e);
        } catch (ConnectorException e) {
            throw e;
        } catch (Exception e) {
            TRACE.error(METHOD, e);
            throw new ConnectorException(e);
        } finally {
            poolConnection(_endPort);
        }
        TRACE.info(METHOD);
        return result;
    }

    private UDSObject submitUpdateObject(UDSObject update, ArrayOfString attributesToReturn) throws ConnectorException {
        final String METHOD = "submitUpdateObject";
        TRACE.entry1(METHOD);
        UDSObject result = null;
        USDWebServiceSoap _endPort = getConnection();
        try {
            if (null != update && null != update.getHandle() && update.getAttributes().getAttribute().size() > 0) {
                if (TRACE.level3(METHOD)) {
                    StringBuilder sb = new StringBuilder("Updating: ");
                    sb.append(update.getHandle()).append(", attributes\n");
                    for (UDSAttribute attr : update.getAttributes().getAttribute()) {
                        sb.append("<string>").append(attr.getAttrName()).append("</string>").append("<string>").append(attr.getAttrValue()).append("</string>\n");
                    }
                    TRACE.info3(METHOD, sb.toString());
                }
                TRACE.list2(METHOD, "attributesToReturn", attributesToReturn.getString());
                String s = _endPort.updateObject(getSID(), update.getHandle(), update.getStringArray(), attributesToReturn);
                result = unmarshal(UDSObject.class, s);
            }
        } catch (SOAPFaultException e) {
            TRACE.caught2(METHOD, e);
            throw new ConnectorException(e);
        } catch (ConnectorException e) {
            throw e;
        } catch (Exception e) {
            TRACE.error(METHOD, e);
            throw new ConnectorException(e);
        } finally {
            poolConnection(_endPort);
        }
        TRACE.info(METHOD);
        return result;
    }

    private UDSObject submitGetObjectValues(String handler, ArrayOfString attrVals) throws ConnectorException {
        final String METHOD = "submitGetObjectValues";
        TRACE.entry1(METHOD);
        UDSObject result = null;
        USDWebServiceSoap _endPort = getConnection();
        try {
            TRACE.list2(METHOD, "attributesToReturn", attrVals.getString());
            String s = _endPort.getObjectValues(getSID(), handler, attrVals);
            result = unmarshal(UDSObject.class, s);
        } catch (SOAPFaultException e) {
            TRACE.caught2(METHOD, e);
            throw new ConnectorException(e);
        } catch (ConnectorException e) {
            throw e;
        } catch (Exception e) {
            TRACE.error(METHOD, e);
            throw new ConnectorException(e);
        } finally {
            poolConnection(_endPort);
        }
        TRACE.info(METHOD);
        return result;
    }

    private UDSObjectList submitDoSelect(String objectType, String whereClause, int maxRows, ArrayOfString attributes) throws ConnectorException {
        final String METHOD = "submitDoSelect";
        TRACE.entry1(METHOD, new Object[]{objectType, whereClause, maxRows, attributes.getString()});
        UDSObjectList result = null;
        USDWebServiceSoap _endPort = getConnection();
        try {
            if (null == attributes) {
                attributes = new ArrayOfString();
            }
            String s = _endPort.doSelect(getSID(), objectType, whereClause, maxRows, attributes);
            result = unmarshal(UDSObjectList.class, s);
        } catch (SOAPFaultException e) {
            TRACE.caught2(METHOD, e);
            throw new ConnectorException(e);
        } catch (ConnectorException e) {
            throw e;
        } catch (Exception e) {
            TRACE.error(METHOD, e);
            throw new ConnectorException(e);
        } finally {
            poolConnection(_endPort);
        }
        TRACE.info(METHOD);
        return result;
    }

    private ListResult submitDoQuery(String objectType, String whereClause) throws ConnectorException {
        final String METHOD = "submitDoQuery";
        TRACE.entry1(METHOD, new Object[]{objectType, whereClause});
        ListResult result = null;
        USDWebServiceSoap _endPort = getConnection();
        try {
            result = _endPort.doQuery(getSID(), objectType, whereClause != null ? whereClause : "");
        } catch (SOAPFaultException e) {
            TRACE.caught2(METHOD, e);
            throw new ConnectorException(e);
        } catch (Exception e) {
            TRACE.error(METHOD, e);
            throw new ConnectorException(e);
        } finally {
            poolConnection(_endPort);
        }
        TRACE.info(METHOD);
        return result;
    }

    private UDSObjectList submitGetListValues(ListResult listResult, Integer startIndex, Integer numberOfRows, ArrayOfString attributes) throws ConnectorException {
        final String METHOD = "submitGetListValues";
        TRACE.entry1(METHOD, new Object[]{listResult.getListHandle(), startIndex, numberOfRows});
        UDSObjectList result = null;
        USDWebServiceSoap _endPort = getConnection();
        try {
            int from = Math.max(0, startIndex);
            int to = Math.min(listResult.getListLength() - 1, from + numberOfRows);
            if (from < listResult.getListLength()) {
                if (null == attributes) {
                    attributes = new ArrayOfString();
                }
                TRACE.list2(METHOD, "attributes", attributes.getString());
                String s = _endPort.getListValues(getSID(), listResult.getListHandle(), from, to, attributes);
                result = unmarshal(UDSObjectList.class, s);
            }
        } catch (SOAPFaultException e) {
            TRACE.caught2(METHOD, e);
            throw new ConnectorException(e);
        } catch (ConnectorException e) {
            throw e;
        } catch (Exception e) {
            TRACE.error(METHOD, e);
            throw new ConnectorException(e);
        } finally {
            poolConnection(_endPort);
        }
        TRACE.info(METHOD);
        return result;
    }

    private String submitGetHandleForUserid(WSUser user) throws ConnectorException {
        final String METHOD = "submitGetHandleForUserid";
        TRACE.entry1(METHOD);
        String userHandler = getResourceInfo(user).getAccountGUID();
        if (null == userHandler) {
            String userID = getIdentity(user);
            USDWebServiceSoap _endPort = getConnection();
            try {
                if (null == userHandler) {
                    ArrayOfString attributes = new ArrayOfString();
                    attributes.getString().add(AA_USERID);
                    String s = _endPort.doSelect(getSID(), OBJECT_TYPE_CONTACT, String.format("userid like '%s'", userID), 1, attributes);
                    UDSObjectList result = unmarshal(UDSObjectList.class, s);
                    if (!result.getUDSObject().isEmpty()) {
                        UDSObject o = result.getUDSObject().get(0);
                        userHandler = o.getHandle();
                    }
                }
                //userHandler = _endPort.getHandleForUserid(getSID(), userID);
            } catch (SOAPFaultException e) {
                TRACE.caught2(METHOD, e);
                //Suppress error if user not exists
                //throw new ConnectorException(e);
            } catch (Exception e) {
                TRACE.error(METHOD, e);
                throw new ConnectorException(e);
            } finally {
                poolConnection(_endPort);
            }
        }
        TRACE.info(METHOD, userHandler);
        return userHandler;
    }

    private synchronized Integer getSID() throws ConnectorException {
        String URL = (String) getRequiredResAttrVal(RA_WSDL_URL);
        Integer SID = _sessionCacheMap.get(URL);
        TRACE.variable1("getSID", "SID", (null != SID ? SID.toString() : "none"));
        return SID;
    }

    private synchronized boolean setSID(Integer SID) throws ConnectorException {
        final String METHOD = "setSID";
        TRACE.entry1(METHOD, SID.toString());
        if (null != SID && SID > 0) {
            String URL = (String) getRequiredResAttrVal(RA_WSDL_URL);
            _sessionCacheMap.put(URL, SID);
            TRACE.info(METHOD, SID);
            return true;
        } else {
            return false;
        }
    }
}
