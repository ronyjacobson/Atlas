package il.ac.tau.cs.databases.atlas.connector.command;

import com.mysql.jdbc.PreparedStatement;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.Location;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22/05/2015.
 */
public class DummyCMD extends BaseDBCommand<List<Location>> {
    private int param;

    public DummyCMD(int param) {
        this.param = param;
    }

    @Override
    protected List<Location> innerExecute(Connection con) throws AtlasServerException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            List<Location> list = new ArrayList<>();
            statement = con.createStatement();
            resultSet = statement.executeQuery("SELECT * from location");
            while (resultSet.next()) {
                final int location_id = resultSet.getInt("location_ID");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            safelyClose(statement, resultSet);
        }

        return null;
    }


    public static void main(String[] args) throws AtlasServerException {
        DummyCMD cmd = new DummyCMD(123);
        cmd.execute();
    }
}
