/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.     
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
package org.identityconnectors.solaris;

import static org.identityconnectors.solaris.SolarisMessages.MSG_NOT_SUPPORTED_OBJECTCLASS;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationalAttributes;
import org.identityconnectors.solaris.attr.AccountAttribute;
import org.identityconnectors.solaris.command.MatchBuilder;
import org.identityconnectors.solaris.operation.AbstractOp;
import org.identityconnectors.solaris.operation.OpCreateImpl;
import org.identityconnectors.solaris.operation.OpUpdateImpl;
import org.identityconnectors.solaris.operation.search.SolarisEntry;

import expect4j.matches.Match;


/** helper class for Solaris specific operations */
public class SolarisUtil {
    
    /** Maximum number of characters per line in Solaris shells */
    public static final int DEFAULT_LIMIT = 120;
    
    private static StringBuilder limitString(StringBuilder data, int limit) {
        StringBuilder result = new StringBuilder(limit);
        if (data.length() > limit) {
            result.append(data.substring(0, limit));
            result.append("\\\n"); // /<newline> separator of Unix command line cmds.
            
            final String remainder = data.substring(limit, data.length()); 
            if (remainder.length() > limit) {
                // TODO performance: might be a more effective way to handle this copying. Maybe skip copying and pass the respective part of stringbuffer directly to the recursive call.
                StringBuilder sbtmp = new StringBuilder();
                sbtmp.append(remainder);
                result.append(limitString(sbtmp, limit));
            } else {
                result.append(remainder);
            }
        } else {
            return data;
        }
        return result;
    }

    /**
     * Cut the command into pieces, so it doesn't have a longer line than the given DEFAULT_LIMIT
     * @param data
     * @return
     */
    public static String limitString(StringBuilder data) {
        return limitString(data, DEFAULT_LIMIT /* == max length of line from SolarisResourceAdapter#getUpdateNativeUserScript(), line userattribparams */).toString();
    }
    
    /** helper method for getting the password from an attribute map */
    public static GuardedString getPasswordFromMap(Map<String, Attribute> attrMap) {
        Attribute attrPasswd = attrMap.get(OperationalAttributes.PASSWORD_NAME);
        if (attrPasswd == null) {
            throw new IllegalArgumentException("Password missing from attribute map");
        }
        return AttributeUtil.getGuardedStringValue(attrPasswd);
    }
    
    public static void controlObjectClassValidity(ObjectClass oclass, ObjectClass[] acceptedObjectClasses, Class<? extends AbstractOp> operation) {
        for (ObjectClass objectClass : acceptedObjectClasses) {
            if (objectClass.equals(oclass)) {
                return;
            }
        }
        
        throw new IllegalArgumentException(String.format(
                MSG_NOT_SUPPORTED_OBJECTCLASS, oclass, operation.getName()));
    }
    
    public static void sendPassword(GuardedString passwd, final SolarisConnection conn) {
        passwd.access(new GuardedString.Accessor() {
            public void access(char[] clearChars) {
                try {
                    conn.send(new String(clearChars));
                } catch (IOException e) {
                    throw ConnectorException.wrap(e);
                }
            }
        });
    }
    
    public static Match[] prepareMatches(String string, Match[] commonErrMatches) {
        MatchBuilder builder = new MatchBuilder();
        builder.addNoActionMatch(string);
        builder.addMatches(commonErrMatches);
        
        return builder.build();
    }
    
    public static SolarisEntry forConnectorAttributeSet(String userName, Set<Attribute> attrs) {
        // translate connector attributes to native counterparts
        final SolarisEntry.Builder builder = new SolarisEntry.Builder(userName);
        for (Attribute attribute : attrs) {
            final AccountAttribute accAttrName = AccountAttribute.forAttributeName(attribute.getName());
            if (accAttrName != null) {
                builder.addAttr(accAttrName.getNative(), attribute.getValue());
            }
        }
        return builder.build();
    }
    
    /*
     * MUTEXING
     */
    /** mutex acquire constants */
    private static final String tmpPidMutexFile = "/tmp/WSlockuid.$$";
    private static final String pidMutexFile = "/tmp/WSlockuid";
    private static final String pidFoundFile = "/tmp/WSpidfound.$$";
    /**
     * Mutexing script is used to prevernt race conditions when creating
     * multiple users. These conditions are present at {@link OpCreateImpl} and
     * {@link OpUpdateImpl}. The code is taken from the resource adapter.
     */
    public static String getAcquireMutexScript(SolarisConnection conn) {
        Long timeout = conn.getConfiguration().getMutexAcquireTimeout();
        String rmCmd = conn.buildCommand("rm");
        String catCmd = conn.buildCommand("cat");

        if (timeout < 1) {
            timeout = SolarisConfiguration.DEFAULT_MUTEX_ACQUIRE_TIMEOUT;
        }

        String pidMutexAcquireScript =
            "TIMEOUT=" + timeout + "; " +
            "echo $$ > " + tmpPidMutexFile + "; " +
            "while test 1; " +
            "do " +
              "ln -n " + tmpPidMutexFile + " " + pidMutexFile + " 2>/dev/null; " +
              "rc=$?; " +
              "if [ $rc -eq 0 ]; then\n" +
                "LOCKPID=`" + catCmd + " " +  pidMutexFile + "`; " +
                "if [ \"$LOCKPID\" = \"$$\" ]; then " +
                  rmCmd + " -f " + tmpPidMutexFile + "; " +
                  "break; " +
                "fi; " +
              "fi\n" +
              "if [ -f " + pidMutexFile + " ]; then " +
                "LOCKPID=`" + catCmd + " " + pidMutexFile + "`; " +
                "if [ \"$LOCKPID\" = \"$$\" ]; then " +
                  rmCmd + " -f " + pidMutexFile + "\n" +
                "else " +
                  "ps -ef | while read REPLY\n" +
                  "do " +
                    "TESTPID=`echo $REPLY | awk '{ print $2 }'`; " +
                    "if [ \"$LOCKPID\" = \"$TESTPID\" ]; then " +
                      "touch " + pidFoundFile + "; " +
                      "break; " +
                    "fi\n" +
                  "done\n" +
                  "if [ ! -f " + pidFoundFile + " ]; then " +
                    rmCmd + " -f " + pidMutexFile + "; " +
                  "else " +
                    rmCmd + " -f " + pidFoundFile + "; " +
                  "fi\n" +
                "fi\n" +
              "fi\n" +
              "TIMEOUT=`echo | awk 'BEGIN { n = '$TIMEOUT' } { n -= 1 } END { print n }'`\n" +
              "if [ $TIMEOUT = 0 ]; then " +
                "echo \"ERROR: failed to obtain uid mutex\"; " +
                rmCmd + " -f " + tmpPidMutexFile + "; " +
                "break; " +
              "fi\n" +
              "sleep 1; " +
            "done";

        return pidMutexAcquireScript;
    }

    /** Counterpart of {@link OpUpdateImpl#getAcquireMutexScript(SolarisConnection)} */
    public static String getMutexReleaseScript(SolarisConnection conn) {
        String rmCmd = conn.buildCommand("rm");
        String pidMutexReleaseScript =
            "if [ -f " + pidMutexFile + " ]; then " +
              "LOCKPID=`cat " + pidMutexFile + "`; " +
              "if [ \"$LOCKPID\" = \"$$\" ]; then " +
                rmCmd + " -f " + pidMutexFile + "; " +
              "fi; " +
            "fi";
        return pidMutexReleaseScript;
    }
}
