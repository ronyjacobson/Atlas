package il.ac.tau.cs.databases.atlas.parsing;

public class PersonLifetime {
    long yagoId;
    long bornInLocation;
    Long diedInLocation; // nullable
    String bornOnDate;
    String diedOnDate;

    public PersonLifetime(long yagoId) {
        this.yagoId = yagoId;
    }

    public long getYagoId() {
        return yagoId;
    }

    public void setYagoId(long yagoId) {
        this.yagoId = yagoId;
    }

    public long getBornInLocation() {
        return bornInLocation;
    }

    public void setBornInLocation(long bornInLocation) {
        this.bornInLocation = bornInLocation;
    }

    public Long getDiedInLocation() {
        return diedInLocation;
    }

    public void setDiedInLocation(Long diedInLocation) {
        this.diedInLocation = diedInLocation;
    }

    public String getBornOnDate() {
        return bornOnDate;
    }

    public void setBornOnDate(String bornOnDate) {
        this.bornOnDate = bornOnDate;
    }

    public String getDiedOnDate() {
        return diedOnDate;
    }

    public void setDiedOnDate(String diedOnDate) {
        this.diedOnDate = diedOnDate;
    }
}
