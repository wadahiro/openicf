/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.xml.query;

/**
 *
 * @author slogum
 */
public class ComparisonQuery implements IPart {

    private String name;
    private String operator;
    private String value;
   
    
    public ComparisonQuery(String name, String operator, String value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    public String getExpression() {
        return String.format("%s %s %s", this.name, this.operator, this.value);
    }
}
