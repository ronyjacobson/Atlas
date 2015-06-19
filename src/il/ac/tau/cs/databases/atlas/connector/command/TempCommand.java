package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPoolHolder;
import il.ac.tau.cs.databases.atlas.connector.DynamicConnectionPool;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseProgressDBCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;


/**
 * Created by user on 17/06/2015.
 */
public class TempCommand extends BaseProgressDBCommand {
    private int end;

    public TempCommand(int end) {
        this.end = end;
    }

    @Override
    protected String getFrameLabel() {
        return "Temp command";
    }

    @Override
    protected String getSuccessMessage() {
        return "Process finished. You are the queen!";
    }

    @Override
    protected void runProgressCmd(Connection con) throws AtlasServerException {
        for (int i = 0; i < end; i++) {
            System.out.println(i);
            progressUpdater.updateProgress(4*i, "progress: " + i);
            if (i == 10) {
                throw new AtlasServerException("paz");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Temp cmd stopped");
                throw new AtlasServerException("stopped");
            }
        }
    }

    @Override
    protected String getDisplayLabel() {
        return "Atlas, making the world a better place";
    }

    public static void main(String[] args) throws AtlasServerException {
        DynamicConnectionPool dynamicConnectionPool = new DynamicConnectionPool();
        dynamicConnectionPool.initialize("DbMysql06", "DbMysql06", "localhost", "3306", "DbMysql06");
        ConnectionPoolHolder.INSTANCE.set(dynamicConnectionPool);
        new TempCommand(4).execute();
    }
}
