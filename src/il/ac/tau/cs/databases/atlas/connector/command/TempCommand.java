package il.ac.tau.cs.databases.atlas.connector.command;

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
    protected void runProgressCmd(Connection con) throws AtlasServerException {
        for (int i = 0; i < end; i++) {
            System.out.println(i);
            progressUpdater.updateProgress(4*i, "progress: " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getDisplayLabel() {
        return "Atlas, making the world a better place";
    }

    public static void main(String[] args) throws AtlasServerException {
        DynamicConnectionPool.INSTANCE.initialize("DbMysql06", "DbMysql06", "127.0.0.1", "3306", "DbMysql06");
        new TempCommand(4).execute();
    }
}
