/**
 * Copyright (c) 2012 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2012 [name of copyright owner]
 * 
 * Portions Copyrighted 2008-2009 Sun Microsystems, Inc.
 */
package org.identityconnectors.solaris.mode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.solaris.SolarisConnection;
import org.identityconnectors.solaris.attr.NativeAttribute;
import org.identityconnectors.solaris.operation.search.AuthsCommand;
import org.identityconnectors.solaris.operation.search.LastCommand;
import org.identityconnectors.solaris.operation.search.LoginsCommand;
import org.identityconnectors.solaris.operation.search.ProfilesCommand;
import org.identityconnectors.solaris.operation.search.RolesCommand;
import org.identityconnectors.solaris.operation.search.SolarisEntry;

/**
 * Driver for solaris-specific user management commands.
 * 
 * Partially copied from the original (hard-coded) Solaris connector code and modified.
 * 
 * @see UnixModeDriver
 * 
 * @author David Adam
 * @author Radovan Semancik
 *
 */
public class SolarisModeDriver extends UnixModeDriver {
	
	public static final String MODE_NAME = "solaris";
	
	private static final String TMPFILE  = "/tmp/connloginsError.$$";
	private static final String SHELL_CONT_CHARS = "> ";
    private static final int CHARS_PER_LINE = 160;
	
	public SolarisModeDriver(SolarisConnection conn) {
		super(conn);
	}

	@Override
	public List<SolarisEntry> buildAccountEntries(List<String> blockUserNames, boolean isLast) {
		conn.doSudoStart();
        String out = null;
        try {
            conn.executeCommand(conn.buildCommand("rm -f", TMPFILE));

            String getUsersScript = buildGetUserScript(blockUserNames, isLast);
            out = conn.executeCommand(getUsersScript, conn.getConfiguration().getBlockFetchTimeout());

            conn.executeCommand(conn.buildCommand("rm -f", TMPFILE));
        } finally {
            conn.doSudoReset();
        }
        
        List<SolarisEntry> fetchedEntries = processOutput(out, blockUserNames, isLast);
        if (fetchedEntries.size() != blockUserNames.size()) {
            throw new RuntimeException("ERROR: expecting to return " + blockUserNames.size() + " instead of " + fetchedEntries.size());
            // TODO possibly compare by content.
        }
        
        return fetchedEntries;
	}
	
    /** retrieve account info from the output */
    private List<SolarisEntry> processOutput(String out, List<String> blockUserNames, boolean isLast) {
//        SVIDRA# getUsersFromCaptureList(CaptureList captureList, ArrayList users)()
        
        List<String> lines = Arrays.asList(out.split("\n"));
        Iterator<String> it = lines.iterator();
        int captureIndex = 0;
        List<SolarisEntry> result = new ArrayList<SolarisEntry>(blockUserNames.size());
        
        while (it.hasNext()) {
            final String currentAccount = blockUserNames.get(captureIndex);
            String line = it.next();
            String lastLoginLine = null;
            
            // Weed out shell continuation chars
            if (line.startsWith(SHELL_CONT_CHARS)) {
                int index = line.lastIndexOf(SHELL_CONT_CHARS);

                line = line.substring(index + SHELL_CONT_CHARS.length());
            }
            
            if (isLast) {
                if (!it.hasNext()) {
                    throw new ConnectorException(String.format("User '%s' is missing last login time.", currentAccount));
                }

                lastLoginLine = "";

                while (lastLoginLine.length() < 3) {
                    lastLoginLine = it.next();
                }
            }// if (isLast)
            
            SolarisEntry entry = buildUser(currentAccount, line, lastLoginLine);
            if (entry != null) {
                result.add(entry);
            }
            
            captureIndex++;
        }// while (it.hasNext())
        
        return result;
    }

