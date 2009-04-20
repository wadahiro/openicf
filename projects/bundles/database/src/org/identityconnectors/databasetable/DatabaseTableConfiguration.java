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
package org.identityconnectors.databasetable;

import static org.identityconnectors.databasetable.DatabaseTableConstants.*;

import org.identityconnectors.common.StringUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.dbcommon.JNDIUtil;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import org.identityconnectors.framework.spi.operations.SyncOp;


/**
 * Implements the {@link Configuration} interface to provide all the necessary
 * parameters to initialize the JDBC Connector.
 */
public class DatabaseTableConfiguration extends AbstractConfiguration {

    // =======================================================================
    // DatabaseTableConfiguration
    // =======================================================================

    /**
     * How to quote a column in SQL statements. Possible values can be NONE, SINGLE, DOUBLE, BRACKETS, BACKSLASH
     */
    private String quoting = EMPTY_STR;

    /**
     * NameQoute getter
     * 
     * @return quoting value
     */
    @ConfigurationProperty(order = 1, 
                           displayMessageKey = "DBTABLE_CONNECTOR_QUOTING.display", 
                           helpMessageKey = "DBTABLE_CONNECTOR_QUOTING.help")
    public String getQuoting() {
        return this.quoting;
    }

    /**
     * NameQuote Setter
     * @param value
     */
    public void setQuoting(String value) {
        this.quoting = value;
    }
    
    /**
     * The host value
     */
    private String host = EMPTY_STR;

    /**
     * NameQoute getter
     * 
     * @return quoting value
     */
    @ConfigurationProperty(order = 2, 
                           displayMessageKey = "DBTABLE_CONNECTOR_HOST.display", 
                           helpMessageKey = "DBTABLE_CONNECTOR_HOST.help")
    public String getHost() {
        return this.host;
    }

    /**
     * NameQuote Setter
     * @param value
     */
    public void setHost(String value) {
        this.host = value;
    }    
    
    
    /**
     * The port value
     */
    private String port = EMPTY_STR;

    /**
     * NameQoute getter
     * 
     * @return quoting value
     */
    @ConfigurationProperty(order = 3, 
                           displayMessageKey = "DBTABLE_CONNECTOR_PORT.display", 
                           helpMessageKey = "DBTABLE_CONNECTOR_PORT.help")
    public String getPort() {
        return this.port;
    }

    /**
     * NameQuote Setter
     * @param value
     */
    public void setPort(String value) {
        this.port = value;
    }     
    
    /**
     * Database Login User name. This user name is used to connect to database. The provided user name and password 
     * should have rights to insert/update/delete the rows in the configured identity holder table.
     * Required configuration property, and should be validated
     */
    private String user = EMPTY_STR;

    /**
     * @return user value
     */
    @ConfigurationProperty(order = 4,
            displayMessageKey = "DBTABLE_CONNECTOR_USER.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_USER.help")
    public String getUser() {
        return this.user;
    }

    /**
     * @param value
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Database access Password. This password is used to connect to database. The provided user name and password 
     * should have rights to insert/update/delete the rows in the configured identity holder table.
     * Required configuration property, and should be validated      
     */
    private GuardedString password;

    /**
     * @return password value
     */
    @ConfigurationProperty ( order=5, confidential=true,
            displayMessageKey = "DBTABLE_CONNECTOR_PASSWORD.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_PASSWORD.help")
    public GuardedString getPassword() {
        return this.password;
    }

    /**
     * @param value
     */
    public void setPassword(GuardedString value) {
        this.password = value;
    }    
    
    
    /**
     * Database name.  
      */
    private String database = EMPTY_STR;

    /**
     * @return user value
     */
    @ConfigurationProperty(order = 6,
            displayMessageKey = "DBTABLE_CONNECTOR_DATABASE.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_DATABASE.help")
    public String getDatabase() {
        return this.database;
    }
    
    /**
     * @param value
     */
    public void setDatabase(String value) {
        this.database = value;
    }    
    

    /**
     * Database Table name. The name of the identity holder table (Integration table). 
     */
    private String table = EMPTY_STR;

    /**
     * The table name
     * @return the user account table name
     * Please notice, there are used non default message keys
     */
    @ConfigurationProperty(order = 7, required=true, 
            displayMessageKey = "DBTABLE_CONNECTOR_TABLE.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_TABLE.help")
    public String getTable() {
        return this.table;
    }

