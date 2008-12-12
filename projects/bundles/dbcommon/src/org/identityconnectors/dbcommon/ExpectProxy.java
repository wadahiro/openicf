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
package org.identityconnectors.dbcommon;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

/**
 * This is a Test helper class for testing expected method calls and return values of interfaces
 * <p>Limitation:</p><p>First implementation supports just a method name checking</p> 
 *   
 * @version $Revision 1.0$
 * @param <T> Type of the interface for testing
 * @since 1.0
 */
public class ExpectProxy<T> implements InvocationHandler {

    private List<String> methodNames = new ArrayList<String>();
    private List<Object> retVals = new ArrayList<Object>();
    private int count = 0;

    /**
     * Program the expected function call
     * @param methodName the expected method name
     * @param retVal the expected return value or proxy
     * @return the proxy
     */
    public ExpectProxy<T> expectAndReturn(final String methodName, final Object retVal) {
        this.methodNames.add(methodName);
        this.retVals.add(retVal);
        return this;
    }

    /**
     * Program the expected method call
     * @param methodName the expected method name
     * @return the proxy
     */
    public ExpectProxy<T> expect(final String methodName) {
        this.methodNames.add(methodName);
        //retVals must have same number of values as methodNames
        this.retVals.add(null);
        return this;
    }


    /**
     * Program the expected method call
     * @param methodName the expected method name
     * @param throwEx the expected exception
     * @return the proxy
     */
    public ExpectProxy<T> expectAndThrow(final String methodName, final Throwable throwEx) {
        return this.expectAndReturn(methodName, throwEx);
    }    
    
    /**
     * Test that all expected was called in the order
     * @return true/false all was called
     */
    public boolean isDone() {
        return count == methodNames.size();
    }

    /**
     * The InvocationHandler method
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (methodNames.size() > count && this.methodNames.get(count).equals(method.getName())) {
            final Object ret = retVals.get(count++);
            if(ret instanceof Throwable) {
                throw (Throwable) ret;
            }
            return ret;
        }
        Assert.fail("The call of method :" + method+ " was not ecpected. To do so, please call expectAndReturn(methodName,retVal)");
        return null;
    }

    /**
     * Return the Proxy implementation of the Interface
     * @param clazz of the interface
     * @return the proxy
     */
    @SuppressWarnings("unchecked")
    public T getProxy(Class<T> clazz) {
        ClassLoader cl = getClass().getClassLoader();
        Class<?> intef[] = new Class<?>[] { clazz };
        return (T) Proxy.newProxyInstance(cl, intef, this);
    }
}