    /**
     * build user based on the content given.
     * @param loginsLine
     * @param lastLoginLine
     * @return the build user.
     */
    private SolarisEntry buildUser(String username, String loginsLine, String lastLoginLine) {
        if (lastLoginLine == null) {
            return LoginsCommand.getEntry(loginsLine, username);
        } else {
            SolarisEntry.Builder entryBuilder = new SolarisEntry.Builder(username).addAttr(NativeAttribute.NAME, username);
            // logins
            SolarisEntry entry = LoginsCommand.getEntry(loginsLine, username);
            entryBuilder.addAllAttributesFrom(entry);
            
            //last
            Attribute attribute = LastCommand.parseOutput(username, lastLoginLine);
            entryBuilder.addAttr(NativeAttribute.LAST_LOGIN, attribute.getValue());
            
            return entryBuilder.build();
        }
    }

    private String buildGetUserScript(List<String> blockUserNames, boolean isLast) {
        // make a list of users, separated by space.
        StringBuilder connUserList = new StringBuilder();
        int charsThisLine = 0;
        for (String user : blockUserNames) {
            final int length = user.length();
            // take care that line meets the limit on 160 chars per line
            if ((charsThisLine + length + 3) > CHARS_PER_LINE) {
                connUserList.append("\n");
                charsThisLine = 0;
            }
            
            connUserList.append(user);
            connUserList.append(" ");
            charsThisLine += length + 1;
        }
        
        StringBuilder getUsersScript = new StringBuilder();
        getUsersScript.append("WSUSERLIST=\"");
        getUsersScript.append(connUserList.toString() + "\n\";");
        getUsersScript.append("for user in $WSUSERLIST; do ");
        
        String getScript = null;
        if (isLast) {
            getScript = 
                conn.buildCommand("logins") + " -oxma -l $user 2>>" + TMPFILE + "; " +
                "LASTLOGIN=`" + conn.buildCommand("last") + " -1 $user`; " +
                "if [ -z \"$LASTLOGIN\" ]; then " +
                     "echo \"wtmp begins\" ; " +
                "else " +
                     "echo $LASTLOGIN; " +
                "fi; ";
        } else {
            getScript = conn.buildCommand("logins") + " -oxma -l $user 2>>" + TMPFILE + "; ";
        }
        getUsersScript.append(getScript);
        getUsersScript.append("done");
        
        return getUsersScript.toString();
    }

	@Override
	public SolarisEntry buildAccountEntry(String username, Set<NativeAttribute> attrsToGet) {
		/** bunch of boolean flags says if the command is needed to be launched (based on attributes to get) */
		boolean isLogins = LoginsCommand.isLoginsRequired(attrsToGet);
        boolean isProfiles = attrsToGet.contains(NativeAttribute.PROFILES);
        boolean isAuths = attrsToGet.contains(NativeAttribute.AUTHS);
        boolean isLast = attrsToGet.contains(NativeAttribute.LAST_LOGIN);
        boolean isRoles = attrsToGet.contains(NativeAttribute.ROLES);
        
	//      if (conn.isNis()) {
	//      return buildNISUser(username);
	//  }
	  SolarisEntry.Builder entryBuilder = new SolarisEntry.Builder(username).addAttr(NativeAttribute.NAME, username);
	  
	  // we need to execute Logins command always, to figure out if the user exists at all.
	  SolarisEntry loginsEntry = LoginsCommand.getAttributesFor(username, conn);
	
	  // Null indicates that the user was not found.
	  if (loginsEntry == null) {
	      return null;
	  }
	  
	  if (isLogins) {
	      entryBuilder.addAllAttributesFrom(loginsEntry);
	  }
	  if (isProfiles) {
	      final Attribute profiles = ProfilesCommand.getProfilesAttributeFor(username, conn);
	      entryBuilder.addAttr(NativeAttribute.PROFILES, profiles.getValue());
	  }
	  if (isAuths) {
	      final Attribute auths = AuthsCommand.getAuthsAttributeFor(username, conn);
	      entryBuilder.addAttr(NativeAttribute.AUTHS, auths.getValue());
	  }
	  if (isLast) {
	      final Attribute last = LastCommand.getLastAttributeFor(username, conn);
	      entryBuilder.addAttr(NativeAttribute.LAST_LOGIN, last.getValue());
	  }
	  if (isRoles) {
	      final Attribute roles = RolesCommand.getRolesAttributeFor(username, conn);
	      entryBuilder.addAttr(NativeAttribute.ROLES, roles.getValue());
	  }
	  return entryBuilder.build();
        
        
	}

	
}
