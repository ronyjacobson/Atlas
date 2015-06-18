package il.ac.tau.cs.databases.atlas.connector.command.base;

import il.ac.tau.cs.databases.atlas.ProgressUpdater;

/**
 * Created by user on 17/06/2015.
 */
public abstract class BaseProgressDBCommand<T> extends BaseDBCommand<T> {
    protected ProgressUpdater progressUpdater = null;

    public void setUpdater(ProgressUpdater progressUpdater) {
        this.progressUpdater = progressUpdater;
    }
}
