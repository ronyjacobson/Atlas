package il.ac.tau.cs.databases.atlas.db;

import java.util.Date;

/**
 *  A class representing a user of the system
 */
public class User {
	
	private Integer userID;
	private String username;
	private String password;
	private Date dateOfBirth;
	private Long locationID;
	private Location location;
	private boolean isFemale;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password, Date dateOfBirth, Long locationID, boolean isFemale) {
		this.username = username;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.locationID = locationID;
		this.isFemale = isFemale;
	}
	
	public User(Integer ID, String username, String password, Date dateOfBirth, Long locationID, boolean isFemale) {
		this(username,password,dateOfBirth,locationID,isFemale);
		this.userID = ID;
	}
	
	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Long getLocationID() {
		return locationID;
	}

	public void setLocationID(Long locationID) {
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
	
	public boolean isFemale() {
		return isFemale;
	}
	
	public void setFemale(boolean isFemale) {
		this.isFemale = isFemale;
	}
	

}
