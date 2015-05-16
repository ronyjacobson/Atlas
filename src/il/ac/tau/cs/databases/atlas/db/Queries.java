package il.ac.tau.cs.databases.atlas.db;

import java.util.List;

public interface Queries {

	public boolean isRegisteredUser(User user);
	public boolean areUsernamePasswordCorrect(User user);
	public boolean registerUser(User user);
	public List<Location> getAllGeoLocations();
	public List<Result> getResults(int year);
	public List<String> getAllGeoLocationsNames();
}
