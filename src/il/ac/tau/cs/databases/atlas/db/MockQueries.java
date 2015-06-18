package il.ac.tau.cs.databases.atlas.db;

import il.ac.tau.cs.databases.atlas.ProgressBarDemo;
import il.ac.tau.cs.databases.atlas.ProgressBarTask;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.TempCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
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
	public List<Result> getResults(int startYear, int endYear, String category) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<Location> geoLocations = getAllGeoLocations();
		List<Result> results = new ArrayList<>();
		if (startYear % 1000 == 0 )results.add(new Result("1", "a", geoLocations.get(0), null, true, "scientists","summary a", "https://en.wikipedia.org/w/index.php?title=A"));
		results.add(new Result("2", "b", geoLocations.get(1), null, false, "kings-and-queens", "summary b", "https://en.wikipedia.org/w/index.php?title=B"));
		if (startYear == 1300) results.add(new Result("3", "c", geoLocations.get(2), null, true, "philosophers", "summary c", "https://en.wikipedia.org/w/index.php?title=C"));
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
	 * @param fullPathDirectory
	 */
	@Override
	public void update(Map<String, File> fullPathDirectory) {
		try {
			DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06", "127.0.0.1", "3305", "DbMysql06");
		} catch (AtlasServerException e) {
			e.printStackTrace();
		}
		new ProgressBarTask(new TempCommand(100)).startTask();
	}

	/**
	 * @returns a User from the DB that matches the user param
	 */
	@Override
	public User fetchUser(User user) throws AtlasServerException {
		if (new Random().nextBoolean()){
			return new User(user.getUsername(), user.getPassword() + "a");
		}
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

	public boolean addNew(String name, String category, String birthDate,
			int birthlocationID, String deathDate, int deathlocationID,
			String wikiLink) {
		return false;
	}
		
	@Override
	public void addNew(String name, String category, Date birthDate,
			Integer birthlocationID, Date deathDate, Integer deathlocationID,
			String wikiLink, boolean isFemale) {
		// TODO Auto-generated method stub
		return;
	}


	@Override
	public List<Result> getResults(Date sdate, Date edate)
			throws AtlasServerException {
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.setTime(sdate);
		endCal.setTime(edate);
		return getResults(startCal.get(Calendar.YEAR), endCal.get(Calendar.YEAR), "");
	}


	@Override
	public List<Result> getFavorites() throws AtlasServerException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("here");
		return getResults("");
	}

	/**
	 * @return The start year of the latest search results fetched from the database 
	 */
	@Override
	public int getLatestResultsStartTimeLine() {
		return 1100;
	}

	/**
	 * @return The end year of the latest search results fetched from the database 
	 */
	@Override
	public int getLatestResultsEndTimeLine() {
		return 1200;
	}


	@Override
	public void storeFavoriteIDs(List<String> favoritesList,
			List<String> removeFromFavorites) throws AtlasServerException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<String> getFavoritesIDs() throws AtlasServerException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getBirthsOfLatestResults() {
		// TODO Auto-generated method stub
		return 0;
	}



}
