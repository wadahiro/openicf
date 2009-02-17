/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007-2008 Sun Microsystems, Inc. All rights reserved.     
 * 
 * The contents of this file are subject to the terms of the Common Development 
 * and Distribution License("CDDL") (the "License").  You may not use this file 
 * except in compliance with the License.
 * 
 * You can obtain a copy of the License at 
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing permissions and limitations 
 * under the License. 
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields 
 * enclosed by brackets [] replaced by your own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.racf;

import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.ContainsFilter;
import org.identityconnectors.framework.common.objects.filter.EndsWithFilter;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.StartsWithFilter;

/**
 * Create command line search Filter
 * <p>
 * Supported filters are wild cards on name (username or groupname)
 */
public class RacfCommandLineFilterTranslator extends AbstractFilterTranslator<String> {
    @Override
    protected String createAndExpression(String leftExpression,
            String rightExpression) {
        // Although RACF does not support AND, we can use one of the filters,
        // since we just need to do partial filtering
        //
        if (leftExpression!=null)
            return leftExpression;
        else if (rightExpression!=null)
            return rightExpression;
        else
            return super.createAndExpression(leftExpression, rightExpression);
    }
    @Override
    protected String createStartsWithExpression(StartsWithFilter filter,
            boolean not) {
        if (!not && filter.getAttribute().is(Name.NAME))
            return AttributeUtil.getAsStringValue(filter.getAttribute())+"*";
        else
            return super.createStartsWithExpression(filter, not);
    }

    @Override
    protected String createEqualsExpression(EqualsFilter filter, boolean not) {
        if (!not && filter.getAttribute().is(Name.NAME))
            return AttributeUtil.getAsStringValue(filter.getAttribute());
        else
            return super.createEqualsExpression(filter, not);
    }

    @Override
    protected String createEndsWithExpression(EndsWithFilter filter, boolean not) {
        if (!not && filter.getAttribute().is(Name.NAME))
            return "*"+AttributeUtil.getAsStringValue(filter.getAttribute());
        else
            return super.createEndsWithExpression(filter, not);
    }

    @Override
    protected String createContainsExpression(ContainsFilter filter, boolean not) {
        if (!not && filter.getAttribute().is(Name.NAME))
            return "*"+AttributeUtil.getAsStringValue(filter.getAttribute())+"*";
        else
            return super.createContainsExpression(filter, not);
    }

}

