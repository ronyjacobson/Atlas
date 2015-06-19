package il.ac.tau.cs.databases.atlas.parsing;

public class YagoLocation {
    private long locationId;
    private String name;
    private double latitude;
    private double longitude;
    private String wikiUrl;
    private boolean used;

    public YagoLocation() {
    }

    public YagoLocation(long locationId) {
        this.locationId = locationId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isValidLocation() {
        return ((!(latitude == 0.0 && longitude == 0.0)) && locationId != 0l && name != null);
    }
}
