package il.ac.tau.cs.databases.atlas.db.command;

import il.ac.tau.cs.databases.atlas.db.command.base.BaseDBCommand;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Check if we have basic connection to the DB.
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
