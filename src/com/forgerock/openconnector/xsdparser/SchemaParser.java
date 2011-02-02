package com.forgerock.openconnector.xsdparser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.parser.XSOMParser;

public class SchemaParser {
	
	
	private void parseSchema(){
		XSOMParser p = new XSOMParser();
		XSSchemaSet set = null;
		try {
			p.setAnnotationParser(new XSDAnnotationFactory());
			p.parse(new File("test_sample.xsd"));
			set = p.getResult();
		} catch (SAXException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		if(set != null){
			Collection<XSSchema> cs= set.getSchemas();
			
			for(XSSchema er : cs){		
				System.out.println(er.getTargetNamespace());
				
				Map<String, XSComplexType> types = er.getComplexTypes();
				Set<String> typesKeys = types.keySet();
				Iterator<String> typesIterator = typesKeys.iterator();
				
				while(typesIterator.hasNext()){
					System.out.println();
					XSComplexType type = er.getComplexType(typesIterator.next());
					
					if(type != null){
						if(type.getAnnotation() != null)
							System.out.println(type.getName() + " " + type.getAnnotation().getAnnotation());
					    XSContentType xsType = type.getContentType();
					    XSParticle paricle = xsType.asParticle();
					    	
					    if(paricle != null){
					        XSTerm term = paricle.getTerm();
					        
					        if(term.isModelGroup()){
					            XSModelGroup grp = term.asModelGroup();
					            XSParticle[] particles = grp.getChildren();
					            
					            for(XSParticle pu : particles ){
					                XSTerm puTerm = pu.getTerm();
					                if(puTerm.isElementDecl()){
					                    System.out.println(puTerm.asElementDecl().getName() + " : " + puTerm.asElementDecl().getType());
					                    if(puTerm.getAnnotation() != null)
					                    	System.out.print(puTerm.getAnnotation().getAnnotation());
					                }
					            }
					        }
					    }
					}
				}
			}
		}
	}
	
	public static void main(String [] args){
		new SchemaParser().parseSchema();
	}
}
