 /*
  *
 * Copyright (c) 2010 ForgeRock Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1.php or
 * OpenIDM/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at OpenIDM/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted 2010 [name of copyright owner]"
 *
 * $Id$
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
