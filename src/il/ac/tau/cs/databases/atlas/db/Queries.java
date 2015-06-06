package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Queries {

	public static HashMap<String, Integer> locationsMap= new HashMap<String, Integer>();
	public static ArrayList<String> locationsNames= new ArrayList<String>();
	
	
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
	public void getGeoLocationsHashMap() throws AtlasServerException;
	
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
	 */
	public void update(String fullPathDirectory);

	/**
	 * @return a User from the DB that matches the user given
	 */
	public User fetchUser(User user) throws AtlasServerException;

	/**
	 * Add a new entry to the database
	 */
	public boolean addNew(String name, String category, String birthDate, int birthlocationID, String deathDate, int deathlocationID, String wikiLink, boolean isFemale);
	
	/**
	 * Store all the chosen favorite IDs to the database
	 * @param favoritesList
	 * @return True if the favorites were stored successfully
	 */
	public boolean storeFavoriteIDs(List<String> favoritesList);
	
	/**
	 * TODO
	 */
	public Integer getLocationId(String locationName);

}
