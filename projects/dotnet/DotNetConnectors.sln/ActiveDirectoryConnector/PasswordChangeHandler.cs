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
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.DirectoryServices;
using Org.IdentityConnectors.Common.Security;
using ActiveDs;
using Org.IdentityConnectors.Framework.Common.Exceptions;
using System.DirectoryServices.AccountManagement;
using System.DirectoryServices.ActiveDirectory;
using System.Threading;
using Org.IdentityConnectors.Framework.Common.Objects;
using System.Security.Principal;

namespace Org.IdentityConnectors.ActiveDirectory
{

    /** 
     * This class will decrypt passwords, and handle
     * authentication and password changes (both
     * administrative and user)
     */
    internal class PasswordChangeHandler
    {
        String _currentPassword;
        String _newPassword;
        ActiveDirectoryConfiguration _configuration = null;
        static Semaphore authenticationSem = new Semaphore(1, 1, "ActiveDirectoryConnectorAuthSem");
        static readonly int ERR_PASSWORD_MUST_BE_CHANGED = -2147022989;
        static readonly int ERR_PASSWORD_EXPIRED = -2147023688;


        internal PasswordChangeHandler(ActiveDirectoryConfiguration configuration)
        {
            _configuration = configuration;
        }

        /// <summary>
        /// sets the _currentPassword variable
        /// </summary>
        /// <param name="clearChars"></param>
        internal void setCurrentPassword(UnmanagedArray<char> clearChars)
        {
            _currentPassword = "";

            // build up the string from the unmanaged array
            for (int i = 0; i < clearChars.Length; i++)
            {
                _currentPassword += clearChars[i];
            }
        }

        /// <summary>
        /// Sets the _newPassword variable
        /// </summary>
        /// <param name="clearChars"></param>
        internal void setNewPassword(UnmanagedArray<char> clearChars)
        {
            _newPassword = "";

            // build up the string from the unmanaged array
            for (int i = 0; i < clearChars.Length; i++)
            {
                _newPassword += clearChars[i];
            }
        }

        /// <summary>
        /// Does an administrative password change.  The Directory
        /// entry must be created with username and password of 
        /// a user with permission to change the password
        /// </summary>
        /// <param name="directoryEntry"></param>
        /// <param name="gsNewPassword"></param>
        internal void changePassword(DirectoryEntry directoryEntry,
            GuardedString gsNewPassword)
        {
            // decrypt and save the new password
            gsNewPassword.Access(setNewPassword);

            // get the native com object as an IADsUser, and set the 
            // password
            IADsUser user = (IADsUser)directoryEntry.NativeObject;
            user.SetPassword(_newPassword);
        }

        /// <summary>
        /// Does a user password change.  Must supply the currentpassword
        /// and the new password
        /// </summary>
        /// <param name="directoryEntry"></param>
        /// <param name="gsCurrentPassword"></param>
        /// <param name="gsNewPassword"></param>
        internal void changePassword(DirectoryEntry directoryEntry,
            GuardedString gsCurrentPassword, GuardedString gsNewPassword)
        {
            // decrypt and save the old nad new passwords
            gsNewPassword.Access(setNewPassword);
            gsCurrentPassword.Access(setCurrentPassword);

            // get the native com object as an IADsUser, and change the 
            // password
            IADsUser user = (IADsUser)directoryEntry.NativeObject;
            user.ChangePassword(_currentPassword, _newPassword);
        }

        /// <summary>
        ///     Authenticates the user
        /// </summary>
        /// <param name="directoryEntry"></param>
        /// <param name="username"></param>
        /// <param name="password"></param>
        internal Uid Authenticate(/*DirectoryEntry directoryEntry,*/ string username,
            Org.IdentityConnectors.Common.Security.GuardedString password)
        {
            password.Access(setCurrentPassword);

            // create principle context for authentication
            string serverName = _configuration.LDAPHostName;
            PrincipalContext context = null;
            UserPrincipal userPrincipal = null;
            try
            {
                // according to microsoft docs:
                // Wait on return - true if the current instance receives a signal. If the current instance is never signaled, WaitOne never returns. 
                // no need to check return, since it will not return false;
                authenticationSem.WaitOne();
                if ((serverName == null) || (serverName.Length == 0))
                {
                    // if they haven't specified an ldap host, use the domain that is
                    // in the connector configuration
                    DomainController domainController = ActiveDirectoryUtils.GetDomainController(_configuration);
                    context = new PrincipalContext(ContextType.Domain,
                        domainController.Domain.Name, _configuration.DirectoryAdminName,
                        _configuration.DirectoryAdminPassword);
                }
                else
                {
                    // if the specified an ldap host, use it.
                    context = new PrincipalContext(ContextType.Machine,
                        _configuration.LDAPHostName, _configuration.DirectoryAdminName,
                        _configuration.DirectoryAdminPassword);
                }

                if (context == null)
                {
                    throw new ConnectorException("Unable to get PrincipalContext");
                }

                if (!context.ValidateCredentials(username, _currentPassword))
                {
                    throw new InvalidCredentialException(_configuration.ConnectorMessages.Format(
                    "ex_InvalidCredentials", "Invalid credentials supplied for user {0}",
                    username));
                }
                return GetUidFromSamAccountName(context, username);
            }
            catch (PrincipalOperationException e)
            {
                if ((e.ErrorCode.Equals(ERR_PASSWORD_MUST_BE_CHANGED)) || 
                    (e.ErrorCode.Equals(ERR_PASSWORD_EXPIRED)))
                {
                    Uid uid = GetUidFromSamAccountName(context, username);
                    PasswordExpiredException exception = new PasswordExpiredException(e.Message);
                    exception.Uid = uid;
                    throw exception;
                }

                throw;
            }
            finally
            {
                if (context != null)
                {
                    context.Dispose();
                    context = null;
                }
                authenticationSem.Release();
            }
        }

        public Uid GetUidFromSamAccountName(PrincipalContext context, String sAMAccountName)
        {
            UserPrincipal userPrincipal = null;

            try
            {
                userPrincipal = UserPrincipal.FindByIdentity(context,
                    IdentityType.SamAccountName, sAMAccountName);

                if (userPrincipal.Sid == null)
                {
                    throw new ConnectorException(_configuration.ConnectorMessages.Format(
                    "ex_SIDLookup", "An execption occurred during validation of user {0}.  The user was successfully authenticated, but the user's sid could not be determined.",
                    sAMAccountName));
                }

                string sidString = "<SID=" + userPrincipal.Sid.Value + ">";
                DirectoryEntry userDe = new DirectoryEntry(
                    ActiveDirectoryUtils.GetLDAPPath(_configuration.LDAPHostName, sidString),
                    _configuration.DirectoryAdminName, _configuration.DirectoryAdminPassword);

                return new Uid(ActiveDirectoryUtils.ConvertUIDBytesToGUIDString(userDe.Guid.ToByteArray()));
            }
            finally
            {
                if (userPrincipal != null)
                {
                    userPrincipal.Dispose();
                    userPrincipal = null;
                }
            }
        }
    }
}
