/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.xml.query;

import java.util.Iterator;
import org.identityconnectors.framework.common.objects.ObjectClass;

/**
 *
 * @author slogum
 */
public class QueryBuilder {

    private IQuery query;
    private String selectPart;
    private String wherePart;
    private String returnPart;

    public QueryBuilder(IQuery query, ObjectClass objClass) {
        this.query = query;
        createSelectPart(objClass);
        wherePart = "where ";
        createReturnPart();
        if (query != null)
            processQuery();
    }

    private void createSelectPart(ObjectClass objClass) {
        this.selectPart = "for $x in /OpenICFContainer/" + objClass.getObjectClassValue();
    }

    private void createReturnPart() {
        this.returnPart = "return $x";
    }

    private void processQuery() {
        Iterator<IPart> it = query.iterator();
        while (it.hasNext()) {
            IPart part = it.next();
            wherePart += part.getExpression();
        }
    }

    // String query = "for $x in /OpenICFContainer/__ACCOUNT__ where $x/firstname='Jan Eirik' return $x";
    public String toString() {
        if (query == null || query.getParts().isEmpty()) {
            return String.format("%s %s", selectPart, returnPart);
        } else {
            return String.format("%s %s %s", selectPart, wherePart, returnPart);
        }

    }
}
