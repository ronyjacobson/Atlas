package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.db.DBConstants;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.utils.DateUtils;
import il.ac.tau.cs.databases.atlas.utils.GrapicUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Date;

public class UpdatePerson extends BaseModifyPerson {

    protected int NUM_OF_COMPONENTS = 9;
    protected int GAP_BETWEEN_COMPONENTS = 16;
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
    protected String getTitleText() {
        return "Edit this person:";
    }

    @Override
    protected void addClearTextBoxListenersIfNeeded() {}

    @Override
    protected void setCategoriesComboBox(Font fieldFont) {}

    @Override
    protected void addCategoryBarToPanel(JPanel panel) {}

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

        // if name wasn't changed we are changing the very same person, so he's obviously in the db
        boolean checkIfPersonExists = true;
        if (name.equals(name.getText())) {
            checkIfPersonExists = false;
        }
        Main.queries.updateRecord(
                personId, name.getText(), birthDate, birthLocationId,
                deathDate, deathLocationId, link, isFemale.isSelected(), checkIfPersonExists);
    }

    @Override
    protected boolean isInputValidated() {
        if (name.getText().trim().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(null, "Name can not be blank.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (name.getText().length() > DBConstants.PREF_LABEL_SIZE) {
            JOptionPane.showMessageDialog(null, "Name can not exceed "
                            + DBConstants.PREF_LABEL_SIZE + " characters.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (wasBornOn.getCalendar() == null) {
            JOptionPane.showMessageDialog(null, "Please choose a birth date",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if ((hasDiedOn.getCalendar() != null)
                && DateUtils.isSameDay(wasBornOn.getCalendar(),
                hasDiedOn.getCalendar())
                && (!hasDiedIn.getSelectedItem().toString()
                .equals(NOT_DEAD_LOCATION))) {
            JOptionPane.showMessageDialog(null,
                    "No way that the birth and death dates are the same.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (hasDiedOn.getCalendar() != null
                && DateUtils.isAfterDay(wasBornOn.getCalendar(),
                hasDiedOn.getCalendar())
                && (!hasDiedIn.getSelectedItem().toString()
                .equals(NOT_DEAD_LOCATION))) {
            JOptionPane.showMessageDialog(null,
                    "No way that the birth date is after the death date.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (wasBornIn.getSelectedItem().toString()
                .equals(DEFAULT_BIRTH_LOCATION) || wasBornIn.getSelectedItem().toString()
                .equals("")) {
            JOptionPane.showMessageDialog(null,
                    "Please choose a birth place from the list.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (!locations.contains(wasBornIn.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(null,
                    "Please choose a birth place that exists in the list.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (!hasDiedIn.getSelectedItem().toString().equals("") &&
                !locations.contains(hasDiedIn.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(null,
                    "Please choose a death place that exists in the list.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (hasDiedIn.getSelectedItem().toString().equals("")
                && hasDiedOn.getCalendar() != null) {
            JOptionPane.showMessageDialog(null,
                    "Please choose a death place from the list.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (wikiLink.getText().length() > DBConstants.WIKI_URL_SIZE) {
            JOptionPane.showMessageDialog(null,
                    "Wikipedia link can not exceed "
                            + DBConstants.WIKI_URL_SIZE + " characters.",
                    GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (hasDiedOn.getCalendar() == null) {
            //make sure the date is valid if a location was entered
            if (!hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION) &&
                    !hasDiedIn.getSelectedItem().toString().equals("")) {
                JOptionPane.showMessageDialog(null,
                        "Please choose a death date or remove the death location",
                        GrapicUtils.PROJECT_NAME, JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        } else if (hasDiedIn.getSelectedItem().toString().equals(NOT_DEAD_LOCATION)) {
            if (hasDiedOn.getCalendar() != null) {
                int reply = JOptionPane
                        .showConfirmDialog(
                                null,
                                "<html>You mentioned this person is not dead but entered a death date.<br>"
                                        + "This person will be added without the death date.<br>Continue anyway?</html>",
                                GrapicUtils.PROJECT_NAME,
                                JOptionPane.YES_NO_OPTION);
                return reply == JOptionPane.YES_OPTION;
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException, AtlasServerException {
        DynamicConnectionPool dynamicConnectionPool = new DynamicConnectionPool();
        dynamicConnectionPool.initialize("DbMysql06", "DbMysql06", "localhost", "3306", "DbMysql06");
        ConnectionPoolHolder.INSTANCE.set(dynamicConnectionPool);

        new UpdatePerson(123, "paz", "London", new Date(325454643), "London", new Date(1253254), "http://www.etan.rona", true);
    }
}
