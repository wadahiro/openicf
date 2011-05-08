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
package org.forgerock.openicf.usd;

import java.util.List;
import org.forgerock.openicf.usd.query.IQuery;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.filter.*;
import org.identityconnectors.common.StringUtil;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.Uid;

/**
 * This is an implementation of AbstractFilterTranslator that gives a concrete representation
 * of which filters can be applied at the connector level (natively). If the 
 * USD doesn't support a certain expression type, that factory
 * method should return null. This level of filtering is present only to allow any
 * native contructs that may be available to help reduce the result set for the framework,
 * which will (strictly) reapply all filters specified after the connector does the initial
 * filtering.<p><p>Note: The generic query type is most commonly a String, but does not have to be.
 * 
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */
public class CAServiceDeskFilterTranslator extends AbstractFilterTranslator<IQuery> {

    private MetaResource resource;
    private ResourceRegistry registry;

    public CIMSFilterTranslator(MetaResource resource, ResourceRegistry registry) {
        this.resource = resource;
        this.registry = registry;
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createAndExpression(java.lang.Object, java.lang.Object)
     */
    @Override
    protected IQuery createAndExpression(IQuery leftExpression, IQuery rightExpression) {
        leftExpression.and(rightExpression);
        return leftExpression;
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createContainsAllValuesExpression(org.identityconnectors.framework.common.objects.filter.ContainsAllValuesFilter, boolean)
     */
    @Override
    protected IQuery createContainsAllValuesExpression(ContainsAllValuesFilter filter, boolean not) {
        List<Object> list = filter.getAttribute().getValue();
        String value = formatValue(list, true);
        return createQuery(filter.getAttribute(), not ? "NOT IN" : "IN", value);
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createContainsExpression(org.identityconnectors.framework.common.objects.filter.ContainsFilter, boolean)
     */
    @Override
    protected IQuery createContainsExpression(ContainsFilter filter, boolean not) {
        Object obj = getValue(filter.getAttribute());
        String value = "'%" + obj.toString() + "%'";
        return createQuery(filter.getAttribute(), not ? "NOT LIKE" : "LIKE", value);
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createEndsWithExpression(org.identityconnectors.framework.common.objects.filter.EndsWithFilter, boolean)
     */
    @Override
    protected IQuery createEndsWithExpression(EndsWithFilter filter, boolean not) {
        Object obj = getValue(filter.getAttribute());
        String value = "'%" + obj.toString() + "'";
        return createQuery(filter.getAttribute(), not ? "NOT LIKE" : "LIKE", value);
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createEqualsExpression(org.identityconnectors.framework.common.objects.filter.EqualsFilter, boolean)
     */
    @Override
    protected IQuery createEqualsExpression(EqualsFilter filter, boolean not) {
        Attribute attribute = filter.getAttribute();
        if (attribute instanceof Uid) {
            try {
                Integer.parseInt(AttributeUtil.getStringValue(attribute));
            } catch (NumberFormatException ex) {
                return createQuery(new Name(AttributeUtil.getStringValue(attribute)), not ? "<>" : "=");
            }
        }
        return createQuery(attribute, not ? "<>" : "=");
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createGreaterThanExpression(org.identityconnectors.framework.common.objects.filter.GreaterThanFilter, boolean)
     */
    @Override
    protected IQuery createGreaterThanExpression(GreaterThanFilter filter, boolean not) {
        return createQuery(filter.getAttribute(), not ? "<" : ">");
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createGreaterThanOrEqualExpression(org.identityconnectors.framework.common.objects.filter.GreaterThanOrEqualFilter, boolean)
     */
    @Override
    protected IQuery createGreaterThanOrEqualExpression(GreaterThanOrEqualFilter filter, boolean not) {
        return createQuery(filter.getAttribute(), not ? "<=" : ">=");
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createLessThanExpression(org.identityconnectors.framework.common.objects.filter.LessThanFilter, boolean)
     */
    @Override
    protected IQuery createLessThanExpression(LessThanFilter filter, boolean not) {
        return createQuery(filter.getAttribute(), not ? ">" : "<");
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createLessThanOrEqualExpression(org.identityconnectors.framework.common.objects.filter.LessThanOrEqualFilter, boolean)
     */
    @Override
    protected IQuery createLessThanOrEqualExpression(LessThanOrEqualFilter filter, boolean not) {
        return createQuery(filter.getAttribute(), not ? ">=" : "<=");
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createOrExpression(java.lang.Object, java.lang.Object)
     */
    @Override
    protected IQuery createOrExpression(IQuery leftExpression, IQuery rightExpression) {
        leftExpression.or(rightExpression);
        return leftExpression;
    }

    /* (non-Javadoc)
     * @see org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator#createStartsWithExpression(org.identityconnectors.framework.common.objects.filter.StartsWithFilter, boolean)
     */
    @Override
    protected IQuery createStartsWithExpression(StartsWithFilter filter, boolean not) {
        Object obj = getValue(filter.getAttribute());
        String value = "'" + obj.toString() + "%'";
        return createQuery(filter.getAttribute(), not ? "NOT LIKE" : "LIKE", value);
    }

    private IQuery createQuery(Attribute attribute, String operator) {
        Object actualValue = getValue(attribute);
        String formattedValue = formatValue(actualValue);
        return createQuery(attribute, operator, formattedValue);
    }

    private IQuery createQuery(Attribute attribute, String operator, String value) {
        MetaAttribute metaAttr = resource.find(attribute.getName());
        if (metaAttr.isQueriable()) {
            IQuery query = new QueryImpl(resource.getICResource());
            query.set(new QueryPartImpl(attribute.getName(), operator, value));
            return query;
        }

        return null;
    }

    /**
     * @param attribute
     * @return
     */
    private Object getValue(Attribute attribute) {
        MetaAttribute meta = resource.find(attribute.getName());

        // Verify meta data
        if (meta == null) {
            String msg = String.format("No such attribute '%s' in the resource '%s'", attribute.getName(), resource.getName());
            throw new IllegalArgumentException(msg);
        }

        AttributeInfo info = registry.getAttributeInfo(meta);

        try {
            if (info.isMultiValued()) {
                return attribute.getValue();
            } else if (Integer.class.equals(info.getType()) || int.class.equals(info.getType())) {
                return AttributeUtil.getIntegerValue(attribute);
            } else if (String.class.equals(info.getType()) || Character.class.equals(info.getType()) || char.class.equals(info.getType())) {
                return AttributeUtil.getStringValue(attribute);
            } else if (Long.class.equals(info.getType()) || long.class.equals(info.getType())) {
                return AttributeUtil.getLongValue(attribute);
            } else if (BigDecimal.class.equals(info.getType())) {
                return AttributeUtil.getBigDecimalValue(attribute);
            } else if (Boolean.class.equals(info.getType()) || boolean.class.equals(info.getType())) {
                return AttributeUtil.getBooleanValue(attribute);
            } else if (Double.class.equals(info.getType()) || double.class.equals(info.getType())) {
                return AttributeUtil.getDoubleValue(attribute);
            } else {
                return AttributeUtil.getAsStringValue(attribute);
            }
        } catch (ClassCastException ex) {
            return AttributeUtil.getAsStringValue(attribute);
        }
    }

    private String formatValue(Object obj) {
        return formatValue(obj, false);
    }

    private String formatValue(Object obj, boolean asList) {
        if (obj instanceof List<?>) {
            List<Object> list = (List<Object>) obj;

            if (list.size() == 1 && !asList) {
                return formatValue(list.get(0));
            }

            String result = "(";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    result += ", ";
                }
                result += formatValue(list.get(i));
            }
            return result + ")";
        } else if (obj instanceof String) {
            return "'" + (String) obj + "'";
        } else {
            return obj.toString();
        }
    }
}
