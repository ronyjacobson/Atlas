package il.ac.tau.cs.databases.atlas.db.command;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.db.connection.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.utils.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * A query that creates the Atlas DB schema with all the tables.
 */
public class CreateSchemaCommand extends BaseDBCommand<Boolean> {
    @Override
    protected Boolean innerExecute(Connection con) throws AtlasServerException {
        ScriptRunner runner = new ScriptRunner(con, false, true);
        try {
            runner.runScript(new BufferedReader(new FileReader("sql\\createSchema.sql")));
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            throw new AtlasServerException(e.getMessage());
        }
        return true;
    }

    public static void main(String[] args) throws AtlasServerException {
        DynamicConnectionPool dynamicConnectionPool = new DynamicConnectionPool();
        dynamicConnectionPool.initialize("DbMysql06", "DbMysql06", "127.0.0.1", "3305", "DbMysql06");
        ConnectionPoolHolder.INSTANCE.set(dynamicConnectionPool);
        new CreateSchemaCommand().execute();
    }
}


