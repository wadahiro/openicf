package org.identityconnectors.test.common;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Property bag is readonly set of properties.
 * Properties in bag are kept as objects and can be accessed using name and its type  .
 * @author kitko
 *
 */
public final class PropertyBag {

	private final Map<String, Object> bag;

	PropertyBag(Map<String, Object> bag) {
		this.bag = new HashMap<String, Object>(bag);
	}

	/**
	 * Gets property by name and type.
	 * If no property exists with the name IllegalArgumentException is thrown. 
	 * @param <T> type of property
	 * @param name Name of Property
	 * @param type Type of property
	 * @return value of property in bag, also null if there is property named Name with null value
	 * @throws IllegalArgumentException if no property with name is stored in bag
	 * @throws ClassCastException if property with the name has not compatible type
	 */
	public <T> T getProperty(String name, Class<T> type) {
		if(!bag.containsKey(name)){
			throw new IllegalArgumentException(MessageFormat.format("Property named [{0}] not found in bag", name));
		}
		return type.cast(bag.get(name));
	}

	/**
	 * Retrieves String property.
	 * Method calls just {@link #getProperty(String, Class)} with String.class type, it does not try to 
	 * convert value from the bag to String.  
	 * @param name
	 * @return String value 
	 */
	public String getStringProperty(String name) {
		return getProperty(name, String.class);
	}
	
	@Override
	public String toString(){
		return bag.toString();
	}
	
	Map<String, Object> toMap(){
		return bag;
	}

}
