package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by user on 19/06/2015.
 */
public class CheckConnectivityCommand extends BaseDBCommand<Boolean> {
    @Override
    protected Boolean innerExecute(Connection con) {
        Statement statement = null;
        try {
            statement = con.createStatement();
            statement.execute("SELECT 1");
        } catch (SQLException sqle) {
            return false;
        } finally {
            safelyClose(statement);
        }
        return true;
    }
}
