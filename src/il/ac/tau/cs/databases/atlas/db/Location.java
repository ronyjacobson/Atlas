package il.ac.tau.cs.databases.atlas.db;

/**
 * A class representing a location in the world
 */
public class Location {
	private int id;
	private String name;
	private double lat = 0;
	private double lng = 0;

	public Location(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public Location(int id, String name, double lat, double lng) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLat() {
		return lat;
	}

	public void setLongLat(double lng, double lat) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLng() {
		return lng;
	}

}
