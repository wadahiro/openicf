package org.identityconnectors.test.common;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.identityconnectors.common.IOUtil;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.spi.Connector;

/** This class is just helper that can read connector configuration from different sources.
 *  There is no logic how to read the configuration and which source to use nor caching is not implemented here.
 * @author kitko
 *
 */
final class TestConfigurationReader {
	private TestConfigurationReader(){}
	
    private static final String GLOBAL_PROPS = "connectors.properties";
    private static final String BUNDLE_PROPS = "build.properties";
    private static final Log LOG = Log.getLog(TestHelpers.class);

	
    static List<Class<? extends Connector>> resolveConnectorImplementations(ClassLoader loader){
    	ServiceLoader<Connector> serviceLoader = ServiceLoader.load(Connector.class, loader);
    	List<Class<? extends Connector>> res = new ArrayList<Class<? extends Connector>>();
    	for(Connector ci : serviceLoader){
    		res.add(ci.getClass());
    	}
    	return res;
    }
    
    
    
    
    static Map<?, ?> loadConnectorConfigurationAsResource(Class<? extends Connector> clazz, ClassLoader loader){
		String prefix = clazz.getName();
		Map<?, ?> properties = loadConnectorConfigurationAsResource(prefix, loader);
		if(properties.isEmpty()){
		    throw new ConnectorException(MessageFormat.format("No properties read from classpath for the connector class [{0}] ",clazz));
		}
		return properties;
    }
    
    static Map<?, ?> loadConnectorConfigurationAsResource(String prefix, ClassLoader loader){
    	Map<Object, Object> ret = new HashMap<Object, Object>();
		String cfg = System.getProperty("testConfig", null);
		URL url = loader.getResource(prefix + "/public/build.groovy");
		if(url != null){
		    appendProperties(ret, loadGroovyConfigFile(url));
		}
		if (StringUtil.isNotBlank(cfg) && !"default".equals(cfg)) {
		    url = loader.getResource(prefix + "/public/" + cfg + "/build.groovy");
		    if(url != null){
		    	appendProperties(ret, loadGroovyConfigFile(url));
		    }
		}
		url = loader.getResource(prefix + "/private/build.groovy");
		if (url != null){
		    appendProperties(ret, loadGroovyConfigFile(url));
		}
		if (StringUtil.isNotBlank(cfg) && !"default".equals(cfg)) {
		    url = loader.getResource(prefix + "/private/" + cfg + "/build.groovy");
		    if(url != null){
		    	appendProperties(ret, loadGroovyConfigFile(url));
		    }
		}
		return ret;
    	
    }
    
    static void appendProperties(Map<Object, Object> ret, Map<?, ?> props){
    	if(props != null){
    	    ret.putAll(props);
    	}
    }
    
    
    /** Loads properties from filesystem */
    static Map<?, ?> loadConnectorConfigurationFromFS() {
        final String ERR = "Unable to load optional properties file: {0}";
        final String GERR = "Unable to load configuration groovy file: {0}";
        final String BERR = "Unable to load bundle properties file: {0}";
        final char FS = File.separatorChar;
        final String CONNECTORS_DIR = System.getProperty("user.home") + FS + ".connectors";
        final String CONFIG_DIR = (new File(".")).getAbsolutePath() + FS + "config";
        final String BUILD_GROOVY = "build.groovy";
        Map<?, ?> props = null;
        Map<Object, Object> ret = new HashMap<Object, Object>();
        String fName = null;

        // load global properties (if present)
        try {
            fName = CONNECTORS_DIR + FS + GLOBAL_PROPS;
            props = IOUtil.loadPropertiesFile(fName);
            ret.putAll(props);
        } catch (IOException e) {
            LOG.info(ERR, fName);
        }

        //load the private bundle properties file (if present)
        try {
            props = IOUtil.loadPropertiesFile(BUNDLE_PROPS);
            ret.putAll(props);
        } catch (IOException e) {
            LOG.error(BERR, BUNDLE_PROPS);
        }

        // load the project (public) configuration groovy file
        try {
            fName = CONFIG_DIR + FS + BUILD_GROOVY;
            props = loadGroovyConfigFile(IOUtil.makeURL(null, fName));
            ret.putAll(props);
        } catch (IOException e) {
            LOG.info(GERR, fName);
        }
        String cfg = System.getProperty("testConfig", null);

        // load the project (public) configuration-specific configuration groovy file
        if (StringUtil.isNotBlank(cfg) && !"default".equals(cfg)) {
            try {
                fName = CONFIG_DIR + FS + cfg + FS + BUILD_GROOVY;
                props = loadGroovyConfigFile(IOUtil.makeURL(null, fName));
                ret.putAll(props);
            } catch (IOException e) {
                LOG.info(GERR, fName);
            }
        }

        String prjName = System.getProperty("project.name", null);
        if (StringUtil.isNotBlank(prjName)) {
            fName = null;
            //load the private bundle configuration groovy file (if present)
            try {
                fName = CONNECTORS_DIR + FS + prjName + FS + BUILD_GROOVY;
                props = loadGroovyConfigFile(IOUtil.makeURL(null, fName));
                ret.putAll(props);
            } catch (IOException e) {
                LOG.info(GERR, fName);
            }

            if (StringUtil.isNotBlank(cfg) && !"default".equals(cfg)) {
                //load the configuration-specific configuration groovy file (if present)
                try {
                    fName = CONNECTORS_DIR + FS + prjName + FS + cfg + FS + BUILD_GROOVY;
                    props = loadGroovyConfigFile(IOUtil.makeURL(null, fName));
                    ret.putAll(props);
                } catch (IOException e) {
                    LOG.info(GERR, fName);
                }
            }
        }
        // load the system properties
        ret.putAll(System.getProperties());
        return ret;
    }


    static Map<?, ?> loadGroovyConfigFile(URL url) {
        try {
            Class<?> slurper = Class.forName("groovy.util.ConfigSlurper");
            Class<?> configObject = Class.forName("groovy.util.ConfigObject");
            Object slurpInstance = slurper.newInstance();
            Method parse = slurper.getMethod("parse", URL.class);
            Object config = parse.invoke(slurpInstance, url);
            Method toProps = configObject.getMethod("flatten");
            Object result = toProps.invoke(config);
            return (Map<?, ?>) result;
        } catch (Exception e) {
            LOG.error(e, "Could not load Groovy objects: {0}", e.getMessage());
            return null;
        }
    }
    
	
}
