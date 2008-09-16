/*
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * U.S. Government Rights - Commercial software. Government users 
 * are subject to the Sun Microsystems, Inc. standard license agreement
 * and applicable provisions of the FAR and its supplements.
 * 
 * Use is subject to license terms.
 * 
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and Project Identity 
 * Connectors are trademarks or registered trademarks of Sun 
 * Microsystems, Inc. or its subsidiaries in the U.S. and other
 * countries.
 * 
 * UNIX is a registered trademark in the U.S. and other countries,
 * exclusively licensed through X/Open Company, Ltd. 
 * 
 * -----------
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved. 
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License(CDDL) (the License).  You may not use this file
 * except in  compliance with the License. 
 * 
 * You can obtain a copy of the License at
 * http://identityconnectors.dev.java.net/CDDLv1.0.html
 * See the License for the specific language governing permissions and 
 * limitations under the License.  
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each
 * file and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields 
 * enclosed by brackets [] replaced by your own identifying information: 
 * "Portions Copyrighted [year] [name of copyright owner]"
 * -----------
 */
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;
using Org.IdentityConnectors.Framework;
using Org.IdentityConnectors.Framework.Spi;
using Org.IdentityConnectors.Framework.Spi.Operations;
using Org.IdentityConnectors.Framework.Common.Objects;
using Org.IdentityConnectors.Framework.Common.Objects.Filters;
using Org.IdentityConnectors.Framework.Test;
using Org.IdentityConnectors.Common.Security;
using System.Diagnostics;
using System.Resources;
using System.IO;
using System.Security.AccessControl;
using System.Security.Principal;

namespace Org.IdentityConnectors.ActiveDirectory
{
    [TestFixture]
    public class ActiveDirectoryConnectorTest
    {
        public static readonly string CONFIG_PROPERTY_USER = "config_user";
        public static readonly string CONFIG_PROPERTY_PASSWORD = "config_password";
        public static readonly string CONFIG_PROPERTY_HOST = "config_host";
        public static readonly string CONFIG_PROPERTY_PORT = "config_port";
        public static readonly string CONFIG_PROPERTY_CONTAINER = "config_container";
        public static readonly string CONFIG_PROPERTY_SCRIPT_USER_LOCAL = "config_script_user_local";
        public static readonly string CONFIG_PROPERTY_SCRIPT_PASSWORD_LOCAL = "config_script_password_local";
        public static readonly string CONFIG_PROPERTY_SCRIPT_USER_DOMAIN = "config_script_user_domain";
        public static readonly string CONFIG_PROPERTY_SCRIPT_PASSWORD_DOMAIN = "config_script_password_domain";
        public static readonly string CONFIG_PROPERTY_LDAPHOSTNAME = "config_ldap_hostname";
        public static readonly string CONFIG_PROPERTY_SEARCH_CONTEXT = "config_search_context";
        public static readonly string CONFIG_PROPERTY_SYNC_SEARCH_CONTEXT_ROOT = "config_sync_search_context_root";
        public static readonly string CONFIG_PROPERTY_SYNC_SEARCH_CONTEXT_CHILD = "config_sync_search_context_child";
        public static readonly string CONFIG_PROPERTY_DOMAIN_NAME = "config_domain_name";
        public static readonly string CONFIG_PROPERTY_SYNC_DOMAIN_CONTROLLER = "config_sync_domain_controller";
        public static readonly string CONFIG_PROPERTY_GC_DOMAIN_CONTROLLER = "config_sync_gc_domain_controller";
        public static readonly string TEST_PARAM_SHARED_HOME_FOLDER = "test_param_shared_home_folder";

        [Test]
        public void TestConfiguration() {
            ActiveDirectoryConfiguration config = new ActiveDirectoryConfiguration();
            String directoryAdminName = GetProperty(CONFIG_PROPERTY_USER);
            config.DirectoryAdminName = directoryAdminName;
            Assert.AreEqual(directoryAdminName, config.DirectoryAdminName);
        }

        [Test]
        public void TestSchema()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Schema schema = connector.Schema();
            Boolean foundOptionalAttributes = false;
            Boolean foundOperationalAttributes = false;

            // just a very high level check of things.  Should have 3 ObjectClassInfos,
            // (group, account, and organizationalUnit) and nothing contained in them 
            // should be null.  Group and account should have some operational 
            // attributes
            Assert.AreEqual(3, schema.ObjectClassInfo.Count);
            foreach(ObjectClassInfo ocInfo in schema.ObjectClassInfo) {
                Assert.IsNotNull(ocInfo);
                Assert.That((ocInfo.ObjectType == ObjectClass.ACCOUNT.GetObjectClassValue())
                    || (ocInfo.ObjectType == ObjectClass.GROUP.GetObjectClassValue()) 
                    || (ocInfo.ObjectType == ActiveDirectoryConnector.OBJECTCLASS_OU));
                Console.WriteLine("****** " + ocInfo.ObjectType);

                // skip this for organizational unit ... it doesnt really have this
                if (ocInfo.ObjectType.Equals(ActiveDirectoryConnector.ouObjectClass))
                {
                    continue;
                }

                foreach (ConnectorAttributeInfo caInfo in ocInfo.ConnectorAttributeInfos)
                {                    
                    Assert.IsNotNull(caInfo);
                    Console.WriteLine("{0} {1} {2} {3}", caInfo.Name, 
                        caInfo.IsWritable ? "writable" : "",
                        caInfo.IsRequired ? "required" : "",
                        caInfo.IsMultiValue ? "multivalue" : "");
                    if(ConnectorAttributeUtil.IsSpecial(caInfo)) {
                        foundOperationalAttributes = true;
                    } else {
                        if (!caInfo.IsRequired)
                        {
                            foundOptionalAttributes = true;
                        }     
                    }
                }
                Assert.That(foundOperationalAttributes && foundOptionalAttributes);
            }
        }

        // test proper behavior of each supported operation
        // and test proper reporting of unsuppoorted operations
        [Test]
        public void TestBasics_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            // create user
            ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();
            Uid createUid = CreateAndVerifyObject(connector,
                ObjectClass.ACCOUNT, createAttributes);

            // update the user - replace
            ICollection<ConnectorAttribute> updateReplaceAttrs =
                new List<ConnectorAttribute>();
            Name oldName = ConnectorAttributeUtil.GetNameFromAttributes(createAttributes);
            String newName = ActiveDirectoryUtils.GetRelativeName(oldName);
            newName = newName.Trim() + "_new, " + GetProperty(CONFIG_PROPERTY_CONTAINER);
            updateReplaceAttrs.Add(ConnectorAttributeBuilder.Build(
                Name.NAME, newName));
            updateReplaceAttrs.Add(ConnectorAttributeBuilder.Build(
                "sn", "newsn"));
            Uid updateReplaceUid = UpdateReplaceAndVerifyObject(connector,
                ObjectClass.ACCOUNT, createUid, updateReplaceAttrs);

            // update the user - add
            ICollection<ConnectorAttribute> updateAddAttrs =
                new List<ConnectorAttribute>();
            updateAddAttrs.Add(ConnectorAttributeBuilder.Build("otherHomePhone", "123.456.7890", "098.765.4321"));
            Uid updateAddUid = UpdateAddAndVerifyUser(connector,
                ObjectClass.ACCOUNT, createUid, updateAddAttrs, null);

            // update the user - delete

