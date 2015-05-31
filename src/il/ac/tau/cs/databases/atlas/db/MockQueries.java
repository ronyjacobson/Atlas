package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.util.ArrayList;
import java.util.List;

public class MockQueries implements Queries {
	
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
	public List<Location> getAllGeoLocations() {
		ArrayList<Location> geoLocations = new ArrayList<>();
		geoLocations.add(new Location(1,"Tel-Aviv", 32.0667, 34.8000));
		locationsMap.put("Tel-Aviv", 0);
		geoLocations.add(new Location(2,"New-York", 40.748817, -73.985428));
		locationsMap.put("New-York", 1);
		geoLocations.add(new Location(3, "Paris", 48.8567, 2.3508));
		locationsMap.put("Paris", 2);
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
		locationsNames.addAll(geoLocationsNames);
		return geoLocationsNames;
	}

	/**
	 * @return A list of results of all the matching entries in the database
	 */
	public List<Result> getResults(String name) {
		return getResults(1000, 2015, "");
	}
	
	/**
	 * @return A list of results of all the matching entries in the database
	 */
	@Override
	public ArrayList<Result> getResults(int startYear, int endYear, String category) {
		List<Location> geoLocations = getAllGeoLocations();
		ArrayList<Result> results = new ArrayList<>();
		if (startYear % 1000 == 0 )results.add(new Result("1", "a", geoLocations.get(0), null, true, "summary a", "https://en.wikipedia.org/w/index.php?title=A"));
		results.add(new Result("2", "b", geoLocations.get(1), null, false, "summary b", "https://en.wikipedia.org/w/index.php?title=B"));
		if (startYear == 1300) results.add(new Result("3", "c", geoLocations.get(2), null, true, "summary c", "https://en.wikipedia.org/w/index.php?title=C"));
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

	/**
	 * Update the DB with the Yago files in the given full path directoty
	 */
	@Override
	public void update(String fullPathDirectory) {
		System.out.println(fullPathDirectory);
	}
	
	/**
	 * @returns a User from the DB that matches the user param
	 */
	@Override
	public User fetchUser(User user) throws AtlasServerException {
		return user;
	}


	@Override
	public void getGeoLocationsHashMap() {
		// TODO Auto-generated method stub
		return;
	}


	@Override
	public Integer getLocationId(String locationName) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean addNew(String name, String category, String birthDate,
			int birthlocationID, String deathDate, int deathlocationID,
			String wikiLink) {
		// TODO Auto-generated method stub
		return false;
	}

}
