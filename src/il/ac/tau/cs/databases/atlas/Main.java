package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.State;
import il.ac.tau.cs.databases.atlas.graphics.Utils;
import java.awt.Toolkit;

public class Main {

	/**
	 * Initialize the program parameters and load its state.
	 * 
	 * @throws Exception
	 */
	private static void initialize() throws Exception {
		// Get the user's screen size
		Utils.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Load earlier state
		try {
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
