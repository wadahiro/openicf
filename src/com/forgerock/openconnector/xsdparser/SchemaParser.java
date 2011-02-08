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
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.FrameworkUtil;

public class SchemaParser {

    private static final Log log = Log.getLog(SchemaParser.class);
    private Class< ? extends Connector> connectorClass;
    private String filePath;
    private XSSchemaSet schemaSet;
    
    public SchemaParser(Class< ? extends Connector> connectorclass, String filePath){
        this.connectorClass = connectorclass;
        this.filePath = filePath;
        parseXSDSchema();
    }

    public Schema parseSchema() {
        final String METHOD = "persaSchema";
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
                            log.error(e, "SupportedObjectClass {0} not supported", removeOp.toString());
                        }
                    }
                }
            }
        }
        
        log.info("Exit{0}", METHOD);
        return schemaBuilder.build();
    }

    public XSSchemaSet getXsdSchema(){
        return schemaSet;
    }

    private void parseXSDSchema(){
        XSOMParser parser = new XSOMParser();

        try {
            File file = new File(filePath);
            
            parser.setAnnotationParser(new XSDAnnotationFactory());
            parser.parse(file);

            this.schemaSet = parser.getResult();
        } catch (SAXException e) {
            log.error(e, "Failed to parser XSD-schema from file: {0}" , filePath);
        } catch (IOException e) {
            log.error(e, "Failed to read from file: {0}" , filePath);
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

            String className = null;

            if (name.equals("string")) {
                className = "java.lang.String";

            } else if (name.equals("boolean")) {
                className = "java.lang.Boolean";

            } else if (name.equals("long")) {
                className = "java.lang.Long";

            } else if (name.equals("int")) {
                className = "java.lang.Integer";

            } else if (name.equals("float")) {
                className = "java.lang.Float";

            } else if (name.equals("double")) {
                className = "java.lang.Double";

            } else if (name.equals("base64Binary")) {
                className = "org.identityconnectors.common.security.GuardedByteArray";
                
            } else if (name.equals("decimal")) {
                className = "java.math.BigDecimal";
                
            }
            try {
                if (className != null) {
                    return Class.forName(className);
                }
            } catch (ClassNotFoundException e) {
               log.error(e, "Class {0} not found.", className);
            }
        }
        return null;
    }

    private Class<?> getJavaClassType(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String fileString = list.get(i);
                    
            if (fileString.contains("javaclass")){
                try {
                    return Class.forName(list.get(i + 1));
                } catch (ClassNotFoundException e) {
                   log.error(e, "Class {0} not found", fileString);
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
