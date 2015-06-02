package il.ac.tau.cs.databases.atlas.db;


import java.sql.Date;
import java.util.Map;

// TODO: probably not needed

public class Person {
    private Map<String, Boolean> labels;
    private Location bornInlocation;
    private Location diedInlocation;
    private Date bornOnDate;
    private Date diedOnDate;
    private boolean gender;
    private int addedByUser;
    private String wikiLink;
    private String category;
    private String yagoID;

    public Person(Map<String, Boolean> labels, Location bornInlocation, Location diedInlocation, Date bornOnDate, Date diedOnDate, boolean gender, int addedByUser, String wikiLink, String category, String yagoID) {
        this.labels = labels;
        this.bornInlocation = bornInlocation;
        this.diedInlocation = diedInlocation;
        this.bornOnDate = bornOnDate;
        this.diedOnDate = diedOnDate;
        this.gender = gender;
        this.addedByUser = addedByUser;
        this.wikiLink = wikiLink;
        this.category = category;
        this.yagoID = yagoID;
    }

    public Person(String yagoID) {
        this.yagoID = yagoID;
    }

    public Map<String, Boolean> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Boolean> labels) {
        this.labels = labels;
    }

    public Location getBornInlocation() {
        return bornInlocation;
    }

    public void setBornInlocation(Location bornInlocation) {
        this.bornInlocation = bornInlocation;
    }

    public Location getDiedInlocation() {
        return diedInlocation;
    }

    public void setDiedInlocation(Location diedInlocation) {
        this.diedInlocation = diedInlocation;
    }

    public Date getBornOnDate() {
        return bornOnDate;
    }

    public void setBornOnDate(Date bornOnDate) {
        this.bornOnDate = bornOnDate;
    }

    public Date getDiedOnDate() {
        return diedOnDate;
    }

    public void setDiedOnDate(Date diedOnDate) {
        this.diedOnDate = diedOnDate;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getAddedByUser() {
        return addedByUser;
    }

    public void setAddedByUser(int addedByUser) {
        this.addedByUser = addedByUser;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public void setWikiLink(String wikiLink) {
        this.wikiLink = wikiLink;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getYagoID() {
        return yagoID;
    }

    public void setYagoID(String yagoID) {
        this.yagoID = yagoID;
    }
}




