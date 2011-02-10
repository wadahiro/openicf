package com.forgerock.openconnector.xsdparser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.operations.*;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.FrameworkUtil;
import org.identityconnectors.framework.common.exceptions.ConnectorIOException;

public class SchemaParser {

    private static final Log log = Log.getLog(SchemaParser.class);
    private Class< ? extends Connector> connectorClass;
    private String filePath;
    private XSSchemaSet schemaSet;
    
    public SchemaParser(Class< ? extends Connector> connectorClass, String filePath){

        Assertions.nullCheck(connectorClass, "connectorClass");
        Assertions.blankCheck(filePath, "filePath");
        
        this.connectorClass = connectorClass;
        this.filePath = filePath;
        
        parseXSDSchema();
    }

    /*
     * Takes the xsd-schema and parses it to icf-schema
     */

    public Schema parseSchema() {
        final String METHOD = "parseSchema";
        log.info("Entry {0}", METHOD);

        SchemaBuilder schemaBuilder = new SchemaBuilder(connectorClass);

        XSSchema schema = schemaSet.getSchema(1);

        Map<String, XSElementDecl> types = schema.getElementDecls();
        Set<String> typesKeys = types.keySet();
        Iterator<String> typesIterator = typesKeys.iterator();

        while (typesIterator.hasNext()) {
            XSElementDecl type = schema.getElementDecl(typesIterator.next());

            Set<AttributeInfo> attributes = new HashSet<AttributeInfo>();
            List<Class<? extends SPIOperation>> supportedOp = new LinkedList<Class<? extends SPIOperation>>();

            ObjectClassInfoBuilder objectClassBuilder = new ObjectClassInfoBuilder();
            objectClassBuilder.setType(type.getName());

            if (type != null) {
                XSComplexType xsCompType = type.getType().asComplexType();

                if (xsCompType.getAnnotation() != null) {
                    String supportedOpString = xsCompType.getAnnotation().getAnnotation().toString();
                    String[] supportedOpStringSplit = supportedOpString.split(" |\n");
                    List<String> supportedOpListString = Arrays.asList(supportedOpStringSplit);

                    supportedOp = getSupportedOpClasses(supportedOpListString);
                }

                XSContentType xsContType = xsCompType.getContentType();
                XSParticle particle = xsContType.asParticle();

                if (particle != null) {
                    XSTerm term = particle.getTerm();

                    if (term.isModelGroup()) {
                        XSModelGroup grp = term.asModelGroup();
                        XSParticle[] particles = grp.getChildren();

                        for (XSParticle childParticle : particles) {
                            XSTerm childParticleTerm = childParticle.getTerm();

                            if (childParticleTerm.isElementDecl()) {
                                XSElementDecl elementTerm = childParticleTerm.asElementDecl();
                                Set<Flags> flags = new HashSet<Flags>();
                                Class<?> attrType = null;

                                if (childParticle.getMinOccurs() == 1) {
                                    flags.add(Flags.REQUIRED);
                                }
                                if (childParticle.getMaxOccurs() > 1 || childParticle.getMaxOccurs() == -1) {
                                    flags.add(Flags.MULTIVALUED);
                                }
                                if (elementTerm.getAnnotation() != null) {
                                    String annotations = elementTerm.getAnnotation().getAnnotation().toString();

                                    String[] annotationsSplit = annotations.split(" |\n");
                                    List<String> annotationList = Arrays.asList(annotationsSplit);

                                    if (getFlags(annotationList) != null) {
                                        flags.addAll(getFlags(annotationList));
                                    }
                                    attrType = getJavaClassType(annotationList);

                                }
                                if (attrType == null) {
                                    XSType typeNotFlagedJavaclass = elementTerm.getType();
                                    attrType = findJavaClassType(typeNotFlagedJavaclass.getName());
                                }

                                try {
                                    AttributeInfo attributeInfo = null;

                                    if (attrType != null) {
                                        attributeInfo = AttributeInfoBuilder.build(elementTerm.getName(), attrType, flags);
                                    } else {
                                        attributeInfo = AttributeInfoBuilder.build(elementTerm.getName());
                                    }

                                    if(attributeInfo != null){
                                        attributes.add(attributeInfo);
                                    }
                                } catch (Exception e) {
                                    log.error(e, "Failed to build Attribute {0}", elementTerm.getName());
                                }
                            }
                        }
                    }
                }
            }
            objectClassBuilder.addAllAttributeInfo(attributes);
            ObjectClassInfo objectClassInfo = objectClassBuilder.build();

            schemaBuilder.defineObjectClass(objectClassInfo);

            if (supportedOp.size() >= 1) {
                for (Class<? extends SPIOperation> removeOp : FrameworkUtil.allSPIOperations()) {
                    if (!supportedOp.contains(removeOp)) {
                        try {
                            schemaBuilder.removeSupportedObjectClass(removeOp, objectClassInfo);
                        } catch (IllegalArgumentException e) {
                            log.info("SupportedObjectClass {0} not supported", removeOp.toString());
                        }
                    }
                }
            }
        }
        
        log.info("Exit {0}", METHOD);
        return schemaBuilder.build();
    }

