package il.ac.tau.cs.databases.atlas.db;

import java.util.Date;

/**
 *  A class representing a user of the system
 */
public class User {
	
	private String username;
	private String password;
	private Date dateOfBirth;
	private String location;
	
	public User(String username, String password, Date dateOfBirth, String location) {
		this.username = username;
		this.password = password;
		this.dateOfBirth = dateOfBirth;
		this.location = location;
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
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

}
