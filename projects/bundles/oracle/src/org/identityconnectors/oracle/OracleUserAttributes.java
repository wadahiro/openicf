package org.identityconnectors.oracle;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

/**
 * Helper structure for creating/altering user
 * @author kitko
 *
 */
final class OracleUserAttributes implements Cloneable{
    Operation operation;
    String userName;
    OracleAuthentication auth;
    GuardedString password;
    String globalName;
    Boolean expirePassword;
    Boolean enable;
    String defaultTableSpace;
    String tempTableSpace;
    String profile;
    String defaultTSQuota;
    String tempTSQuota;
    
    
    protected OracleUserAttributes clone(){
        try {
            return (OracleUserAttributes) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ConnectorException("Cannot clone CreateAlterAttributes",e);
        }
    }
    
}


enum Operation{
    CREATE,
    ALTER;
}