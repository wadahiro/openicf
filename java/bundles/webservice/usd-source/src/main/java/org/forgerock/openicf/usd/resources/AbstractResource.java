package org.forgerock.openicf.usd.resources;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.Id;
import javax.transaction.UserTransaction;

import com.statoil.cims.common.util.BeanProperty;
import com.statoil.cims.connector.CIMSConnection;
import com.statoil.cims.connector.ICIMSConnection;
import com.statoil.cims.connector.annotation.ICResource;
import org.forgerock.openicf.usd.meta.MetaAttribute;
import org.forgerock.openicf.usd.meta.MetaResource;
import com.statoil.cims.connector.translators.IFieldTranslator;

public abstract class AbstractResource<T extends Serializable> {
	
	public static final String SELF_REFERENCE = "this";
	
	private boolean modified = false;
	
	private MetaResource resource;
	private WeakReference<T> entityRef;
	private WeakReference<ICIMSConnection> connectionRef;
	
	public AbstractResource(T entity, ICIMSConnection connection) {
		this.entityRef = new WeakReference<T>(entity);
		this.connectionRef = new WeakReference<ICIMSConnection>(connection);
		
		// Create meta resource
		ICResource res = getClass().getAnnotation(ICResource.class);
		resource = new MetaResource(res, (Class<? extends AbstractResource<?>>) getClass());
		
		registerTranslators();
	}
	
	/**
	 * This method can be used to initialize lazy loaded fields. 
	 */
	public void initLazyLoadedFields(Set<MetaAttribute> attributes) {
		// noop
	}

	private void registerTranslators() {
		ICIMSConnection conn = getConnection();
		
		for (Entry<String, Class<? extends IFieldTranslator<?, ?>>> entry : resource.getFieldTranslators().entrySet()) {
			conn.registerTranslator(getClass(), entry.getKey(), entry.getValue());
		}
	}
	
