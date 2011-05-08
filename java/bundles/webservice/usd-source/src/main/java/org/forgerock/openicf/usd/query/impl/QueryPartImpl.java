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

import org.forgerock.openicf.usd.query.IQueryPart;

/**
 * Sample Class Doc
 *
 * @author $author$
 * @version $Revision$ $Date$
 * @since 1.0
 */
public class QueryPartImpl implements IQueryPart {
	
	private String attributeName;
	private String operatorValue;
	private String columnValue;
	
	public QueryPartImpl(String name, String operator, String value) {
		attributeName = name;
		operatorValue = operator;
		columnValue = value;
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQueryPart#getAttributeName()
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IQueryPart#getValue()
	 */
	public String getValue() {
		return columnValue;
	}

	/* (non-Javadoc)
	 * @see org.forgerock.openicf.usd.query.IPart#getOperator()
	 */
	public String getOperator() {
		return operatorValue;
	}

}
