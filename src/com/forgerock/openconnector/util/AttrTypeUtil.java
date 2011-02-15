/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;

public class AttrTypeUtil {

    public static final String STRING = "String";
    public static final String INT_PRIMITIVE = "int";
    public static final String INTEGER = "Integer";
    public static final String LONG_PRIMITIVE = "long";
    public static final String LONG = "Long";
    public static final String BOOLEAN_PRIMITIVE = "boolean";
    public static final String BOOLEAN = "Boolean";
    public static final String DOUBLE_PRIMITIVE = "double";
    public static final String DOUBLE = "Double";
    public static final String FLOAT_PRIMITIVE = "float";
    public static final String FLOAT = "Float";
    public static final String CHAR_PRIMITIVE = "char";
    public static final String CHARACTER = "Character";
    public static final String BIG_INTEGER = "BigInteger";
    public static final String BIG_DECIMAL = "BigDecimal";
    public static final String GUARDED_STRING = "GuardedString";
    public static final String GUARDED_BYTE_ARRAY = "GuardedByteArray";
    public static final String BYTE_ARRAY = "byte[]";

    public static Object createInstantiatedObject(String attrValue, String javaclass) {
        if (javaclass.equals(STRING)) {
            String s = new String(attrValue);
            return s;
        } else if (javaclass.equals(INT_PRIMITIVE)) {
            int i = new Integer(attrValue);
            return i;
        } else if (javaclass.equals(INTEGER)) {
            Integer i = new Integer(attrValue);
            return i;
        } else if (javaclass.equals(LONG)) {
            Long l = new Long(attrValue);
            return l;
        } else if (javaclass.equals(LONG_PRIMITIVE)) {
            long l = new Long(attrValue);
            return l;
        } else if (javaclass.equals(BOOLEAN)) {
            Boolean b = new Boolean(attrValue);
            return b;
        } else if (javaclass.equals(BOOLEAN_PRIMITIVE)) {
            boolean b = new Boolean(attrValue);
            return b;
        } else if (javaclass.equals(DOUBLE)) {
            Double d = new Double(attrValue);
            return d;
        } else if (javaclass.equals(DOUBLE_PRIMITIVE)) {
            double d = new Double(attrValue);
            return d;
        } else if (javaclass.equals(FLOAT)) {
            Float f = new Float(attrValue);
            return f;
        } else if (javaclass.equals(FLOAT_PRIMITIVE)) {
            float f = new Float(attrValue);
            return f;
        } else if (javaclass.equals(CHARACTER)) {
            Character c = attrValue.charAt(0);
            return c;
        } else if (javaclass.equals(CHAR_PRIMITIVE)) {
            char c = attrValue.charAt(0);
            return c;
        } else if (javaclass.equals(BIG_INTEGER)) {
            BigInteger bi = new BigInteger(attrValue);
            return bi;
        } else if (javaclass.equals(BIG_DECIMAL)) {
            BigDecimal bd = new BigDecimal(attrValue);
            return bd;
        } else if (javaclass.equals(GUARDED_STRING)) {
            GuardedString gs = new GuardedString(attrValue.toCharArray());
            return gs;
        } else if (javaclass.equals(GUARDED_BYTE_ARRAY)) {
            GuardedByteArray gb = new GuardedByteArray(attrValue.getBytes());
            return gb;
        } else if (javaclass.equals(BYTE_ARRAY)) {
            byte[] b = attrValue.getBytes();
            return b;
        } else {
            return null;
        }
    }

    public static String findAttributeValue(Attribute attr, AttributeInfo attrInfo) {

        return null;
    }
}
