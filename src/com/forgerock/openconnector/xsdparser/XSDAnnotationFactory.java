package com.forgerock.openconnector.xsdparser;

import com.sun.xml.xsom.parser.AnnotationParser;
import com.sun.xml.xsom.parser.AnnotationParserFactory;

public class XSDAnnotationFactory implements AnnotationParserFactory{

	@Override
	public AnnotationParser create() {
		return new XSDAnnotationParser();
	}

}
