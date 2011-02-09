/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml.query;

import java.util.Collection;
import java.util.Iterator;


public interface IQuery {

    
    public void set(IQueryPart part);
    public Iterator<IPart> iterator();
    public Collection<IPart> getParts();
    public void and(IQuery part);
    public void or(IQuery part);

}
