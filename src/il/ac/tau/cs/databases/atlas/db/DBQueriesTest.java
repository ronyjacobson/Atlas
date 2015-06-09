package il.ac.tau.cs.databases.atlas.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DBQueriesTest {

	DBQueries queries = new DBQueries();
	String tester = "Rony";

	@Before
	public void setUp() throws Exception {
		// Initialize connection according to debugger
		if (tester == "Rony") {
			DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06",
					"127.0.0.1", "3306", "dbmysql06");
		}

	}

	// @Test
	public void isRegisteredUserTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		User existingUser = new User("rony", "0000", new Date(), 0, true);
		User nonExistingUser = new User("johnDo", "", new Date(), 0, false);
		User fetchedUser = queries.fetchUser(existingUser);
		assertEquals("UserName", fetchedUser.getUsername(),
				existingUser.getUsername());
		assertEquals("Password", fetchedUser.getPassword(),
				existingUser.getPassword());
		assertNull(queries.fetchUser(nonExistingUser));
	}

	// @Test
	public void registerUserTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		User existingUser = new User("rony", "0000", new Date(), 1, true);
		User nonExistingUser = new User("newUser3", "password", new Date(), 1, false);
		try {
			assertFalse(queries.registerUser(existingUser));
		} catch (AtlasServerException e) {

		}
		assertTrue(queries.registerUser(nonExistingUser));
		User fetchedUser = queries.fetchUser(nonExistingUser);
		assertEquals("UserName", fetchedUser.getUsername(),
				nonExistingUser.getUsername());
	}

	// @Test
	public void getGeoLocationsHashMapTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		assertTrue(Queries.locationsMap.isEmpty());
		queries.getGeoLocationsHashMap();
		assertFalse(Queries.locationsMap.isEmpty());
	}

	// @Test
	public void getCategoriesTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		List<String> cat = queries.getAllCategoriesNames();
		assertFalse(cat.isEmpty());
		System.out.println(cat.toString());
	}

	// @Test
	public void getResultsTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		Main.user = new User(1, "erer", "Sfsf", new Date(), 5, false);
		List<Result> res = queries.getResults(1900, 1950, "Actores");
		assertFalse(res.isEmpty());
		res = queries.getResults(1900, 1950, "Actores", "dsd");
		assertFalse(res.isEmpty());
		System.out.println("Results: \n" + res.toString());
	}

	// @Test
	public void SearchResultsOnlyByNameTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		Main.user = new User(1, "erer", "Sfsf", new Date(), 5, false);
		List<Result> res = queries.getResults("dsd");
		assertFalse(res.isEmpty());
		res = queries.getResults("sdsdsdsdsdsdsdsdsd");
		assertTrue(res.isEmpty());
	}

	// @Test
	public void AddPerson() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		Queries.categoriesMap.put("Actores", 1);
		Main.user = new User(1, "erer", "Sfsf", new Date(), 5, false);
		try {
			queries.addNew("NewPersonTest3", "Actores", new Date(), 1,
					new Date(), 1, "url", true);
		} catch (AtlasServerException e) {
		}
		queries.addNew("NewPersonTest5", "Actores", new Date(), 1, new Date(),
				1, "url", true);
	}

	// @Test
	public void AddFavorites() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		Main.user = new User(1, "erer", "Sfsf", new Date(), 5, false);
		List<String> fav= new ArrayList<String>();
		fav.add("2");
		fav.add("4");
		queries.storeFavoriteIDs(fav);
	}

	@SuppressWarnings("deprecation")
	//@Test
	public void getResultsByDatesTest() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		Main.user = new User(1, "erer", "Sfsf", new Date(), 5, true);
		@SuppressWarnings("deprecation")
		List<Result> res = queries.getResults(new Date(0, 5, 1),
				new Date(200, 5, 1));
		assertFalse(res.isEmpty());
		res = queries.getResults(new Date(1300, 5, 1), new Date(1500,
				5, 1));
		assertTrue(res.isEmpty());
	}

	@Test
	public void getFavs() throws AtlasServerException {
		if (tester != "Rony") {
			return;
		}
		Main.user = new User(1, "erer", "Sfsf", new Date(), 5, true);
		List<Result> res = queries.getFavorites();
		assertFalse(res.isEmpty());
		Main.user = new User(67, "erer", "Sfsf", new Date(), 5, true);
		res = queries.getFavorites();
		assertTrue(res.isEmpty());
	}
}
