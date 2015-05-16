package il.ac.tau.cs.databases.atlas.connector.command.base;

import il.ac.tau.cs.databases.atlas.connector.ConnectionPool;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by user on 14/05/2015.
 */
public abstract class BaseDBCommand<T> {
    protected final Logger logger = Logger.getLogger(this.getClass().getName());
    private static ConnectionPool connectionPool = ConnectionPool.INSTANCE;


    public T execute() {
        final Connection con = connectionPool.getConnectionFromPool();
        T result = innerExecute(con);
        connectionPool.returnConnectionToPool(con);
        return result;
    }

    /**
     * This method is responsible for logic using the connection
     *
     * @return the value extracted from statement or resultSet
     */
    protected abstract T innerExecute(Connection con);

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