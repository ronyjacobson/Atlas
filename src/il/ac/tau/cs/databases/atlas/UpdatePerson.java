package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.IOException;
import java.util.Date;

public class UpdatePerson extends BaseModifyPerson{

    private int personId;
    private String personName;
    private String bLocation;
    private Date bDate;
    private String dLocation;
    private Date dDate;
    private String wikiURL;
    private boolean personIsFemale;

    public UpdatePerson(int personId, String personName, String bLocation, Date bDate, String dLocation, Date dDate, String wikiURL, boolean personIsFemale) {
        this.personId = personId;
        this.personName = personName;
        this.bLocation = bLocation;
        this.bDate = bDate;
        this.dLocation = dLocation;
        this.dDate = dDate;
        this.wikiURL = wikiURL;
        this.personIsFemale = personIsFemale;

        populateFields();
    }

    public void populateFields() {
        name.setText(personName);
        if (personIsFemale) {
            isFemale.setSelected(true);
            isMale.setSelected(false);
        } else {
            isFemale.setSelected(false);
            isMale.setSelected(true);
        }
        wasBornIn.setSelectedItem(bLocation);
        hasDiedIn.setSelectedItem(dLocation);
        wasBornOn.setDate(bDate);
        hasDiedOn.setDate(dDate);
        wikiLink.setText(wikiURL);
    }

    @Override
    protected String getButtonText() {
        return "Update Record";
    }

    @Override
    protected void showMessage() {
        triggerJsCode("personUpdated('"
                + name.getText() + "');");
    }

    @Override
    protected void execQuery(Long birthLocationId, Long deathLocationId, Date birthDate, Date deathDate, String link) throws AtlasServerException {
        Main.queries.updateRecord(
                personId, name.getText(), birthDate, birthLocationId,
                deathDate, deathLocationId, link, isFemale.isSelected());
    }

    public static void main(String[] args) throws IOException, AtlasServerException {
        DynamicConnectionPool dynamicConnectionPool = new DynamicConnectionPool();
        dynamicConnectionPool.initialize("DbMysql06", "DbMysql06", "localhost", "3306", "DbMysql06");
        ConnectionPoolHolder.INSTANCE.set(dynamicConnectionPool);

        new UpdatePerson(123, "paz", "London", new Date(325454643), "London", new Date(1253254), "http://www.etan.rona", true);
    }
}
