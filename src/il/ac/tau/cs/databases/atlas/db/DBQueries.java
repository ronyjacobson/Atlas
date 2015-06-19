package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.ParserConstants;
import il.ac.tau.cs.databases.atlas.connector.command.*;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;

public class DBQueries implements Queries {

	protected final Logger logger = Logger.getLogger(this.getClass().getName());
	
	public static int amountOfLatestResults = 0;
	public static int amountOfFemaleResults = 0;
	public static int amountOfBirthResults = 0;
	private static int maxYear = 0;
	private static int minYear = 0;

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
		logger.info(String.format(
				"Registering user with username: %s...", user.getUsername()));
		// Execute query
		User newUser = query.execute();
		if (newUser == null) {
			return false;
		} else {
			user.setUserID(newUser.getUserID());
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
		logger.info("Fetching category names...");
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

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	@Override
	public int getBirthsOfLatestResults() {
		return amountOfBirthResults;
	}

	@Override
	public Long getLocationId(String locationName) {
		if (!locationsMap.isEmpty()) {
			return locationsMap.get(locationName);
		} else {
			return null;
		}
	}

	/**
	 * @return true if the current user should be added to results set
	 */
	private Result AddUserToResults(int startYear, int endYear) {
		User user = Main.user;
		// Get year of birth
		Calendar cal = Calendar.getInstance();
		cal.setTime(user.getDateOfBirth());
		int year = cal.get(Calendar.YEAR);
		Result result = null;

		// Check if year of birth in scope
		if (year >= startYear && year <= endYear) {
			Location l = user.getLocation();
			result = new Result(null, user.getUsername(), l,
					user.getDateOfBirth(), true, "favorites", "This is you!",
					null);
		}
		return result;
	}

	/**
	 * @return A list of results of all the favorite entries in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<Result> getFavorites() throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;
		amountOfBirthResults = 0;
		GetFavoritesResultsQuery query = new GetFavoritesResultsQuery();
		logger.info("Fetching Favorites...");
		results.addAll(query.execute());
		amountOfLatestResults = results.size();
		return results;
	}

	/**
	 * @return A list of all the favorite id's in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<String> getFavoritesIDs() throws AtlasServerException {
		List<Result> results = getFavorites();
		List<String> ids = new ArrayList<String>();
		for (Result res : results) {
			String id = res.getID();
			if (!ids.contains(id)) {
				ids.add(id);
			}
		}
		amountOfFemaleResults = 0;
		amountOfBirthResults = 0;
		amountOfLatestResults = 0;
		return ids;
	}

	/**
	 * Store all the chosen favorite IDs to the database
	 * 
	 * @param favoritesList
	 * @throws AtlasServerException
	 */
	@Override
	public void storeFavoriteIDs(List<String> favoritesList,
			List<String> removeFromFavorits) throws AtlasServerException {
		// Initialize DB query
		UpdateFavoritesQuery query = new UpdateFavoritesQuery(favoritesList,
				removeFromFavorits);
		logger.info("Updating favorites...");

		// Execute query
		query.execute();
	}

	/**
	 * Update the DB with the Yago files in the given full path directory
	 * @param fullPathDirectory
	 */
	@Override
	public void update(File fullPathDirectory) throws AtlasServerException {
		Map<String,File> filesMap = checkAndGetFiles(fullPathDirectory);
		new ParseFilesCommand(filesMap).execute();
	}

	/**
	 * Add a new entry to the database
	 * 
	 * @throws AtlasServerException
	 */
	@Override
	public void addNew(String name, String category, Date birthDate,
			Long birthlocationID, Date deathDate, Long deathlocationID,
			String wikiLink, boolean isFemale) throws AtlasServerException {
		
		// Initialize DB query
		if (category.equals("Favorites")) {
			throw new AtlasServerException(
					"Cant add to favorites, choose a category and then add");
		}
		int catId = categoriesMap.get(category);
		AddPersonQuery query = new AddPersonQuery(name, catId, birthDate, birthlocationID, deathDate, deathlocationID, wikiLink, isFemale);

		logger.info(String.format("Adding person: %s...", name));

		// Execute query
		query.execute();
	}

	/**
	 * @return The start year of the latest search results fetched from the
	 *         database
	 */
	@Override
	public int getLatestResultsStartTimeLine() {
		return minYear;
	}

	/**
	 * @return The end year of the latest search results fetched from the
	 *         database
	 */
	@Override
	public int getLatestResultsEndTimeLine() {
		return maxYear;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<Result> getResults(Date sdate, Date edate)
			throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;
		amountOfBirthResults = 0;
		logger.info("Fetching results by dates...");
		SearchResultsByDatesQuery query = new SearchResultsByDatesQuery(sdate,
				edate);
		results.addAll(query.execute());
		amountOfLatestResults = results.size();
		return results;
	}

	@Override
	public List<Result> getResults(String name) throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		amountOfFemaleResults = 0;
		amountOfBirthResults = 0;
		maxYear = 0;
		minYear = 0;
		SearchResultsByNameQuery query = new SearchResultsByNameQuery(name);
		logger.info("Fetching results by name...");
		results.addAll(query.execute());
		maxYear = query.getMaxYear();
		minYear = query.getMinYear();
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
		amountOfFemaleResults = 0;
		amountOfBirthResults = 0;
		logger.info("Fetching results by category and years...");
		List<Result> results = new ArrayList<Result>();
		GetResultsQuery query = new GetResultsQuery(startYear, endYear,
				category);
		results.addAll(query.execute());
		amountOfLatestResults = results.size();
		return results;
	}

	private Map<String,File> checkAndGetFiles(File fullPath) throws AtlasServerException {
		final File[] files = fullPath.listFiles();
		if (files == null) {
			throw new AtlasServerException(fullPath + ": The path doesn't exist");
		}
		Map<String, File> fileMap = new HashMap<>();
		Set<String> required = new HashSet<>(Arrays.asList(ParserConstants.REQUIRED_FILES));
		for (File file : files) {
			final String relevantFileName = file.getName();
			if (required.remove(relevantFileName)) {
				fileMap.put(relevantFileName, file);
			}
		}
		if (!required.isEmpty()) {
			String msg = "The following files are missing:";
			for (String missing : required) {
				msg += "\n" + missing;
			}
			throw new AtlasServerException(msg);
		}

		for (Map.Entry<String, File> stringFileEntry : fileMap.entrySet()) {
			final File file = stringFileEntry.getValue();
			if (!(file.exists() && !file.isDirectory() && file.canRead())) {
				throw new AtlasServerException("The file: " + stringFileEntry.getKey() + ", is not valid");
			}
		}
		return fileMap;
	}

}
