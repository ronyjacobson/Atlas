package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.ScriptRunner;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseDBCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by user on 20/05/2015.
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
        DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06", "127.0.0.1", "3305", "DbMysql06");
        new CreateSchemaCommand().execute();
    }
}


