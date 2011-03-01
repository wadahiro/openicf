 /*
  *
 * Copyright (c) 2010 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * $Id$
 */
package com.forgerock.openconnector.xml;

import com.forgerock.openconnector.util.NamespaceLookup;
import com.forgerock.openconnector.xml.query.IQuery;
import com.forgerock.openconnector.xml.query.QueryImpl;
import com.forgerock.openconnector.xml.query.ComparisonQuery;
import com.forgerock.openconnector.xml.query.FunctionQuery;
import java.util.ArrayList;
import java.util.List;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.filter.*;


public class XMLFilterTranslator extends AbstractFilterTranslator<IQuery> {


    @Override
    public IQuery createEndsWithExpression(EndsWithFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createFunctionQuery(args, "ends-with", not);
    }

    @Override
    public IQuery createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        List<Object> values = filter.getAttribute().getValue();

        

        if (values.size() == 0) {
            return null;
        } else if (values.size() == 1) {
            ContainsFilter cf = new ContainsFilter(AttributeBuilder.build(tmpName, values.get(0)));
            return createContainsExpression(cf, not); 
        } else {

            List<IQuery> equalsQueries = new ArrayList<IQuery>();

            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i).toString();
                EqualsFilter ef = new EqualsFilter(AttributeBuilder.build(tmpName, value));
                IQuery query = createEqualsExpression(ef, not);
                equalsQueries.add(query);
            }

            IQuery orQuery = null;
            IQuery leftSide = equalsQueries.get(0);

            for (int i = 1; i < equalsQueries.size(); i++) {
                IQuery rightSide = equalsQueries.get(i);
                orQuery = createOrExpression(leftSide, rightSide);
                if (i < (equalsQueries.size() - 1)) {
                    leftSide = orQuery;
                }
            }
            return orQuery;
        }
    }

    @Override
    public IQuery createStartsWithExpression(StartsWithFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createFunctionQuery(args, "starts-with", not);
    }

    @Override
    public IQuery createContainsExpression(ContainsFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createFunctionQuery(args, "matches", not);
    }

    @Override
    public IQuery createEqualsExpression(EqualsFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createComparisonQuery(attrName, not ? "!=" : "=", value);
    }

    @Override
    public IQuery createGreaterThanExpression(GreaterThanFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createComparisonQuery(attrName, not ? "<" : ">", value);
    }
    
    @Override
    public IQuery createGreaterThanOrEqualExpression(GreaterThanOrEqualFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createComparisonQuery(attrName, not ? "<=" : ">=", value);
    }

    @Override
    public IQuery createAndExpression(IQuery leftExpression, IQuery rightExpression) {
        leftExpression.and(rightExpression);
        return leftExpression;
    }

    @Override
    public IQuery createOrExpression(IQuery leftExpression, IQuery rightExpression) {
        leftExpression.or(rightExpression);
        return leftExpression;
    }

    @Override
    public IQuery createLessThanExpression(LessThanFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createComparisonQuery(attrName, not ? ">" : "<", value);
    }

    @Override
    public IQuery createLessThanOrEqualExpression(LessThanOrEqualFilter filter, boolean not) {
        String tmpName = filter.getAttribute().getName();
        String attrName = createNameWithNamespace(tmpName);
        String value = getAttributeValue(filter.getAttribute());
        String [] args = createFunctionArgs(attrName, value);
        return createComparisonQuery(attrName, not ? ">=" : "<=", value);
    }

    private IQuery createComparisonQuery(String name, String operator, String value) {
        IQuery query = new QueryImpl();
        query.set(new ComparisonQuery("$x/" + name, operator, "'" + value + "'"));
        return query;
    }

    private IQuery createFunctionQuery(String [] args, String function, boolean not) {
        IQuery query = new QueryImpl();
        query.set(new FunctionQuery(args, function, not));
        return query;
    }

    private String[] createFunctionArgs(String attrName, String value) {
        String[] args = {"$x/" + attrName, "'" + value + "'"};
        return args;
    }

    private String createNameWithNamespace(String attrName) {
        String ns = NamespaceLookup.INSTANCE.getNamespace(attrName);
        if (ns == null)
            ns = XMLHandlerImpl.RI_NAMESPACE_PREFIX;
        return ns + ":" + attrName;
    }

    private String getAttributeValue(Attribute attribute) {
        List<Object> values = attribute.getValue();
        if (!values.isEmpty()) {
            return values.get(0).toString();
        } else {
            return "";
        }
    }
}
