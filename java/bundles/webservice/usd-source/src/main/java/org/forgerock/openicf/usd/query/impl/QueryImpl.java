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
package org.forgerock.openicf.usd.query.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.forgerock.openicf.usd.query.IPart;
import org.forgerock.openicf.usd.query.IQuery;
import org.forgerock.openicf.usd.query.IQueryPart;

/**
 * Sample Class Doc
 *
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */
public class QueryImpl implements IQuery {
	
	static final IPart RIGHT = new SimplePart("(");
	static final IPart LEFT = new SimplePart(")");
	static final IPart AND = new SimplePart(" AND ");
	static final IPart OR = new SimplePart(" OR ");
	
	private ICResource resource;
	private IQueryPart mainPart;
	private LinkedList<IPart> parts;
	
	public QueryImpl(ICResource resource) {
		this.resource = resource;
		this.parts = new LinkedList<IPart>();
	}
	
	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQuery#set(org.forgerock.openicf.usd.query.IQueryPart)
	 */
	public void set(IQueryPart part) {
		if (mainPart != null && parts.contains(mainPart)) {
			int index = parts.indexOf(mainPart);
			parts.remove(mainPart);
			parts.add(index, part);
		} else {
			parts.add(part);
		}
		
		// keep pointer to current part
		mainPart = part;
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQuery#and(org.forgerock.openicf.usd.query.IQuery)
	 */
	public void and(IQuery part) {
		parts.addFirst(RIGHT);
		parts.addLast(AND);
		
		for (IPart p : part.getParts()) {
			parts.addLast(p);
		}
		parts.addLast(LEFT);
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQuery#getParts()
	 */
	public Collection<IPart> getParts() {
		return parts;
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQuery#getResource()
	 */
	public ICResource getResource() {
		return resource;
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQuery#iterator()
	 */
	public Iterator<IPart> iterator() {
		return parts.iterator();
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQuery#or(org.forgerock.openicf.usd.query.IQuery)
	 */
	public void or(IQuery part) {
		parts.addFirst(RIGHT);
		parts.addLast(OR);
		
		for (IPart p : part.getParts()) {
			parts.addLast(p);
		}
		parts.addLast(LEFT);
	}
	
	static class SimplePart implements IPart {
		
		private String value;
		
		public SimplePart(String value) {
			this.value = value;
		}

		/* (non-Javadoc)
		 * @see org.forgerock.openicf.usd.query.IPart#getOperator()
		 */
		public String getOperator() {
			return value;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return getOperator();
		}
		
	}

}
