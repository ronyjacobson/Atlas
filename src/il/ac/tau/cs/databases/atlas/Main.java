package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.State;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.db.DBQueries;
import il.ac.tau.cs.databases.atlas.db.MockQueries;
import il.ac.tau.cs.databases.atlas.db.Queries;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.awt.Toolkit;

public class Main {

	// DB queries object
	public static final Queries queries = new DBQueries();
	public static User user;
	
	/**
	 * Initialize the program parameters and load its state.
	 * 
	 * @throws Exception
	 */
	private static void initialize() throws Exception {
		// Get the user's screen size
		GrapicUtils.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Load earlier state
		try {
			// Initialize DB
			DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06", "127.0.0.1", "3306", "dbmysql06");
			State.autoLoad();
		} catch (Exception e) {
			// Throw exception to main screen
			throw e;
		}
	}

	/**
	 * Main running method
	 */
	public static void main(String[] args) {
		try {
			// Initialize the program
			initialize();
			// Show splash screen
			new Splash();
		} catch (Exception e) {
			// TODO Handle Exception
			e.printStackTrace();
		}
	}
}
