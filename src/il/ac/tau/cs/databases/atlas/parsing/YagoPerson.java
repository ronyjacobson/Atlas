package il.ac.tau.cs.databases.atlas.parsing;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * java representation of a person record
 */
public class YagoPerson {
    private long yagoId;
    private int personId;
    private Long bornInLocation;
    private Long diedInLocation;
    private java.sql.Date bornOnDate;
    private java.sql.Date diedOnDate;
    private Set<Integer> categories;
    private String wikiUrl;
    private boolean isFemale;
    private String prefLabel;
    private Set<String> labels;

    public YagoPerson(long yagoId) {
        this.yagoId = yagoId;
        this.categories = new HashSet<>();
        this.labels = new HashSet<>();
    }

    public long getYagoId() {
        return yagoId;
    }

    public Long getBornInLocation() {
        return bornInLocation;
    }

    public void setBornInLocation(Long bornInLocation) {
        this.bornInLocation = bornInLocation;
    }

    public Long getDiedInLocation() {
        return diedInLocation;
    }

    public void setDiedInLocation(Long diedInLocation) {
        this.diedInLocation = diedInLocation;
    }

    public Date getDiedOnDate() {
        return diedOnDate;
    }

    public void setDiedOnDate(Date diedOnDate) {
        this.diedOnDate = diedOnDate;
    }

    public Date getBornOnDate() {
        return bornOnDate;
    }

    public void setBornOnDate(Date bornOnDate) {
        this.bornOnDate = bornOnDate;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }

    public boolean isFemale() {
        return isFemale;
    }

    public void setIsFemale(boolean isFemale) {
        this.isFemale = isFemale;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public Set<Integer> getCategories() {
        return categories;
    }

    public void addCategory(int categoryId) {
        this.categories.add(categoryId);
    }

    public boolean isValidPerson() {
        return (bornInLocation != null && bornOnDate != null);
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void addLabel(String label) {
        this.labels.add(label);
    }

    /**
     * true iff person has at least one label
     * if was missing prefLabel, set it to the first label
     */
    public boolean isValidPersonLabels() {
        String label = labels.iterator().next();
        if (prefLabel == null) {
            prefLabel = label;
        }
        return (label != null);
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }
}
