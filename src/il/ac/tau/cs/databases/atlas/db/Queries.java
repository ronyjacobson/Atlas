package il.ac.tau.cs.databases.atlas.db;

import java.util.List;

public interface Queries {

	/**
	 * @return True if the server is connected and online
	 */
	public boolean isConnectedToDB();
	
	/**
	 * @return true if the user is already registered in the system
	 */
	public boolean isRegisteredUser(User user);
	
	/**
	 * @return true if the user's user name and password match
	 */
	public boolean areUsernamePasswordCorrect(User user);
	
	/**
	 * Register the user to the system
	 * @return true if the user was successfully registered in the system
	 */
	public boolean registerUser(User user);
	
	/**
	 * @return A list of all geographical locations in the database
	 */
	public List<Location> getAllGeoLocations();
	
	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 */
	public List<String> getAllGeoLocationsNames();
	
	/**
	 * @return A list of results of all the matching entries in the database
	 */
	public List<Result> getResults(int timeSlot);
		
	/**
	 * @return A list of all the categories in the database
	 */
	public List<String> getAllCategoriesNames();
}
