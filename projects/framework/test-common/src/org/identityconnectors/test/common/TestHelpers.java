/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.     
 * 
 * The contents of this file are subject to the terms of the Common Development 
 * and Distribution License("CDDL") (the "License").  You may not use this file 
 * except in compliance with the License.
 * 
 * You can obtain a copy of the License at 
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields 
 * enclosed by brackets [] replaced by your own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.test.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.operations.SearchApiOp;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.test.common.spi.TestHelpersSpi;

/**
 * Bag of utility methods useful to connector tests.
 */
public final class TestHelpers {

    private static final Object LOCK = new Object();

    private TestHelpers() {
    }

    /**
     * Method for convenient testing of local connectors.
     */
    public static APIConfiguration createTestConfiguration(
            Class<? extends Connector> clazz, Configuration config) {
        return getSpi().createTestConfiguration(clazz, config);
    }

    /**
     * Fills a configuration bean with data from the given map. The map
     * keys are configuration property names and the values are
     * configuration property values.
     * 
     * @param config the configuration bean.
     * @param configData the map with configuration data.
     */
    public static void fillConfiguration(Configuration config,
            Map<String, ? extends Object> configData) {
        getSpi().fillConfiguration(config, configData);
    }

    /**
     * Creates an dummy message catalog ideal for unit testing. All messages are
     * formatted as follows:
     * <p>
     * <code><i>message-key</i>: <i>arg0.toString()</i>, ..., <i>argn.toString</i></code>
     * 
     * @return A dummy message catalog.
     */
    public static ConnectorMessages createDummyMessages() {
        return getSpi().createDummyMessages();
    }

    public static List<ConnectorObject> searchToList(SearchApiOp search,
            ObjectClass oclass, Filter filter) {
        return searchToList(search, oclass, filter, null);
    }

    public static List<ConnectorObject> searchToList(SearchApiOp search,
            ObjectClass oclass, Filter filter, OperationOptions options) {
        ToListResultsHandler handler = new ToListResultsHandler();
        search.search(oclass, filter, handler, options);
        return handler.getObjects();
    }

    /**
     * Performs a raw, unfiltered search at the SPI level, eliminating
     * duplicates from the result set.
     * 
     * @param search
     *            The search SPI
     * @param oclass
     *            The object class - passed through to connector so it may be
     *            null if the connecor allowing it to be null. (This is
     *            convenient for unit tests, but will not be the case in
     *            general)
     * @param filter
     *            The filter to search on
     * @return The list of results.
     */
    public static List<ConnectorObject> searchToList(SearchOp<?> search,
            ObjectClass oclass, Filter filter) {
        return searchToList(search, oclass, filter, null);
    }

    /**
     * Performs a raw, unfiltered search at the SPI level, eliminating
     * duplicates from the result set.
     * 
     * @param search
     *            The search SPI
     * @param oclass
     *            The object class - passed through to connector so it may be
     *            null if the connecor allowing it to be null. (This is
     *            convenient for unit tests, but will not be the case in
     *            general)
     * @param filter
     *            The filter to search on
     * @param options
     *            The options - may be null - will be cast to an empty
     *            OperationOptions
     * @return The list of results.
     */
    public static List<ConnectorObject> searchToList(SearchOp<?> search,
            ObjectClass oclass, Filter filter, OperationOptions options) {
        ToListResultsHandler handler = new ToListResultsHandler();
        search(search, oclass, filter, handler, options);
        return handler.getObjects();
    }

    /**
     * Performs a raw, unfiltered search at the SPI level, eliminating
     * duplicates from the result set.
     * 
     * @param search
     *            The search SPI
     * @param oclass
     *            The object class - passed through to connector so it may be
     *            null if the connecor allowing it to be null. (This is
     *            convenient for unit tests, but will not be the case in
     *            general)
     * @param filter
     *            The filter to search on
     * @param handler
     *            The result handler
     * @param options
     *            The options - may be null - will be cast to an empty
     *            OperationOptions
     */
    public static void search(SearchOp<?> search, final ObjectClass oclass,
            final Filter filter, ResultsHandler handler,
            OperationOptions options) {
        getSpi().search(search, oclass, filter, handler, options);
    }

    // At some point we might make this pluggable, but for now, hard-code
    private static final String IMPL_NAME = "org.identityconnectors.framework.impl.test.TestHelpersImpl";

    private static TestHelpersSpi _instance;

