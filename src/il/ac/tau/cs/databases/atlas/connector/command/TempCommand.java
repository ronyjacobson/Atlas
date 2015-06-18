package il.ac.tau.cs.databases.atlas.connector.command;

import il.ac.tau.cs.databases.atlas.ProgressUpdater;
import il.ac.tau.cs.databases.atlas.connector.command.base.BaseProgressDBCommand;
import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;

/**
 * Created by user on 17/06/2015.
 */
public class TempCommand extends BaseProgressDBCommand<Boolean> {
    private int end;

    public TempCommand(int end) {
        this.end = end;
    }

    @Override
    protected Boolean innerExecute(Connection con) throws AtlasServerException {
        for (int i = 0; i < end; i++) {
            System.out.println(i);
            progressUpdater.updateProgress(4*i, "progress: " + i);
            if (i == 10) {
                throw new AtlasServerException("paz");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