            // delete user
            DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, createUid, true, true);
        }

        // test proper behaviour of each supported operation
        // and test proper reporting of unsuppoorted operations
        [Test]
        public void TestBasics_Group()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            // create group
            ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Group();
            createAttributes.Add(ConnectorAttributeBuilder.Build(PredefinedAttributes.ACCOUNTS_NAME,
                CreateGroupMember(connector)));

            // create object
            Uid createUid = connector.Create(ObjectClass.GROUP, createAttributes, null);
            Assert.IsNotNull(createUid);

            // find new object ... have to add groups to list of things to return
            OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
            ICollection<String> attributesToGet = GetDefaultAttributesToGet(ObjectClass.GROUP);
            attributesToGet.Add(PredefinedAttributes.ACCOUNTS_NAME);
            optionsBuilder.AttributesToGet = attributesToGet.ToArray();

            ConnectorObject newObject = GetConnectorObjectFromUid(connector,
                ObjectClass.GROUP, createUid, optionsBuilder.Build());
            VerifyObject(createAttributes, newObject);
            // update the group - replace
            ICollection<ConnectorAttribute> updateReplaceAttrs =
                new List<ConnectorAttribute>();
            Name oldName = ConnectorAttributeUtil.GetNameFromAttributes(createAttributes);
            String newName = ActiveDirectoryUtils.GetRelativeName(oldName);
            newName = newName.Trim() + "_new, " + GetProperty(CONFIG_PROPERTY_CONTAINER);

            updateReplaceAttrs.Add(createUid);
            updateReplaceAttrs.Add(ConnectorAttributeBuilder.Build(
                Name.NAME, newName));
            updateReplaceAttrs.Add(ConnectorAttributeBuilder.Build(
                "description", "New description"));
            Uid updateReplaceUid = UpdateReplaceAndVerifyObject(connector,
                ObjectClass.GROUP, createUid, updateReplaceAttrs);

            // update the group - add
            ICollection<ConnectorAttribute> updateAddAttrs =
                new List<ConnectorAttribute>();
            updateAddAttrs.Add(ConnectorAttributeBuilder.Build(PredefinedAttributes.ACCOUNTS_NAME,
                CreateGroupMember(connector), CreateGroupMember(connector)));

            Uid updateAddUid = UpdateAddAndVerifyUser(connector,
                ObjectClass.GROUP, createUid, updateAddAttrs, optionsBuilder.Build());

            // delete user
            DeleteAndVerifyObject(connector, ObjectClass.GROUP, createUid, true, true);
        }

        [Test]
        public void TestCreate_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;
            try
            {
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();
                createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT, createAttributes);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        [Test]
        public void TestCreate_Group()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;
            try
            {
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Group();
                createUid = CreateAndVerifyObject(connector, ObjectClass.GROUP, createAttributes);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        [Test]
        public void TestCreateWithHomeDirectory_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            ActiveDirectoryConfiguration config = (ActiveDirectoryConfiguration)GetConfiguration();
            config.CreateHomeDirectory = true;
            connector.Init(config);
            Uid userUid = null;
            try
            {
                // get the normal attributes
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();

                // read the homedir path, and append the samaccountname
                StringBuilder homeDirPathBuilder = new StringBuilder(GetProperty(TEST_PARAM_SHARED_HOME_FOLDER));
                if (!homeDirPathBuilder.ToString().EndsWith("\\"))
                {
                    homeDirPathBuilder.Append('\\');
                }
                ConnectorAttribute samAccountNameAttr = ConnectorAttributeUtil.Find(
                    ActiveDirectoryConnector.ATT_SAMACCOUNT_NAME, createAttributes);
                homeDirPathBuilder.Append(ConnectorAttributeUtil.GetStringValue(samAccountNameAttr));

                // if it exists, delete it
                String homeDir = homeDirPathBuilder.ToString();
                if (Directory.Exists(homeDir))
                {
                    Directory.Delete(homeDir);
                }
                Assert.IsFalse(Directory.Exists(homeDir));

                // add homeDirectory to the attributes, and create user
                createAttributes.Add(ConnectorAttributeBuilder.Build("homeDirectory", homeDir));
                userUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);

                // now directory should exist
                Assert.IsTrue(Directory.Exists(homeDir));

                // get sid to check permissions
                OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
                ICollection<String> attributesToGet = GetDefaultAttributesToGet(ObjectClass.ACCOUNT);
                attributesToGet.Add(ActiveDirectoryConnector.ATT_OBJECT_SID);
                optionsBuilder.AttributesToGet = attributesToGet.ToArray();

                ConnectorObject newUser = GetConnectorObjectFromUid(connector,
                    ObjectClass.ACCOUNT, userUid, optionsBuilder.Build());
                ConnectorAttribute sidAttr =
                    newUser.GetAttributeByName(ActiveDirectoryConnector.ATT_OBJECT_SID);
                Byte[] sidBytes = (Byte[])ConnectorAttributeUtil.GetSingleValue(sidAttr);
                SecurityIdentifier newUserSid = new SecurityIdentifier(sidBytes, 0);

                // check permissions
                DirectoryInfo dirInfo = new DirectoryInfo(homeDir);
                DirectorySecurity dirSec = dirInfo.GetAccessControl();
                AuthorizationRuleCollection rules = dirSec.GetAccessRules(true, true, typeof(SecurityIdentifier));
                bool foundCorrectRule = false;
                foreach (AuthorizationRule rule in rules)
                {
                    if (rule is FileSystemAccessRule)
                    {
                        FileSystemAccessRule fsaRule = (FileSystemAccessRule)rule;
                        if (fsaRule.IdentityReference.Equals(newUserSid))
                        {
                            if ((fsaRule.AccessControlType.Equals(AccessControlType.Allow)) &&
                                (fsaRule.FileSystemRights.Equals(FileSystemRights.FullControl)) &&
                                (fsaRule.InheritanceFlags.Equals(InheritanceFlags.ContainerInherit | InheritanceFlags.ObjectInherit)) &&
                                (fsaRule.IsInherited.Equals(false)))
                            {
                                foundCorrectRule = true;
                            }

                        }
                    }
                }

                // remove the directory (before assertion may fail)
                Directory.Delete(homeDir);

                // check that we found the proper permission record
                Assert.IsTrue(foundCorrectRule);
            }
            finally
            {
                if (userUid != null)
                {
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, userUid, false, false);
                }
            }
        }

        [Test] // tests that if create home directory is set to false, no directory is created
        public void TestCreateWithHomeDirectoryNoCreateConfig_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            ActiveDirectoryConfiguration config = (ActiveDirectoryConfiguration)GetConfiguration();
            config.CreateHomeDirectory = false;
            connector.Init(config);
            Uid userUid = null;
            try
            {
                // get the normal attributes
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();

                // read the homedir path, and append the samaccountname
                StringBuilder homeDirPathBuilder = new StringBuilder(GetProperty(TEST_PARAM_SHARED_HOME_FOLDER));
                if (!homeDirPathBuilder.ToString().EndsWith("\\"))
                {
                    homeDirPathBuilder.Append('\\');
                }
                ConnectorAttribute samAccountNameAttr = ConnectorAttributeUtil.Find(
                    ActiveDirectoryConnector.ATT_SAMACCOUNT_NAME, createAttributes);
                homeDirPathBuilder.Append(ConnectorAttributeUtil.GetStringValue(samAccountNameAttr));

                // if it exists, delete it
                String homeDir = homeDirPathBuilder.ToString();
                if (Directory.Exists(homeDir))
                {
                    Directory.Delete(homeDir);
                }
                Assert.IsFalse(Directory.Exists(homeDir));

                // add homeDirectory to the attributes, and create user
                createAttributes.Add(ConnectorAttributeBuilder.Build("homeDirectory", homeDir));
                userUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);

                // now directory should not exist 
                // (createhomedirectory was set to false in the configuration)
                Assert.IsFalse(Directory.Exists(homeDir));
            }
            finally
            {
                if (userUid != null)
                {
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, userUid, false, false);
                }
            }
        }

        [Test]
        public void TestSearchNoFilter_Account()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            ICollection<Uid> createdUids = new HashSet<Uid>();

            try
            {
                int numCreated = 0;
                for (numCreated = 0; numCreated < 5; numCreated++)
                {
                    createdUids.Add(CreateAndVerifyObject(connector, 
                        ObjectClass.ACCOUNT, GetNormalAttributes_Account()));
                }

                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, null);

                // not sure how many should be found ... it should find everything
                // it's hard to say how many that is, but it should at least find the
                // number we created
                Assert.GreaterOrEqual(results.Count, numCreated);

                // check that they are all of the proper objectclass
                foreach (ConnectorObject co in results)
                {
                    ConnectorAttribute objectClassAttr =
                        co.GetAttributeByName("objectClass");
                    Boolean foundCorrectObjectClass = false;
                    foreach (Object o in objectClassAttr.Value)
                    {
                        if ((o is String) && (o != null))
                        {
                            String stringValue = (String)o;
                            if (stringValue.ToUpper().Trim().Equals("USER"))
                            {
                                foundCorrectObjectClass = true;
                            }
                        }
                    }
                    Assert.IsTrue(foundCorrectObjectClass);
                }
            }
            finally
            {
                foreach (Uid uid in createdUids)
                {
                    if (uid != null)
                    {
                        DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, uid, false, true);
                    }
                }
            }
        }

        [Test]
        public void TestSearchNoFilter_Group()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            ICollection<Uid> createdUids = new HashSet<Uid>();

            try
            {
                int numCreated = 0;
                for (numCreated = 0; numCreated < 5; numCreated++)
                {
                    createdUids.Add(CreateAndVerifyObject(connector, 
                        ObjectClass.GROUP, GetNormalAttributes_Group()));
                }

                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.GROUP, null);

                // not sure how many should be found ... it should find everything
                // it's hard to say how many that is, but it should at least find the
                // number we created
                Assert.GreaterOrEqual(results.Count, numCreated);

                // check that they are all of the proper objectclass
                foreach (ConnectorObject co in results)
                {
                    ConnectorAttribute objectClassAttr =
                        co.GetAttributeByName("objectClass");
                    Boolean foundCorrectObjectClass = false;
                    foreach (Object o in objectClassAttr.Value)
                    {
                        if ((o is String) && (o != null))
                        {
                            String stringValue = (String)o;
                            if (stringValue.ToUpper().Trim().Equals("GROUP"))
                            {
                                foundCorrectObjectClass = true;
                            }
                        }
                    }
                    Assert.IsTrue(foundCorrectObjectClass);
                }
            }
            finally
            {
                foreach (Uid uid in createdUids)
                {
                    if (uid != null)
                    {
                        DeleteAndVerifyObject(connector, ObjectClass.GROUP, uid, false, true);
                    }
                }
            }
        }

        [Test]
        public void TestSearchByName_account()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            Uid createUid = null;

            try
            {
                createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    GetNormalAttributes_Account());

                // find out what the name was
                ConnectorObject newObject = GetConnectorObjectFromUid(connector, 
                    ObjectClass.ACCOUNT, createUid);
                Name nameAttr = newObject.Name;
                Assert.IsNotNull(nameAttr);

                //search normally
                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, FilterBuilder.EqualTo(nameAttr));

                // there really should only be one
                Assert.AreEqual(results.Count, 1);

                // and it must have the value we were searching for
                String createName = ActiveDirectoryUtils.NormalizeLdapString(nameAttr.GetNameValue());
                String foundName = ActiveDirectoryUtils.NormalizeLdapString(results.ElementAt(0).Name.GetNameValue());
                Assert.AreEqual(createName, foundName);

                //search in uppercase
                ConnectorAttribute nameUpper = ConnectorAttributeBuilder.Build(
                    nameAttr.Name, nameAttr.GetNameValue().ToUpper());
                results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, FilterBuilder.EqualTo(nameUpper));

                // there really should only be one
                Assert.AreEqual(results.Count, 1);

                // and it must have the value we were searching for
                createName = ActiveDirectoryUtils.NormalizeLdapString(nameAttr.GetNameValue());
                foundName = ActiveDirectoryUtils.NormalizeLdapString(results.ElementAt(0).Name.GetNameValue());
                Assert.AreEqual(createName, foundName);

                //search in lowercase
                ConnectorAttribute nameLower = ConnectorAttributeBuilder.Build(
                    nameAttr.Name, nameAttr.GetNameValue().ToLower());
                results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, FilterBuilder.EqualTo(nameLower));

                // there really should only be one
                Assert.AreEqual(results.Count, 1);

                // and it must have the value we were searching for
                createName = ActiveDirectoryUtils.NormalizeLdapString(nameAttr.GetNameValue());
                foundName = ActiveDirectoryUtils.NormalizeLdapString(results.ElementAt(0).Name.GetNameValue());
                Assert.AreEqual(createName, foundName);

            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        [Test]
        public void TestSearchByName_group()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            Uid createUid = null;

            try
            {
                createUid = CreateAndVerifyObject(connector, ObjectClass.GROUP,
                    GetNormalAttributes_Group());

                // find out what the name was
                ConnectorObject newObject = GetConnectorObjectFromUid(connector,
                    ObjectClass.GROUP, createUid);
                Name nameAttr = newObject.Name;
                Assert.IsNotNull(nameAttr);

                //search normally
                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.GROUP, FilterBuilder.EqualTo(nameAttr));

                // there really should only be one
                Assert.AreEqual(results.Count, 1);

                // and it must have the value we were searching for
                String createName = ActiveDirectoryUtils.NormalizeLdapString(nameAttr.GetNameValue());
                String foundName = ActiveDirectoryUtils.NormalizeLdapString(results.ElementAt(0).Name.GetNameValue());
                Assert.AreEqual(createName, foundName);

                //search in uppercase
                ConnectorAttribute nameUpper = ConnectorAttributeBuilder.Build(
                    nameAttr.Name, nameAttr.GetNameValue().ToUpper());
                results = TestHelpers.SearchToList(connector,
                    ObjectClass.GROUP, FilterBuilder.EqualTo(nameUpper));

                // there really should only be one
                Assert.AreEqual(results.Count, 1);

                // and it must have the value we were searching for
                createName = ActiveDirectoryUtils.NormalizeLdapString(nameAttr.GetNameValue());
                foundName = ActiveDirectoryUtils.NormalizeLdapString(results.ElementAt(0).Name.GetNameValue());
                Assert.AreEqual(createName, foundName);

                //search in lowercase
                ConnectorAttribute nameLower = ConnectorAttributeBuilder.Build(
                    nameAttr.Name, nameAttr.GetNameValue().ToLower());
                results = TestHelpers.SearchToList(connector,
                    ObjectClass.GROUP, FilterBuilder.EqualTo(nameLower));

                // there really should only be one
                Assert.AreEqual(results.Count, 1);

                // and it must have the value we were searching for
                createName = ActiveDirectoryUtils.NormalizeLdapString(nameAttr.GetNameValue());
                foundName = ActiveDirectoryUtils.NormalizeLdapString(results.ElementAt(0).Name.GetNameValue());
                Assert.AreEqual(createName, foundName);

            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.GROUP,
                        createUid, false, true);
                }
            }
        }

        [Test]
        public void TestSearchByCNWithWildcard_account()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            Uid uid1 = null;
            Uid uid2 = null;

            try
            {
                // create a couple things to find
                uid1 = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    GetNormalAttributes_Account());
                uid2 = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    GetNormalAttributes_Account());

                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, FilterBuilder.EqualTo(
                    ConnectorAttributeBuilder.Build("CN", "nunit*")));

                // there should be at least the two we just created
                Assert.GreaterOrEqual(results.Count, 2);
                foreach (ConnectorObject co in results)
                {
                    // and it must have the value we were searching for
                    String foundName = ActiveDirectoryUtils.NormalizeLdapString(
                        co.Name.GetNameValue());
                    Assert.That(foundName.ToUpper().StartsWith("CN=NUNIT"));
                }
            }
            finally
            {
                if (uid1 != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        uid1, false, true);
                }
                if (uid2 != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        uid2, false, true);
                }
            }
        }

        [Test]
        public void TestSearchByRegularAttribute_account()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            Uid createUid = null;

            try
            {
                ConnectorAttribute createSnAttr =
                    ConnectorAttributeBuilder.Build("sn", "nunitSearch");
                ICollection<ConnectorAttribute> attributes = GetNormalAttributes_Account();
                attributes.Remove(ConnectorAttributeUtil.Find("sn", attributes));
                attributes.Add(createSnAttr);
                createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    attributes);

                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, FilterBuilder.EqualTo(createSnAttr));

                // there should be at least the newly created one
                Assert.GreaterOrEqual(results.Count, 1);

                // and it must have the value we were searching for
                Boolean foundCreated = false;
                foreach (ConnectorObject resultObject in results)
                {
                    ConnectorAttribute foundSnAttr =
                        resultObject.GetAttributeByName("sn");
                    Assert.AreEqual(createSnAttr, foundSnAttr);

                    // keep track of if we've found the one we created
                    if (createUid.Equals(resultObject.Uid))
                    {
                        // cant have it twice
                        Assert.IsFalse(foundCreated);
                        foundCreated = true;
                    }
                }
                // be certain we saw the one we created
                Assert.IsTrue(foundCreated);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        [Test]
        public void TestSearchByRegularAttributeWithWildcard_account()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            ICollection<Uid> uids = new HashSet<Uid>();

            try
            {
                Random random = new Random();
                Int32 randomNumber = random.Next(1000000);

                String snPrefix = "nunitWCTest";

                for (int i = 0; i < 10; i++)
                {
                    ICollection<ConnectorAttribute> attributes =
                        GetNormalAttributes_Account();
                    attributes.Remove(ConnectorAttributeUtil.Find("sn", attributes));
                    attributes.Add(ConnectorAttributeBuilder.Build("sn",
                        snPrefix + random.Next()));
                    Uid tempUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        attributes);
                    Assert.IsNotNull(tempUid);
                    uids.Add(tempUid);
                }

                ICollection<ConnectorObject> results = TestHelpers.SearchToList(connector,
                    ObjectClass.ACCOUNT, FilterBuilder.StartsWith(
                    ConnectorAttributeBuilder.Build("sn", snPrefix)));

                // there should be at least the newly created one
                Assert.GreaterOrEqual(results.Count, 1);

                // make a duplicate list
                ICollection<Uid> uidsToValidate = new HashSet<Uid>(uids);

                // and it must have the value we were searching for
                foreach (ConnectorObject resultObject in results)
                {
                    ConnectorAttribute foundSnAttr =
                        resultObject.GetAttributeByName("sn");
                    String snValue = ConnectorAttributeUtil.GetStringValue(foundSnAttr);
                    Assert.That(snValue.StartsWith(snPrefix));
                    uidsToValidate.Remove(resultObject.Uid);
                    if (uidsToValidate.Count == 0)
                    {
                        break;
                    }
                }
                Assert.AreEqual(0, uidsToValidate.Count);
            }
            finally
            {
                foreach (Uid createdUid in uids)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createdUid, false, true);
                }
            }
        }

        // test proper behavior of create with ALL attributes specified
        [Test]
        public void TestCreateWithAllAttributes_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;

            try
            {
                // create user
                ICollection<ConnectorAttribute> createAttributes = GetAllAttributes_Account();
                createUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        // test proper behavior of create with ALL attributes specified
        [Test]
        public void Test_OpAtt_Enabled_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;

            try
            {
                // create user
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();
                createAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(true));
                createUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);

                ICollection<ConnectorAttribute> updateReplaceAttributes =
                    new HashSet<ConnectorAttribute>();
                updateReplaceAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(false));
                UpdateReplaceAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    createUid, updateReplaceAttributes);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        // Test scripting
        [Test]
        public void TestScriptOnResource()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            try
            {
                RunScript(connector, "", "");
                RunScript(connector, GetProperty(CONFIG_PROPERTY_SCRIPT_USER_LOCAL),
                    GetProperty(CONFIG_PROPERTY_SCRIPT_PASSWORD_LOCAL));
                RunScript(connector, GetProperty(CONFIG_PROPERTY_SCRIPT_USER_DOMAIN),
                    GetProperty(CONFIG_PROPERTY_SCRIPT_PASSWORD_DOMAIN));


                // try with invalid credentials
                bool scriptFailed = false;
                try
                {
                    RunScript(connector, GetProperty(CONFIG_PROPERTY_USER), "bogus");
                }
                catch (Exception e)
                {
                    scriptFailed = true;
                }
                Assert.That(scriptFailed);
            }
            finally
            {
            }
        }

        [Test]
        public void TestAddGroup_Account()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid groupUid = null;
            Uid userUid = null;
            try 
            {
                userUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, GetNormalAttributes_Account());
                Filter userUidFilter = FilterBuilder.EqualTo(userUid);
                IList<ConnectorObject> foundUserObjects =
                    TestHelpers.SearchToList(connector, ObjectClass.ACCOUNT, userUidFilter);
                Assert.AreEqual(1, foundUserObjects.Count);

                groupUid = CreateAndVerifyObject(connector, 
                    ObjectClass.GROUP, GetNormalAttributes_Group());
                Filter groupUidFilter = FilterBuilder.EqualTo(groupUid);
                IList<ConnectorObject> foundGroupObjects =
                    TestHelpers.SearchToList(connector, ObjectClass.GROUP, groupUidFilter);
                Assert.AreEqual(1, foundGroupObjects.Count);
                String groupName = foundGroupObjects[0].Name.GetNameValue();

                ICollection<ConnectorAttribute> modifiedAttrs = new HashSet<ConnectorAttribute>();
                modifiedAttrs.Add(ConnectorAttributeBuilder.Build(PredefinedAttributes.GROUPS_NAME, groupName));
                OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
                ICollection<String> attributesToGet = GetDefaultAttributesToGet(ObjectClass.ACCOUNT);
                attributesToGet.Add(PredefinedAttributes.GROUPS_NAME);
                optionsBuilder.AttributesToGet = attributesToGet.ToArray();
                UpdateAddAndVerifyUser(connector, ObjectClass.ACCOUNT, 
                    userUid, modifiedAttrs, optionsBuilder.Build());

            } finally {
                DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, userUid, false, false);
                DeleteAndVerifyObject(connector, ObjectClass.GROUP, groupUid, false, false);
            }
        }

        [Test]
        public void TestRemoveAttributeValue()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            Uid createUid = null;

            try
            {
                Random random = new Random();
                Int32 randomNumber = random.Next(1000000);
                ICollection<ConnectorAttribute> attributes = new HashSet<ConnectorAttribute>();

                attributes.Add(ConnectorAttributeBuilder.Build(
                    "ad_container", GetProperty(CONFIG_PROPERTY_CONTAINER)));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "userPassword", "secret"));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "sAMAccountName", "nunit" + randomNumber));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "givenName", "nunit"));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "sn", "TestUser" + randomNumber));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "displayName", "nunit test user " + randomNumber));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    Name.NAME, "cn=nunit" + randomNumber + "," +
                    GetProperty(CONFIG_PROPERTY_CONTAINER)));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "mail", "nunitUser" + randomNumber + "@some.com"));
                attributes.Add(ConnectorAttributeBuilder.Build(
                    "otherHomePhone", "512.555.1212", "512.123.4567"));
                
                createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    attributes);

                ICollection<ConnectorAttribute> modifyAttributes = new HashSet<ConnectorAttribute>();
                modifyAttributes.Add(createUid);
                modifyAttributes.Add(ConnectorAttributeBuilder.Build("otherHomePhone", "512.555.1212"));

                connector.Update(UpdateType.DELETE, ObjectClass.ACCOUNT, modifyAttributes, null);

                Filter uidFilter = FilterBuilder.EqualTo(createUid);
                IList<ConnectorObject> objects = TestHelpers.SearchToList(connector, ObjectClass.ACCOUNT, uidFilter);
                Assert.AreEqual(1, objects.Count);

                ConnectorAttribute otherHomePhoneAttr = ConnectorAttributeUtil.Find(
                    "otherHomePhone", objects[0].GetAttributes());

                Assert.AreEqual(1, otherHomePhoneAttr.Value.Count);
                Assert.AreEqual("512.123.4567", ConnectorAttributeUtil.GetSingleValue(otherHomePhoneAttr));
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }


        [Ignore]
        [Test]
        public void TestContainerChange_account()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            Uid createGroupUid = null;
            Uid createUserUid = null;

            try {
                // create container for this test
                ICollection<ConnectorAttribute> groupAttributes = GetNormalAttributes_Group();
                createGroupUid = CreateAndVerifyObject(connector,
                    ObjectClass.GROUP, groupAttributes);
                ICollection<ConnectorObject> groupResults = TestHelpers.SearchToList(
                    connector, ObjectClass.GROUP, FilterBuilder.EqualTo(createGroupUid));
                Assert.AreEqual(1, groupResults.Count);
                Assert.AreEqual(createGroupUid, groupResults.ElementAt(0).Uid);
                ConnectorAttribute groupDnAttr = 
                    groupResults.ElementAt(0).GetAttributeByName("distinguishedName");
                Assert.IsNotNull(groupDnAttr);
                String groupPath = ConnectorAttributeUtil.GetStringValue(groupDnAttr);

                // create user
                createUserUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, GetNormalAttributes_Account());

                //now change user container to the newly created one
                ICollection<ConnectorAttribute> updateAttrs = new HashSet<ConnectorAttribute>();
                updateAttrs.Add(ConnectorAttributeBuilder.Build("ad_container", groupPath));
                updateAttrs.Add(createUserUid);

                connector.Update(UpdateType.REPLACE, ObjectClass.ACCOUNT, updateAttrs, null);

                ICollection<ConnectorObject> results = TestHelpers.SearchToList(
                    connector, ObjectClass.ACCOUNT, FilterBuilder.EqualTo(createUserUid));
                Assert.AreEqual(1, results.Count);
                Assert.AreEqual(createUserUid, results.ElementAt(0).Uid);
                ConnectorAttribute foundContainerAttr = results.ElementAt(0).GetAttributeByName("ad_container");
                Assert.IsNotNull(foundContainerAttr);

                String lhs = ActiveDirectoryUtils.NormalizeLdapString(groupPath);
                String rhs = ActiveDirectoryUtils.NormalizeLdapString(ConnectorAttributeUtil.GetStringValue(foundContainerAttr));
                Assert.AreEqual(lhs, rhs);
            } 
            finally 
            {
                if (createUserUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUserUid, false, true);
                }

                if (createGroupUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.GROUP,
                        createGroupUid, false, true);
                }
            }
        }

        [Ignore]
        [Test]
        public void TestContainerChange_group()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());

            // create user
            Uid createUid = CreateAndVerifyObject(connector,
                ObjectClass.ACCOUNT, GetNormalAttributes_Account());

            // create container for this test

            //now change user container to the newly created one

            throw new NotImplementedException();
        }

        [Ignore]
        [Test]
        public void TestEnableDate()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;

            try
            {
                // create user
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();
                createAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(true));
                createAttributes.Add(ConnectorAttributeBuilder.BuildEnableDate(new DateTime(2000, 01, 01)));
                createUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);

                ICollection<ConnectorAttribute> updateReplaceAttributes =
                    new HashSet<ConnectorAttribute>();
                updateReplaceAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(false));
                UpdateReplaceAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    createUid, updateReplaceAttributes);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        [Ignore]
        [Test]
        public void TestDisableDate()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;

            try
            {
                // create user
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();
                createAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(true));

                // disable tommorrow
                DateTime disableDate = DateTime.Now.AddHours(24);

                createAttributes.Add(ConnectorAttributeBuilder.BuildDisableDate(disableDate));
                createUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);

                ICollection<ConnectorAttribute> updateReplaceAttributes =
                    new HashSet<ConnectorAttribute>();
                updateReplaceAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(false));
                UpdateReplaceAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    createUid, updateReplaceAttributes);
            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        // test sync
        [Test]
        public void TestSyncGC()
        {
            // test with searchChildDomain (uses GC)
            TestSync(true, GetProperty(CONFIG_PROPERTY_SYNC_SEARCH_CONTEXT_ROOT));
            TestSync(true, GetProperty(CONFIG_PROPERTY_SYNC_SEARCH_CONTEXT_CHILD));
        }

        // test sync
        [Test]
        public void TestSyncDC()
        {
            // test withouth searchChildDomains (uses DC)
            TestSync(false, GetProperty(CONFIG_PROPERTY_SYNC_SEARCH_CONTEXT_ROOT));
            TestSync(false, GetProperty(CONFIG_PROPERTY_SYNC_SEARCH_CONTEXT_CHILD));
        }

        [Test]
        public void TestUserPasswordChange()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid createUid = null;

            try
            {
                // create user
                ICollection<ConnectorAttribute> createAttributes = GetNormalAttributes_Account();
                // remove password, and set to something memorable
                createAttributes.Remove(ConnectorAttributeUtil.Find(OperationalAttributes.PASSWORD_NAME, createAttributes));
                GuardedString gsCurrentPassword = new GuardedString();
                foreach (char c in "1Password")
                {
                    gsCurrentPassword.AppendChar(c);
                }
                createAttributes.Add(ConnectorAttributeBuilder.BuildPassword(gsCurrentPassword));
                createAttributes.Add(ConnectorAttributeBuilder.BuildEnabled(true));
                createUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, createAttributes);

                // make sure authenticate works here                
                connector.Authenticate(ConnectorAttributeUtil.GetNameFromAttributes(createAttributes).GetNameValue(),
                    gsCurrentPassword, null);

                ICollection<ConnectorAttribute> updateReplaceAttributes =
                    new HashSet<ConnectorAttribute>();
                GuardedString gsNewPassword = new GuardedString();
                foreach (char c in "LongPassword2MeetTheRequirements!")
                {
                    gsNewPassword.AppendChar(c);
                }
                updateReplaceAttributes.Add(ConnectorAttributeBuilder.BuildCurrentPassword(gsCurrentPassword));
                updateReplaceAttributes.Add(ConnectorAttributeBuilder.BuildPassword(gsNewPassword));
                UpdateReplaceAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    createUid, updateReplaceAttributes);

                // make sure authenticate works here
                connector.Authenticate(ConnectorAttributeUtil.GetNameFromAttributes(createAttributes).GetNameValue(),
                    gsNewPassword, null);

                bool caughtAuthenticateFailedException = false;
                try
                {
                    // make sure authenticate doesnt work with original password
                    connector.Authenticate(ConnectorAttributeUtil.GetNameFromAttributes(createAttributes).GetNameValue(),
                        gsCurrentPassword, null);
                }
                catch (Exception e)
                {
                    caughtAuthenticateFailedException = true;
                }

                Assert.IsTrue(caughtAuthenticateFailedException, "Negative test case should throw an exception");


                // now a negative test case
                GuardedString gsBogusPassword = new GuardedString();
                foreach (char c in "BogusPassword")
                {
                    gsBogusPassword.AppendChar(c);
                }
                ICollection<ConnectorAttribute> updateErrorReplaceAttributes =
                    new HashSet<ConnectorAttribute>();
                updateErrorReplaceAttributes.Add(ConnectorAttributeBuilder.BuildCurrentPassword(gsBogusPassword));
                updateErrorReplaceAttributes.Add(ConnectorAttributeBuilder.BuildPassword(gsNewPassword));
                bool caughtWrongCurrentPasswordException = false;
                try
                {
                    // update should fail due to wrong current password
                    UpdateReplaceAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, updateErrorReplaceAttributes);
                }
                catch (Exception e)
                {
                    caughtWrongCurrentPasswordException = true;
                }

                Assert.IsTrue(caughtWrongCurrentPasswordException, "Negative test case should throw an exception");

                // make sure authenticate works here                

            }
            finally
            {
                if (createUid != null)
                {
                    //remove the one we created
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        createUid, false, true);
                }
            }
        }

        public void TestSync(bool searchChildDomains, String syncSearchContext)
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            ActiveDirectoryConfiguration configuration = 
                (ActiveDirectoryConfiguration)GetConfiguration();
            configuration.SyncSearchContext = syncSearchContext;
            configuration.SearchChildDomains = searchChildDomains;
            connector.Init(configuration);

            Uid createUid = null;

            ICollection<Uid> createdUids = new List<Uid>();
            try
            {
                SyncTestHelper syncHelper = new SyncTestHelper();

                // do the first sync
                connector.Sync(ObjectClass.ACCOUNT, syncHelper.Token, syncHelper.SyncHandler_Initial, null);

                syncHelper.Init();
                ICollection<ConnectorAttribute> attributes = null;

                // create some users
                for (int i = 0; i < 10; i++)
                {
                    attributes = GetNormalAttributes_Account();
                    createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        attributes);
                    syncHelper.AddModUid(createUid, attributes);
                    createdUids.Add(createUid);
                }

                // sync, and verify
                connector.Sync(ObjectClass.ACCOUNT, syncHelper.Token, syncHelper.SyncHandler_ModifiedAccounts, null);
                syncHelper.CheckAllSyncsProcessed();

                // reset everything
                syncHelper.Init();

                // modify a user, then add some users, then modify one of the added users
                attributes = new List<ConnectorAttribute>();
                attributes.Add(createdUids.First());
                attributes.Add(ConnectorAttributeBuilder.Build("sn", "replaced"));
                connector.Update(UpdateType.REPLACE, ObjectClass.ACCOUNT, attributes, null);
                syncHelper.AddModUid(createdUids.First(), attributes);

                for(int i = 0; i < 10; i++)
                {
                    attributes = GetNormalAttributes_Account();
                    createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                        attributes);
                    syncHelper.AddModUid(createUid, attributes);
                    createdUids.Add(createUid);
                }

                attributes = new List<ConnectorAttribute>();
                attributes.Add(createdUids.Last());
                attributes.Add(ConnectorAttributeBuilder.Build("sn", "replaced"));
                connector.Update(UpdateType.REPLACE, ObjectClass.ACCOUNT, attributes, null);
                syncHelper.AddModUid(createdUids.Last(), attributes);

                // sync, and verify
                connector.Sync(ObjectClass.ACCOUNT, syncHelper.Token, syncHelper.SyncHandler_ModifiedAccounts, null);
                syncHelper.CheckAllSyncsProcessed();

                syncHelper.Init();
                // delete the user
                foreach (Uid uid in createdUids)
                {
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, uid,
                        false, true);
                    syncHelper.AddDelUid(uid);
                }
                // sync and verify
                connector.Sync(ObjectClass.ACCOUNT, syncHelper.Token, syncHelper.SyncHandler_DeletedAccounts, null);
                syncHelper.CheckAllSyncsProcessed();

                createUid = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                    GetNormalAttributes_Account());
                syncHelper.AddModUid(createUid, attributes);
                createdUids.Add(createUid);

                // now get the latest sync token, and it
                // should be greater or equal to the last one we saw
                SyncToken latestToken = connector.GetLatestSyncToken();
                Assert.Greater(GetUpdateUsnFromToken(latestToken), GetUpdateUsnFromToken(syncHelper.Token));
                Assert.GreaterOrEqual(GetDeleteUsnFromToken(latestToken), GetDeleteUsnFromToken(syncHelper.Token));
            }
            finally
            {
                foreach (Uid uid in createdUids)
                {
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, uid,
                        false, false);
                }
            }
        }

        public long GetUpdateUsnFromToken(SyncToken token)
        {
            string[] tokenParts = ((string)token.Value).Split('|');
            return long.Parse(tokenParts[0]);
        }

        public long GetDeleteUsnFromToken(SyncToken token)
        {
            string[] tokenParts = ((string)token.Value).Split('|');
            return long.Parse(tokenParts[1]);
        }

        class SyncTestHelper
        {
            IDictionary<Uid, ICollection<ConnectorAttribute>> _mods = null;
            IList<Uid> _dels = null;

            public SyncToken Token { get; set; }

            public void Init()
            {
                _mods = new Dictionary<Uid, ICollection<ConnectorAttribute>>();
                _dels = new List<Uid>();
            }

            public void AddModUid(Uid uid, ICollection<ConnectorAttribute> attributes)
            {
                _mods[uid]=attributes;
            }

            public void AddDelUid(Uid uid)
            {
                _dels.Add(uid);
            }

            public bool SyncHandler_Initial(SyncDelta delta)
            {
                // do nothing .. just establishing the baseline
                Token = delta.Token;
                return true;
            }

            public bool SyncHandler_ModifiedAccounts(SyncDelta delta)
            {
                Token = delta.Token;
                if(delta.DeltaType.Equals(SyncDeltaType.UPDATE)) {
                    // just ignore extra ones.  they might have come in by other means
                    if (_mods.ContainsKey(delta.Uid))
                    {
                        ICollection<ConnectorAttribute> requestedAttrs = _mods[delta.Uid];

                        ActiveDirectoryConnectorTest.VerifyObject(requestedAttrs,
                            delta.Object);
                        _mods.Remove(delta.Uid);
                    }
                }
                return true;
            }

            public bool SyncHandler_DeletedAccounts(SyncDelta delta)
            {
                Token = delta.Token;

                _dels.Remove(delta.Uid);
                return true;
            }

            public bool SyncHandler_Mixed(SyncDelta delta)
            {
                return true;
            }

            public void CheckAllSyncsProcessed()
            {
                // since the handlers remove things from
                // the list as found, this method is called
                // at then end of a sync, and all arrays should
                // be empty ... meaning everything is accounted
                // for.
                Assert.AreEqual(0, _dels.Count);
                Assert.AreEqual(0, _mods.Count);
            }
        }

        public void RunScript(ActiveDirectoryConnector connector, String user, 
            string password)
        {
            string tempFileName = Path.GetTempFileName();
            string scriptText = String.Format(
                "echo %ARG0%:%ARG1%:%USERNAME%:%PASSWORD% > \"{0}\"", tempFileName);
           
            IDictionary<string, object> arguments = new Dictionary<string, object>();
            string arg0 = "argument_zero";
            string arg1 = "argument one";
            arguments.Add("ARG0", arg0);
            arguments.Add("ARG1", arg1);

            OperationOptionsBuilder builder = new OperationOptionsBuilder();
            if (user.Length > 0)
            {
                builder.RunAsUser = user;
            }
            if (password.Length > 0)
            {
                builder.RunWithPassword = password;
            }

            ScriptContext context = new ScriptContext("Shell", scriptText, arguments);
            object resultObject = connector.RunScriptOnResource(context, builder.Build());
            Assert.IsNotNull(resultObject);
            Assert.That(resultObject is int);
            Assert.AreEqual(0, resultObject);
            FileStream outputFs = new FileStream(tempFileName, FileMode.Open, FileAccess.Read);
            StreamReader outputReader = new StreamReader(outputFs);
            // read the first line
            string output = outputReader.ReadLine();
            string[] returnedArray = output.Split(':');
            Assert.AreEqual(4, returnedArray.Length);
            Assert.AreEqual(arg0, returnedArray[0]);
            Assert.AreEqual(arg1, returnedArray[1]);
        }

        // does a create and verify, then looks up and returns
        // the new user's dn (used for adding to a group)
        public String CreateGroupMember(ActiveDirectoryConnector connector)
        {
            Uid uidMember = CreateAndVerifyObject(connector, ObjectClass.ACCOUNT,
                GetNormalAttributes_Account());

            Filter uidFilter = FilterBuilder.EqualTo(uidMember);
            ICollection<ConnectorObject> foundObjects = TestHelpers.SearchToList(
                connector, ObjectClass.ACCOUNT, uidFilter);
            Assert.IsTrue(foundObjects.Count == 1);
            String dnMember = ConnectorAttributeUtil.GetAsStringValue(
                foundObjects.ElementAt(0).GetAttributeByName("distinguishedName"));
            return dnMember;
        }

        public Uid UpdateReplaceAndVerifyObject(ActiveDirectoryConnector connector,
            ObjectClass oclass, Uid uid, ICollection<ConnectorAttribute> attributes)
        {
            attributes.Add(uid);
            Filter uidFilter = FilterBuilder.EqualTo(uid);

            // find the object ... can't update if it doesn't exist
            ICollection<ConnectorObject> currentConnectorObjects = TestHelpers.SearchToList(
                connector, oclass, uidFilter);
            Assert.AreEqual(1, currentConnectorObjects.Count);

            Uid updatedUid = connector.Update(UpdateType.REPLACE, oclass,
                attributes, null);

            Assert.IsNotNull(updatedUid);

            ICollection<ConnectorObject> updatedConnectorObjects = TestHelpers.SearchToList(
                connector, oclass, uidFilter);
            Assert.IsTrue(updatedConnectorObjects.Count == 1);
            VerifyObject(attributes, updatedConnectorObjects.ElementAt(0));
            return updatedUid;
        }

        public Uid UpdateAddAndVerifyUser(ActiveDirectoryConnector connector,
            ObjectClass oclass, Uid uid, ICollection<ConnectorAttribute> attributes, 
            OperationOptions searchOptions)
        {
            // find the existing one, and save off all attributes
            Filter uidFilter = FilterBuilder.EqualTo(uid);
            ICollection<ConnectorObject> currentObjects = TestHelpers.SearchToList(
                connector, oclass, uidFilter, searchOptions);
            Assert.IsTrue(currentObjects.Count == 1);
            ICollection<ConnectorAttribute> currentAttributes =
                currentObjects.ElementAt(0).GetAttributes();

            // build a list that has the 'added' values added to the existing values
            ICollection<ConnectorAttribute> comparisonAttributes = new List<ConnectorAttribute>();
            foreach (ConnectorAttribute updateAttribute in attributes)
            {
                ConnectorAttribute existingAttribute = ConnectorAttributeUtil.Find(
                    updateAttribute.Name, currentAttributes);
                comparisonAttributes.Add(AttConcat(updateAttribute, existingAttribute));
            }

            // make sure the uid is present in the attributes
            attributes.Add(uid);
            // now update with ADD to add additional home phones
            Uid updatedUid = connector.Update(UpdateType.ADD, oclass,
                attributes, null);

            // find it back
            ICollection<ConnectorObject> updatedObjects = TestHelpers.SearchToList(
                connector, oclass, uidFilter, searchOptions);
            Assert.IsTrue(updatedObjects.Count == 1);

            VerifyObject(comparisonAttributes, updatedObjects.ElementAt(0));

            return updatedUid;
        }

        /// <summary>
        /// Concatenates two attributes' values 
        /// </summary>
        /// <param name="ca1">Must be non null</param>
        /// <param name="ca2">May be null</param>
        /// <returns>new attribute with name of ca1 and value of ca1 + ca2</returns>
        public ConnectorAttribute AttConcat(ConnectorAttribute ca1, ConnectorAttribute ca2)
        {
            ConnectorAttributeBuilder builder = new ConnectorAttributeBuilder();
            Assert.IsNotNull(ca1);
            if (ca2 == null)
            {
                // if the second is null, just build up a dummy one
                ca2 = ConnectorAttributeBuilder.Build(ca1.Name);
            }

            Assert.AreEqual(ca1.Name, ca2.Name);
            builder.Name = ca1.Name;
            builder.AddValue(ca1.Value);
            builder.AddValue(ca2.Value);

            return builder.Build();
        }

        public Uid UpdateDeleteAndVerifyUser(ActiveDirectoryConnector connector,
            ICollection<ConnectorAttribute> attributes)
        {
            throw new NotImplementedException();
        }

        public void DeleteAndVerifyObject(ActiveDirectoryConnector connector,
            ObjectClass oclass, Uid uid, bool verifyExists, bool verifyDeleted)
        {
            Filter uidFilter = FilterBuilder.EqualTo(uid);

            if (verifyExists)
            {
                // verify that object currently exists
                ICollection<ConnectorObject> foundObjects = TestHelpers.SearchToList(
                    connector, oclass, uidFilter);

                // verify that it was deleted
                Assert.AreEqual(1, foundObjects.Count);
            }

            // delete
            try
            {
                connector.Delete(oclass, uid, null);
            }
            catch
            {
                if (verifyDeleted)
                {
                    throw;
                }
            }

            if (verifyDeleted)
            {
                // verify that object was deleted
                ICollection<ConnectorObject> deletedObjects = TestHelpers.SearchToList(
                    connector, oclass, uidFilter);

                // verify that it was deleted
                Assert.AreEqual(0, deletedObjects.Count);
            }
        }

        [Test]
        // note that you must create at least one ou for this test to work
        public void TestOuSearch()
        {
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            ActiveDirectoryConfiguration config = (ActiveDirectoryConfiguration)GetConfiguration();
            connector.Init(config);
            ObjectClass OUObjectClass = new ObjectClass("organizationalUnit");
            
            ICollection<ConnectorObject> foundObjects = TestHelpers.SearchToList(
                connector, OUObjectClass, null);
            Assert.Greater(foundObjects.Count, 0);
        }

        [Test]
        public void TestUnmatchedCaseGUIDSearch()
        {
            //Initialize Connector
            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Uid userUid = null;
            try
            {
                // test normal case
                userUid = CreateAndVerifyObject(connector,
                    ObjectClass.ACCOUNT, GetNormalAttributes_Account());
                Filter userUidFilter = FilterBuilder.EqualTo(userUid);
                IList<ConnectorObject> foundUserObjects =
                    TestHelpers.SearchToList(connector, ObjectClass.ACCOUNT, userUidFilter);
                Assert.AreEqual(1, foundUserObjects.Count);

                // now test for searching with uppercase guid
                userUidFilter = FilterBuilder.EqualTo(new Uid(userUid.GetUidValue().ToUpper()));
                foundUserObjects = TestHelpers.SearchToList(
                    connector, ObjectClass.ACCOUNT, userUidFilter);
                Assert.AreEqual(1, foundUserObjects.Count);

                // now test for searching with lowercase guid
                userUidFilter = FilterBuilder.EqualTo(new Uid(userUid.GetUidValue().ToLower()));
                foundUserObjects = TestHelpers.SearchToList(
                    connector, ObjectClass.ACCOUNT, userUidFilter);
                Assert.AreEqual(1, foundUserObjects.Count);
            }
            finally
            {
                if (userUid != null)
                {
                    DeleteAndVerifyObject(connector, ObjectClass.ACCOUNT, userUid, false, false);
                }
            }
        }

        public Uid CreateAndVerifyObject(ActiveDirectoryConnector connector,
            ObjectClass oclass, ICollection<ConnectorAttribute> attributes)
        {
            // create object
            Uid uid = CreateObject(connector, oclass, attributes);
            VerifyObject(connector, uid, oclass, attributes);
            return uid;
        }

        public Uid CreateObject(ActiveDirectoryConnector connector,
            ObjectClass oclass, ICollection<ConnectorAttribute> attributes)
        {
            // if it exists, remove and recreate
            Filter nameFilter = FilterBuilder.EqualTo(ConnectorAttributeBuilder.Build(
                Name.NAME, ConnectorAttributeUtil.GetNameFromAttributes(attributes).Value));
            ICollection<ConnectorObject> foundObjects = TestHelpers.SearchToList(connector, oclass, nameFilter);
            if ((foundObjects != null) && (foundObjects.Count > 0))
            {
                Assert.AreEqual(1, foundObjects.Count);
                DeleteAndVerifyObject(connector, oclass, foundObjects.ElementAt(0).Uid, false, true);
            }
            
            // create object
            Uid uid = connector.Create(oclass, attributes, null);
            Assert.IsNotNull(uid);

            return uid;
        }

        public void VerifyObject(ActiveDirectoryConnector connector, Uid uid,
            ObjectClass oclass, ICollection<ConnectorAttribute> attributes)
        {
            // find the object
            Filter uidFilter = FilterBuilder.EqualTo(uid);
            ICollection<ConnectorObject> foundObjects = TestHelpers.SearchToList(
                connector, oclass, uidFilter);
            Assert.IsTrue(foundObjects.Count == 1);

            // verify the object
            VerifyObject(attributes, foundObjects.ElementAt(0));
        }


        public Configuration GetConfiguration()
        {
            ActiveDirectoryConfiguration config = new ActiveDirectoryConfiguration();
            config.DomainName = GetProperty(CONFIG_PROPERTY_DOMAIN_NAME);
            config.LDAPHostName = GetProperty(CONFIG_PROPERTY_LDAPHOSTNAME);
            config.DirectoryAdminName = GetProperty(CONFIG_PROPERTY_USER);
            config.DirectoryAdminPassword = GetProperty(CONFIG_PROPERTY_PASSWORD);
            config.SearchContext = GetProperty(CONFIG_PROPERTY_SEARCH_CONTEXT);
            config.SyncDomainController = GetProperty(CONFIG_PROPERTY_SYNC_DOMAIN_CONTROLLER);
            config.SyncGlobalCatalogServer = GetProperty(CONFIG_PROPERTY_GC_DOMAIN_CONTROLLER);
            return config;
        }

        /**
         * NOTES:
         * - cn and @@NAME@@ should be the same.  Test if they are not
         * test for proper behavior if @@name@@ is not supplied
         * - test bogus attributes to like attribut named BogusAttr = hello 
         * or something
         * - test writing to a read only attribute
         * - test attributes with special values such as *, (, ), \
         */

        public ICollection<ConnectorAttribute> GetNormalAttributes_Account()
        {
            ICollection<ConnectorAttribute> attributes = new HashSet<ConnectorAttribute>();
            Random random = new Random();
            Int32 randomNumber = random.Next(10000);

            // the container ... is a fabricated attribute
            attributes.Add(ConnectorAttributeBuilder.Build(
                "ad_container", GetProperty(CONFIG_PROPERTY_CONTAINER)));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "userPassword", "secret"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "sAMAccountName", "nunitUser" + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "givenName", "nunit"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "sn", "TestUser" + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "displayName", "nunit test user " + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                Name.NAME, "cn=nunit" + randomNumber + "," +
                GetProperty(CONFIG_PROPERTY_CONTAINER)));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "mail", "nunitUser" + randomNumber + "@some.com"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "otherHomePhone", "512.555.1212", "512.123.4567"));
            return attributes;
        }

        public ICollection<ConnectorAttribute> GetAllAttributes_Account()
        {
            ICollection<ConnectorAttribute> attributes = new HashSet<ConnectorAttribute>();
            Random random = new Random();
            Int32 randomNumber = random.Next(10000);

            // the container ... is a fabricated attribute
            attributes.Add(ConnectorAttributeBuilder.Build(
                "ad_container", GetProperty(CONFIG_PROPERTY_CONTAINER)));
            GuardedString password = new GuardedString();
            foreach (char c in "secret")
            {
                password.AppendChar(c);
            }
            attributes.Add(ConnectorAttributeBuilder.BuildPassword(
                password));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "sAMAccountName", "nunit" + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "givenName", "nunit"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "sn", "TestUser" + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "displayName", "nunit test user " + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "mail", "nunitUser" + randomNumber + "@some.com"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "telephoneNumber", "333-547-8453"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "msExchHomeServerName", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "employeeID", "1234567"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "division", "Identity Services"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "mobile", "554-210-8631"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "mDBOverQuotaLimit", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "middleName", "testCase"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "description", "This user was created as a test case for the AD Connector"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "mDBOverHardQuotaLimit", ""));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "mDBUseDefaults", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "department", "Connector Affairs"));
            // for manager, it looks like the manager has to exist
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "manager", "Some Guy"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "mDBStorageQuota", ""));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "mailNickName", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "title", "Manager"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "initials", "XYZ"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "homeMTA", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "co", "United States"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                "homeMDB", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "company", "NUnit Test Company"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "facsimileTelephoneNumber", "111-222-3333"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "homePhone", "222-333-4444"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "directoryEntryWS_PasswordExpired", ""));
            attributes.Add(ConnectorAttributeBuilder.Build(
                            "streetAddress", "12345 Some Street"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                            "l", "Austin"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                            "st", "Texas"));
            attributes.Add(ConnectorAttributeBuilder.Build(
                            "postalCode", "78717"));
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "AccountLocked", ""));

            // used to be 'Terminal Services Initial Program'
            attributes.Add(ConnectorAttributeBuilder.Build(
                            TerminalServicesUtils.TS_INITIAL_PROGRAM, "myprog.exe"));

            // used to be 'Terminal Services Initial Program Directory'
            attributes.Add(ConnectorAttributeBuilder.Build(
                            TerminalServicesUtils.TS_INITIAL_PROGRAM_DIR, "c:\\nunittest\\dir"));

            // unknown ...
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "Terminal Services Inherit Initial Program", true));

            // used to be 'Terminal Services Allow Logon' - defaults to false, so testing true
            attributes.Add(ConnectorAttributeBuilder.Build(
                            TerminalServicesUtils.TS_ALLOW_LOGON, 1));

            // used to be 'Terminal Services Active Session Timeout'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_MAX_CONNECTION_TIME, 10000));

            // used to be 'Terminal Services Disconnected Session Timeout'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_MAX_DISCONNECTION_TIME, 20000));

            // used to be 'Terminal Services Idle Timeout'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_MAX_IDLE_TIME, 30000));

            // used to be 'Terminal Services Connect Client Drives At Logon'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_CONNECT_CLIENT_DRIVES_AT_LOGON, 1));

            // used to be 'Terminal Services Connect Client Printers At Logon'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_CONNECT_CLIENT_PRINTERS_AT_LOGON, 1));

            // used to be 'Terminal Services Default To Main Client Printer'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_DEFAULT_TO_MAIN_PRINTER, 1));

            // used to be 'Terminal Services End Session On Timeout Or Broken Connection'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_BROKEN_CONNECTION_ACTION, 1));

            // used to be 'Terminal Services Allow Reconnect From Originating Client Only'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_RECONNECTION_ACTION, 1));

            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "Terminal Services Callback Settings", ""));

            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "Terminal Services Callback Phone Number", ""));

            // used to be 'Terminal Services Remote Control Settings'
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_ENABLE_REMOTE_CONTROL, 1));

            // used to be 'Terminal Services User Profile
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_PROFILE_PATH, "\\My Profile"));

            // used to be 'Terminal Services Local Home Directory
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_HOME_DIRECTORY, "\\My Home Dir"));

            // used to be 'Terminal Services Home Directory Drive
            attributes.Add(ConnectorAttributeBuilder.Build(
                TerminalServicesUtils.TS_HOME_DRIVE, "C:"));

            // uSNChanged should be read only
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "uSNChanged", ""));
            // objectGUID should be read only
            //            attributes.Add(ConnectorAttributeBuilder.Build(
            //                            "objectGUID", ""));


            // now set name operational attribute
            attributes.Add(ConnectorAttributeBuilder.Build(
                Name.NAME, "cn=nunit" + randomNumber + "," +
                GetProperty(CONFIG_PROPERTY_CONTAINER)));


            /*
            // a few attributes not used in IDM
             
            // country code is not returned by default
            attributes.Add(ConnectorAttributeBuilder.Build(
                "countryCode", 23));
            
            */
            return attributes;
        }

        public ICollection<ConnectorAttribute> GetNormalAttributes_Group()
        {
            ICollection<ConnectorAttribute> attributes = new List<ConnectorAttribute>();
            Random random = new Random();
            Int32 randomNumber = random.Next(10000);

            // the container ... is a fabricated attribute
            attributes.Add(ConnectorAttributeBuilder.Build(
                "ad_container", GetProperty(CONFIG_PROPERTY_CONTAINER)));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "displayName", "nunit test group" + randomNumber + "," +
                GetProperty(CONFIG_PROPERTY_CONTAINER)));
            attributes.Add(ConnectorAttributeBuilder.Build(
                "description", "Original Description" + randomNumber));
            attributes.Add(ConnectorAttributeBuilder.Build(
                Name.NAME, "cn=nunitGroup" + randomNumber + "," +
                GetProperty(CONFIG_PROPERTY_CONTAINER)));
            return attributes;
        }

        private static void VerifyObject(ICollection<ConnectorAttribute> requestedAttributes,
            ConnectorObject returnedObject)
        {

            // for now, skipping values that are very difficult to 
            // determine equality ... or they are not returned like
            // 'userPassword'.
            ICollection<String> skipAttributeNames = new List<String>();
            skipAttributeNames.Add("USERPASSWORD");
            skipAttributeNames.Add(OperationalAttributes.PASSWORD_NAME);
            skipAttributeNames.Add(OperationalAttributes.CURRENT_PASSWORD_NAME);

            ICollection<String> ldapStringAttributes = new List<String>();
            ldapStringAttributes.Add("AD_CONTAINER");
            ldapStringAttributes.Add(Name.NAME);
            ldapStringAttributes.Add(PredefinedAttributes.GROUPS_NAME);
            ldapStringAttributes.Add(PredefinedAttributes.ACCOUNTS_NAME);

            // for each attribute in the connector object ...
            foreach (ConnectorAttribute attribute in requestedAttributes)
            {
                ConnectorAttribute returnedAttribute = returnedObject.GetAttributeByName(
                    attribute.Name);

                if (skipAttributeNames.Contains(attribute.Name.ToUpper()))
                {
                    Trace.TraceWarning("Skipping comparison of attribute {0}",
                        attribute.Name);
                    Trace.TraceWarning("requested values were:");
                    foreach (Object requestedValueObject in attribute.Value)
                    {
                        Trace.TraceWarning(requestedValueObject.ToString());
                    }
                    if (returnedAttribute == null)
                    {
                        Trace.TraceWarning("<null> no {0} attribute was returned",
                            attribute.Name);
                    }
                    else
                    {
                        Trace.TraceWarning("returned values were:");
                        foreach (Object returnedValueObject in returnedAttribute.Value)
                        {
                            Trace.TraceWarning(returnedValueObject.ToString());
                        }
                    }
                    continue;
                }

                Assert.IsNotNull(returnedAttribute);

                // for each value in the attribute ...
                foreach (Object requestedValueObject in attribute.Value)
                {
                    // order of multivalue attributes is not gauranted, so check
                    // all values of the returned object against the value
                    // also attributes like the ldap 'objectclass' might return
                    // more values than I set ... (set User, return user, top, inetorgperson)
                    Boolean foundValue = false;
                    foreach (Object returnedValueObject in returnedAttribute.Value)
                    {
                        Object lhs = requestedValueObject;
                        Object rhs = returnedValueObject;

                        // if its an ldap string, put it in a standard form
                        if (ldapStringAttributes.Contains(attribute.Name.ToUpper()))
                        {
                            Assert.That(requestedValueObject is String);
                            Assert.That(returnedValueObject is String);
                            lhs = ActiveDirectoryUtils.NormalizeLdapString((String)requestedValueObject);
                            rhs = ActiveDirectoryUtils.NormalizeLdapString((String)returnedValueObject);
                            /*
                            // if either of them start with a server name, take it off
                            // it's not important to the comparison
                            string []lhsParts = ((string)lhs).Split('/');
                            Assert.LessOrEqual(lhsParts.Length, 2);
                            lhs = (lhsParts.Length) == 1 ? lhsParts[0] : lhsParts[1];
                            string[] rhsParts = ((string)rhs).Split('/');
                            Assert.LessOrEqual(rhsParts.Length, 2);
                            lhs = (rhsParts.Length) == 1 ? rhsParts[0] : rhsParts[1];
                            */
                        }

                        if (lhs.Equals(rhs))
                        {
                            foundValue = true;
                            break;
                        }
                    }
                    Assert.IsTrue(foundValue,
                        String.Format("Could not find value {0} for attribute named {1}",
                        requestedValueObject, attribute.Name));
                }
            }
        }

        // this needs to be replaced by the real one.
        public string GetProperty(string propertyName)
        {
            string x = TestHelpers.GetProperty(propertyName, null);
            return x;
        }

        public ConnectorObject GetConnectorObjectFromUid(
            ActiveDirectoryConnector connector, ObjectClass oclass, Uid uid)
        {
            return GetConnectorObjectFromUid(connector, oclass, uid, null);
        }

        public ConnectorObject GetConnectorObjectFromUid(
            ActiveDirectoryConnector connector, ObjectClass oclass, Uid uid, 
            OperationOptions options)
        {
            // get sid to check permissions
            Filter uidFilter = FilterBuilder.EqualTo(uid);
            ICollection<ConnectorObject> objects = TestHelpers.SearchToList(connector,
                oclass, uidFilter, options);
            Assert.AreEqual(1, objects.Count);
            return objects.ElementAt(0);
        }

        public ICollection<String> GetDefaultAttributesToGet(ObjectClass oclass)
        {
            ICollection<string> attributesToGet = new HashSet<String>();

            ActiveDirectoryConnector connector = new ActiveDirectoryConnector();
            connector.Init(GetConfiguration());
            Schema schema = connector.Schema();
            ObjectClassInfo ocInfo = schema.FindObjectClassInfo(oclass.GetObjectClassValue());
            Assert.IsNotNull(ocInfo);

            foreach (ConnectorAttributeInfo caInfo in ocInfo.ConnectorAttributeInfos)
            {
                if (caInfo.IsReturnedByDefault)
                {
                    attributesToGet.Add(caInfo.Name);
                }
            }

            return attributesToGet;
        }
    }
}
