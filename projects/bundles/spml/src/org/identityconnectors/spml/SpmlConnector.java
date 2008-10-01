/*
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * U.S. Government Rights - Commercial software. Government users 
 * are subject to the Sun Microsystems, Inc. standard license agreement
 * and applicable provisions of the FAR and its supplements.
 * 
 * Use is subject to license terms.
 * 
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and Project Identity 
 * Connectors are trademarks or registered trademarks of Sun 
 * Microsystems, Inc. or its subsidiaries in the U.S. and other
 * countries.
 * 
 * UNIX is a registered trademark in the U.S. and other countries,
 * exclusively licensed through X/Open Company, Ltd. 
 * 
 * -----------
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved. 
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License(CDDL) (the License).  You may not use this file
 * except in  compliance with the License. 
 * 
 * You can obtain a copy of the License at
 * http://identityconnectors.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each
 * file and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields 
 * enclosed by brackets [] replaced by your own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * -----------
 */
package org.identityconnectors.spml;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.common.CollectionUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.common.script.ScriptExecutor;
import org.identityconnectors.common.script.ScriptExecutorFactory;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.exceptions.UnknownUidException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;
import org.openspml.v2.msg.OpenContentElement;
import org.openspml.v2.msg.pass.ExpirePasswordRequest;
import org.openspml.v2.msg.pass.ExpirePasswordResponse;
import org.openspml.v2.msg.pass.SetPasswordRequest;
import org.openspml.v2.msg.pass.SetPasswordResponse;
import org.openspml.v2.msg.spml.AddRequest;
import org.openspml.v2.msg.spml.AddResponse;
import org.openspml.v2.msg.spml.DeleteRequest;
import org.openspml.v2.msg.spml.DeleteResponse;
import org.openspml.v2.msg.spml.ErrorCode;
import org.openspml.v2.msg.spml.ExecutionMode;
import org.openspml.v2.msg.spml.Extensible;
import org.openspml.v2.msg.spml.ListTargetsRequest;
import org.openspml.v2.msg.spml.ListTargetsResponse;
import org.openspml.v2.msg.spml.LookupRequest;
import org.openspml.v2.msg.spml.LookupResponse;
import org.openspml.v2.msg.spml.Modification;
import org.openspml.v2.msg.spml.ModificationMode;
import org.openspml.v2.msg.spml.ModifyRequest;
import org.openspml.v2.msg.spml.ModifyResponse;
import org.openspml.v2.msg.spml.PSO;
import org.openspml.v2.msg.spml.PSOIdentifier;
import org.openspml.v2.msg.spml.Response;
import org.openspml.v2.msg.spml.ReturnData;
import org.openspml.v2.msg.spml.StatusCode;
import org.openspml.v2.msg.spml.Target;
import org.openspml.v2.msg.spmlsearch.CloseIteratorRequest;
import org.openspml.v2.msg.spmlsearch.IterateRequest;
import org.openspml.v2.msg.spmlsearch.IterateResponse;
import org.openspml.v2.msg.spmlsearch.Query;
import org.openspml.v2.msg.spmlsearch.ResultsIterator;
import org.openspml.v2.msg.spmlsearch.Scope;
import org.openspml.v2.msg.spmlsearch.SearchRequest;
import org.openspml.v2.msg.spmlsearch.SearchResponse;
import org.openspml.v2.msg.spmlsuspend.ActiveRequest;
import org.openspml.v2.msg.spmlsuspend.ActiveResponse;
import org.openspml.v2.msg.spmlsuspend.ResumeRequest;
import org.openspml.v2.msg.spmlsuspend.SuspendRequest;
import org.openspml.v2.profiles.DSMLProfileRegistrar;
import org.openspml.v2.profiles.dsml.DSMLAttr;
import org.openspml.v2.profiles.dsml.DSMLModification;
import org.openspml.v2.profiles.dsml.DSMLValue;
import org.openspml.v2.profiles.dsml.EqualityMatch;
import org.openspml.v2.profiles.dsml.FilterItem;
import org.openspml.v2.profiles.spmldsml.AttributeDefinitionReference;
import org.openspml.v2.profiles.spmldsml.AttributeDefinitionReferences;
import org.openspml.v2.profiles.spmldsml.DSMLSchema;
import org.openspml.v2.profiles.spmldsml.ObjectClassDefinition;
import org.openspml.v2.util.Spml2Exception;
import org.openspml.v2.util.Spml2ExceptionWithResponse;
import org.openspml.v2.util.xml.ObjectFactory;