    /**
     * Table setter
     * @param table name value
     */
    public void setTable(String table) {
        this.table = table;
    }


    /**
     * Key Column, The name of the key column is required
     * This non empty value must be validated
     */
    private String keyColumn = EMPTY_STR;

    /**
     * Key Column getter
     * @return keyColumn value
     */
    @ConfigurationProperty(order = 8, required = true,
            displayMessageKey = "DBTABLE_CONNECTOR_KEY_COLUMN.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_KEY_COLUMN.help")
    public String getKeyColumn() {
        return this.keyColumn;
    }

    /**
     * Key Column setter
     * @param keyColumn value
     */
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
    }    

    /**
     * Password Column. If non empty, password is supported in the schema
     * empty password column means, the password is not supported and also should not be in the schema    
     */
    private String passwordColumn = EMPTY_STR;

    /**
     * Password Column getter
     * 
     * @return passwordColumn value
     */
    @ConfigurationProperty(order = 9,
            displayMessageKey = "DBTABLE_CONNECTOR_PASSWORD_COLUMN.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_PASSWORD_COLUMN.help")
    public String getPasswordColumn() {
        return this.passwordColumn;
    }

    /**
     * Password Column setter
     * 
     * @param value
     */
    public void setPasswordColumn(String value) {
        this.passwordColumn = value;
    }    
    
    /**
     * The Driver class. The jdbcDriver is located by connector framework to connect to database.
     * Required configuration property, and should be validated
     */
    private String jdbcDriver = DEFAULT_DRIVER;

    /**
     * @return jdbcDriver value
     */
    @ConfigurationProperty(order = 10,
            displayMessageKey = "DBTABLE_CONNECTOR_JDBC_DRIVER.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_JDBC_DRIVER.help")
    public String getJdbcDriver() {
        return this.jdbcDriver;
    }

    /**
     * @param value
     */
    public void setJdbcDriver(String value) {
        this.jdbcDriver = value;
    }    
        
    /**
     * Database connection URL. The url is used to connect to database.
     * Required configuration property, and should be validated
     */
    private String jdbcUrlTemplate = DEFAULT_TEMPLATE;

    /**
     * Return the jdbcUrlTemplate 
     * @return url value
     */
    @ConfigurationProperty(order = 11,
            displayMessageKey = "DBTABLE_CONNECTOR_URL_TEMPLATE.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_URL_TEMPLATE.help")
    public String getJdbcUrlTemplate() {
        return jdbcUrlTemplate;
    }    
   
    /**
     * @param value
     */
    public void setJdbcUrlTemplate(String value) {
        this.jdbcUrlTemplate = value;
    }    
    
    /**
     * The empty string setting
     * allow conversion of a null into an empty string for not-null char columns
     */
    public boolean enableEmptyString = false;

    /**
     * Accessor for the enableEmptyString property
     * @return the enableEmptyString
     */
    @ConfigurationProperty(order = 12,
            displayMessageKey = "DBTABLE_CONNECTOR_ENABLE_EMPTY_STRING.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_ENABLE_EMPTY_STRING.help")
    public boolean isEnableEmptyString() {
        return enableEmptyString;
    }

    /**
     * Setter for the enableEmptyString property.
     * @param enableEmptyString the enableEmptyString to set
     */
    public void setEnableEmptyString(boolean enableEmptyString) {
        this.enableEmptyString = enableEmptyString;
    }

    /**
     * Some database drivers will throw the SQLError when setting the
     * parameters to the statement with zero ErrorCode. This mean no error.
     * This switch allow to switch off ignoring this SQLError 
     */
    public boolean rethrowAllSQLExceptions = true;

    /**
     * Accessor for the rethrowAllSQLExceptions property
     * @return the rethrowAllSQLExceptions
     */
    @ConfigurationProperty(order = 14,
            displayMessageKey = "DBTABLE_CONNECTOR_RETHROW_ALL_SQLEXCEPTIONS.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_RETHROW_ALL_SQLEXCEPTIONS.help")
    public boolean isRethrowAllSQLExceptions() {
        return rethrowAllSQLExceptions;
    }

    /**
     * Setter for the rethrowAllSQLExceptions property.
     * @param rethrowAllSQLExceptions the rethrowAllSQLExceptions to set
     */
    public void setRethrowAllSQLExceptions(boolean rethrowAllSQLExceptions) {
        this.rethrowAllSQLExceptions = rethrowAllSQLExceptions;
    }
    
    /**
     * Some JDBC drivers (ex: Oracle) may not be able to get correct string representation of
     * TIMESTAMP data type of the column from the database table.
     * To get correct value , one needs to use rs.getTimestamp() rather rs.getString().
     */
    public boolean nativeTimestamps = false;
    
    /**
     * Accessor for the nativeTimestamps property
     * @return the nativeTimestamps
     */
    @ConfigurationProperty(order = 15,
            displayMessageKey = "DBTABLE_CONNECTOR_NATIVE_TIMESTAMPS.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_NATIVE_TIMESTAMPS.help")
    public boolean isNativeTimestamps() {
        return nativeTimestamps;
    }

    /**
     * Setter for the nativeTimestamps property.
     * @param nativeTimestamps the nativeTimestamps to set
     */
    public void setNativeTimestamps(boolean nativeTimestamps) {
        this.nativeTimestamps = nativeTimestamps;
    }
    
    /**
     * Some JDBC drivers (ex: DerbyDB) may need to access all the datatypes with native types 
     * to get correct value.
     */
    public boolean allNative = false;
    

    /**
     * Accessor for the allNativeproperty
     * @return the allNative
     */
    @ConfigurationProperty(order = 16,
            displayMessageKey = "DBTABLE_CONNECTOR_ALL_NATIVE.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_ALL_NATIVE.help")
    public boolean isAllNative() {
        return allNative;
    }

    /**
     * Setter for the allNative property.
     * @param allNative the allNative to set
     */
    public void setAllNative(boolean allNative) {
        this.allNative = allNative;
    } 
    
    /**
     * The new connection validation query. The query can be empty. Then the auto commit true/false 
     * command is applied by default. This can be unsufficient on some database drivers because of caching
     * Then the validation query is required.     
     */
    private String validConnectionQuery;

    /**
     * connection validation query getter
     * @return validConnectionQuery value
     */
    @ConfigurationProperty(order = 17,
            displayMessageKey = "DBTABLE_CONNECTOR_VALID_CONNECTION_QUERY.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_VALID_CONNECTION_QUERY.help")
    public String getValidConnectionQuery() {
        return this.validConnectionQuery;
    }

    /**
     * Connection validation query setter
     * @param value
     */
    public void setValidConnectionQuery(String value) {
        this.validConnectionQuery = value;
    }

    /**
     * Change Log Column (should automatically add ORDER BY)
     * If the value is non empty, the SyncOp should be supported
     * It could be nativeTimestamps.
     */
    private String changeLogColumn = EMPTY_STR;

    /**
     * Log Column is required be SyncOp
     * @return Log Column 
     */
    @ConfigurationProperty(order = 19, operations = SyncOp.class,
            displayMessageKey = "DBTABLE_CONNECTOR_CHANGE_LOG_COLUMN.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_CHANGE_LOG_COLUMN.help")
    public String getChangeLogColumn() {
        return this.changeLogColumn;
    }

    /**
     * @param value
     */
    public void setChangeLogColumn(String value) {
        this.changeLogColumn = value;
    }

    // =======================================================================
    // DataSource
    // =======================================================================
    
    /**
     * The datasource name is used to connect to database.
     */
    private String datasource = EMPTY_STR;

    /**
     * Return the datasource 
     * @return datasource value
     */
    @ConfigurationProperty(order = 20,
            displayMessageKey = "DBTABLE_CONNECTOR_DATASOURCE.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_DATASOURCE.help")
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
    private String[] jndiProperties;

    /**
     * Return the jndiFactory 
     * @return jndiFactory value
     */
    @ConfigurationProperty(order = 21,
            displayMessageKey = "DBTABLE_CONNECTOR_JNDI_PROPERTIES.display", 
            helpMessageKey = "DBTABLE_CONNECTOR_JNDI_PROPERTIES.help")
    public String[] getJndiProperties() {
        return jndiProperties;
    }

    /**
     * @param value
     */
    public void setJndiProperties(String[] value) {
        this.jndiProperties = value;
    }
    // =======================================================================
    // Configuration Interface
    // =======================================================================

    /**
     * Attempt to validate the arguments added to the Configuration.
     * 
     * @see org.identityconnectors.framework.Configuration#validate()
     */
    @Override
    public void validate() {
        // check that there is a table to query..
        if (StringUtil.isBlank(getTable())) {
            throw new IllegalArgumentException(getMessage(MSG_TABLE_BLANK));
         }      

        // check the url is configured
        if (StringUtil.isBlank(getJdbcUrlTemplate())) {
            throw new IllegalArgumentException(getMessage(MSG_JDBC_TEMPLATE_BLANK));
        }   
        
        // determine if you can get a key column
        if (StringUtil.isBlank(getKeyColumn())) {
           throw new IllegalArgumentException(getMessage(MSG_KEY_COLUMN_BLANK));
        } else {
            if (getKeyColumn().equalsIgnoreCase(getChangeLogColumn())) {
                throw new IllegalArgumentException(getMessage(MSG_KEY_COLUMN_EQ_CHANGE_LOG_COLUMN));               
            }                       
        }
        
        // key column, password column
        if (StringUtil.isNotBlank(getPasswordColumn())) {
           if (getPasswordColumn().equalsIgnoreCase(getKeyColumn())) {
               throw new IllegalArgumentException(getMessage(MSG_PASSWD_COLUMN_EQ_KEY_COLUMN));               
           }
           
           if (getPasswordColumn().equalsIgnoreCase(getChangeLogColumn())) {
               throw new IllegalArgumentException(getMessage(MSG_PASSWD_COLUMN_EQ_CHANGE_LOG_COLUMN));               
           }           
        }        
        
        
        // check that there is not a datasource
        if(StringUtil.isBlank(getDatasource())){ 
            // determine if you can get a connection to the database..
            if (getUser() == null) {
                throw new IllegalArgumentException(getMessage(MSG_USER_BLANK));
             }
            // check that there is a pwd to query..
            if (getPassword() == null) {
                throw new IllegalArgumentException(getMessage(MSG_PASSWORD_BLANK));
             }

            // host required
            if (getJdbcUrlTemplate().contains("%h")) {
                if (StringUtil.isBlank(getHost())) {
                    throw new IllegalArgumentException(getMessage(MSG_HOST_BLANK));
                }
            }
            // port required
            if(getJdbcUrlTemplate().contains("%p")) {
                if (StringUtil.isBlank(getPort())) {
                    throw new IllegalArgumentException(getMessage(MSG_PORT_BLANK));
                }                
            }
            // database required            
            if(getJdbcUrlTemplate().contains("%d")) {
                if (StringUtil.isBlank(getDatabase())) {
                    throw new IllegalArgumentException(getMessage(MSG_DATABASE_BLANK));
                }                   
            }
            // make sure the jdbcDriver is in the class path..
            if (StringUtil.isBlank(getJdbcDriver())) {
                throw new IllegalArgumentException(getMessage(MSG_JDBC_DRIVER_BLANK));
            }   
            try {
                Class.forName(getJdbcDriver());
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(getMessage(MSG_JDBC_DRIVER_NOT_FOUND));
            }
        } else {
            //Validate the JNDI properties
            JNDIUtil.arrayToHashtable(getJndiProperties(), getConnectorMessages());
        }
        
        try {
            DatabaseTableSQLUtil.quoteName(getQuoting(), "test");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(getMessage(MSG_INVALID_QUOTING, getQuoting()));
        }
    }
    
    /**
     * Format a URL given a template. Recognized template characters are:
     *  % literal % h host p port d database
     * @return the database url 
     */
    public String formatUrlTemplate() {
        final StringBuffer b = new StringBuffer();
        final String url = getJdbcUrlTemplate();
        final int len = url.length();
        for (int i = 0; i < len; i++) {
            char ch = url.charAt(i);
            if (ch != '%')
                b.append(ch);
            else if (i + 1 < len) {
                i++;
                ch = url.charAt(i);
                if (ch == '%')
                    b.append(ch);
                else if (ch == 'h')
                    b.append(getHost());
                else if (ch == 'p')
                    b.append(getPort());
                else if (ch == 'd')
                    b.append(getDatabase());
            }
        }
        String formattedURL = b.toString();
        return formattedURL;
    }
    
    /**
     * Format the connector message
     * @param key key of the message
     * @return return the formated message
     */
    public String getMessage(String key) {
        return getConnectorMessages().format(key, key);
    }
    
    /**
     * Format message with arguments 
     * @param key key of the message
     * @param objects arguments
     * @return the localized message string
     */
    public String getMessage(String key, Object... objects) {
        return getConnectorMessages().format(key, key, objects);
    }    
}