	public String getIdentifier() {
		String result = null;
		Object entity = getEntity();
		try {
			BeanInfo info = Introspector.getBeanInfo(entity.getClass());
			for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
				Method rm = pd.getReadMethod();
				if (rm.isAnnotationPresent(Id.class)) {
					Object res = rm.invoke(entity);
					result = res.toString();
					break;
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		if (result == null) {
			// Try field annotations
			for (Field f : entity.getClass().getDeclaredFields()) {
				if (f.isAnnotationPresent(Id.class)) {
					boolean access = f.isAccessible();
					f.setAccessible(true);
					try {
						Object res = f.get(entity);
						result = res.toString();
						f.setAccessible(access);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Returns the weak referenced entity. If the entity has been 
	 * garbage collected, this method may return null.
	 * 
	 * @return T the entity.
	 */
	public T getEntity() {
		T result = entityRef.get();
		if (result == null) {
			throw new IllegalStateException("Entity was null. Probably garbage collected..");
		}
		
		return result;
	}

    /**
     * Returns the weak referenced unsaved entity. If the entity has been
	 * garbage collected, this method may return null.
     * @return
     */
    public T getUnsavedEntity() {
		return getEntity();
	}

	/**
	 * Returns the weak referenced connection. If the connection has
	 * been garbage collected, this method may return null.
	 * 
	 * @return {@link CIMSConnection} the connection.
	 */
	protected ICIMSConnection getConnection() {
		ICIMSConnection result = connectionRef.get();
		if (result == null) {
			throw new IllegalStateException("Connection was null. Must have been garbage collected.");
		}
		
		return result;
	}
	
	/**
	 * Tries to retrieve the user transaction from the parent connection.
	 * This method will throw an {@link IllegalStateException} if the
	 * parent connection is null, or if the resulting transaction is null.
	 * 
	 * @return {@link UserTransaction} the user transaction.
	 * @throws IllegalStateException
	 */
	protected UserTransaction getUserTransaction() {
		ICIMSConnection conn = getConnection();		
		UserTransaction tx = conn.getUserTransaction();
		if (tx == null) {
			throw new IllegalStateException("Unable to obtain user transaction from connection...");
		}
		
		return tx;
	}
	
	public Object getProperty(String property) {
		Object value = internalGetProperty(property);
		IFieldTranslator translator = internalGetFieldTranslator(property);
		return translator == null ? value : translator.fromInternal(value);
	}

	public void setProperty(String property, Object value) {
		Object translatedValue = null;
		IFieldTranslator translator = internalGetFieldTranslator(property);
		if (translator != null) {
			translatedValue = translator.fromExternal(value);
		} else {
			translatedValue = value;
		}
		
		internalSetProperty(property, translatedValue);
	}

	private Object internalGetProperty(String property) throws RuntimeException {
		try {
			BeanProperty prop = resolveBean(property);
			if (prop == null) {
				throw new RuntimeException("No such property named '" + property + "'");
			}
			
			return prop.getProperty();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Unable to get property: " + property, ex);
		}
	}

	private void internalSetProperty(String property, Object value) throws RuntimeException {
		if (!modified) {
			beforeModification(property);
		}
		
		BeanProperty prop = resolveBean(property);
		if (prop == null) {
			throw new RuntimeException("No such property named '" + property + "'");
		}
		
		try {
			if (verifyChange(property, prop.getProperty(), value)) {
				prop.setProperty(value);
				modified = true;
			}
		} catch (Exception ex) {
			throw new RuntimeException("Unable to set property '" + property + "'", ex);
		}
	}
	
	/**
	 * This method is called during the {@link #setProperty(String, Object)} method
	 * to verify if the property should actually be changed or not. Subclasses may 
	 * override this method to provide additional behavior. 
	 * <p>
	 * If this method returns <code>false</code>, then the property will not be
	 * changed.
	 * </p>
	 * 
	 * @param property the property being changed.
	 * @param oldValue the old value of the property.
	 * @param newValue the new value of the property.
	 * @return <code>true</code> if a change occurred, <code>false</code> otherwise. 
	 */
	protected boolean verifyChange(String property, Object oldValue, Object newValue) {
		if ((oldValue == null) && (newValue == null)) {
			return false;
		}
		if (((oldValue == null) && (newValue != null)) || ((oldValue != null) && (newValue == null))) {
			return true;
		}
		
		return !oldValue.equals(newValue);
	}

	/**
	 * This method is called before any modification is performed on
	 * the underlying entity. Clients that requires special processing
	 * in this case should override this method.
	 * 
	 * @param property the property being changed.
	 */
	protected void beforeModification(String property) {
		// noop
	}
	
	/**
	 * Call this method after all modifications has been
	 * committed to the entity. This method will reset the
	 * internal modification flag. Sub-classes can override
	 * this method to provide special processing, but should
	 * make sure to call super in order to update the state.
	 */
	public void modificationFinished() {
		modified = false;
	}

	private BeanProperty resolveBean(String property) {
		String[] segments = property.split("\\.");
		Object curr = getEntity();
		
		// Check for self reference
		if (segments[0].equals(SELF_REFERENCE)) {
			curr = this;
			
			// Remove self reference
			String[] newSegments = new String[segments.length - 1];
			System.arraycopy(segments, 1, newSegments, 0, newSegments.length);
			segments = newSegments;
		}
		
		try {
			// Navigate the bean
			for (int i = 0; i < segments.length; i++) {
				BeanProperty prop = new BeanProperty(curr, segments[i]);
				if (i == segments.length - 1) {
					// Reached the end
					return prop;
				}
				
				curr = prop.getProperty();
				if (curr == null) {
					return null;
				}
			}
		} catch (Exception ex) {
			return null;
		}
		
		// Nothing found
		return null;
	}
	
	private IFieldTranslator<?, ?> internalGetFieldTranslator(String property) {
		ICIMSConnection conn = getConnection();
		if (conn != null) {
			return conn.getTranslator(getClass(), property);
		}
		
		return null;
	}

}
