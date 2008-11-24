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
package org.identityconnectors.dbcommon;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EndsWithFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.GreaterThanFilter;
import org.identityconnectors.framework.common.objects.filter.GreaterThanOrEqualFilter;
import org.identityconnectors.framework.common.objects.filter.LessThanFilter;
import org.identityconnectors.framework.common.objects.filter.LessThanOrEqualFilter;
import org.identityconnectors.framework.common.objects.filter.StartsWithFilter;


/**
 * DatabaseFilterTranslator abstract class translate filters to database WHERE clause
 * The resource specific getAttributeName must be provided in real translator
 * 
 * @version $Revision 1.0$
 * @since 1.0
 */
public abstract class DatabaseFilterTranslator extends AbstractFilterTranslator<FilterWhereBuilder> {
    
    ObjectClass oclass;
    OperationOptions options;

    /**
     * DatabaseFilterTranslator translate filters to database WHERE clause   
     *   
     * @param oclass the object class  
     * @param options the filter options 
     */
    public DatabaseFilterTranslator(ObjectClass oclass, OperationOptions options) {
        this.oclass = oclass;
        this.options = options;
    }
    

    /**
     * @return
     */
    protected FilterWhereBuilder createBuilder() {
        return new FilterWhereBuilder();
    }    
    
    @Override
    protected FilterWhereBuilder createAndExpression(FilterWhereBuilder leftExpression,
            FilterWhereBuilder rightExpression) {
        FilterWhereBuilder build = createBuilder();
        build.join("AND", leftExpression, rightExpression);
        return build;
    }
    
    @Override
    protected FilterWhereBuilder createOrExpression(FilterWhereBuilder leftExpression,
            FilterWhereBuilder rightExpression) {
        FilterWhereBuilder build = createBuilder();
        build.join("OR", leftExpression, rightExpression);
        return build;
    }
    
    @Override
    protected FilterWhereBuilder createEqualsExpression(EqualsFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final Object value = AttributeUtil.getSingleValue(attribute);
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }        
        final FilterWhereBuilder ret = createBuilder();
        if (not) ret.getWhere().append("NOT ");
        if (value == null) {
            ret.addNull(dbname);
            return ret;
        }
        ret.addBind(dbname, "=", value);
        return ret;
    }
   
    @Override
    protected FilterWhereBuilder createContainsExpression(ContainsFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }        
        String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }        
        final FilterWhereBuilder ret = createBuilder();
        if (not) ret.getWhere().append("NOT ");
        //To be sure, this is not already quoted
        if(!value.startsWith("%")) {
            value = "%" + value;
        }
        if(!value.endsWith("%")) {
            value = value + "%";
        }
        ret.addBind(dbname, "LIKE", value);
        return ret;
    }
    
    @Override
    protected FilterWhereBuilder createEndsWithExpression(EndsWithFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }        
        String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }            
        final FilterWhereBuilder ret = createBuilder();
        if (not) ret.getWhere().append("NOT ");
        //To be sure, this is not already quoted
        if(!value.startsWith("%")) {
            value = "%" + value;
        }
        ret.addBind(dbname, "LIKE", value);
        return ret;
    }
    
    @Override
    protected FilterWhereBuilder createStartsWithExpression(StartsWithFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }     
        String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }            
        final FilterWhereBuilder ret = createBuilder();
        if (not) ret.getWhere().append("NOT ");
        //To be sure, this is not already quoted
        if(!value.endsWith("%")) {
            value = value + "%";
        }
        ret.addBind(dbname, "LIKE", value);
        return ret;
    }
    
    @Override
    protected FilterWhereBuilder createGreaterThanExpression(GreaterThanFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }   
        final String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }        
        final FilterWhereBuilder ret = createBuilder();
        final String op = not ? "<=" : ">";
        ret.addBind(dbname, op, value);
        return ret;
    }
    
    @Override
    protected FilterWhereBuilder createGreaterThanOrEqualExpression(GreaterThanOrEqualFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }        
        final String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }        
        final FilterWhereBuilder ret = createBuilder();
        final String op = not ? "<" : ">=";
        ret.addBind(dbname, op, value);
        return ret;
    }
    
    @Override
    protected FilterWhereBuilder createLessThanExpression(LessThanFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }      
        final String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }         
        final FilterWhereBuilder ret = createBuilder();
        final String op = not ? ">=" : "<";
        ret.addBind(dbname, op, value);
        return ret;
    }
    
    @Override
    protected FilterWhereBuilder createLessThanOrEqualExpression(LessThanOrEqualFilter filter, boolean not) {
        final Attribute attribute = filter.getAttribute();
        if (!validateSearchAttribute(attribute)) {
            return null;
        }
        final String dbname = getDatabaseColumnName(attribute, oclass, options);
        if (dbname == null) {
            return null;
        }
        final String value = AttributeUtil.getAsStringValue(attribute);
        //Null value filter is not supported
        if (value == null) {
            return null;
        }         
        final FilterWhereBuilder ret = createBuilder();
        final String op = not ? ">" : "<=";
        ret.addBind(dbname, op, value);
        return ret;
    }
    
    /**
     * Get the name of attribute translated to database column name
     * @param attribute to translate
     * @return the expected column name, or null if column is not supported
     */
    protected abstract String getDatabaseColumnName(Attribute attribute, ObjectClass oclass, OperationOptions options);

    /**
     * Validate the attribute to supported search types
     * @param singleValue
     */
    private boolean validateSearchAttribute(final Attribute attribute) {
        //Ignore streamed ( byte[] objects ) from query
        if(byte[].class.equals(AttributeUtil.getSingleValue(attribute).getClass())) {
            return false;
        }
        //Otherwise let the database process
        return true;
    }    
}
