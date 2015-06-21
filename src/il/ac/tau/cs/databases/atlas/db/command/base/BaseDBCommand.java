package il.ac.tau.cs.databases.atlas.db.command.base;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPool;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;

import java.sql.Connection;

import org.apache.log4j.Logger;

/**
 * Created by user on 14/05/2015.
 */
public abstract class BaseDBCommand<T> {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    private ConnectionPool connectionPool;

    public BaseDBCommand() {
        connectionPool = ConnectionPoolHolder.INSTANCE.get();
    }

    public T execute() throws AtlasServerException {
        final Connection con = connectionPool.checkOut();
        T result = innerExecute(con);
        connectionPool.checkIn(con);
        return result;
    }

    /**
     * This method is responsible for logic using the connection
     *
     * @return the value extracted from statement or resultSet
     */
    protected abstract T innerExecute(Connection con) throws AtlasServerException;

    /**
     * Attempts to close all the given resources, ignoring errors
     *
     * @param resources resources for closing
     */
    protected void safelyClose(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            try {
                resource.close();
            } catch (Exception e) {
            }
        }
    }
}