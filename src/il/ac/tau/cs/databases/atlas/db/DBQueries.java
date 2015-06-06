package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.connector.command.*;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
import java.util.Date;
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
		System.out.println(String.format(
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
				"Registering user with username: %s...", user.getUsername()));
		// Execute query
		User newUser = query.execute();
		if (newUser == null) {
			return false;
		} else {
			Main.user.setUserID(newUser.getUserID());
			return true;
		}

	}

	/**
	 * @return A Hash Map of all geographical locations in the database
	 */
	@Override
	public void getGeoLocationsHashMap() throws AtlasServerException {
		// Initialize DB query
		GetGeoLocationsQuery query = new GetGeoLocationsQuery();
		System.out.println("Fetching GeoLocations names and Id's...");
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
		// TODO - Etan??
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
		System.out.println("Fetching category names...");
		// Execute query
		ArrayList<String> categories = query.execute();
		return categories;
	}

	/**
	 * @return The amount of results found in the last results query
	 */
	@Override
	public int getAmountOfLatestResults() {
		return amountOfLatestResults;
	}

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	@Override
	public int getStatsOfLatestResults() {
		return amountOfFemaleResults;
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
	public List<Result> getResults(String name) throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;

		// Get Births
		SearchResultsByNameQuery query = new SearchResultsByNameQuery(name,
				true);
		System.out
				.println("Fetching births results by name, category and years");
		results.addAll(query.execute());

		// Get Deaths
		query = new SearchResultsByNameQuery(name, false);
		System.out
				.println("Fetching death results by name, category and years");
		results.addAll(query.execute());

		amountOfLatestResults = results.size();
		return results;

	}

	public List<Result> getResults(int startYear, int endYear, String category,
			String name) throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;

		// Get Births
		GetResultsQuery query = new GetResultsQuery(startYear, endYear,
				category, name, true);
		System.out
				.println("Fetching births results by name, category and years");
		results.addAll(query.execute());

		// Get Deaths
		query = new GetResultsQuery(startYear, endYear, category, name, false);
		System.out
				.println("Fetching death results by name, category and years");
		results.addAll(query.execute());

		amountOfLatestResults = results.size();
		return results;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<Result> getResults(int startYear, int endYear, String category)
			throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;

		// Get Births
		GetResultsQuery query = new GetResultsQuery(startYear, endYear,
				category, null, true);
		System.out.println("Fetching births results by category and years");
		results.addAll(query.execute());

		// Get Deaths
		query = new GetResultsQuery(startYear, endYear, category, null, false);
		System.out.println("Fetching deaths results by category and years");
		results.addAll(query.execute());

		amountOfLatestResults = results.size();
		return results;
	}

	/**
	 * Store all the chosen favorite IDs to the database
	 * @param favoritesList
	 * @throws AtlasServerException 
	 */
	@Override
	public void storeFavoriteIDs(List<String> favoritesList) throws AtlasServerException {
		// Initialize DB query
		UpdateFavoritesQuery query = new UpdateFavoritesQuery(favoritesList);
		System.out.println("Updating favorites...");

		// Execute query
		query.execute();
	}

	/**
	 * Update the DB with the Yago files in the given full path directory
	 */
	@Override
	public void update(String fullPathDirectory) {
		// TODO PAZ

	}

	/**
	 * Add a new entry to the database
	 * @throws AtlasServerException
	 */
	@Override
	public void addNew(String name, String category, Date birthDate,
			int birthlocationID, Date deathDate, int deathlocationID,
			String wikiLink, boolean isFemale) throws AtlasServerException {

		// Initialize DB query
		if (category.equals("Favorites")) {
			throw new AtlasServerException("Cant add to favorites, choose a category and then add");
		}
		int catId = categoriesMap.get(category);
		AddPersonQuery query = new AddPersonQuery(name, catId, birthDate,
				birthlocationID, deathDate, deathlocationID, wikiLink, isFemale);

		System.out.println(String.format("Adding person: %s...", name));

		// Execute query
		query.execute();
	}

}
