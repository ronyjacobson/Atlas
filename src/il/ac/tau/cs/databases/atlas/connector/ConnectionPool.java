package il.ac.tau.cs.databases.atlas.connector;

import java.util.*;
import java.sql.*;

public class ConnectionPool {
    private static final String dbBaseUrl = "jdbc:mysql://";
    private String userName;
    private String password;
    private String ip;
    private String port;
    private String dbName;
    private int maxConnections;
    private Vector<Connection> connectionPool;

    public ConnectionPool(String userName, String password, String ip, String port, String dbName) {
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.maxConnections = DriverConstants.MAX_NUM_OF_CONNECTIONS;
        connectionPool = new Vector<>();
        initialize();
    }

    private void initialize() {
        //Here we can initialize all the information that we need
        DriverManager.setLoginTimeout(DriverConstants.CONNECTION_TIMEOUT);
        initializeConnectionPool();
    }

    private void initializeConnectionPool() {
        while (!isConnectionPoolFull()) {
            System.out.println("Connection Pool is NOT full. Proceeding with adding new connections");
            //Adding new connection instance until the pool is full
            connectionPool.addElement(createNewConnectionForPool());
        }
        System.out.println("Connection Pool is full.");
    }

    private synchronized boolean isConnectionPoolFull() {
        //Check the pool size
        return connectionPool.size() >= maxConnections;
    }

    //Creating a connection
    private Connection createNewConnectionForPool() {
        Connection connection;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbBaseUrl + ip + ":" + port + "/" + dbName, userName, password);
            System.out.println(DriverManager.getLoginTimeout());
            System.out.println("Connection: " + connection);
        } catch (SQLException sqle) {
            System.err.println("SQLException: " + sqle);
            System.out.println("Unable to connect - " + sqle.getMessage());
            return null;
        } catch (ClassNotFoundException cnfe) {
            System.err.println("ClassNotFoundException: " + cnfe);
            System.out.println("Unable to load the MySQL JDBC driver..");
            return null;
        }

        return connection;
    }

    public synchronized Connection getConnectionFromPool() {
        Connection connection = null;

        //Check if there is a connection available. There are times when all the connections in the pool may be used up
        if (connectionPool.size() > 0) {
            connection = connectionPool.firstElement();
            connectionPool.removeElementAt(0);
        }
        //Giving away the connection from the connection pool
        return connection;
    }

    public synchronized void returnConnectionToPool(Connection connection) {
        //Adding the connection from the client back to the connection pool
        connectionPool.addElement(connection);
    }

    public int getNumberOfTotalConnectionsAvailable() {
        return this.maxConnections;
    }

    public void close() {
        for (Connection conn : connectionPool)
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                //System.out.println("error while closing DB connections+: e");
            }
    }
}
