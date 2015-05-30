package il.ac.tau.cs.databases.atlas.db;

import static org.junit.Assert.*;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DBQueriesTest {
	
	DBQueries queries = new DBQueries();
	String Tester = "Rony" ;
	
	@Before
	public void setUp() throws Exception {
		// Initialize connection according to debugger
		if (Tester == "Rony") {
			DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06","127.0.0.1", "3306", "dbmysql06");
		}
		
	}
	
	@Test
	public void isRegisteredUserTest() throws AtlasServerException {
		User existingUser = new User("rony", "0000", new Date(), 0);
		User nonExistingUser = new User("johnDo", "", new Date(), 0);
		User fetchedUser = queries.fetchUser(existingUser);
		assertEquals("UserName", fetchedUser.getUsername(), existingUser.getUsername());
		assertEquals("Password", fetchedUser.getPassword(), existingUser.getPassword());		
		assertNull(queries.fetchUser(nonExistingUser));
	}
	
	@Test
	public void registerUserTest() throws AtlasServerException {
		User existingUser = new User("rony", "0000", new Date(), 1);
		User nonExistingUser = new User("newUser", "password", new Date(), 1);
		assertFalse(queries.registerUser(existingUser));
		assertTrue(queries.registerUser(nonExistingUser));
		User fetchedUser = queries.fetchUser(nonExistingUser);
		assertEquals("UserName", fetchedUser.getUsername(), nonExistingUser.getUsername());
	}

}
