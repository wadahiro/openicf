/**
 * 
 */
package org.identityconnectors.db2;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.*;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.*;
import org.identityconnectors.framework.api.operations.UpdateApiOp.Type;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.*;
import org.identityconnectors.framework.test.TestHelpers;
import org.junit.*;


/**
 * Test for {@link DB2Connector}
 * @author kitko
 *
 */
public class DB2ConnectorTest {
	private static DB2Configuration testConf;
    private static ConnectorFacade facade;

	/**
	 * Setup for all tests
	 */
	@BeforeClass
	public static void setupClass(){
		testConf = DB2ConfigurationTest.createTestConfiguration();
		facade = getFacade();
	}
	
    private static ConnectorFacade getFacade() {
        ConnectorFacadeFactory factory = ConnectorFacadeFactory.getInstance();
        APIConfiguration apiCfg = TestHelpers.createTestConfiguration(DB2Connector.class, testConf);
        return factory.newInstance(apiCfg);
    }
    
    
    /**
     * Just call test
     */
    @Test
    public void testTest(){
    	getFacade().test();
    }
    
    /**
     * Test schema api
     */
    @Test
    public void testSchemaApi() {
        Schema schema = facade.schema();
        // Schema should not be null
        assertNotNull(schema);
        Set<ObjectClassInfo> objectInfos = schema.getObjectClassInfo();
        assertNotNull(objectInfos);
        assertEquals(1, objectInfos.size());
        ObjectClassInfo objectInfo = (ObjectClassInfo) objectInfos.toArray()[0];
        assertNotNull(objectInfo);
        // the object class has to ACCOUNT_NAME
        assertEquals(ObjectClass.ACCOUNT_NAME, objectInfo.getType());
        // iterate through AttributeInfo Set
        Set<AttributeInfo> attInfos = objectInfo.getAttributeInfo();
        
        assertNotNull(AttributeInfoUtil.find(Name.NAME, attInfos));
    }
    
	/**
	 * test successful Authenticate
	 */
	@Test
	public void testAuthenticateSuc(){
		String username = getTestRequiredProperty("testUser");
		String password = getTestRequiredProperty("testPassword");
		Map<String, Object> emptyMap = Collections.emptyMap();
		facade.authenticate(username, new GuardedString(password.toCharArray()),new OperationOptions(emptyMap));
	}
	
	/**
	 * Test fail of Authenticate
	 */
	@Test(expected=RuntimeException.class)
	public void testAuthenticateFail(){
		String username = "undefined";
		String password = "testPassword";
		Map<String, Object> emptyMap = Collections.emptyMap();
		facade.authenticate(username, new GuardedString(password.toCharArray()),new OperationOptions(emptyMap));
	}

	static Connection createTestConnection() throws Exception{
		DB2Configuration conf = DB2ConfigurationTest.createTestConfiguration();
		return conf.createAdminConnection();
	}

	static String getTestRequiredProperty(String name){
	    String value = TestHelpers.getProperty(name,null);
	    if(value == null){
	        throw new IllegalArgumentException("Property named [" + name + "] is not defined");
	    }
	    return value;
	}
	
	/**
	 * test create
	 */
	@Test
	public void testCreate(){
		String username = getTestRequiredProperty("testUser");
		Map<String, Object> emptyMap = Collections.emptyMap();
		Set<Attribute> attributes = new HashSet<Attribute>();
		attributes.add(new Name(username));
		attributes.add(AttributeBuilder.buildPassword(new char[]{'a','b','c'}));
		attributes.add(AttributeBuilder.build("grants","CONNECT ON DATABASE"));
		Uid uid = null;
		try{
			uid = facade.create(ObjectClass.ACCOUNT, attributes, new OperationOptions(emptyMap));
			assertNotNull(uid);
		}
		catch(AlreadyExistsException e){
			facade.delete(ObjectClass.ACCOUNT, new Uid(username), new OperationOptions(emptyMap));
			uid = facade.create(ObjectClass.ACCOUNT, attributes, new OperationOptions(emptyMap));
			assertNotNull(uid);
		}
		//find user
		uid = findUser(username);
		assertNotNull(uid);
	}
	
