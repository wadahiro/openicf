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
package org.identityconnectors.ldap.search;

import static org.identityconnectors.common.CollectionUtil.newSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoUtil;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.identityconnectors.framework.common.objects.QualifiedUid;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterBuilder;
import org.identityconnectors.ldap.LdapConfiguration;
import org.identityconnectors.ldap.LdapConnection;
import org.identityconnectors.ldap.LdapConnector;
import org.identityconnectors.ldap.LdapConnectorTestBase;
import org.identityconnectors.test.common.TestHelpers;
import org.identityconnectors.test.common.ToListResultsHandler;
import org.junit.Test;

public class LdapSearchTests extends LdapConnectorTestBase {

    // TODO operational attributes.
    // TODO LDAP directory attributes (entryDN, etc.).

    @Override
    protected boolean restartServerAfterEachTest() {
        return false;
    }

    @Test
    public void testLdapFilter() {
        LdapConnection conn = new LdapConnection(newConfiguration());

        LdapFilter filter = LdapFilter.forEntryDN(BUGS_BUNNY_DN);
        ToListResultsHandler handler = new ToListResultsHandler();
        new LdapSearch(conn, ObjectClass.ACCOUNT, filter, new OperationOptionsBuilder().build()).execute(handler);
        assertEquals(1, handler.getObjects().size());

        filter = filter.withNativeFilter("(foo=bar)");
        handler = new ToListResultsHandler();
        new LdapSearch(conn, ObjectClass.ACCOUNT, filter, new OperationOptionsBuilder().build()).execute(handler);
        assertTrue(handler.getObjects().isEmpty());
    }

    @Test
    public void testLdapFilterWithNonExistingEntryDN() {
        LdapFilter filter = LdapFilter.forEntryDN("dc=foo,dc=bar");

        // VLV index.
        LdapConfiguration config = newConfiguration();
        config.setUseBlocks(true);
        config.setUsePagedResultControl(false);
        searchExpectingNoResult(config, filter);

        // Simple paged results.
        config = newConfiguration();
        config.setUseBlocks(true);
        config.setUsePagedResultControl(true);
        searchExpectingNoResult(config, filter);

        // No paging.
        config = newConfiguration();
        config.setUseBlocks(false);
        searchExpectingNoResult(config, filter);
    }


    @Test
    public void testLdapFilterWithInvalidEntryDN() {
        LdapFilter filter = LdapFilter.forEntryDN("dc=foo,,");

        // VLV index.
        LdapConfiguration config = newConfiguration();
        config.setUseBlocks(true);
        config.setUsePagedResultControl(false);
        searchExpectingNoResult(config, filter);

        // Simple paged results.
        config = newConfiguration();
        config.setUseBlocks(true);
        config.setUsePagedResultControl(true);
        searchExpectingNoResult(config, filter);

        // No paging.
        config = newConfiguration();
        config.setUseBlocks(false);
        searchExpectingNoResult(config, filter);
    }

    private void searchExpectingNoResult(LdapConfiguration config, LdapFilter filter) {
        LdapConnection conn = new LdapConnection(config);
        ToListResultsHandler handler = new ToListResultsHandler();
        // Should not fail with NameNotFoundException or InvalidNameException.
        new LdapSearch(conn, ObjectClass.ACCOUNT, filter, new OperationOptionsBuilder().build()).execute(handler);
        assertTrue(handler.getObjects().isEmpty());
    }

    @Test
    public void testCanCancelSearch() {
        // VLV Index.
        LdapConfiguration config = newConfiguration();
        config.setBaseContexts(ACME_DN, BIG_COMPANY_DN);
        config.setUseBlocks(true);
        config.setUsePagedResultControl(false);
        searchExpectingSingleResult(config);

        // Simple paged results.
        config = newConfiguration();
        config.setBaseContexts(ACME_DN, BIG_COMPANY_DN);
        config.setUseBlocks(true);
        config.setUsePagedResultControl(true);
        searchExpectingSingleResult(config);

        // No paging.
        config = newConfiguration();
        config.setBaseContexts(ACME_DN, BIG_COMPANY_DN);
        config.setUseBlocks(false);
        searchExpectingSingleResult(config);
    }

