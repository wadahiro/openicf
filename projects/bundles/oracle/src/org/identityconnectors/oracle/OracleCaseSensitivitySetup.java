package org.identityconnectors.oracle;

import java.util.*;

import org.identityconnectors.framework.spi.AttributeNormalizer;

/** OracleCaseSensitivity is responsible for normalizing and formatting oracle objects tokens (users,schema...).
 *  Maybe we do not need such granurality, and have just one settings for all objects, but using this scenario
 *  we should cover all corner cases.
 *  For user we will provide some simplification how to define this caseSensitivy setup.
 *  This setup is used for formatting and normalizing of user attributes.
 *  
 *  We distinguish between normalizing and formatting of attributes.
 *  Normalizing is called from framework by {@link AttributeNormalizer} while delegating to normalizeToken.
 *  
 *  Formating is called when building sql for create/alter where we just e.g append quotes 
 */
interface OracleCaseSensitivitySetup {
	/**
	 * Normalize token, e.g make it uppercase 
	 * @param attr
	 * @param token
	 * @return
	 */
    public String normalizeToken(OracleUserAttributeCS attr,String token);
    /**
     * Compound operation that normalizes and then formats
     * @param attr
     * @param token
     * @return
     */
    public String normalizeAndFormatToken(OracleUserAttributeCS attr,String token);
    /**
     * Just format token, e.g append quotes
     * @param attr
     * @param token
     * @return
     */
    public String formatToken(OracleUserAttributeCS attr,String token);
    /**
     * Formats char array , useful for password to not convert to String
     * @param attr
     * @param token
     * @return
     */
    public char[] formatToken(OracleUserAttributeCS attr,char[] token);
    /**
     * Gets formatter by attribue
     * @param attribute
     * @throws  IllegalArgumentException when formatter by attribute is not found
     * @return CSTokenFormatter by attribute
     */
    public CSTokenFormatter getAttributeFormatter(OracleUserAttributeCS attribute) throws IllegalArgumentException;
    /**
     * Gets normalizer by attribue
     * @param attribute
     * @throws  IllegalArgumentException when normalizer by attribute is not found
     * @return CSTokenFormatter by attribute
     */
    public CSTokenNormalizer getAttributeNormalizer(OracleUserAttributeCS attribute) throws IllegalArgumentException;
    
}

/** Formatter that formats given token. 
 *  It can append quates etc.
 *
 */
final class CSTokenFormatter{
    private OracleUserAttributeCS attribute;
    private String quatesChar = "";
    OracleUserAttributeCS getAttribute() {
        return attribute;
    }
    String getQuatesChar() {
        return quatesChar;
    }
    char[] formatToken(char[] token){
        if(token == null){
            return token;
        }
        StringBuilder builder = new StringBuilder(token.length + 2);
        builder.append(quatesChar);
        builder.append(token);
        builder.append(quatesChar);
        char[] dst = new char[builder.length()];
        builder.getChars(0, builder.length() , dst, 0);
        builder.delete(0, builder.length());
        return dst;
    }
    String formatToken(String token){
        if(token == null){
            return token;
        }
        return new String(formatToken(token.toCharArray()));
    }
    private CSTokenFormatter(){}
    
    static CSTokenFormatter build(OracleUserAttributeCS attribute,String quatesChar){
    	return new Builder().setAttribute(attribute).setQuatesChar(quatesChar).build();
    }
    
    final static class Builder{
        CSTokenFormatter element = new CSTokenFormatter();
        Builder setAttribute(OracleUserAttributeCS attribute){
            element.attribute = attribute;
            return this;
        }
        Builder setQuatesChar(String quatesChar){
            element.quatesChar = quatesChar;
            return this;
        }
        Builder setValues(Map<String, Object> aMap){
            Map<String, Object> map = new HashMap<String, Object>(aMap);
            String quates = (String) map.remove("quates");
            if(quates != null){
                element.quatesChar = quates;
            }
            if(!map.isEmpty()){
                throw new RuntimeException("Some elements in aMap not recognized : " + map);
            }
            return this;
        }
        CSTokenFormatter build(){
            if(element.attribute == null){
                throw new IllegalStateException("Attribute not set");
            }
            return element;
        }
        CSTokenFormatter buildWithDefaultValues(OracleUserAttributeCS attribute){
            setAttribute(attribute);
            element.quatesChar = element.attribute.getDefQuatesChar();
            return build();
        }
    }
}

