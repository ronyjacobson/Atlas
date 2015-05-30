package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.connector.command.GetUserQuery;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
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
	public boolean registerUser(User user) {
		return false;
		//TODO
	}

	/**
	 * @return A list of all geographical locations in the database
	 */
	@Override
	public List<Location> getAllGeoLocations() {
		ArrayList<Location> geoLocations = new ArrayList<>();
		//TODO
		return geoLocations;
	}

	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 */
	@Override
	public List<String> getAllGeoLocationsNames() {
		ArrayList<String> geoLocationsNames = new ArrayList<>();
		//TODO
		return geoLocationsNames;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 */
	//TODO
	@Override
	public ArrayList<Result> getResults(int startYear, int endYear,
			String category) {
		List<Location> geoLocations = getAllGeoLocations();
		ArrayList<Result> results = new ArrayList<>();
		if (startYear % 3 != 0)
			results.add(new Result("a", geoLocations.get(0), null, true,
					"summary a", "https://en.wikipedia.org/w/index.php?title=A"));
		results.add(new Result("b", geoLocations.get(1), null, false,
				"summary b", "https://en.wikipedia.org/w/index.php?title=B"));
		if (startYear % 2 != 0)
			results.add(new Result("c", geoLocations.get(2), null, true,
					"summary c", "https://en.wikipedia.org/w/index.php?title=C"));
		return results;
	}

	/**
	 * @return True if the server is connected and online
	 */
	//TODO
	@Override
	public boolean isConnectedToDB() {
		return true;
	}

	/**
	 * @return A list of all the categories in the database
	 */
	//TODO
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
	//TODO
	@Override
	public int getAmountOfLatestResults() {
		return 10;
	}

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	//TODO
	@Override
	public int getStatsOfLatestResults() {
		return 6;
	}
	
	//TODO
	@Override
	public void update(String fullPathDirectory) {
		// TODO Auto-generated method stub

	}


	@Override
	public boolean addNew(String name, String category, String birthDate,
			int birthPlace, String deathDate, int deatePlace, String wikiLink) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Result> getResults(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
