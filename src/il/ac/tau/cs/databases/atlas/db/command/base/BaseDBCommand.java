package il.ac.tau.cs.databases.atlas.db.command.base;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPool;
import il.ac.tau.cs.databases.atlas.db.connection.ConnectionPoolHolder;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Base command for getting a connection from the thread pool and returning it for all commands.
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

    protected void rollback(Connection con) {
        try {
            logger.warn("Transaction failed! Rolling back");
            con.rollback();
            logger.info("Rollback Successfully :)");
        } catch (SQLException e) {
            logger.error("ERROR demoTransactions (when rollbacking) - " + e.getMessage());
        }
    }

    /**
     * Attempts to set the connection back to auto-commit, ignoring errors.
     */
    protected void safelyResetAutoCommit(Connection con) {
        try {
            con.setAutoCommit(true);
        } catch (Exception e) {
        }
    }
}