    /**
     * Returns the instance of the SPI implementation.
     * 
     * @return The instance of the SPI implementation.
     */
    private static synchronized TestHelpersSpi getSpi() {
        if (_instance == null) {
            try {
                Class<?> clazz = Class.forName(IMPL_NAME);
                Object object = clazz.newInstance();
                _instance = TestHelpersSpi.class.cast(object);
            } catch (Exception e) {
                throw ConnectorException.wrap(e);
            }
        }
        return _instance;
    }

    /** Properties read from filesystem when project.name is set */
    private static Map<?, ?> _fsProperties;
    /** Properties per connector class */
    private static Map<Class<? extends Connector>,Map<?,?>> _perConnectorProperties = new HashMap<Class<? extends Connector>, Map<?,?>>();
    private static Class<? extends Connector> _discoveredImpl;
    /**
     * Loads the properties files just like the connector 'build' environment
     * the only exception is properties in the 'global' file are filtered for
     * those properties that prefix the project's name.
     * 
     * @param name
     *            Key to the properties..
     * @param def
     *            default value to return if the key does not exist
     * @return def if key is not preset return the default.
     */
    public static String getProperty(String name, String def) {
        // attempt to find the property..
        Map<?,?> properties = getPropertiesInternal();
        Object value = properties.get(name);
        if(value == null){
        	return def;
        }
        //User wanted String, so give him string
        return (String) value;
    }
    
    public static String getProperty(Class<? extends Connector> clazz, String name, String def) {
        // attempt to find the property..
        Map<?,?> properties = getPropertiesInternal(clazz);
        Object value = properties.get(name);
        if(value == null){
        	return def;
        }
        //User wanted String, so give him string
        return (String) value;
    }

    /**
     * Loads the properties files just like the connector 'build' environment
     * the only exception is properties in the 'global' file are filtered for
     * those properties that prefix the project's name.
     */
    public static Properties getProperties() {
    	Map<?,?> properties = getPropertiesInternal();
    	return copyProperties(properties);
    }
    
    public static Properties getProperties(Class<? extends Connector> clazz) {
    	Map<?,?> properties = getPropertiesInternal(clazz);
    	return copyProperties(properties);
    }
    
    
    private static Map<?,?> getPropertiesInternal(Class<? extends Connector> clazz){
    	synchronized (LOCK) {
    		Map<?,?> properties = _perConnectorProperties.get(clazz);
    		if(properties != null){
    			return properties;
    		}
    		properties = TestConfigurationReader.loadConnectorConfigurationAsResource(clazz, createConfigResourceClassLoader());
    		_perConnectorProperties.put(clazz, properties);
    		return properties;
    	}
    }
    
    private static Map<?,?> getPropertiesInternal(){
        synchronized (LOCK) {
        	//Once we loaded the file system properties, return them, it means we have project.name system property
        	if(_fsProperties != null){
        		return _fsProperties;
        	}
        	//If we have discovered already one connector impl , return the properties 
        	if(_discoveredImpl != null){
        		return _perConnectorProperties.get(_discoveredImpl);
        	}
        	//If we do not have project.name, we must discover connector class
        	if(StringUtil.isBlank(System.getProperty("project.name"))){
        		List<Class<? extends Connector>> impls = TestConfigurationReader.resolveConnectorImplementations(createConfigResourceClassLoader());
        		if(impls.isEmpty()){
        			throw new ConnectorException("No connector class found");
        		}
        		if(impls.size() > 1){
        			throw new ConnectorException("More than 1 connector implementation found, please call TestHelpers.getProperties(Class<? extends Connector> clazz)");
        		}
        		_discoveredImpl = impls.get(0);
        		Map<?,?> properties = TestConfigurationReader.loadConnectorConfigurationAsResource(_discoveredImpl, createConfigResourceClassLoader());
        		_perConnectorProperties.put(_discoveredImpl, properties);
        		return properties;
        	}
        	else{
        		_fsProperties = TestConfigurationReader.loadConnectorConfigurationFromFS();
        		return _fsProperties;
        	}
        }
    }

    
	private static Properties copyProperties(Map<?, ?> p) {
		Properties ret = new Properties();
        for (Entry<?, ?> entry : p.entrySet()) {
            Object value = entry.getValue();
            // Hashtable doesn't take null values.
            if (value != null) {
                ret.put(entry.getKey(), value.toString());
            }
        }
		return ret;
	}
	
	private static ClassLoader createConfigResourceClassLoader(){
		return TestHelpers.class.getClassLoader();
	}

    

}
