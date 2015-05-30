package il.ac.tau.cs.databases.atlas.db;

import java.util.ArrayList;
import java.util.List;
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
	 * @return A list of all geographical locations in the database
	 */
	@Override
	public List<Location> getAllGeoLocations() {
		ArrayList<Location> geoLocations = new ArrayList<>();
		geoLocations.add(new Location("Tel-Aviv", 32.0667, 34.8000));
		geoLocations.add(new Location("New-York", 40.748817, -73.985428));
		geoLocations.add(new Location("Paris", 48.8567, 2.3508));
		return geoLocations;
	}

	/**
	 * @return A list of strings representing the display names of all
	 *         geographical locations in the database
	 */
	@Override
	public List<String> getAllGeoLocationsNames() {
		List<Location> geoLocations = getAllGeoLocations();
		ArrayList<String> geoLocationsNames = new ArrayList<>();
		for (Location location : geoLocations){
			geoLocationsNames.add(location.getName());
		}
		return geoLocationsNames;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 */
	@Override
	public ArrayList<Result> getResults(int startYear, int endYear, String category) {
		List<Location> geoLocations = getAllGeoLocations();
		ArrayList<Result> results = new ArrayList<>();
		if (startYear % 1000 == 0 )results.add(new Result("a", geoLocations.get(0), null, true, "summary a", "https://en.wikipedia.org/w/index.php?title=A"));
		results.add(new Result("b", geoLocations.get(1), null, false, "summary b", "https://en.wikipedia.org/w/index.php?title=B"));
		if (startYear == 1300) results.add(new Result("c", geoLocations.get(2), null, true, "summary c", "https://en.wikipedia.org/w/index.php?title=C"));
		return results;
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
	 */
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
	@Override
	public int getAmountOfLatestResults() {
		return 10;
	}

	/**
	 * @return The male/females statistics of the results found in the last results query
	 */
	@Override
	public int getStatsOfLatestResults() {
		return 6;
	}


}
