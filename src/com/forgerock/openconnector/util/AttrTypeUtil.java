/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeUtil;

public class AttrTypeUtil {

    public static Object createInstantiatedObject(String attrValue, String javaclass) {
        if (javaclass.equals(XmlHandlerUtil.STRING)) {
            String s = new String(attrValue);
            return s;
        } else if (javaclass.equals(XmlHandlerUtil.INT_PRIMITIVE)) {
            int i = new Integer(attrValue);
            return i;
        } else if (javaclass.equals(XmlHandlerUtil.INTEGER)) {
            Integer i = new Integer(attrValue);
            return i;
        } else if (javaclass.equals(XmlHandlerUtil.LONG)) {
            Long l = new Long(attrValue);
            return l;
        } else if (javaclass.equals(XmlHandlerUtil.LONG_PRIMITIVE)) {
            long l = new Long(attrValue);
            return l;
        } else if (javaclass.equals(XmlHandlerUtil.BOOLEAN)) {
            Boolean b = new Boolean(attrValue);
            return b;
        } else if (javaclass.equals(XmlHandlerUtil.BOOLEAN_PRIMITIVE)) {
            boolean b = new Boolean(attrValue);
            return b;
        } else if (javaclass.equals(XmlHandlerUtil.DOUBLE)) {
            Double d = new Double(attrValue);
            return d;
        } else if (javaclass.equals(XmlHandlerUtil.DOUBLE_PRIMITIVE)) {
            double d = new Double(attrValue);
            return d;
        } else if (javaclass.equals(XmlHandlerUtil.FLOAT)) {
            Float f = new Float(attrValue);
            return f;
        } else if (javaclass.equals(XmlHandlerUtil.FLOAT_PRIMITIVE)) {
            float f = new Float(attrValue);
            return f;
        } else if (javaclass.equals(XmlHandlerUtil.CHARACTER)) {
            Character c = attrValue.charAt(0);
            return c;
        } else if (javaclass.equals(XmlHandlerUtil.CHAR_PRIMITIVE)) {
            char c = attrValue.charAt(0);
            return c;
        } else if (javaclass.equals(XmlHandlerUtil.BIG_INTEGER)) {
            BigInteger bi = new BigInteger(attrValue);
            return bi;
        } else if (javaclass.equals(XmlHandlerUtil.BIG_DECIMAL)) {
            BigDecimal bd = new BigDecimal(attrValue);
            return bd;
        } else if (javaclass.equals(XmlHandlerUtil.GUARDED_STRING)) {
            GuardedString gs = new GuardedString(attrValue.toCharArray());
            return gs;
        } else if (javaclass.equals(XmlHandlerUtil.GUARDED_BYTE_ARRAY)) {
            GuardedByteArray gb = new GuardedByteArray(attrValue.getBytes());
            return gb;
        } else if (javaclass.equals(XmlHandlerUtil.BYTE_ARRAY)) {
            byte[] b = attrValue.getBytes();
            return b;
        } else {
            return null;
        }
    }

    public static List<String> findAttributeValue(Attribute attr, AttributeInfo attrInfo) {

        List<String> results = new ArrayList<String>();
        String javaClass = attrInfo.getType().getName();
        String stringValue = null;


        for (Object value : attr.getValue()) {
            
             Class clazz;
            try {
                clazz = Class.forName(javaClass);
                if (!clazz.isInstance(value)) {
                    throw new IllegalArgumentException(attrInfo.getName() + " not valid type. Should be of type " + clazz.getName());
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AttrTypeUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (javaClass.equals("org.identityconnectors.common.security.GuardedString")) {
                GuardedStringAccessor accessor = new GuardedStringAccessor();
                GuardedString gs = AttributeUtil.getGuardedStringValue(attr);
                gs.access(accessor);
                stringValue = String.valueOf(accessor.getArray());
            }
            else if (javaClass.equals("org.identityconnectors.common.security.GuardedByteArray")) {
                GuardedByteArrayAccessor accessor = new GuardedByteArrayAccessor();
                GuardedByteArray gba = (GuardedByteArray)attr.getValue().get(0);
                gba.access(accessor);
                stringValue = new String(accessor.getArray());
            }
            else {
                stringValue = AttributeUtil.getAsStringValue(attr);
            }
             results.add(stringValue);
        }
        return results;
    }
}
