/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.forgerock.openconnector.xml.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


public class QueryImpl implements IQuery {

    
    private static final IPart AND = new SimplePart(" and ");
    private static final IPart OR = new SimplePart(" or ");

    private IPart mainPart;
    private LinkedList<IPart> parts;
    
    public QueryImpl() {
        this.parts = new LinkedList<IPart>();
    }

    public void set(IPart part) {
        if (mainPart != null && parts.contains(mainPart)) {
            int index = parts.indexOf(mainPart);
            parts.remove(mainPart);
            parts.add(index, part);
        } else {
            parts.add(part);
        }
        mainPart = part;
    }

    public Iterator<IPart> iterator() {
        return parts.iterator();
    }

    public void and(IQuery part) {
        parts.addLast(AND);

        for (IPart p : part.getParts()) {
            parts.addLast(p);
        }
    }

    public Collection<IPart> getParts() {
        return parts;
    }

    public void or(IQuery part) {
        parts.addLast(OR);

        for (IPart p : part.getParts()) {
            parts.addLast(p);
        }
    }

    static class SimplePart implements IPart {

        private String value;

        public SimplePart(String value) {
            this.value = value;
        }

        public String getExpression() {
            return this.value;
        }
    }
}
