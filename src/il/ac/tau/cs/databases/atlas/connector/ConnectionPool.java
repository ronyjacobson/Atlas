package il.ac.tau.cs.databases.atlas.connector;

import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.Connection;

/**
 * Created by user on 19/05/2015.
 */
public interface ConnectionPool {
    String dbBaseUrl = "jdbc:mysql://";



    void initialize(String userName, String password, String ip, String port, String dbName) throws AtlasServerException;
    Connection checkOut() throws AtlasServerException;
    void checkIn(Connection connection);
    void close();

}
