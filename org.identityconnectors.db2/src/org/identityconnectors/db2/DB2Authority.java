package org.identityconnectors.db2;

/**
 *  Utility class for representing a DB2 authority, including
 *  table type, specific function of permission, object to
 *  apply permission to and user name.
 */
class DB2Authority {
    /**
     *  Constructor.
     */
    DB2Authority(String authorityType,
        String authorityFunction, String authorityObject,
        String userName)
    {
        this.authorityType = authorityType;
        this.authorityFunction = authorityFunction;
        this.authorityObject = authorityObject;
        this.userName = userName;
    }
    final String authorityType;
    final String authorityFunction;
    final String authorityObject;
    final String userName;

    public String toString() {
        return "{DB2Authority: Type=" + authorityType + ", Function="
            + authorityFunction + ", Object=" + authorityObject
            + ", User=" + userName + "}";
    }
}
