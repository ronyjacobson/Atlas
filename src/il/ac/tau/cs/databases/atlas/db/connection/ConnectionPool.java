package il.ac.tau.cs.databases.atlas.db.connection;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;

import java.sql.Connection;

/**
 * TODO
 */
public interface ConnectionPool {
    String dbBaseUrl = "jdbc:mysql://";



    void initialize(String userName, String password, String ip, String port, String dbName) throws AtlasServerException;
    Connection checkOut() throws AtlasServerException;
    void checkIn(Connection connection);
    void close();

}
