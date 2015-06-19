package il.ac.tau.cs.databases.atlas;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.IOException;
import java.util.Date;

public class UpdatePerson extends BaseModifyPerson{

    private int personId;

    public UpdatePerson(int personId) throws IOException {
        this.personId = personId;
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
    protected void execQuery(Long birthLocaionID, Long deathLocaionID, Date birthDate, Date deathDate, String link) throws AtlasServerException {
        Main.queries.updateRecord(personId, name.getText(), category.getSelectedItem().toString(), birthDate, birthLocaionID, deathDate, deathLocaionID, link, isFemale.isSelected());
    }

    public static void main(String[] args) throws IOException, AtlasServerException {
        DynamicConnectionPool dynamicConnectionPool = new DynamicConnectionPool();
        dynamicConnectionPool.initialize("DbMysql06", "DbMysql06", "localhost", "3306", "DbMysql06");
        ConnectionPoolHolder.INSTANCE.set(dynamicConnectionPool);

        new UpdatePerson(123);
    }
}
