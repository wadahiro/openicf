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
package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.util.NamespaceLookup;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryImpl;
import com.forgerock.openconnector.xml.query.ComparisonQuery;
import com.forgerock.openconnector.xml.query.FunctionQuery;
import java.util.List;
import org.identityconnectors.framework.common.objects.filter.*;

/**
 * This is an implementation of AbstractFilterTranslator that gives a concrete representation
 * of which filters can be applied at the connector level (natively). If the 
 * XML doesn't support a certain expression type, that factory
 * method should return null. This level of filtering is present only to allow any
 * native contructs that may be available to help reduce the result set for the framework,
 * which will (strictly) reapply all filters specified after the connector does the initial
 * filtering.<p><p>Note: The generic query type is most commonly a String, but does not have to be.
 * 
 * @author slogum
 * @version 1.0
 * @since 1.0
 */
public class XMLFilterTranslator extends AbstractFilterTranslator<IQuery> {

     /**
     * {@inheritDoc}
     */
    //return createComparisonQuery(attrName, not ? "!=" : "=", value);
    @Override
    protected IQuery createEndsWithExpression(EndsWithFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String [] args = createFunctionArgs(attrName, obj);
        return createFunctionQuery(args, "ends-with", not);
    }

    // TODO
    @Override
    protected IQuery createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createStartsWithExpression(StartsWithFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String [] args = createFunctionArgs(attrName, obj);
        return createFunctionQuery(args, "starts-with", not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createContainsExpression(ContainsFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String [] args = createFunctionArgs(attrName, obj);
        return createFunctionQuery(args, "matches", not);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createEqualsExpression(EqualsFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String value = removeBrackets(obj.toString());
        return createComparisonQuery(attrName, not ? "!=" : "=", value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createGreaterThanExpression(GreaterThanFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String value = removeBrackets(obj.toString());
        return createComparisonQuery(attrName, not ? "<" : ">", value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createGreaterThanOrEqualExpression(GreaterThanOrEqualFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String value = removeBrackets(obj.toString());
        return createComparisonQuery(attrName, not ? "<=" : ">=", value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createAndExpression(IQuery leftExpression, IQuery rightExpression) {
        leftExpression.and(rightExpression);
        return leftExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createOrExpression(IQuery leftExpression, IQuery rightExpression) {
        leftExpression.or(rightExpression);
        return leftExpression;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createLessThanExpression(LessThanFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String value = removeBrackets(obj.toString());
        return createComparisonQuery(attrName, not ? ">" : "<", value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery createLessThanOrEqualExpression(LessThanOrEqualFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        Object obj = filter.getAttribute().getValue();
        String value = removeBrackets(obj.toString());
        return createComparisonQuery(attrName, not ? ">=" : "<=", value);
    }

    private String formatValue(Object obj, boolean asList) {
        if (obj instanceof List<?>) { // if list
            List<Object> list = (List<Object>) obj;

            if (list.size() == 1 && !asList) {
                return formatValue(list.get(0));
            }

            String result = "(";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0)
                    result += ", ";
                result += formatValue(list.get(i));
            }
            return result + ")";
        }
        else if (obj instanceof String) { // if string
            return "'" + (String) obj + "'";
        }
        else {
            return obj.toString();
        }
    }

    private String formatValue(Object obj) {
        return formatValue(obj, false);
    }


    private IQuery createComparisonQuery(String attribute, String operator, String value) {
        IQuery query = new QueryImpl();
        query.set(new ComparisonQuery("$x/" + attribute, operator, "'" + value + "'"));
        return query;
    }

    private IQuery createFunctionQuery(String [] args, String function, boolean not) {
        IQuery query = new QueryImpl();
        query.set(new FunctionQuery(args, function, not));
        return query;
    }

    private String removeBrackets(String name) {
        return name.substring(1, name.length()-1);
    }

    private String[] createFunctionArgs(String attrName, Object obj) {
        String[] args = {"$x/" + attrName, "'" + removeBrackets(obj.toString()) + "'"};
        return args;
    }

    private String createNameWithNamespace(String attrName) {
        String ns = NamespaceLookup.INSTANCE.getNamespace(attrName);
        if (ns == null)
            ns = XMLHandlerImpl.RI_NAMESPACE_PREFIX;
        return ns + ":" + attrName;
    }
}
