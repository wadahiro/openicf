/**
 * 
 */
package org.forgerock.openicf.usd.query;

import java.util.Collection;
import java.util.Iterator;

import com.statoil.cims.connector.annotation.ICResource;

/**
 * @author andrbj
 *
 */
public interface IQuery {
	
	public ICResource getResource();
	public Collection<IPart> getParts();
	public Iterator<IPart> iterator();
	public void and(IQuery part);
	public void or(IQuery part);
	public void set(IQueryPart part);

}
