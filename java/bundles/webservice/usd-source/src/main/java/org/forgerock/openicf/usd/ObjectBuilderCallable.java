/**
 * 
 */
package org.forgerock.openicf.usd;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.Uid;

import org.forgerock.openicf.usd.meta.MetaAttribute;
import org.forgerock.openicf.usd.resources.AbstractResource;

/**
 * @author andrbj
 *
 */
public class ObjectBuilderCallable implements Callable<ConnectorObject> {
	
	private static final Log log = Log.getLog(ObjectBuilderCallable.class);
	
	private AbstractResource<?> resource;
	private Set<MetaAttribute> attributes;
	private ConnectorObjectBuilder builder;
	
	public ObjectBuilderCallable(AbstractResource<?> resource, ObjectClass objectClass, Set<MetaAttribute> attributes) {
		this.resource = resource;
		this.attributes = attributes;
		
		// Create builder
		builder = new ConnectorObjectBuilder();
		builder.setObjectClass(objectClass);
	}

	public ConnectorObject call() throws Exception {
		for (MetaAttribute attribute : attributes) {
			Object value = null;
			try {
				value = resource.getProperty(attribute.getProperty());
			} catch (RuntimeException ex) {
				String msg = String.format("Unable to get property '%s' for resource '%s'. See attached stacktrace..", attribute.getProperty(), attribute.getParent().getName());
				throw new RuntimeException(msg, ex);
			}
			
			// Parse value
			if (attribute.is(Name.NAME)) {
				builder.setName(value.toString());
			} else if (attribute.is(Uid.NAME)) {
				builder.setUid(value.toString());
			} else {
				if (value instanceof Collection<?>) {
					builder.addAttribute(attribute.getName(), (Collection<?>) value);
				} else {
					builder.addAttribute(attribute.getName(), value);
				}
			}
		}
		
		return builder.build();
	}

}
