package il.ac.tau.cs.databases.atlas.db.connection;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;

import java.sql.Connection;

/**
 * Interface for connection pooling
 */
public interface ConnectionPool {
    String dbBaseUrl = "jdbc:mysql://";



    void initialize(String userName, String password, String ip, String port, String dbName) throws AtlasServerException;

    /**
     * @return connection from pool
     * @throws AtlasServerException
     */
    Connection checkOut() throws AtlasServerException;

    /**
     * @param connection
     * Return connection to pool
     */
    void checkIn(Connection connection);

    /**
     * Close all connections
     */
    void close();

}
