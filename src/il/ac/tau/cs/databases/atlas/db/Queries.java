package il.ac.tau.cs.databases.atlas.db;

import java.util.ArrayList;

public interface Queries {

	public boolean isRegisteredUser(User user);
	public boolean areUsernamePasswordCorrect(User user);
	public boolean registerUser(User user);
	public ArrayList<String> getAllGeoLocations();
}
