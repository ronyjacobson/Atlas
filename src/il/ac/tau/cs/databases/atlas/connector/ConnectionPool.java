package il.ac.tau.cs.databases.atlas.connector;

import il.ac.tau.cs.databases.atlas.exception.AtlasServerException;

import java.sql.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public enum ConnectionPool {
    INSTANCE;

    private static final String dbBaseUrl = "jdbc:mysql://";
    private String userName;
    private String password;
    private String ip;
    private String port;
    private String dbName;
    private int maxConnections;
    private ConcurrentLinkedQueue<Connection> connectionPool;

    public synchronized void initialize(String userName, String password, String ip, String port, String dbName) throws AtlasServerException {
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.maxConnections = DriverConstants.MAX_NUM_OF_CONNECTIONS;
        connectionPool = new ConcurrentLinkedQueue<>();

        //Here we can initialize all the information that we need
        DriverManager.setLoginTimeout(DriverConstants.CONNECTION_TIMEOUT);
        initializeConnectionPool();
    }

    private void initializeConnectionPool() throws AtlasServerException {
        while (!isConnectionPoolFull()) {
            System.out.println("Connection Pool is NOT full. Proceeding with adding new connections");
            //Adding new connection instance until the pool is full
            connectionPool.add(createNewConnectionForPool());

        }
        System.out.println("Connection Pool is full.");
    }

    private boolean isConnectionPoolFull() {
        //Check the pool size
        return connectionPool.size() >= maxConnections;
    }

    //Creating a connection
    private Connection createNewConnectionForPool() throws AtlasServerException {
        Connection connection;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbBaseUrl + ip + ":" + port + "/" + dbName, userName, password);
            System.out.println(DriverManager.getLoginTimeout());
            System.out.println("Connection: " + connection);
        } catch (SQLException sqle) {
            System.err.println("SQLException: " + sqle);
            System.out.println("Unable to connect - " + sqle.getMessage());
            throw new AtlasServerException("Unable to create connection pool");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("ClassNotFoundException: " + cnfe);
            System.out.println("Unable to load the MySQL JDBC driver..");
            throw new AtlasServerException("Unable to create connection pool");
        }

        return connection;
    }

    /**
     * @return connection if there is one available or null where pool is empty
     */
    public Connection getConnectionFromPool() {
        //Check if there is a connection available. There are times when all the connections in the pool may be used up
        Connection connection = connectionPool.poll();
        //Giving away the connection from the connection pool
        return connection;
    }

    public void returnConnectionToPool(Connection connection) {
        //Adding the connection from the client back to the connection pool
        connectionPool.add(connection);
    }

    public int getNumberOfTotalConnectionsAvailable() {
        return maxConnections;
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