	private Uid createTestUser(){
		String username = getTestRequiredProperty("testUser");
		Map<String, Object> emptyMap = Collections.emptyMap();
		Set<Attribute> attributes = new HashSet<Attribute>();
		attributes.add(new Name(username));
		attributes.add(AttributeBuilder.buildPassword(new char[]{'a','b','c'}));
		attributes.add(AttributeBuilder.build("grants","CONNECT ON DATABASE"));
		Uid uid = null;
		try{
			uid = facade.create(ObjectClass.ACCOUNT, attributes, new OperationOptions(emptyMap));
			assertNotNull(uid);
			return uid;
		}
		catch(AlreadyExistsException e){
			return new Uid(username);
		}
		
	}
	
	/**
	 * Test delete of user
	 */
	@Test
	public void testDelete(){
		String username = TestHelpers.getProperty("testUser","TEST");
		Map<String, Object> emptyMap = Collections.emptyMap();
		Set<Attribute> attributes = new HashSet<Attribute>();
		attributes.add(new Name(username));
		attributes.add(AttributeBuilder.buildPassword(new char[]{'a','b','c'}));
		Uid uid = findUser(username);
		if(uid == null){
			uid = facade.create(ObjectClass.ACCOUNT, attributes, new OperationOptions(emptyMap));
			assertNotNull(uid);
		}
		facade.delete(ObjectClass.ACCOUNT, new Uid(username), new OperationOptions(emptyMap));
		uid = findUser(username);
		assertNull("User not deleted",uid);
	}
	