final class CSTokenNormalizer{
    private OracleUserAttributeCS attribute;
    private boolean toUpper;
    OracleUserAttributeCS getAttribute() {
        return attribute;
    }
    boolean isToUpper() {
        return toUpper;
    }
    String normalizeToken(String token){
        if(token == null || token.length() == 0){
            return token;
        }
        return toUpper ? token.toUpperCase() : token;
    }
    
    static CSTokenNormalizer build(OracleUserAttributeCS attribute,boolean toUpper){
    	return new Builder().setAttribute(attribute).setToUpper(toUpper).build();
    }

    
    final static class Builder{
        CSTokenNormalizer element = new CSTokenNormalizer();
        Builder setAttribute(OracleUserAttributeCS attribute){
            element.attribute = attribute;
            return this;
        }
        Builder setToUpper(boolean toUpper){
            element.toUpper = toUpper;
            return this;
        }
        Builder setValues(Map<String, Object> aMap){
            Map<String, Object> map = new HashMap<String, Object>(aMap);
            String toUpper = (String) map.remove("upper");
            if(toUpper != null){
                element.toUpper = Boolean.valueOf(toUpper);
            }
            if(!map.isEmpty()){
                throw new RuntimeException("Some elements in aMap not recognized : " + map);
            }
            return this;
        }
        CSTokenNormalizer build(){
            if(element.attribute == null){
                throw new IllegalStateException("Attribute not set");
            }
            return element;
        }
        CSTokenNormalizer buildWithDefaultValues(OracleUserAttributeCS attribute){
            setAttribute(attribute);
            element.toUpper = element.attribute.isDefToUpper();
            return build();
        }
    }
}


/** Builder of OracleCaseSensitivity using formatters for user attributes */
final class OracleCaseSensitivityBuilder{
    private Map<OracleUserAttributeCS,CSTokenFormatter> formatters = new HashMap<OracleUserAttributeCS, CSTokenFormatter>(6);
    private Map<OracleUserAttributeCS,CSTokenNormalizer> normalizers = new HashMap<OracleUserAttributeCS, CSTokenNormalizer>(6);
    
    OracleCaseSensitivityBuilder defineFormatters(CSTokenFormatter... formatters){
        for(CSTokenFormatter element : formatters){
            this.formatters.put(element.getAttribute(),element);
        }
        return this;
    }
    
    
    OracleCaseSensitivityBuilder defineNormalizers(CSTokenNormalizer... normalizers){
        for(CSTokenNormalizer element : normalizers){
            this.normalizers.put(element.getAttribute(),element);
        }
        return this;
    }
    
    
    @SuppressWarnings("unchecked")
    OracleCaseSensitivityBuilder parseMap(String format){
        //If we set string default, all formatters will have its default value
        if("default".equalsIgnoreCase(format)){
            return this;
        }
        final Map<String, Object> map = MapParser.parseMap(format);
        final Map<String, Object> formatters = (Map<String, Object>) map.remove("formatters");
        final Map<String, Object> normalizers = (Map<String, Object>) map.remove("normalizers");
        if(!map.isEmpty()){
            throw new IllegalArgumentException("Unrecognized element in format map " + map.keySet());
        }
        if(formatters != null){
            for(String attributeName : formatters.keySet()){
                Map<String, Object> elementMap = (Map<String, Object>) formatters.get(attributeName);
                if("ALL".equalsIgnoreCase(attributeName)){
                    for(OracleUserAttributeCS attribute : OracleUserAttributeCS.values()){
                        this.formatters.put(attribute, new CSTokenFormatter.Builder().setAttribute(attribute).setValues(elementMap).build());
                    }
                    continue;
                }
                OracleUserAttributeCS attribute = OracleUserAttributeCS.valueOf(attributeName);
                CSTokenFormatter element = new CSTokenFormatter.Builder().setAttribute(attribute).setValues(elementMap).build(); 
                this.formatters.put(element.getAttribute(),element);    
            }
        }
        if(normalizers != null){
            for(String attributeName : normalizers.keySet()){
                Map<String, Object> elementMap = (Map<String, Object>) normalizers.get(attributeName);
                if("ALL".equalsIgnoreCase(attributeName)){
                    for(OracleUserAttributeCS attribute : OracleUserAttributeCS.values()){
                        this.normalizers.put(attribute, new CSTokenNormalizer.Builder().setAttribute(attribute).setValues(elementMap).build());
                    }
                    continue;
                }
                OracleUserAttributeCS attribute = OracleUserAttributeCS.valueOf(attributeName);
                CSTokenNormalizer element = new CSTokenNormalizer.Builder().setAttribute(attribute).setValues(elementMap).build(); 
                this.normalizers.put(element.getAttribute(),element);    
            }
        }
        return this;
    }
    
