/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.     
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
package org.identityconnectors.dbcommon;


import java.util.ArrayList;
import java.util.List;

import org.identityconnectors.common.CollectionUtil;


/**
 * The update set builder create the database update statement.
 * <p>The main functionality is create set part of update statement from Attribute set</p>
 *
 * @version $Revision 1.0$
 * @since 1.0
 */
public class UpdateSetBuilder {
    private List<Object> params = new ArrayList<Object>();
    private List<Integer> sqlTypes = new ArrayList<Integer>();
    private StringBuilder set = new StringBuilder();
    
    /**
     * Add column name and value pair
     * The names are quoted using the {@link #columnQuote} value
     * 
     * @param name name
     * @param param value
     * @param sqlType
     * @return self
     */
    public UpdateSetBuilder addBind(String name, Object param, Integer sqlType) {
        return addBind(name,"?", param, sqlType);
    }

    /**
     * Add column name and expression value pair
     * The names are quoted using the {@link #columnQuote} value
     * @param name of the column
     * @param value the Comparable expression
     * @param param the value to bind
     * @param sqlType the SQL database type
     * @return self
     */
    public UpdateSetBuilder addBind(String name, Object value, Object param, Integer sqlType) {
        if(set.length()>0) {
            set.append(" , ");
        }
        set.append(name).append(" = ").append(value);
        params.add(param);
        sqlTypes.add(sqlType);
        return this;
    }    
    
    /**
     * Build the set SQL 
     * @return The update set clause 
     */
    public String getSQL() {
        return set.toString();
    }

    /**
     * @param value
     * @param sqlType
     */
    public void addValue(String value, int sqlType) {
        params.add(value);
        sqlTypes.add(sqlType);
    }

    
    /**
     * @return the param values
     */
    public List<Object> getParams() {
        return CollectionUtil.newReadOnlyList(params);
    }
    
    /**
     * @return the sqlTypes
     */
    public List<Integer> getSQLTypes() {
        return CollectionUtil.newReadOnlyList(sqlTypes);
    }
}