	private Uid findUser(String name){
        final Uid expected = new Uid(name);
        FindUidObjectHandler handler = new FindUidObjectHandler(expected);
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(expected), handler, null);
        final Uid actual = handler.getFoundUID();
        return actual;
	}
	
    /**
     * test find by uid 
     */
    @Test
    public void testFindUserByUid() {
    	String username = TestHelpers.getProperty("testUser","TEST");
        createTestUser();
        final Uid expected = new Uid(username);
        FindUidObjectHandler handler = new FindUidObjectHandler(expected);
        // attempt to find the newly created object..
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(expected), handler, null);
        assertTrue("The testuser was not found", handler.found);
        final Uid actual = handler.getFoundUID();
        assertNotNull(actual);
        assertTrue(actual.is(expected.getName()));  
     }
    
    /**
     * Test find by end with operator
     */
    @Test
    public void testFindUserByEndWith() {
    	String username = TestHelpers.getProperty("testUser","TEST");
        createTestUser();
        final Attribute expected = AttributeBuilder.build(Name.NAME, username);
        FindUidObjectHandler handler = new FindUidObjectHandler(new Uid(username));
        // attempt to find the newly created object..
        facade.search(ObjectClass.ACCOUNT, new EndsWithFilter(expected), handler, null);
        assertTrue("The user was not found", handler.found);
        final ConnectorObject actual = handler.getFoundObject();
        assertNotNull(actual);
        assertEquals("Expected user is not same",username, AttributeUtil.getAsStringValue(actual.getName()));
     }


    /**
     * test find by start with operator
     */
    @Test
    public void testFindUserByStartWith() {
    	String username = TestHelpers.getProperty("testUser","TEST");
        createTestUser();
        final Attribute expected = AttributeBuilder.build(Name.NAME, username);
        FindUidObjectHandler handler = new FindUidObjectHandler(new Uid(username));
        // attempt to find the newly created object..
        facade.search(ObjectClass.ACCOUNT, new StartsWithFilter(expected), handler, null);
        assertTrue("The user was not found", handler.found);
        final ConnectorObject actual = handler.getFoundObject();
        assertNotNull(actual);
        assertEquals("Expected user is not same",username, AttributeUtil.getAsStringValue(actual.getName()));
     }
    
    /**
     * Test find by uid and check grants attribute
     */
    @Test
    public void testFindCheckAttributes(){
    	String username = TestHelpers.getProperty("testUser","TEST");
    	final Uid expected = new Uid(username);
        createTestUser();
        FindUidObjectHandler handler = new FindUidObjectHandler(new Uid(username));
        OperationOptionsBuilder builder = new OperationOptionsBuilder();
        builder.setAttributesToGet(Arrays.asList(Name.NAME,DB2Connector.USER_AUTH_GRANTS));
        OperationOptions options = builder.build();
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(expected), handler, options);
        assertTrue("The user was not found", handler.found);
        final ConnectorObject actual = handler.getFoundObject();
        assertNotNull(actual);
        final Attribute grants = actual.getAttributeByName(DB2Connector.USER_AUTH_GRANTS);
        assertNotNull(grants);
        assertNotNull(grants.getValue());
    }
    
    /**
     * Testing update
     */
    @Test
    public void testUpdate(){
		String username = getTestRequiredProperty("testUser");
    	final Uid uid = new Uid(username);
        createTestUser();
        FindUidObjectHandler handler = new FindUidObjectHandler(new Uid(username));
        OperationOptionsBuilder builder = new OperationOptionsBuilder();
        builder.setAttributesToGet(Arrays.asList(Name.NAME,DB2Connector.USER_AUTH_GRANTS));
        OperationOptions options = builder.build();
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(uid), handler, options);
        ConnectorObject actual = handler.getFoundObject();
        assertNotNull(actual);
        Attribute grants1 = actual.getAttributeByName(DB2Connector.USER_AUTH_GRANTS);
        Attribute oldGrants = grants1; 
        grants1 = AttributeBuilder.build(DB2Connector.USER_AUTH_GRANTS,"LOAD ON DATABASE,SELECT ON TABLE SYSIBM.DUAL");
        Set<Attribute> attributes = new HashSet<Attribute>();
        attributes.add(uid);
        attributes.add(grants1);
        Map<String, Object> emptyMap = Collections.emptyMap();
        
        //Test add
        facade.update(Type.ADD, ObjectClass.ACCOUNT,attributes, new OperationOptions(emptyMap));
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(uid), handler, options);
        actual = handler.getFoundObject();
        String newGrantsValue = (String) actual.getAttributeByName(DB2Connector.USER_AUTH_GRANTS).getValue().get(0);
		assertTrue(newGrantsValue.contains("LOAD ON DATABASE"));
        assertTrue(newGrantsValue.contains("CONNECT ON DATABASE"));
        assertTrue(newGrantsValue.contains("SELECT ON SYSIBM.DUAL"));
        
        //Test replace
        handler.clear();
        attributes.clear();
        attributes.add(uid);
        attributes.add(AttributeBuilder.build(DB2Connector.USER_AUTH_GRANTS,"SELECT ON TABLE SYSIBM.DUAL"));
        facade.update(Type.REPLACE,ObjectClass.ACCOUNT,attributes, new OperationOptions(emptyMap));
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(uid), handler, options);
        actual = handler.getFoundObject();
        newGrantsValue = (String) actual.getAttributeByName(DB2Connector.USER_AUTH_GRANTS).getValue().get(0);
		assertFalse(newGrantsValue.contains("LOAD ON DATABASE"));
        assertTrue(newGrantsValue.contains("CONNECT ON DATABASE"));
        assertTrue(newGrantsValue.contains("SELECT ON SYSIBM.DUAL"));
        
        //Test delete
        handler.clear();
        facade.update(Type.DELETE,ObjectClass.ACCOUNT,attributes, new OperationOptions(emptyMap));
        facade.search(ObjectClass.ACCOUNT, new EqualsFilter(uid), handler, options);
        actual = handler.getFoundObject();
        newGrantsValue = (String) actual.getAttributeByName(DB2Connector.USER_AUTH_GRANTS).getValue().get(0);
		assertFalse(newGrantsValue.contains("LOAD ON DATABASE"));
        assertTrue(newGrantsValue.contains("CONNECT ON DATABASE"));
        assertFalse(newGrantsValue.contains("SELECT ON SYSIBM.DUAL"));
        
        //Reset to old value
        attributes.clear();
        attributes.add(uid);
        attributes.add(oldGrants);
        facade.update(Type.REPLACE,ObjectClass.ACCOUNT,attributes, new OperationOptions(emptyMap));
    }
    
    
    
	
    
    private static class FindUidObjectHandler implements ResultsHandler {
        private ConnectorObject connectorObject = null;
        private boolean found = false;
        private final Uid uid;
        
        /**
         * @param uid
         */
        public FindUidObjectHandler(Uid uid) {
            this.uid = uid;
        }
        
        ConnectorObject getFoundObject() {
			return connectorObject;
		}

		Uid getFoundUID() {
            return connectorObject != null ? connectorObject.getUid() : null;
        }
		
		void clear(){
			found = false;
			connectorObject = null;
		}
        

        public boolean handle(ConnectorObject obj) {
            System.out.println("Object: " + obj);
            if (obj.getUid().equals(uid)) {
                found = true;
                connectorObject = obj;
                return false;
            }
            return true;
        }
    }
    
    
    
    
    
    
	
	

	
}
