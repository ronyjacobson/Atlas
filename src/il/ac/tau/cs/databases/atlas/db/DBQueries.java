package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.connector.command.GetUserQuery;
import il.ac.tau.cs.databases.atlas.connector.command.RegisterUserQuery;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class DBQueries implements Queries {

	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	@Override
	/**
	 * @returns a User from the DB that matches the user parameter
	 */
	public User fetchUser(User user) throws AtlasServerException {
		// Initialize DB query
		GetUserQuery query = new GetUserQuery(user);
		logger.info(String.format(
				"Checking for regitered user with username: %s...",
				user.getUsername()));
		// Execute query
		return query.execute();
	}

	@Override
	/**
	 * Register the user to the system
	 * @return true if the user was successfully registered in the system
	 */
	public boolean registerUser(User user) throws AtlasServerException {
		// Initialize DB query
		RegisterUserQuery query = new RegisterUserQuery(user);
		System.out.println(String.format(
				"Registering user with username: %s...",
				user.getUsername()));
		// Execute query
		User newUser = query.execute();
		if (newUser == null) {
			return false;
		} else return true;
	}

	/**
	 * @return A list of all geographical locations in the database
	 */
	@Override
	public HashMap<String, Integer> getGeoLocationsHashMap() {
		HashMap<String, Integer> geoLocations = new HashMap<String, Integer>();
		// TODO
		return geoLocations;
	}

	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 */
	@Override
	public List<String> getAllGeoLocationsNames() {
		ArrayList<String> geoLocationsNames = new ArrayList<>();
		// TODO
		return geoLocationsNames;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 */
	// TODO
	@Override
	public ArrayList<Result> getResults(int startYear, int endYear,
			String category) {
		return null;
	}

	/**
	 * @return True if the server is connected and online
	 */
	// TODO
	@Override
	public boolean isConnectedToDB() {
		return true;
	}

	/**
	 * @return A list of all the categories in the database
	 */
	// TODO
	@Override
	public List<String> getAllCategoriesNames() {
		List<String> categories = new ArrayList<>();
		categories.add("Scientists");
		categories.add("Philosophers");
		categories.add("Kings And Queens");
		categories.add("Favorites");
		return categories;
	}

	/**
	 * @return The amount of results found in the last results query
	 */
	// TODO
	@Override
	public int getAmountOfLatestResults() {
		return 10;
	}

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	// TODO
	@Override
	public int getStatsOfLatestResults() {
		return 6;
	}

	// TODO
	@Override
	public void update(String fullPathDirectory) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getLocationId(String locationName) {
		if (locationsList != null) {
			return locationsList.get(locationName);
		} else {
			return null;
		}
	}

	@Override
	public boolean addNew(String name, String category, String birthDate,
			int birthlocationID, String deathDate, int deathlocationID,
			String wikiLink) {
		// TODO Auto-generated method stub
		return false;
	}

}