    OracleCaseSensitivitySetup build(){
        Map<OracleUserAttributeCS,CSTokenFormatter> formatters = new HashMap<OracleUserAttributeCS, CSTokenFormatter>(this.formatters);
        Map<OracleUserAttributeCS,CSTokenNormalizer> normalizers = new HashMap<OracleUserAttributeCS, CSTokenNormalizer>(this.normalizers);
        
        //If any elements is not defined in specified map set default value
        for(OracleUserAttributeCS attribute : OracleUserAttributeCS.values()){
            CSTokenFormatter formatter = formatters.get(attribute);
            if(formatter == null){
                formatter = new CSTokenFormatter.Builder().buildWithDefaultValues(attribute);
                formatters.put(attribute,formatter);
            }
            CSTokenNormalizer normalizer = normalizers.get(attribute);
            if(normalizer == null){
                normalizer = new CSTokenNormalizer.Builder().buildWithDefaultValues(attribute);
                normalizers.put(attribute,normalizer);
            }
        }
        return new OracleCaseSensitivityImpl(formatters,normalizers);
    }
    
}

/**
 * Implementation of OracleCaseSensitivitySetup that will use map of
 * formatters and normalizers for each {@link OracleUserAttributeCS}
 * @author kitko
 *
 */
final class OracleCaseSensitivityImpl implements OracleCaseSensitivitySetup{
    private Map<OracleUserAttributeCS,CSTokenFormatter> formatters;
    private Map<OracleUserAttributeCS,CSTokenNormalizer> normalizers;    
    
    OracleCaseSensitivityImpl(Map<OracleUserAttributeCS,CSTokenFormatter> formatters,Map<OracleUserAttributeCS,CSTokenNormalizer> normalizers){
        this.formatters = new HashMap<OracleUserAttributeCS, CSTokenFormatter>(formatters);
        this.normalizers = new HashMap<OracleUserAttributeCS, CSTokenNormalizer>(normalizers);
    }
    public CSTokenFormatter getAttributeFormatter(OracleUserAttributeCS attribute){
        final CSTokenFormatter formatter = formatters.get(attribute);
        if(formatter == null){
            throw new IllegalArgumentException("No formatter defined for attribute " + attribute);
        }
        return formatter;
    }
    
    public CSTokenNormalizer getAttributeNormalizer(OracleUserAttributeCS attribute){
        final CSTokenNormalizer normalizer = normalizers.get(attribute);
        if(normalizer == null){
            throw new IllegalArgumentException("No normalizer defined for attribute " + attribute);
        }
        return normalizer;
    }
    
    public String formatToken(OracleUserAttributeCS attr, String token) {
        return getAttributeFormatter(attr).formatToken(token);
    }
    public String normalizeToken(OracleUserAttributeCS attr, String token) {
        return getAttributeNormalizer(attr).normalizeToken(token);
    }
    public char[] formatToken(OracleUserAttributeCS attr, char[] token) {
        return getAttributeFormatter(attr).formatToken(token);
    }
    public String normalizeAndFormatToken(OracleUserAttributeCS attr, String token) {
        token = normalizeToken(attr, token);
        token = formatToken(attr, token);
        return token;
    }
    
}



 