    private void searchExpectingSingleResult(LdapConfiguration config) {
        LdapConnection conn = new LdapConnection(config);
        FirstOnlyResultsHandler handler = new FirstOnlyResultsHandler();
        new LdapSearch(conn, ObjectClass.ACCOUNT, null, new OperationOptionsBuilder().build()).execute(handler);
        handler.assertSingleResult();
    }

    @Test
    public void testSimplePagedSearch() {
        LdapConfiguration config = newConfiguration();
        config.setUseBlocks(true);
        config.setUsePagedResultControl(true);
        ConnectorFacade facade = newFacade(config);

        List<ConnectorObject> objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null);
        assertNotNull(getObjectByName(objects, BUGS_BUNNY_DN));
        assertNotNull(getObjectByName(objects, USER_0_DN));
        // 1000 is the default search size limit for OpenDS.
        assertTrue(objects.size() > 1000);
    }

    @Test
    public void testVlvIndexSearch() {
        LdapConfiguration config = newConfiguration();
        config.setBaseContexts(EXAMPLE_COM_DN);
        config.setUseBlocks(true);
        config.setUsePagedResultControl(false);
        config.setUidAttribute("entryDN");
        ConnectorFacade facade = newFacade(config);

        List<ConnectorObject> objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null);
        assertNotNull(getObjectByName(objects, USER_0_DN));
        // 1000 is the default search size limit for OpenDS.
        assertTrue(objects.size() > 1000);

        // OpenDS-specific.
        OperationOptionsBuilder builder = new OperationOptionsBuilder();
        builder.setAttributesToGet("debugsearchindex");
        FirstOnlyResultsHandler handler = new FirstOnlyResultsHandler();
        facade.search(ObjectClass.ACCOUNT, null, handler, builder.build());
        String debugsearch = handler.getSingleResult().getAttributeByName("debugsearchindex").getValue().get(0).toString();
        assertTrue(debugsearch.contains("vlv"));
    }

    @Test(expected = ConnectorException.class)
    public void testNoUseBlocks() {
        LdapConfiguration config = newConfiguration();
        config.setUseBlocks(false);
        ConnectorFacade facade = newFacade(config);
        // This should fail, since the search will exceed the maximum number of
        // entries to return.
        TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null);
    }

    @Test
    public void testWithFilter() {
        ConnectorFacade facade = newFacade();
        ConnectorObject bunny = searchByAttribute(facade, ObjectClass.ACCOUNT, new Name(BUGS_BUNNY_DN));
        assertEquals(BUGS_BUNNY_DN, bunny.getName().getNameValue());
    }

    @Test
    public void testAttributesToGet() {
        ConnectorFacade facade = newFacade();
        ConnectorObject object = searchByAttribute(facade, ObjectClass.ACCOUNT, new Name(USER_0_DN), "employeeNumber", "telephoneNumber");

        Set<Attribute> attrs = newSet(object.getAttributes());
        assertTrue(attrs.remove(AttributeUtil.find(Uid.NAME, attrs)));
        assertTrue(attrs.remove(AttributeUtil.find(Name.NAME, attrs)));
        assertTrue(attrs.remove(AttributeUtil.find("employeeNumber", attrs)));
        assertTrue(attrs.remove(AttributeUtil.find("telephoneNumber", attrs)));

        assertTrue(attrs.isEmpty());
    }

    @Test
    public void testAttributesReturnedByDefaultWithNoValueAreNotReturned() {
        LdapConfiguration config = newConfiguration();
        config.setReadSchema(true);
        ConnectorFacade facade = newFacade(config);
        AttributeInfo attr = AttributeInfoUtil.find("givenName", facade.schema().findObjectClassInfo(ObjectClass.ACCOUNT_NAME).getAttributeInfo());
        assertTrue(attr.isReturnedByDefault());

        ConnectorObject object = searchByAttribute(facade, ObjectClass.ACCOUNT, new Name(BUGS_BUNNY_DN));
        assertNull(object.getAttributeByName("givenName"));
    }

    @Test
    public void testAttributesToGetNotPresentInEntryAreEmpty() {
        ConnectorFacade facade = newFacade();
        ConnectorObject object = searchByAttribute(facade, ObjectClass.ACCOUNT, new Name(BUGS_BUNNY_DN), "employeeNumber");

        assertTrue(object.getAttributeByName("employeeNumber").getValue().isEmpty());
    }

    @Test
    public void testScope() {
        ConnectorFacade facade = newFacade();
        // Find an organization to pass in OP_CONTAINER.
        ObjectClass oclass = new ObjectClass("organization");
        ConnectorObject organization = searchByAttribute(facade, oclass, new Name(BIG_COMPANY_DN));

        // There are no accounts directly under the organization...
        OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
        optionsBuilder.setScope(OperationOptions.SCOPE_ONE_LEVEL);
        optionsBuilder.setContainer(new QualifiedUid(oclass, organization.getUid()));
        List<ConnectorObject> objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
        assertTrue(objects.isEmpty());

        // ... but there are some in the organization subtree.
        optionsBuilder.setScope(OperationOptions.SCOPE_SUBTREE);
        objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
        assertFalse(objects.isEmpty());
    }

    @Test
    public void testAccountSearchFilter() {
        ConnectorFacade facade = newFacade();
        // Find an organization to pass in OP_CONTAINER.
        ObjectClass oclass = new ObjectClass("organization");
        ConnectorObject organization = searchByAttribute(facade, oclass, new Name(ACME_DN));

        // First just check that there really are some users.
        OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
        optionsBuilder.setScope(OperationOptions.SCOPE_SUBTREE);
        optionsBuilder.setContainer(new QualifiedUid(oclass, organization.getUid()));
        List<ConnectorObject> objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
        assertNotNull(getObjectByName(objects, BUGS_BUNNY_DN));
        assertNotNull(getObjectByName(objects, ELMER_FUDD_DN));

        LdapConfiguration config = newConfiguration();
        config.setAccountSearchFilter("(uid=" + BUGS_BUNNY_UID + ")");
        facade = newFacade(config);
        objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
        assertEquals(1, objects.size());
        assertNotNull(getObjectByName(objects, BUGS_BUNNY_DN));
    }

    @Test
    public void testAccountSearchFilterOnlyAppliesToAccounts() {
        LdapConfiguration config = newConfiguration();
        config.setAccountSearchFilter("(cn=foobarbaz)");
        ConnectorFacade facade = newFacade(config);

        // If the (cn=foobarbaz) filter above applied, the search would return nothing.
        assertNotNull(searchByAttribute(facade, new ObjectClass("organization"), new Name(ACME_DN)));
    }

    @Test
    public void testMissingParenthesesAddedToAccountSearchFilter() {
        LdapConfiguration config = newConfiguration();
        config.setAccountSearchFilter("uid=" + BUGS_BUNNY_UID); // No parentheses enclosing the filter.
        ConnectorFacade facade = newFacade(config);

        // If parentheses were not added, the search would fail.
        assertNotNull(searchByAttribute(facade, ObjectClass.ACCOUNT, new Name(BUGS_BUNNY_DN)));
    }

    @Test
    public void testMultipleBaseDNs() {
        ConnectorFacade facade = newFacade();

        // This should find accounts from both base DNs.
        List<ConnectorObject> objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null);
        assertNotNull(getObjectByName(objects, BUGS_BUNNY_DN));
        assertNotNull(getObjectByName(objects, USER_0_DN));
    }

    @Test
    public void testBaseDNsOption() {
        LdapConfiguration config = newConfiguration();
        config.setBaseContexts(ACME_DN, SMALL_COMPANY_DN, BIG_COMPANY_DN);
        ConnectorFacade facade = newFacade(config);

        // Specifying both OP_BASE_DNS and OP_CONTAINER is prohibited.
        OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
        optionsBuilder.setOption(LdapConnector.OP_BASE_DNS, new String[] { ACME_DN, SMALL_COMPANY_DN });
        List<ConnectorObject> objects = TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
        assertNotNull(getObjectByName(objects, BUGS_BUNNY_DN));
        assertNotNull(getObjectByName(objects, ELMER_FUDD_DN));
        assertNotNull(getObjectByName(objects, SINGLE_ACCOUNT_DN));
        // We only searched inside Acme and Small Company, not inside Big Company.
        assertNull(getObjectByName(objects, USER_0_UID));
    }

    @Test(expected = ConnectorException.class)
    public void testBaseDNsOptionConflictsWithContainerOption() {
        ConnectorFacade facade = newFacade();
        // Find an organization to pass in OP_CONTAINER.
        ObjectClass oclass = new ObjectClass("organization");
        ConnectorObject organization = searchByAttribute(facade, oclass, new Name(BIG_COMPANY_DN));

        // Specifying both OP_BASE_DNS and OP_CONTAINER is prohibited.
        OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
        optionsBuilder.setOption(LdapConnector.OP_BASE_DNS, new String[] { ACME_DN });
        optionsBuilder.setContainer(new QualifiedUid(oclass, organization.getUid()));
        TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
    }

    @Test(expected = ConnectorException.class)
    public void testBaseDNsFromOptionOnlyAllowedFromConfigBaseDNs() {
        LdapConfiguration config = newConfiguration();
        config.setBaseContexts(ACME_DN);
        ConnectorFacade facade = newFacade(config);

        OperationOptionsBuilder optionsBuilder = new OperationOptionsBuilder();
        // Specifying a base DN for OP_BASE_DNS which is not in LdapConfiguration.getBaseDNs() is prohibited.
        optionsBuilder.setOption(LdapConnector.OP_BASE_DNS, new String[] { BIG_COMPANY_DN });
        TestHelpers.searchToList(facade, ObjectClass.ACCOUNT, null, optionsBuilder.build());
    }

    @Test
    public void testUidAttributeCn() {
        LdapConfiguration config = newConfiguration();
        assertFalse(config.getUidAttribute().equalsIgnoreCase("cn"));
        config.setUidAttribute("cn");
        ConnectorFacade facade = newFacade(config);

        ConnectorObject bunny = searchByAttribute(facade, ObjectClass.ACCOUNT, new Uid(BUGS_BUNNY_CN));
        assertEquals(BUGS_BUNNY_DN, bunny.getName().getNameValue());
    }

    @Test
    public void testUidAttributeEntryDN() {
        LdapConfiguration config = newConfiguration();
        assertFalse(config.getUidAttribute().equalsIgnoreCase("entryDN"));
        config.setUidAttribute("entryDN");
        ConnectorFacade facade = newFacade(config);

        ConnectorObject bunny = searchByAttribute(facade, ObjectClass.ACCOUNT, new Uid(BUGS_BUNNY_DN));
        assertEquals(BUGS_BUNNY_DN, bunny.getName().getNameValue());
    }

    @Test
    public void testSearchObjectClassNotInSchema() {
        ConnectorFacade facade = newFacade();

        // Simplest: try w/o filter.
        List<ConnectorObject> objects = TestHelpers.searchToList(facade, new ObjectClass("country"), null, null);
        ConnectorObject czechRep = getObjectByName(objects, CZECH_REPUBLIC_DN);

        // Try with a name filter. Filtering will be done by the framework, because the LDAP attribute for Name is not known.
        // Also try with options.
        Filter filter = FilterBuilder.equalTo(AttributeBuilder.build(Name.NAME, CZECH_REPUBLIC_DN));
        OperationOptionsBuilder builder = new OperationOptionsBuilder();
        builder.setAttributesToGet("c");
        objects = TestHelpers.searchToList(facade, new ObjectClass("country"), filter, builder.build());
        czechRep = getObjectByName(objects, CZECH_REPUBLIC_DN);
        assertEquals(CZECH_REPUBLIC_C, AttributeUtil.getAsStringValue(czechRep.getAttributeByName("c")));
    }

    private static ConnectorObject getObjectByName(List<ConnectorObject> objects, String name) {
        for (ConnectorObject object : objects) {
            if (name.equals(object.getName().getNameValue())) {
                return object;
            }
        }
        return null;
    }

    private static final class FirstOnlyResultsHandler implements ResultsHandler {

        private final List<ConnectorObject> objects = new ArrayList<ConnectorObject>();

        public boolean handle(ConnectorObject obj) {
            objects.add(obj);
            return false; // We only want the first one.
        }

        public void assertSingleResult() {
            assertEquals(1, objects.size());
        }

        public ConnectorObject getSingleResult() {
            return objects.get(0);
        }
    }
}
