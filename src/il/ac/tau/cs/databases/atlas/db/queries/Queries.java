package il.ac.tau.cs.databases.atlas.db.queries;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.core.modal.Result;
import il.ac.tau.cs.databases.atlas.core.modal.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Queries {
	
	// Maps between location names and id's
	Map<String, Long> locationsMap= new HashMap<>();
	
	// Location names list
	List<String> locationsNames= new ArrayList<>();
	
	// Maps between category names and ids
	Map<String, Integer> categoriesMap= new HashMap<>();
	
	/**
	 * @return True if the server is connected and online
	 */
	public boolean isConnectedToDB();
	
	/**
	 * Register the user to the system
	 * @return true if the user was successfully registered in the system
	 * @throws AtlasServerException 
	 */
	public boolean registerUser(User user) throws AtlasServerException;
	
	/**
	 * @return A list of all geographical locations in the database
	 * @throws AtlasServerException 
	 */
	public void getGeoLocationsIntoHashMap() throws AtlasServerException;
	
	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 * @throws AtlasServerException 
	 */
	public List<String> getAllGeoLocationsNames() throws AtlasServerException;
	
	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException 
	 */
	public List<Result> getResults(String name) throws AtlasServerException;
	
	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException 
	 */
	public List<Result> getResults(int startYear, int endYear, String category) throws AtlasServerException;
		
	/**
	 * @return A list of all the categories in the database
	 * @throws AtlasServerException 
	 */
	public List<String> getAllCategoriesNames() throws AtlasServerException;
	
	/**
	 * @return The amount of results found in the last results query
	 */
	public int getAmountOfLatestResults();
	
	/**
	 * @return The male/females statistics of the results found in the last results query
	 */
	public int getStatsOfLatestResults();

	/**
	 * Update the DB with the Yago files in the given full path directory
	 * @param fullPathDirectory
	 */
	public void update(File fullPathDirectory) throws AtlasServerException;

	/**
	 * @return a User from the DB that matches the user given
	 */
	public User fetchUser(User user) throws AtlasServerException;

	/**
	 * Add a new entry to the database
	 * @throws AtlasServerException 
	 */
	public void addNew(String name, String category, Date birthDate, Long birthlocationID, Date deathDate, Long deathlocationID, String wikiLink, boolean isFemale) throws AtlasServerException;
	
	/**
	 * Store all the chosen favorite IDs to the database
	 * @param favoritesList
	 * @param removeFromFavorites 
	 * @return True if the favorites were stored successfully
	 * @throws AtlasServerException 
	 */
	public void storeFavoriteIDs(List<String> favoritesList, List<String> removeFromFavorites) throws AtlasServerException;
	
	/**
	 * @return The location Id of @param locationName.
	 */
	public Long getLocationId(String locationName);

	/**
	 * @return A list of results of all the matching entries in the database
	 * @throws AtlasServerException
	 */
	List<Result> getResults(Date sdate, Date edate)
			throws AtlasServerException;
	
	/**
	 * @return A list of results of all the favorite entries in the database
	 * @throws AtlasServerException
	 */
	List<Result> getFavorites() throws AtlasServerException;
	
	/**
	 * @return The start year of the latest search results fetched from the database 
	 */
	int getLatestResultsStartTimeLine();
	
	/**
	 * @return The end year of the latest search results fetched from the database 
	 */
	int getLatestResultsEndTimeLine();

	/**
	 * @return A list of all the favorite id's in the database
	 * @throws AtlasServerException
	 */
	List<String> getFavoritesIDs() throws AtlasServerException;

	int getBirthsOfLatestResults();

	void updateRecord(
			int personId, String name, Date birthDate,
			Long birthlocationID, Date deathDate, Long deathlocationID,
			String wikiLink, boolean isFemale, boolean checkIfPersonExists
	) throws AtlasServerException;
}
