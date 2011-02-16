package com.forgerock.openconnector.xsdparser;

import com.forgerock.openconnector.util.SchemaParserUtil;
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

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import org.identityconnectors.common.Assertions;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.FrameworkUtil;

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
        
        this.schemaSet = SchemaParserUtil.parseXSDSchema(filePath);
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

                    supportedOp = SchemaParserUtil.getSupportedOpClasses(supportedOpListString);
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

                                    Set<Flags> flagList = SchemaParserUtil.getFlags(annotationList);

                                    if (flagList != null) {
                                        flags.addAll(flagList);
                                    }
                                    
                                    attrType = SchemaParserUtil.getJavaClassType(annotationList);

                                }
                                if (attrType == null) {
                                    XSType typeNotFlagedJavaclass = elementTerm.getType();
                                    if(typeNotFlagedJavaclass.getName() != null){
                                        attrType = SchemaParserUtil.findJavaClassType(typeNotFlagedJavaclass.getName());
                                        
                                    }
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
        
        Schema returnSchema = schemaBuilder.build();

        log.info("Exit {0}", METHOD);
        
        return returnSchema;
    }

    public XSSchemaSet getXsdSchema(){
        if(schemaSet != null){
            return schemaSet;
        }else {
            this.schemaSet = SchemaParserUtil.parseXSDSchema(filePath);
            return schemaSet;
        }
    }  
}
