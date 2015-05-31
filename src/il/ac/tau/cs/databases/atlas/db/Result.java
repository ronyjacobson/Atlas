package il.ac.tau.cs.databases.atlas.db;

import java.util.Date;

public class Result {

	/**
	 *  A class representing a query result
	 */
	private String id;
	private String name;
	private Location location;
	private Date date;
	private boolean isBirth;
	private String summary;
	private String wikiLink;
	
	public Result(String id, String name, Location location, Date date, boolean isBirth, String summary, String wikiLink) {
		this.id = id;
		this.name = name;
		this.location = location;
		this.isBirth = isBirth;
		this.date = date;
		this.summary = summary;
		this.wikiLink = wikiLink;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public boolean isBirth() {
		return isBirth;
	}

	public void setBirth(boolean isBirth) {
		this.isBirth = isBirth;
	}
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getWikiLink() {
		return wikiLink;
	}

	public void setWikiLink(String wikiLink) {
		this.wikiLink = wikiLink;
	}
}
