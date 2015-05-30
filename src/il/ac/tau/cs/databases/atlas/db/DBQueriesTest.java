package il.ac.tau.cs.databases.atlas.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;

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
	public void isRegisteredUserTest() {
		User existingUser = new User("rony", "", new Date(), "");
		User nonExistingUser = new User("johnDo", "", new Date(), "");
		assertTrue(queries.isRegisteredUser(existingUser));
		assertFalse(queries.isRegisteredUser(nonExistingUser));
	}

}
