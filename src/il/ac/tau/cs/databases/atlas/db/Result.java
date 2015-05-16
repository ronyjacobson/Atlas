package il.ac.tau.cs.databases.atlas.db;

import java.util.Date;

public class Result {

	/**
	 *  A class representing a query result
	 */
	private String name;
	private Location placeOnBirth;
	private Date dateOfBirth;
	private Location placeOnDeath;
	private Date dateOfDeath;
	private String summary;
	private String wikiLink;
	
	public Result(String name, Location placeOnBirth, Date dateOfBirth, Location placeOnDeath, Date dateOfDeath, String summary, String wikiLink) {
		this.name = name;
		this.placeOnBirth = placeOnBirth;
		this.dateOfBirth = dateOfBirth;
		this.placeOnDeath = placeOnDeath;
		this.dateOfDeath = dateOfDeath;
		this.summary = summary;
		this.wikiLink = wikiLink;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getPlaceOnBirth() {
		return placeOnBirth;
	}

	public void setPlaceOnBirth(Location placeOnBirth) {
		this.placeOnBirth = placeOnBirth;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Location getPlaceOnDeath() {
		return placeOnDeath;
	}

	public void setPlaceOnDeath(Location placeOnDeath) {
		this.placeOnDeath = placeOnDeath;
	}

	public Date getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(Date dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
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
