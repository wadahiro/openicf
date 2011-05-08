/**
 * 
 */
package org.forgerock.openicf.usd.query;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;

import com.statoil.cims.connector.annotation.AnnotationHelper;
import com.statoil.cims.connector.annotation.Custom;
import com.statoil.cims.connector.annotation.ICAttribute;
import com.statoil.cims.connector.annotation.ICResource;
import com.statoil.cims.connector.annotation.ManyToManyWrapper;

/**
 * @author andrbj
 *
 */
public class QueryBuilder {

    private IQuery query;
    private String selectPart;
    private String fromPart;
    private String joinPart;
    private String wherePart;
    private ICResource mainResource;

    public QueryBuilder(IQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("Input query cannot be null");
        }

        this.query = query;
        this.selectPart = "";
        this.fromPart = "";
        this.joinPart = "";
        this.wherePart = "";

        processQuery();
    }

    /**
     * @param query2
     */
    private void processQuery() {
        mainResource = query.getResource();
        Iterator<IPart> iter = query.iterator();
        while (iter.hasNext()) {
            IPart part = iter.next();
            if (part instanceof IQueryPart) {
                try {
                    processQueryPart((IQueryPart) part);
                } catch (IntrospectionException e) {
                    throw new RuntimeException(e);
                }
            } else {
                wherePart += part.toString();
            }
        }
    }

    /**
     * @param part
     * @throws IntrospectionException
     */
    private void processQueryPart(IQueryPart part) throws IntrospectionException {
        ICAttribute attribute = AnnotationHelper.getAttribute(mainResource, part.getAttributeName());
        String[] segments = attribute.property().split("\\.");
        Class<? extends Serializable> curr = mainResource.type();

        for (int i = 0; i < segments.length; i++) {
            BeanInfo info = Introspector.getBeanInfo(curr);
            PropertyDescriptor descriptor = AnnotationHelper.findPropertyDescriptor(info, segments[i]);
            Class<?> clazz = descriptor.getPropertyType();

            if (AnnotationHelper.isJoinColumn(clazz, curr, segments[i])) {
                Class<? extends Serializable> prev = curr;
                curr = (Class<? extends Serializable>) clazz;
                createTableJoin(prev, curr, segments[i]);

                if (i < segments.length - 1) {
                    continue;
                }
            } else if (containsFlag(attribute, Flags.MULTIVALUED) && AnnotationHelper.isManyToManyJoin(curr, segments[i])) {
                ManyToManyWrapper mw = AnnotationHelper.getManyToManyData(curr, segments[i]);
                createManyToManyJoin(mw);
                curr = mw.getInverseJoin().entity;
            }

            String columnName = AnnotationHelper.getTableColumn(curr, segments[i], attribute);
            String tableName = checkTableName(curr, attribute);
            String value = checkValueType(clazz, attribute.type(), part.getValue());

            // Check if we have a reference to the table
            if (fromPart.indexOf(tableName) == -1) {
                addTable(tableName);
            }

            // Check for custom joins
            Custom cust = attribute.custom();
            if (cust.sourceProperty().length() > 0 && cust.targetClass() != Object.class) {
                createTableJoin(curr, (Class<? extends Serializable>) cust.targetClass(), cust.sourceProperty());
            }

            // build where string
            if (columnName.indexOf("%") > -1) {
                wherePart += String.format(tableName.length() > 0 ? tableName + "." + columnName : columnName, value.replaceAll("'", ""));
            } else {
                wherePart += String.format("%s%s %s %s",
                        tableName.length() > 0 ? tableName + "." : "",
                        columnName,
                        value == null ? "IS" : part.getOperator(),
                        value == null ? "NULL" : value);
            }
            break;
        }
    }

    /**
     * @param curr
     * @param attribute
     * @return
     */
    private String checkTableName(Class<? extends Serializable> currentEntity, ICAttribute attribute) {
        String result = "";

        Custom cust = attribute.custom();
        String custCol = cust.field();
        if (cust.targetClass() != Object.class) {
            // Check if the field has already been prefixed
            if (custCol.indexOf(".") == -1) {
                Class<? extends Serializable> c = (Class<? extends Serializable>) cust.targetClass();
                result = AnnotationHelper.getTableNameForClass(c);
            }
        } else {
            result = AnnotationHelper.getTableNameForClass(currentEntity);
        }

        return result;
    }

    /**
     * @param attribute
     * @param multivalued
     * @return
     */
    private boolean containsFlag(ICAttribute attribute, Flags flag) {
        return Arrays.asList(attribute.flags()).contains(flag);
    }

    /**
     * @param value
     * @param clazz
     * @param type
     * @return
     */
    private String checkValueType(Class<?> propertyType, Class<?> attributeType, String value) {
        if (propertyType == attributeType) {
            return value;
        }

        String result = null;
        if (propertyType == Integer.class) {
            if (attributeType == Boolean.class) {
                result = Boolean.valueOf(value) ? "0" : "1";
            }
        } else if (propertyType == Date.class) {
            if (attributeType == Long.class) {
                Timestamp time = new Timestamp(Long.valueOf(value));
                result = "'" + time.toString() + "'";
            }
        }

        return result == null ? value : result;
    }

    /**
     * @param prev
     * @param curr
     * @param string
     * @throws IntrospectionException
     */
    private void createTableJoin(Class<? extends Serializable> fromClass, Class<? extends Serializable> toClass, String property) throws IntrospectionException {
        String fromColName = AnnotationHelper.getColumnNameFromProperty(fromClass, property);
        String toColName = AnnotationHelper.resolveIdField(toClass);

        String fromTableName = AnnotationHelper.getTableNameForClass(fromClass);
        String toTableName = AnnotationHelper.getTableNameForClass(toClass);

        addTable(fromTableName, toTableName);

        String join = String.format("%s.%s = %s.%s", fromTableName, fromColName, toTableName, toColName);
        if (joinPart.indexOf(join) == -1) {
            if (joinPart.length() > 0) {
                joinPart = " AND " + joinPart;
            }
            joinPart = join + joinPart;
        }
    }

    /**
     * @param mw
     */
    private void createManyToManyJoin(ManyToManyWrapper mw) {
        String joinTable = mw.getJoinTableName();
        addTable(joinTable);

        String fromColName = mw.getJoin().columnName;
        String toColName = mw.getJoin().referenceColumnName;
        String toTableName = AnnotationHelper.getTableNameForClass(mw.getJoin().entity);
        String join1 = String.format("%s.%s = %s.%s", joinTable, fromColName, toTableName, toColName);

        fromColName = mw.getInverseJoin().columnName;
        toColName = mw.getInverseJoin().referenceColumnName;
        toTableName = AnnotationHelper.getTableNameForClass(mw.getInverseJoin().entity);
        String join2 = String.format("%s.%s = %s.%s", joinTable, fromColName, toTableName, toColName);

        String join = join1 + " AND " + join2;
        if (joinPart.length() > 0) {
            joinPart += " AND ";
        }
        joinPart += join;
    }

    private void addTable(String... tables) {
        for (String table : tables) {
            if (fromPart.indexOf(table) == -1) {
                if (fromPart.length() > 0) {
                    fromPart += ", ";
                }
                fromPart += table;
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (selectPart.length() == 0) {
            String mainTableName = AnnotationHelper.getTableNameForClass(mainResource.type());
            selectPart = mainTableName + ".*";
        }

        // combine joins and where parts
        String where = "";
        if (joinPart.length() > 0) {
            where = joinPart;
            if (wherePart.length() > 0) {
                where += " AND " + wherePart;
            }
        } else if (wherePart.length() > 0) {
            where = wherePart;
        }

        // handle extra where clause
        String extraWhere = mainResource.extraWhere();
        if (extraWhere.length() > 0) {
            if (where.length() > 0) {
                where = extraWhere + " AND " + where;
            } else {
                where = extraWhere;
            }
        }

        if (where.length() > 0) {
            where = "WHERE " + where;
        }

        return String.format("SELECT %s FROM %s %s", selectPart, fromPart, where);
    }
}
