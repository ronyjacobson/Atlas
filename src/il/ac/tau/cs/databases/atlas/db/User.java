package il.ac.tau.cs.databases.atlas.db;

import java.util.Date;

/**
 *  A class representing a user of the system
 */
public class User {
	
	private int userID;
	private String username;
	private String password;
	private Date dateOfBirth;
	private int locationID;
	private Location location;
	
	public User(String username, String password, Date dateOfBirth, int locationID) {
		this.username = username;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.locationID = locationID;
	}
	
	public User(int ID, String username, String password, Date dateOfBirth, int locationID) {
		this.userID = ID;
		this.username = username;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.locationID = locationID;
	}
	
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getLocationID() {
		return locationID;
	}

	public void setLocationID(int locationID) {
		this.locationID = locationID;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	

}
