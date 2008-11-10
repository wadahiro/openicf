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
package org.identityconnectors.mysqluser;

import java.text.MessageFormat;

import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConfigurationProperty;


/**
 * Implements the {@link Configuration} interface to provide all the necessary parameters to initialize the JDBC
 * Connector.
 * 
 * @version $Revision $
 * @since 1.0
 */
public class MySQLUserConfiguration extends AbstractConfiguration {
   
    /**
     * User, Id, Key field
     */
    public static final String MYSQL_USER = "User";
    
    /**
     * table name
     */
    public static final String MYSQL_USER_TABLE = "mysql.user";
    
    
    
    /**
     * The datasource name is used to connect to database.
     */
    private String datasource;

    /**
     * Return the datasource 
     * @return datasource value
     */
    @ConfigurationProperty(order = 1, helpMessageKey = "mysqluser.datasource.help", displayMessageKey = "mysqluser.datasource.display")
    public String getDatasource() {
        return datasource;
    }

    /**
     * @param value
     */
    public void setDatasource(String value) {
        this.datasource = value;
    }
    
    
    /**
     * The jndiFactory name is used to connect to database.
     */
    private String jndiFactory;

    /**
     * Return the jndiFactory 
     * @return jndiFactory value
     */
    @ConfigurationProperty(order = 2, helpMessageKey = "mysqluser.datasource.help", displayMessageKey = "mysqluser.datasource.display")
    public String getJndiFactory() {
        return jndiFactory;
    }

    /**
     * @param value
     */
    public void setJndiFactory(String value) {
        this.jndiFactory = value;
    }
    
    
    /**
     * The jndiProvider name is used to connect to database.
     */
    private String jndiProvider;

    /**
     * Return the jndiProvider 
     * @return jndiProvider value
     */
    @ConfigurationProperty(order = 3, helpMessageKey = "mysqluser.jndiProvider.help", displayMessageKey = "mysqluser.jndiProvider.display")
    public String getJndiProvider() {
        return jndiProvider;
    }

    /**
     * @param value
     */
    public void setJndiProvider(String value) {
        this.jndiProvider = value;
    }        

    /**
     * Host
     */
    public static final String HOST = "Host";



    /**
     * driver
     */
    private String driver = "com.mysql.jdbc.Driver"; // Driver

    /**
     * The setter method 
     * @return a driver value
     */
    @ConfigurationProperty(order = 4, helpMessageKey = "mysqluser.driver.help", displayMessageKey = "mysqluser.driver.display")
    public String getDriver() {
        return this.driver;
    }
        
    /**
     * @param value
     */
    public void setDriver(final String value) {
        this.driver = value;
    }

    /**
     * host
     */
    private String host = ""; // Host
    
    /**
     * @return the host
     */
    @ConfigurationProperty(order = 5, helpMessageKey = "mysqluser.host.help", displayMessageKey = "mysqluser.host.display")
    public String getHost() {
        return host;
    }    

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }    

    /**
     * login
     */
    private String login = ""; // Login

    /**
     * The setter method 
     * @return login value
     */
    @ConfigurationProperty(order = 6, helpMessageKey = "mysqluser.login.help", displayMessageKey = "mysqluser.login.display")
    public String getLogin() {
        return this.login;
    }

    /**
     * @param value
     */
    public void setLogin(final String value) {
        this.login = value;
    }

    /**
     * password
     */
    private GuardedString password; // Password


    /**
     * The setter method 
     * @return passport value
     */
    @ConfigurationProperty(order = 7, helpMessageKey = "mysqluser.pwd.help", displayMessageKey = "mysqluser.pwd.display", confidential = true)
    public GuardedString getPassword() {
        return this.password;
    }

    /**
     * @param value
     */
    public void setPassword(final GuardedString value) {
        this.password = value;
    }
    
    /**
     * port
     */
    private String port = "3306"; // Port

    /**
     * @return the port
     */
    @ConfigurationProperty(order = 8, helpMessageKey = "mysqluser.port.help", displayMessageKey = "mysqluser.port.display")
    public String getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }    
    
    /**
     * user model
     */
    private String usermodel = "idm"; // Default User


    /**
     * @return the usermodel
     */
    @ConfigurationProperty(order = 9, helpMessageKey = "mysqluser.model.help", displayMessageKey = "mysqluser.model.display")
    public String getUsermodel() {
        return usermodel;
    }

    /**
     * @param usermodel
     *            the usermodel to set
     */
    public void setUsermodel(String usermodel) {
        this.usermodel = usermodel;
    }

    /**
     * Attempt to validate the arguments added to the Configuration.
     * 
     * @see org.identityconnectors.framework.Configuration#validate()
     */
    @Override
    public void validate() {
        // determine if you can get a connection to the database..
        Assertions.blankCheck(getLogin(), "login");
        // check that there is a table to query..
        Assertions.nullCheck(getPassword(), "password");

        Assertions.blankCheck(getUsermodel(), "usermodel");        
        
        // check that there is not a datasource
        if(StringUtil.isBlank(getDatasource())){ 
            // check that there is a driver..
            Assertions.blankCheck(getDriver(), "driver");

            Assertions.blankCheck(getHost(), "host");

            Assertions.blankCheck(getPort(), "port");

            // make sure the driver is in the class path..
            try {
                Class.forName(getDriver());
            } catch (final ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        } else { // Datasource is active         
            // When JNDI provider is set up, factory need to be there too 
            if (StringUtil.isNotBlank(getJndiProvider()) || StringUtil.isNotBlank(getJndiFactory())) {
                Assertions.blankCheck(getJndiFactory(), "jndiFactory");
                Assertions.blankCheck(getJndiProvider(), "jndiProvider");
            }
        }          
    }
    
    /**
     * The url string
     * @return the url string of the database
     */
    public String getUrlString() {
        final String URL_MASK = "jdbc:mysql://{0}:{1}/mysql";
        // create the connection base on the configuration..
        String url = null;
        try {
            // get the database URL..
            url = MessageFormat.format(URL_MASK, getHost(), getPort());
        } catch (Exception e) {
            throw ConnectorException.wrap(e);
        }
        return url;
    }
      
}
