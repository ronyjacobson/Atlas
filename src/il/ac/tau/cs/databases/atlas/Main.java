package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.modal.User;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPool;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.db.connection.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.db.connection.FixedConnectionPool;
import il.ac.tau.cs.databases.atlas.db.queries.DBQueries;
import il.ac.tau.cs.databases.atlas.db.queries.Queries;
import il.ac.tau.cs.databases.atlas.ui.screens.LoginScreen;
import il.ac.tau.cs.databases.atlas.ui.screens.MapScreen;
import il.ac.tau.cs.databases.atlas.ui.screens.SplashScreen;
import il.ac.tau.cs.databases.atlas.ui.utils.GraphicUtils;

import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);
	// DB queries object
	public static final Queries queries = new DBQueries();
	public static User user;
	
	
	//DEBUG
	static boolean SKIP_SPLASH=false;
	static boolean SKIP_LOGIN = false;
	
	
	/**
	 * Initialize the program parameters and load its state.
	 * 
	 * @throws Exception
	 * @param pathToConfFile
	 */
	private static void initialize(String pathToConfFile) throws Exception {
		
		// Get the user's screen size
		logger.debug("Atlas started");
		GraphicUtils.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		// Initialize DB
		Properties prop = readConfFile(pathToConfFile);
		ConnectionPool connectionPool;
		if ("dynamic".equals(prop.getProperty("connectionType"))) {
			 connectionPool = new DynamicConnectionPool();
		} else {
			connectionPool = new FixedConnectionPool();
		}
		connectionPool.initialize(
				prop.getProperty("user"),
				prop.getProperty("password"),
				prop.getProperty("ip"),
				prop.getProperty("port"),
				prop.getProperty("schemaName"));
		ConnectionPoolHolder.INSTANCE.set(connectionPool);
		
	}

	private static Properties readConfFile(String pathToConfFile) throws AtlasServerException {
		Properties prop = new Properties();
		InputStream input = null;
		boolean ok = true;

		try {
			input = new FileInputStream(pathToConfFile);
			prop.load(input);
			if (! prop.containsKey("user")) {
				logger.error("missing 'user'");
				ok = false;
			}

			if (! prop.containsKey("password")) {
				logger.error("missing 'password'");
				ok = false;
			}

			if (! prop.containsKey("schemaName")) {
				logger.error("missing 'schemaName'");
				ok = false;
			}

			if (! prop.containsKey("ip")) {
				logger.error("missing 'ip'");
				ok = false;
			}

			String port = prop.getProperty("port");
			if (port != null) {
				try {
					Integer.parseInt(port);
				} catch (NumberFormatException e) {
					logger.error("'port' must be an integer");
					ok = false;
				}
			}

			String connectionType = prop.getProperty("connectionType");
			if (connectionType == null || !("dynamic".equals(connectionType.toLowerCase()) || "fixed".equals(connectionType.toLowerCase()))) {
				logger.error("'connectionType' must be either 'dynamic' or 'fixed'");
				ok = false;
			}

			if (! ok) {
				throw new AtlasServerException("Invalid configuration file, program will now terminate");
			}

		} catch (IOException ex) {
			logger.error("", ex);
			throw new AtlasServerException("Caught IOException while trying to read configuration file, program will now terminate");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error("", e);
					throw new AtlasServerException("Caught IOException while trying to close configuration file, program will now terminate");
				}
			}
		}
		return prop;
	}

	/**
	 * Main running method
	 */
	public static void main(String[] args) {
		try {
			// Initialize the program
			String pathToConfFile = "config.properties";
			if (args.length > 0) {
				pathToConfFile = args[0];
			}
			
			initialize(pathToConfFile);
			
			if (SKIP_SPLASH) {
				new LoginScreen();
			} else if (SKIP_LOGIN){
				Main.user = new User(2,"Rony", "0000");
				new MapScreen();
			} else {
				try {
					new ArrayList<String>(Main.queries.getAllGeoLocationsNames());
				} catch (AtlasServerException e){
					JOptionPane.showMessageDialog(null, e.getMessage(), GraphicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
				}
				// Show splash screen
				new SplashScreen();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), GraphicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
			logger.error("Terminating program", e);
		}
	}
}
