package org.forgerock.openicf.usd.resources;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ObjectClassInfoBuilder;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo.Flags;
import org.scannotation.AnnotationDB;
import org.scannotation.ClasspathUrlFinder;

import com.statoil.cims.connector.CIMSConnector;
import com.statoil.cims.connector.ICIMSConnection;
import com.statoil.cims.connector.annotation.ICResource;
import org.forgerock.openicf.usd.meta.MetaAttribute;
import org.forgerock.openicf.usd.meta.MetaResource;

public class ResourceRegistry {
	
	private Map<String, MetaResource> resources;
	
	public ResourceRegistry() {
		resources = new HashMap<String, MetaResource>();
	}

	public void initializeResources() {
		AnnotationDB db = new AnnotationDB();
		db.setScanClassAnnotations(true);
		db.setScanFieldAnnotations(false);
		db.setScanMethodAnnotations(false);
		db.setScanParameterAnnotations(false);
		
		try {
			URL url = ClasspathUrlFinder.findClassBase(getClass());
			db.scanArchives(url);
		} catch (IOException ex) {
			throw new RuntimeException("Unable to scan for resources", ex);
		}
		
		Set<String> classes = db.getAnnotationIndex().get(ICResource.class.getName());
		for (String className : classes) {
			try {
				Class<?> clazz = Class.forName(className);
				ICResource resource = clazz.getAnnotation(ICResource.class);
				resources.put(resource.name().toLowerCase(), new MetaResource(resource, clazz));
			} catch (ClassNotFoundException ex) {
				throw new RuntimeException("Unable to load class: " + className, ex);
			}
		}
	}
	
	public void initializeTestResources(Class<?>...classes) {
		for (Class<?> clazz : classes) {
			ICResource resource = clazz.getAnnotation(ICResource.class);
			if (resource == null) continue;
			resources.put(resource.name().toLowerCase(), new MetaResource(resource, clazz));
		}
	}
	
	public void dispose() {
		if (resources != null) {
			resources.clear();
			resources = null;
		}
	}
	
	public AbstractResource<?> createResource(MetaResource resource, ICIMSConnection connection, Object entity) {
		Class<?> clazz = resource.getInstanceType();
		
		try {
			Constructor<?> constr = clazz.getConstructor(resource.getEntityType(), ICIMSConnection.class);
			return (AbstractResource<?>) constr.newInstance(entity, connection);
		} catch (Exception ex) {
			throw new RuntimeException("Unable to create resource", ex);
		}
	}
	
	public MetaResource findResource(String name) {
		return resources.get(name.toLowerCase());
	}
	
	public MetaAttribute findAttribute(String resourceName, String attributeName) {
		MetaResource resource = findResource(resourceName);
		return resource.find(attributeName);
	}
	
	public Set<MetaAttribute> findAttributes(String resourceName, String...attributeNames) {
		MetaResource resource = findResource(resourceName);
		return findAttributes(resource, attributeNames);
	}
	
	public Set<MetaAttribute> findAttributes(MetaResource resource, String...attributeNames) {
		if (attributeNames == null || attributeNames.length == 0) {
			return Collections.emptySet();
		}
		
		Set<MetaAttribute> results = new HashSet<MetaAttribute>(attributeNames.length);
		for (String name : attributeNames) {
			MetaAttribute attr = resource.find(name);
			if (attr != null) {
				results.add(attr);
			}
		}
		
		return results;
	}

	public Set<MetaAttribute> findAttributesIncludingSpecials(String resourceName, String ...attributeNames) {
		MetaResource resource = findResource(resourceName);
		return findAttributesIncludingSpecials(resource, attributeNames);
	}
	
	public Set<MetaAttribute> findAttributesIncludingSpecials(MetaResource resource, String ...attributeNames) {
		Set<MetaAttribute> results = new HashSet<MetaAttribute>();
		for (MetaAttribute attribute : resource.getAttributes()) {
			if (AttributeUtil.isSpecialName(attribute.getName())) {
				results.add(attribute);
			}
		}
		
		results.addAll(findAttributes(resource, attributeNames));
		return results;		
	}
	
	public AttributeInfo getAttributeInfo(MetaAttribute attribute) {
		AttributeInfoBuilder builder = new AttributeInfoBuilder(attribute.getName());
		builder.setType(attribute.getAttributeType());
		builder.setFlags(new HashSet<Flags>(attribute.getFlags()));
		return builder.build();
	}
	
	public Schema createFromRegistry() {
		SchemaBuilder builder = new SchemaBuilder(CIMSConnector.class);
		for (MetaResource resource : resources.values()) {
			ObjectClassInfoBuilder classInfo = new ObjectClassInfoBuilder();
			classInfo.setType(resource.getName());
			classInfo.setContainer(resource.isContainer());
			for (MetaAttribute attr : resource.getAttributes()) {
				AttributeInfoBuilder attrBuilder = new AttributeInfoBuilder(attr.getName());
				attrBuilder.setType(attr.getAttributeType());
				attrBuilder.setFlags(new HashSet<AttributeInfo.Flags>(attr.getFlags()));
				classInfo.addAttributeInfo(attrBuilder.build());
			}
			builder.defineObjectClass(classInfo.build());
		}
		
		return builder.build();
	}

}
