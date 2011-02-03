package com.forgerock.openconnector.xsdparser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import com.sun.xml.xsom.parser.AnnotationContext;
import com.sun.xml.xsom.parser.AnnotationParser;

public class XSDAnnotationParser extends AnnotationParser{
	
	private boolean parse = false;
	private String addValue = "";
	
	StringBuilder stringBuilder = new StringBuilder();
	@Override
	public ContentHandler getContentHandler(AnnotationContext arg0, String arg1, ErrorHandler arg2, EntityResolver arg3) {
		return new ContentHandler() {
			
			@Override
			public void startPrefixMapping(String arg0, String arg1)
					throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {	
				if(localName.equals("appinfo") || localName.equals("sequence")){
					parse = true;
				}
				
				if(!qName.equals("xsd:appinfo") && !qName.equals("xsd:element")){
					addValue = qName;
				}	
			}
			
			@Override
			public void startDocument() throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void skippedEntity(String arg0) throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setDocumentLocator(Locator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void processingInstruction(String arg0, String arg1)
					throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
					throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void endPrefixMapping(String arg0) throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				if(localName.equals("appinfo")){
					parse = false;
				}
				
			}
			
			@Override
			public void endDocument() throws SAXException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void characters(char[] chars, int start, int length) throws SAXException {
				if(parse){
					StringBuilder sb = new StringBuilder();
					sb.append(chars, start, length);
					if(!sb.toString().replace(" ", "").trim().equals("")){
						String stringToAppend = addValue + " " + sb.toString().trim();
						stringBuilder.append(stringToAppend);
						stringBuilder.append("\n");
					}
				}
			}
		};
	}

	@Override
	public Object getResult(Object arg0) {
		return stringBuilder.toString().trim();
	}

}