    public XSSchemaSet getXsdSchema(){
        if(schemaSet != null){
            return schemaSet;
        }else {
            parseXSDSchema();
            return schemaSet;
        }
    }

    private void parseXSDSchema(){
        XSOMParser parser = new XSOMParser();

        try {
            File file = new File(filePath);
            
            parser.setAnnotationParser(new XSDAnnotationFactory());
            parser.parse(file);

            this.schemaSet = parser.getResult();
            
        } catch (SAXException e) {
            String eMessage =  "Failed to parser XSD-schema from file: " + filePath;

            log.error(e, eMessage);
            throw new ConnectorIOException(filePath, e);
            
        } catch (IOException e) {
            String eMessage =  "Failed to read from file: " + filePath;
            
            log.error(e, eMessage);
            throw new ConnectorIOException(filePath, e);
        }
    }

    private List<Class<? extends SPIOperation>> getSupportedOpClasses(List<String> supportedOpList) {
        List<Class<? extends SPIOperation>> list = new LinkedList<Class<? extends SPIOperation>>();

        for (String s : supportedOpList) {
            if (s.equals("CREATE")) {
                list.add(CreateOp.class);

            } else if (s.equals("AUTHENTICATE")) {
                list.add(AuthenticateOp.class);

            } else if (s.equals("DELETE")) {
                list.add(DeleteOp.class);

            } else if (s.equals("RESOLVEUSERNAME")) {
                list.add(ResolveUsernameOp.class);

            } else if (s.equals("SCHEMA")) {
                list.add(SchemaOp.class);

            } else if (s.equals("SCRIPTONCONNECTOR")) {
                list.add(ScriptOnConnectorOp.class);

            } else if (s.equals("SCRIPTONRESOURCE")) {
                list.add(ScriptOnResourceOp.class);

            } else if (s.equals("SEARCH")) {
                list.add(SearchOp.class);

            } else if (s.equals("SYNC")) {
                list.add(SyncOp.class);

            } else if (s.equals("TEST")) {
                list.add(TestOp.class);

            } else if (s.equals("UPDATEATTRIBUTEVALUES")) {
                list.add(UpdateAttributeValuesOp.class);

            } else if (s.equals("UPDATE")) {
                list.add(UpdateOp.class);
            }
        }
        return list;
    }

    private Class<?> findJavaClassType(String name) {
        if (name != null) {

            if (name.equals("string")) {
                return String.class;

            } else if (name.equals("boolean")) {
                return boolean.class;

            } else if (name.equals("long")) {
                return long.class;
                
            } else if (name.equals("int")) {
                return int.class;

            } else if (name.equals("float")) {
                return float.class;

            } else if (name.equals("double")) {
                return double.class;

            } else if (name.equals("base64Binary")) {
                return byte[].class;

            } else if (name.equals("char")){
                return char.class;
                
            }
        }
        return null;
    }

    private Class<?> getJavaClassType(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String fileString = list.get(i);
                    
            if (fileString.contains("javaclass")){
                String clasString = list.get(i + 1);
                
                try {
                    return Class.forName(clasString);
                    
                } catch (ClassNotFoundException e) {
                   log.error(e, "Class {0} not found.", clasString);
                }
            }
        }
        return null;
    }

    private Set<Flags> getFlags(List<String> list) {
        Set<Flags> flags = new HashSet<Flags>();
        
        for (String s : list) {
            if (s.equals("NOT_CREATABLE")) {
                flags.add(Flags.NOT_CREATABLE);

            } else if (s.equals("NOT_UPDATABLE")) {
                flags.add(Flags.NOT_UPDATEABLE);

            } else if (s.equals("NOT_READABLE")) {
                flags.add(Flags.NOT_READABLE);
                flags.add(Flags.NOT_RETURNED_BY_DEFAULT);

            } else if (s.equals("NOT_RETURNED_BY_DEFAULT")) {
                flags.add(Flags.NOT_RETURNED_BY_DEFAULT);
                
            }
        }
        return flags;
    }
}
