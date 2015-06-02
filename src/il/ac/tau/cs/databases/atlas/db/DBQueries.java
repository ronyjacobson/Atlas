package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.connector.command.GetCategoriesQuery;
import il.ac.tau.cs.databases.atlas.connector.command.GetGeoLocationsNamesAndIDsQuery;
import il.ac.tau.cs.databases.atlas.connector.command.GetResultsQuery;
import il.ac.tau.cs.databases.atlas.connector.command.GetUserQuery;
import il.ac.tau.cs.databases.atlas.connector.command.RegisterUserQuery;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DBQueries implements Queries {

	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	public static int amountOfLatestResults = 0;
	public static int amountOfFemaleResults = 0;

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
		logger.info(String.format("Registering user with username: %s...",
				user.getUsername()));
		// Execute query
		User newUser = query.execute();
		if (newUser == null) {
			return false;
		} else
			return true;
	}

	/**
	 * @return A Hash Map of all geographical locations in the database
	 */
	@Override
	public void getGeoLocationsHashMap() throws AtlasServerException {
		// Initialize DB query
		GetGeoLocationsNamesAndIDsQuery query = new GetGeoLocationsNamesAndIDsQuery();
		logger.info("Fetching GeoLocations names and Id's...");
		query.execute();
	}

	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<String> getAllGeoLocationsNames() throws AtlasServerException {
		if (locationsNames.isEmpty()) {
			getGeoLocationsHashMap();
		}
		return locationsNames;
	}

	/**
	 * @return True if the server is connected and online
	 */
	@Override
	public boolean isConnectedToDB() {
		return true;
	}

	/**
	 * @return A list of all the categories in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<String> getAllCategoriesNames() throws AtlasServerException {
		// Initialize DB query
		GetCategoriesQuery query = new GetCategoriesQuery();
		logger.info("Fetching category names...");
		// Execute query
		ArrayList<String> categories = query.execute();
		return categories;
	}

	/**
	 * @return The amount of results found in the last results query
	 */
	// TODO
	@Override
	public int getAmountOfLatestResults() {
		return amountOfLatestResults;
	}

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	// TODO
	@Override
	public int getStatsOfLatestResults() {
		return amountOfFemaleResults;
	}

	// TODO
	@Override
	public void update(String fullPathDirectory) {
		// TODO Auto-generated method stub -PAZ

	}

	@Override
	public Integer getLocationId(String locationName) {
		if (!locationsMap.isEmpty()) {
			return locationsMap.get(locationName);
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

	@Override
	public List<Result> getResults(String name) {
		amountOfFemaleResults = 0;
		
		
		amountOfLatestResults = 30;
		amountOfFemaleResults = 40;
		return null;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException 
	 */
	// TODO
	@Override
	
	public List<Result> getResults(int startYear, int endYear, String category) throws AtlasServerException {
		//GetResultsQuery(startYear, endYear, category, byName, userOriented, isBirth)
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;
		
		// Get births - user oriented
		GetResultsQuery query = new GetResultsQuery(startYear, endYear, category, false, true, true);
		logger.info("Fetching results (Query 1 out of 4...");
		results.addAll(query.execute());
		
		// Get deaths - user oriented
		query = new GetResultsQuery(startYear, endYear, category, false, true, false);
		logger.info("Fetching results (Query 2 out of 4...");
		results.addAll(query.execute());
		
		// Get all births
		query = new GetResultsQuery(startYear, endYear, category, false, false, true);
		logger.info("Fetching results (Query 3 out of 4...");
		results.addAll(query.execute());
		
		// Get all deaths
		query = new GetResultsQuery(startYear, endYear, category, false, false, false);
		logger.info("Fetching results (Query 4 out of 4...");
		results.addAll(query.execute());
		
		amountOfLatestResults = results.size();
		return results;
	}

	@Override
	public boolean storeFavoriteIDs(List<String> favoritesList) {
		// TODO Auto-generated method stub
		return false;
	}

}
