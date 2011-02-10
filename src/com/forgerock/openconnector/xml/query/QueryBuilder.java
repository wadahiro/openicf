/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.forgerock.openconnector.xml.query;

import com.forgerock.openconnector.xml.XMLHandlerImpl;
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
        StringBuilder sb = new StringBuilder();
        sb.append("declare namespace ");
        sb.append(XMLHandlerImpl.ICF_NAMESPACE_PREFIX);
        sb.append(" = \"http://openidm.forgerock.com/xml/ns/public/resource/openicf/resource-schema-1.xsd\"; ");
        sb.append("declare namespace ");
        sb.append(XMLHandlerImpl.RI_NAMESPACE_PREFIX);
        sb.append(" = \"http://openidm.forgerock.com/xml/ns/public/resource/instances/ef2bc95b-76e0-48e2-86d6-4d4f44d4e4a4\"; ");
        sb.append("for $x in /icf:OpenICFContainer/" + XMLHandlerImpl.RI_NAMESPACE_PREFIX + ":" + objClass.getObjectClassValue());
        this.selectPart = sb.toString();
        System.out.println("SELECTPART: " + selectPart);
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

    public String toString() {
        if (query == null || query.getParts().isEmpty()) {
            return String.format("%s %s", selectPart, returnPart);
        } else {
            System.out.println(String.format("%s %s %s", selectPart, wherePart, returnPart));
            return String.format("%s %s %s", selectPart, wherePart, returnPart);
        }
    }
}
