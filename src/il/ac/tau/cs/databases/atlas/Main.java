package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.FixedConnectionPool;
import il.ac.tau.cs.databases.atlas.db.DBQueries;
import il.ac.tau.cs.databases.atlas.db.Queries;
import il.ac.tau.cs.databases.atlas.db.User;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import javax.swing.*;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);
	// DB queries object
	public static final Queries queries = new DBQueries();
	public static User user;
	
	/**
	 * Initialize the program parameters and load its state.
	 * 
	 * @throws Exception
	 * @param pathToConfFile
	 */
	private static void initialize(String pathToConfFile) throws Exception {
		// Get the user's screen size
		logger.debug("Atlas started");
		GrapicUtils.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Load earlier state

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
		State.autoLoad();
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
		boolean SKIP_LOGIN = false;
		try {
			// Initialize the program
			String pathToConfFile = "config.properties";
			if (args.length > 0) {
				pathToConfFile = args[0];
			}
			initialize(pathToConfFile);
			if (SKIP_LOGIN) {
				Main.user = new User(1, "rony", "0000", new Date(), Long.parseLong("1") , true);
				new Map();
			} else {
				// Show splash screen
				new Splash();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), GrapicUtils.PROJECT_NAME, JOptionPane.ERROR_MESSAGE);
			logger.error("Terminating program", e);
		}
	}
}