/**
 * A Connector to a SPML 2.0 Server.
 */
@ConnectorClass(
        displayNameKey="SPMLConnector",
        configurationClass= SpmlConfiguration.class)
public class SpmlConnector implements PoolableConnector, CreateOp,
DeleteOp, SearchOp<FilterItem>, UpdateOp, SchemaOp {
    private Log log = Log.getLog(SpmlConnector.class);
    private static final ScriptExecutorFactory factory = ScriptExecutorFactory.newInstance("GROOVY");
    private static final ObjectFactory.ProfileRegistrar mDSMLRegistrar = new DSMLProfileRegistrar();

    public static final String              PSOID = "psoID";

    protected SpmlConnection                _connection;
    protected SpmlConfiguration             _configuration;
    
    private ScriptExecutor                  _mapAttributeExecutor;
    private ScriptExecutor                  _mapSetNameExecutor;
    private ScriptExecutor                  _schemaExecutor;
    private Map<String, String>             _objectClassMap;
    private Map<String, String>             _targetMap;

    public SpmlConnector() {
        ObjectFactory.getInstance().register(mDSMLRegistrar);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (_connection != null) {
            _connection.dispose();
            _connection = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Configuration getConfiguration() {
        return _configuration;
    }
    
    public void checkAlive() {
        _connection.test();
    }

    /**
     * {@inheritDoc}
     */
    public void init(Configuration configuration) {
        _configuration = (SpmlConfiguration)configuration;
        _connection = SpmlConnectionFactory.newConnection(_configuration);
        String mapAttributeCommand = _configuration.getMapAttributeCommand();
        String mapSetNameCommand = _configuration.getMapSetNameCommand();
        String schemaCommand = _configuration.getSchemaCommand();
        if (mapAttributeCommand!=null && mapAttributeCommand.length()>0)
            _mapAttributeExecutor = factory.newScriptExecutor(getClass().getClassLoader(), mapAttributeCommand, true);
        if (mapSetNameCommand!=null && mapSetNameCommand.length()>0)
            _mapSetNameExecutor = factory.newScriptExecutor(getClass().getClassLoader(), mapSetNameCommand, true);
        if (schemaCommand!=null && schemaCommand.length()>0)
            _schemaExecutor = factory.newScriptExecutor(getClass().getClassLoader(), schemaCommand, true);
        _objectClassMap = new HashMap<String, String>();
        _targetMap = new HashMap<String, String>();
        if (_configuration.getObjectClassNames()!=null) {
            String[] objectClassNames = _configuration.getObjectClassNames();
            String[] spmlClassNames = _configuration.getSpmlClassNames();
            String[] targetNames = _configuration.getTargetNames();
            for (int i=0; i<objectClassNames.length; i++) {
                _objectClassMap.put(objectClassNames[i], spmlClassNames[i]);
                _targetMap.put(objectClassNames[i], targetNames[i]);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Uid create(ObjectClass objectClass, Set<Attribute> attributes, OperationOptions options) {
        try {
            Map<String, Attribute> attrMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
        	//TODO: need to discuss how to handle group membership
        	// (there may be nothing here, other than remapping name, 
        	// which is handled by scripts, but want to be sure).
            AddRequest request = new AddRequest();
            Name name = AttributeUtil.getNameFromAttributes(attributes);
            log.info("create(''{0}'')", name.getNameValue());
            request.setTargetId(getTargetForObjectClass(objectClass));
            request.setRequestID(objectClassAsString(objectClass.getObjectClassValue())+":"+name.getNameValue());
            request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
            
            // If we are enabling/disabling, that is a separate request
            //
            Attribute enable = attrMap.remove(OperationalAttributes.ENABLE_NAME);
            Attribute disableDate = attrMap.remove(OperationalAttributes.DISABLE_DATE_NAME);
            Attribute enableDate = attrMap.remove(OperationalAttributes.ENABLE_DATE_NAME);

            // If we are expiring password, that is a separate request
            //
            Attribute expirePassword = attrMap.remove(OperationalAttributes.PASSWORD_EXPIRATION_DATE_NAME);

            request.setData(getCreateAttributes(objectClass, attrMap));
            AddResponse response = (AddResponse)_connection.send(request);
            Uid uid = null;
            if (response.getStatus().equals(StatusCode.SUCCESS))
                uid = new Uid(response.getPso().getPsoID().getID());
            else
                throw new ConnectorException(asString(response.getErrorMessages()));
            processEnable(objectClass, uid, enable, disableDate, enableDate);
            processExpirePassword(objectClass, uid, expirePassword);
            return uid;
        } catch (Spml2ExceptionWithResponse e) {
            log.error(e, "create failed:''{0}''", e.getResponse().getError());
            if (e.getResponse().getError()==ErrorCode.ALREADY_EXISTS)
                throw new AlreadyExistsException();
            else
                throw ConnectorException.wrap(e);
        } catch (Exception e) {
            log.error(e, "create failed");
            throw ConnectorException.wrap(e);
        }
    }

    protected String asString(String[] strings) {
        if (strings.length==0)
            return "";
        StringBuffer buffer = new StringBuffer();
        for (String string : strings)
            buffer.append("\n"+string);
        return buffer.toString().substring(1);
    }

    protected Extensible getCreateAttributes(ObjectClass objectClass, Map<String, Attribute> attrMap) throws Exception {
        Extensible extensible = new Extensible();
        for (Attribute attribute : attrMap.values()) {
            extensible.addOpenContentElement(new DSMLAttr(mapSetName(attribute.getName()), asDSMLValueArray(attribute.getValue())));
        }
        extensible.addOpenContentElement(new DSMLAttr("objectclass", _objectClassMap.get(objectClass.getObjectClassValue())));
        return extensible;
    }

    private DSMLValue[] asDSMLValueArray(List<Object> values) {
        DSMLValue[] array = new DSMLValue[values.size()];
        for (int i=0; i<values.size(); i++) {
            Object value = values.get(i);
            if (value instanceof GuardedString) {
                GuardedStringAccessor accessor = new GuardedStringAccessor();
                ((GuardedString)value).access(accessor);
                array[i] = new DSMLValue(new String(accessor.getArray()));
                accessor.clear();
            } else {
                array[i] = new DSMLValue(value.toString());
            }
        }
        return array;
    }

    private List<Object> asValueList(DSMLValue[] values) {
        List<Object> list = new LinkedList<Object>();
        for (DSMLValue value : values) {
            list.add(value.getValue());
        }
        return list;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(ObjectClass objectClass, Uid uid, OperationOptions options) {
        try {
            DeleteRequest request = new DeleteRequest();
            PSOIdentifier pso = new PSOIdentifier();
            log.info("delete(''{0}'')", uid.getUidValue());
            pso.setID(uid.getUidValue());
            pso.setTargetID(getTargetForObjectClass(objectClass));
            request.setPsoID(pso);
            request.setRequestID(uid.getUidValue());
            request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
            DeleteResponse response = (DeleteResponse)_connection.send(request);
            if (response.getStatus().equals(StatusCode.SUCCESS))
                return;
            else
                throw new ConnectorException(asString(response.getErrorMessages()));
        } catch (Spml2ExceptionWithResponse e) {
            log.error(e, "delete failed:''{0}''", e.getResponse().getError());
            throw exceptionForId(e.getResponse());
        } catch (Exception e) {
            log.error(e, "delete failed");
            throw ConnectorException.wrap(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public FilterTranslator<FilterItem> createFilterTranslator(ObjectClass oclass, OperationOptions options) {
        return new SpmlFilterTranslator(_configuration);
    }

    /**
     * {@inheritDoc}
     */
    public void executeQuery(ObjectClass objectClass, FilterItem query, ResultsHandler handler, OperationOptions options) {
        try {
            Set<String> attributesToGet = null;
            if (options!=null && options.getAttributesToGet()!=null)
                attributesToGet = CollectionUtil.newReadOnlySet(options.getAttributesToGet());
            if (query instanceof EqualityMatch) {
                EqualityMatch equalityMatch = (EqualityMatch)query;
                String name = equalityMatch.getName();
                String value = equalityMatch.getValue().getValue();
                // We are filtering by user name
                //
                if (name.equals(PSOID) && !value.equals("*")) {
                    try {
                        ConnectorObject object = get(new Uid(value), objectClass, getTargetForObjectClass(objectClass), attributesToGet);
                        handler.handle(object);
                    } catch (UnknownUidException e) {
                        // Ignore
                    }
                    return;
                }
            }

            SearchRequest request = new SearchRequest();
            Query spmlQuery = new Query();
            spmlQuery.setScope(Scope.ONELEVEL);
            if (query!=null)
                spmlQuery.addQueryClause(new org.openspml.v2.profiles.dsml.Filter(query));
            request.setQuery(spmlQuery);
            request.setReturnData(ReturnData.EVERYTHING);
            request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
            log.info("search(''{0}'')", spmlQuery);
            SearchResponse response = (SearchResponse)_connection.send(request);
            if (!response.getStatus().equals(StatusCode.SUCCESS))
                throw new ConnectorException(asString(response.getErrorMessages()));
            PSO[] psos = response.getPSOs();
            for (PSO pso : psos) {
                log.info("search returned ''{0}'' directly", pso.getPsoID().getID());
                boolean continueQuery = handler.handle(buildConnectorObject(pso, objectClass, attributesToGet));
                if (!continueQuery) {
                    closeIterator(response.getIterator());
                }
            }
            ResultsIterator iterator = response.getIterator();
            while (iterator!=null) {
                IterateRequest iterRequest = new IterateRequest();
                iterRequest.setIterator(iterator);
                iterRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
                IterateResponse iterResponse = (IterateResponse)_connection.send(iterRequest);
                if (!iterResponse.getStatus().equals(StatusCode.SUCCESS))
                    throw new ConnectorException(asString(iterResponse.getErrorMessages()));
                psos = iterResponse.getPSOs();
                for (PSO pso : psos) {
                    log.info("search iterator returned ''{0}''", pso.getPsoID().getID());
                    boolean continueQuery = handler.handle(buildConnectorObject(pso, objectClass, attributesToGet));
                    if (!continueQuery) {
                        closeIterator(iterator);
                    }
                }
                iterator = iterResponse.getIterator();
            }
        } catch (Exception e) {
            log.error(e, "searchRequest failed");
            throw ConnectorException.wrap(e);
        }
    }

    SpmlConnection getConnection() {
        return _connection;
    }
    
    private String getTargetForObjectClass(ObjectClass objectClass) {
        return _targetMap.get(objectClass.getObjectClassValue());
    }

    private void closeIterator(ResultsIterator iterator)
            throws Spml2ExceptionWithResponse, Spml2Exception {
        CloseIteratorRequest ciRequest = new CloseIteratorRequest();
        ciRequest.setIterator(iterator);
        ciRequest.setExecutionMode(ExecutionMode.SYNCHRONOUS);
        _connection.send(ciRequest);
    }

    private ConnectorObject get(Uid uid, ObjectClass objectClass, String targetId, Set<String> attributesToGet) {
        try {
            LookupRequest request = new LookupRequest();
            PSOIdentifier psoId = new PSOIdentifier();
            psoId.setTargetID(targetId);
            psoId.setID(uid.getUidValue());
            log.info("get(''{0}'')", uid.getUidValue());
            request.setPsoID(psoId);
            request.setRequestID(uid.getUidValue());
            request.setReturnData(ReturnData.EVERYTHING);
            request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
            LookupResponse response = (LookupResponse)_connection.send(request);
            if (!response.getStatus().equals(StatusCode.SUCCESS)) {
                throw exceptionForId(response);
            }
            // Get Attributes from PSO
            //
            PSO pso = response.getPso();
            return buildConnectorObject(pso, objectClass, attributesToGet);
        } catch (Spml2ExceptionWithResponse e) {
            log.error(e, "get failed:''{0}''", e.getResponse().getError());
            throw exceptionForId(e.getResponse());
        } catch (Exception e) {
            log.error(e, "get failed");
            throw ConnectorException.wrap(e);
        }
    }
    
    private Attribute getActiveStatus(Uid uid, String targetId) {
        try {
            ActiveRequest request = new ActiveRequest();
            PSOIdentifier psoId = new PSOIdentifier();
            psoId.setTargetID(targetId);
            psoId.setID(uid.getUidValue());
            log.info("getActiveStatus(''{0}'')", uid.getUidValue());
            request.setPsoID(psoId);
            request.setRequestID(uid.getUidValue());
            request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
            ActiveResponse response = (ActiveResponse)_connection.send(request);
            if (!response.getStatus().equals(StatusCode.SUCCESS)) {
                throw exceptionForId(response);
            }
            return AttributeBuilder.build(OperationalAttributes.ENABLE_NAME, Boolean.valueOf(response.getActive()));
        } catch (Spml2ExceptionWithResponse e) {
            log.error(e, "ActiveRequest failed:''{0}''", e.getResponse().getError());
            throw exceptionForId(e.getResponse());
        } catch (Exception e) {
            log.error(e, "ActiveRequest failed");
            throw ConnectorException.wrap(e);
        }
    }

    private ConnectorObject buildConnectorObject(PSO pso, ObjectClass objectClass, Set<String> attributesToGet) throws Exception {
        ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
        Uid uid = new Uid(pso.getPsoID().getID());
        builder.setUid(uid);
        OpenContentElement[] psoElements = pso.getData().getOpenContentElements();
        String name = null;
        for (OpenContentElement element : psoElements) {
            if (element instanceof DSMLAttr) {
                DSMLAttr attr = (DSMLAttr)element;
                String attrName = attr.getName();
                if (attrName.equals(_configuration.getNameAttribute()))
                    name = attr.getValues()[0].getValue();
                if (attributesToGet==null || attrName.equals(Name.NAME) || attributesToGet.contains(attrName)) {
                    builder.addAttribute(mapAttribute(AttributeBuilder.build(attrName, asValueList(attr.getValues()))));
                }
            }
        }
        builder.setObjectClass(objectClass);
        if (attributesToGet==null || attributesToGet.contains(OperationalAttributes.ENABLE_NAME)) {
            builder.addAttribute(getActiveStatus(uid, getTargetforObjectClass(objectClass)));
        }
        builder.setName(name);
        return builder.build();
    }
    
    private String getTargetforObjectClass(ObjectClass objectClass) {
    	String[] classNames = _configuration.getObjectClassNames();
    	String[] targetNames = _configuration.getTargetNames();
    	for (int i=0; i<classNames.length; i++) {
    		if (classNames[i].equals(objectClass.getObjectClassValue()))
    			return targetNames[i];
    	}
    	return null;
    }

    private PSOIdentifier getPsoIdentifier(Uid uid, ObjectClass objectClass) {
        PSOIdentifier pso = new PSOIdentifier();
        pso.setID(uid.getUidValue());
        pso.setTargetID(getTargetForObjectClass(objectClass));
        return pso;
    }

    /**
     * {@inheritDoc}
     */
    public Uid update(ObjectClass objectClass, Set<Attribute> attributes, OperationOptions options) {
        try {
            Map<String, Attribute> attrMap = new HashMap<String, Attribute>(AttributeUtil.toMap(attributes));
            Uid uid = (Uid)attrMap.remove(Uid.NAME);
            Name name = (Name)attrMap.remove(Name.NAME);

            // If we are enabling/disabling, that is a separate request
            //
            Attribute enable = attrMap.remove(OperationalAttributes.ENABLE_NAME);
            Attribute disableDate = attrMap.remove(OperationalAttributes.DISABLE_DATE_NAME);
            Attribute enableDate = attrMap.remove(OperationalAttributes.ENABLE_DATE_NAME);
            processEnable(objectClass, uid, enable, disableDate, enableDate);
            
            // If we are changing password, that is a separate request
            //
            Attribute password = attrMap.remove(OperationalAttributes.PASSWORD_NAME);
            processPassword(objectClass, uid, password);

            // If we are expiring password, that is a separate request
            //
            Attribute expirePassword = attrMap.remove(OperationalAttributes.PASSWORD_EXPIRATION_DATE_NAME);
            processExpirePassword(objectClass, uid, expirePassword);

            // Remaining attributes are handled here
            //
            if (attrMap.size()>0) {
                ModifyRequest request = new ModifyRequest();
                setModifications(request, objectClass, attrMap);
                request.setPsoID(getPsoIdentifier(uid, objectClass));
                request.setRequestID(uid.getUidValue());
                request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
                request.setReturnData(ReturnData.EVERYTHING);
                log.info("update(''{0}''", uid.getUidValue());
                ModifyResponse response = (ModifyResponse)_connection.send(request);
                if (!response.getStatus().equals(StatusCode.SUCCESS)) {
                    log.error("update failed:''{0}''", response.getError());
                    throw exceptionForId(response);
                }
             }
            return uid;
        } catch (Spml2ExceptionWithResponse e) {
            log.error(e, "update failed:''{0}''", e.getResponse().getError());
            throw exceptionForId(e.getResponse());
        } catch (Exception e) {
            log.error(e, "update failed");
            throw ConnectorException.wrap(e);
        }
    }

	private void processPassword(ObjectClass objectClass, Uid uid,
			Attribute password) throws Spml2ExceptionWithResponse,
			Spml2Exception {
		if (password!=null) {
		    SetPasswordRequest request = new SetPasswordRequest();
		    GuardedString passwordGS = AttributeUtil.getGuardedStringValue(password);
		    GuardedStringAccessor accessor = new GuardedStringAccessor();
		    passwordGS.access(accessor);
		    String passwordString = new String(accessor.getArray());
		    accessor.clear();
		    request.setPassword(passwordString);
		    request.setPsoID(getPsoIdentifier(uid, objectClass));
		    request.setRequestID(uid.getUidValue());
		    request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
		    log.info("change password(''{0}'')", uid.getUidValue());
		    SetPasswordResponse response = (SetPasswordResponse)_connection.send(request);
		    if (!response.getStatus().equals(StatusCode.SUCCESS)) {
		        log.error("change password failed:''{0}''", response.getError());
		        throw exceptionForId(response);
		    }
		 }
	}

	private void processExpirePassword(ObjectClass objectClass, Uid uid,
			Attribute expirePassword) throws Spml2ExceptionWithResponse,
			Spml2Exception {
		if (expirePassword!=null) {
		    ExpirePasswordRequest request = new ExpirePasswordRequest();
		    request.setRemainingLogins(0);
		    request.setPsoID(getPsoIdentifier(uid, objectClass));
		    request.setRequestID(uid.getUidValue());
		    request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
		    log.info("expire password(''{0}''", uid.getUidValue());
		    ExpirePasswordResponse response = (ExpirePasswordResponse)_connection.send(request);
		    if (!response.getStatus().equals(StatusCode.SUCCESS)) {
		        log.error("expire password failed:''{0}''", response.getError());
		        throw exceptionForId(response);
		    }
		}
	}

	private void processEnable(ObjectClass objectClass, Uid uid,
			Attribute enable, Attribute disableDate, Attribute enableDate)
			throws Spml2ExceptionWithResponse, Spml2Exception {
		if (enable!=null) {
		    boolean isEnable = AttributeUtil.getBooleanValue(enable);
		    if (isEnable) {
		        ResumeRequest request = new ResumeRequest();
		        Long date = enableDate!=null?AttributeUtil.getLongValue(enableDate):null;
		        if (date!=null) {
		            // Date must be specified as UTC date with no Time Zone component
		            //
		            Date effectiveDate = new Date(date);
		            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS").format(effectiveDate);
		            request.setEffectiveDate(dateString);
		        }
		        request.setPsoID(getPsoIdentifier(uid, objectClass));
		        request.setRequestID(uid.getUidValue());
		        request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
		        log.info("enable(''{0}'')", uid.getUidValue());
		        Response response = _connection.send(request);
		        if (!response.getStatus().equals(StatusCode.SUCCESS)) {
		            log.error("enable failed:''{0}''", response.getError());
		            throw exceptionForId(response);
		        }
		    } else {
		        SuspendRequest request = new SuspendRequest();
		        Long date = disableDate!=null?AttributeUtil.getLongValue(disableDate):null;
		        if (date!=null) {
		            // Date must be specified as UTC date with no Time Zone component
		            //
		            Date effectiveDate = new Date(date);
		            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS").format(effectiveDate);
		            request.setEffectiveDate(dateString);
		        }
		        request.setPsoID(getPsoIdentifier(uid, objectClass));
		        request.setRequestID(uid.getUidValue());
		        request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
		        log.info("disable(''{0}'')", uid.getUidValue());
		        Response response = _connection.send(request);
		        if (!response.getStatus().equals(StatusCode.SUCCESS)) {
		            log.error("disable failed:''{0}''", response.getError());
		            throw exceptionForId(response);
		        }
		    }
		}
	}

    protected void setModifications(ModifyRequest modifyRequest, ObjectClass objectClass, Map<String, Attribute> attributes) throws Exception {
        for (Attribute attribute : attributes.values()) {
            Modification modification = new Modification();
            modification.addOpenContentElement(new DSMLModification(mapSetName(attribute.getName()), asDSMLValueArray(attribute.getValue()), ModificationMode.REPLACE));
            modifyRequest.addModification(modification);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Schema schema() {
        final SchemaBuilder schemaBuilder = new SchemaBuilder(getClass());

        try {
            ListTargetsRequest request = new ListTargetsRequest();
            request.setExecutionMode(ExecutionMode.SYNCHRONOUS);
            log.info("listTargets");
            ListTargetsResponse response = (ListTargetsResponse)_connection.send(request);
            if (!response.getStatus().equals(StatusCode.SUCCESS)) {
                log.error("listTargets failed:''{0}''", response.getError());
                throw new ConnectorException(asString(response.getErrorMessages()));
            }
            Target[] targets = response.getTargets();
            String[] spmlClassNames = _configuration.getSpmlClassNames();
            String[] objectClassNames = _configuration.getObjectClassNames();
            int length = spmlClassNames==null?0:spmlClassNames.length;
            
            for (Target target : targets) {
                org.openspml.v2.msg.spml.Schema[] schemas = target.getSchemas();
                for (org.openspml.v2.msg.spml.Schema schema : schemas) {
                    for (OpenContentElement element : schema.getOpenContentElements()) {
                        if (element instanceof DSMLSchema) {
                            DSMLSchema dsmlSchema = (DSMLSchema)element;
                            for (ObjectClassDefinition ocd : dsmlSchema.getObjectClassDefinitions()) {
                                for (int i=0; i<length; i++) {
                                    if (spmlClassNames[i].equals(ocd.getName())) {
                                        Set<AttributeInfo> attributes = new HashSet<AttributeInfo>();
                                        AttributeDefinitionReferences refs = ocd.getMemberAttributes();
                                        fillInSchemaForObjectClass(schemaBuilder, objectClassNames[i],
                                                refs, attributes);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Spml2ExceptionWithResponse e) {
            log.error(e, "update failed:''{0}''", e.getResponse().getError());
            throw new ConnectorException(asString(e.getResponse().getErrorMessages()));
        } catch (Exception e) {
            log.error(e, "listTargets failed");
            throw ConnectorException.wrap(e);
        }

        return schemaBuilder.build();
    }
    
    private void fillInSchemaForObjectClass(final SchemaBuilder schemaBuilder,
            String objectClass, AttributeDefinitionReferences refs, 
            Set<AttributeInfo> attributes) throws Exception {
        for (AttributeDefinitionReference ref : refs.getAttributeDefinitionReferences()) {
            boolean required = false;
            if (ref.getRequired()!=null)
                required = ref.getRequired();
            attributes.add(AttributeInfoBuilder.build(ref.getName(), required, true, true, true));
        }
        updateSchema(objectClass, attributes);
        schemaBuilder.defineObjectClass(objectClass, attributes);
    }

    private String mapSetName(String name) throws Exception {
        if (_mapSetNameExecutor!=null) {
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put("name", name);
            arguments.put("configuration", _configuration);
            return (String)_mapSetNameExecutor.execute(arguments);
        }
        return name;
    }

    private void updateSchema(String objectClass, Set<AttributeInfo> attributeInfos) throws Exception {
        if (_mapSetNameExecutor!=null) {
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put("objectClass", objectClass);
            arguments.put("attributeInfos", attributeInfos);
            _schemaExecutor.execute(arguments);
        }
    }

    private Attribute mapAttribute(Attribute attribute) throws Exception {
        if (_mapAttributeExecutor!=null) {
            Map<String, Object> arguments = new HashMap<String, Object>();
            arguments.put("attribute", attribute);
            arguments.put("configuration", _configuration);
            return (Attribute)_mapAttributeExecutor.execute(arguments);
        }
        return attribute;
    }

    private ConnectorException exceptionForId(Response response) {
        if (response.getError()==ErrorCode.NO_SUCH_IDENTIFIER)
            return new UnknownUidException();
        else
            return new ConnectorException(asString(response.getErrorMessages()));
    }

    private String objectClassAsString(String objectClass) {
        if (_objectClassMap.containsKey(objectClass)) {
            return _objectClassMap.get(objectClass);
        } else {
            throw new ConnectorException(_configuration.getMessage(SpmlMessages.UNSUPPORTED_OBJECTCLASS, objectClass));
        }
    }
}
