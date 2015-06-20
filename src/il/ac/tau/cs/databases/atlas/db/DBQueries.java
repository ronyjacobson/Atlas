package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.Main;
import il.ac.tau.cs.databases.atlas.ParserConstants;
import il.ac.tau.cs.databases.atlas.ResultsHolder;
import il.ac.tau.cs.databases.atlas.connector.command.AddPersonQuery;
import il.ac.tau.cs.databases.atlas.connector.command.CheckConnectivityCommand;
import il.ac.tau.cs.databases.atlas.connector.command.GetCategoriesQuery;
import il.ac.tau.cs.databases.atlas.connector.command.GetGeoLocationsQuery;
import il.ac.tau.cs.databases.atlas.connector.command.GetUserQuery;
import il.ac.tau.cs.databases.atlas.connector.command.ParseFilesCommand;
import il.ac.tau.cs.databases.atlas.connector.command.RegisterUserQuery;
import il.ac.tau.cs.databases.atlas.connector.command.SearchResultsByDatesQuery;
import il.ac.tau.cs.databases.atlas.connector.command.SearchResultsByNameQuery;
import il.ac.tau.cs.databases.atlas.connector.command.UpdateFavoritesQuery;
import il.ac.tau.cs.databases.atlas.connector.command.UpdatePersonQuery;
import il.ac.tau.cs.databases.atlas.connector.command.NewCommands.GetGoResultsQuery;
import il.ac.tau.cs.databases.atlas.connector.command.NewCommands.GetNewFavoritesResultsQuery;
import il.ac.tau.cs.databases.atlas.connector.command.NewCommands.SearchResultsQuery;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class DBQueries implements Queries {

	protected final Logger logger = Logger.getLogger(this.getClass().getName());
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
		logger.info(String.format("Registering user with username: %s...",
				user.getUsername()));
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
	public void getGeoLocationsIntoHashMap() throws AtlasServerException {
		// Initialize DB query
		GetGeoLocationsQuery query = new GetGeoLocationsQuery();
		logger.info("Fetching GeoLocations names and Id's...");
		query.execute();
		logger.info("Fetching GeoLocations done.");
	}

	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<String> getAllGeoLocationsNames() throws AtlasServerException {
		if (locationsNames.isEmpty()) {
			getGeoLocationsIntoHashMap();
		}
		return locationsNames;
	}

	/**
	 * @return True if the server is connected and online
	 */
	@Override
	public boolean isConnectedToDB() {
		try {
			return new CheckConnectivityCommand().execute();
		} catch (AtlasServerException e) {
			logger.error("error while checking connectivity", e);
			return false;
		}
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
		ArrayList<String> categories = new ArrayList<String>();
		if (categoriesMap.isEmpty()) {
			// Execute query
			categories = query.execute();
		} else {
			categories.addAll(categoriesMap.keySet());
		}
		logger.info("Fetching category names done.");
		return categories;
	}

	/**
	 * @return The amount of results found in the last results query
	 */
	@Override
	public int getAmountOfLatestResults() {
		return ResultsHolder.INSTANCE.getNumOfLatestResults();
	}

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	@Override
	public int getStatsOfLatestResults() {
		return ResultsHolder.INSTANCE.getNumOfFemaleResults();
	}

	/**
	 * @return The male/females statistics of the results found in the last
	 *         results query
	 */
	@Override
	public int getBirthsOfLatestResults() {
		return ResultsHolder.INSTANCE.numOfBirthResults;
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
		ResultsHolder.INSTANCE.resetCounters();
		GetNewFavoritesResultsQuery query = new GetNewFavoritesResultsQuery();
		logger.info("Fetching Favorites...");
		results.addAll(query.execute().values());
		logger.info("Fetching Favorites done.");
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
		ResultsHolder.INSTANCE.resetCounters();
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
		logger.info("Updating favorites done.");
	}

	/**
	 * Update the DB with the Yago files in the given full path directory
	 * 
	 * @param fullPathDirectory
	 */
	@Override
	public void update(File fullPathDirectory) throws AtlasServerException {
		Map<String, File> filesMap = checkAndGetFiles(fullPathDirectory);
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
		AddPersonQuery query = new AddPersonQuery(name, catId, birthDate,
				birthlocationID, deathDate, deathlocationID, wikiLink, isFemale);

		logger.info(String.format("Adding person: %s...", name));

		// Execute query
		query.execute();
		logger.info(String.format("Adding person: %s done.", name));
	}

	/**
	 * Update an existing entry in the database
	 *
	 * @throws AtlasServerException
	 */
	@Override
	public void updateRecord(int personId, String name,
			Date birthDate, Long birthLocationId, Date deathDate,
			Long deathLocationId, String wikiLink, boolean isFemale)
			throws AtlasServerException {

		UpdatePersonQuery query = new UpdatePersonQuery(personId, name,
				birthDate, birthLocationId, deathDate, deathLocationId,
				wikiLink, isFemale);

		logger.info(String.format("Updating person: %s...", name));

		// Execute query
		query.execute();
		logger.info(String.format("Updating person: %s done.", name));
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
		ResultsHolder.INSTANCE.resetCounters();
		logger.info("Fetching results by dates...");
		SearchResultsQuery query = new SearchResultsQuery(sdate,edate);
		results.addAll(query.execute().values());
		logger.info("Fetching results by dates done.");
		return results;
	}

	@Override
	public List<Result> getResults(String name) throws AtlasServerException {
		List<Result> results = new ArrayList<Result>();
		ResultsHolder.INSTANCE.resetCounters();
		maxYear = 0;
		minYear = 0;
		SearchResultsQuery query = new SearchResultsQuery(name);
		logger.info("Fetching results by name...");
		results.addAll(query.execute().values());
		maxYear = query.getMaxYear();
		minYear = query.getMinYear();
		logger.info("Fetching results by name done.");
		return results;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException
	 */
	@Override
	public List<Result> getResults(int startYear, int endYear, String category)
			throws AtlasServerException {
		ResultsHolder.INSTANCE.resetCounters();
		logger.info("Fetching results by category and years...");
		List<Result> results = new ArrayList<Result>();
		GetGoResultsQuery query = new GetGoResultsQuery(startYear, endYear, category);
		results.addAll(query.execute().values());
		logger.info("Fetching results by category and years done.");
		return results;
	}

	private Map<String, File> checkAndGetFiles(File fullPath)
			throws AtlasServerException {
		final File[] files = fullPath.listFiles();
		if (files == null) {
			throw new AtlasServerException(fullPath
					+ ": The path doesn't exist");
		}
		Map<String, File> fileMap = new HashMap<>();
		Set<String> required = new HashSet<>(
				Arrays.asList(ParserConstants.REQUIRED_FILES));
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
				throw new AtlasServerException("The file: "
						+ stringFileEntry.getKey() + ", is not valid");
			}
		}
		return fileMap;
	}

}
