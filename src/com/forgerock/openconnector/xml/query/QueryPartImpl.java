/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.xml.query;

/**
 *
 * @author slogum
 */
public class QueryPartImpl implements IQueryPart {

    private String name;
    private String operator;
    private String value;
   
    
    public QueryPartImpl(String name, String operator, String value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return String.format("attributename: %s, operator: %s, value: %s", name, operator, value);
    }
}
