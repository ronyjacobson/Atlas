package il.ac.tau.cs.databases.atlas.db;

import java.util.ArrayList;
import java.util.Random;

public class MockQueries implements Queries {

	@Override
	/**
	 * @return true if the user is already registered in the system
	 */
	public boolean isRegisteredUser(User user) {
		return (new Random().nextInt(2) == 0) ? true : false;
	}

	@Override
	/**
	 * @return true if the user's user name and password match
	 */
	public boolean areUsernamePasswordCorrect(User user) {
		return true;
	}

	@Override
	/**
	 * Register the user to the system
	 * @return true if the user was successfully registered in the system
	 */
	public boolean registerUser(User user) {
		return true;
	}

	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 */
	@Override
	public ArrayList<String> getAllGeoLocations() {
		ArrayList<String> geoLocations = new ArrayList<>();
		geoLocations.add("Tel-Aviv");
		geoLocations.add("New-York");
		geoLocations.add("Paris");
		return geoLocations;
	}
}
