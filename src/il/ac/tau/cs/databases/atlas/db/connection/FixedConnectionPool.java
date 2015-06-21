package il.ac.tau.cs.databases.atlas.db.connection;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * This class is a singleton connection pool (thus the enum) with a fixed number of connections.
 * When all connections are used up, null is returned instead of connection.
 */
public class FixedConnectionPool implements ConnectionPool {
	
	private static final Logger log = Logger.getLogger(ConnectionPool.class);
    private String userName;
    private String password;
    private String ip;
    private String port;
    private String dbName;
    private int maxConnections;
    private BlockingQueue<Connection> connectionPool;

    public synchronized void initialize(String userName, String password, String ip, String port, String dbName) throws AtlasServerException {
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        this.maxConnections = DriverConstants.MAX_NUM_OF_CONNECTIONS;
        connectionPool = new ArrayBlockingQueue<>(maxConnections);

        //Here we can initialize all the information that we need
        DriverManager.setLoginTimeout(DriverConstants.CONNECTION_TIMEOUT);
        initializeConnectionPool();
    }

    private void initializeConnectionPool() throws AtlasServerException {
        while (!isConnectionPoolFull()) {
            log.info("Connection Pool is NOT full. Proceeding with adding new connections");
            //Adding new connection instance until the pool is full
            connectionPool.add(createNewConnectionForPool());

        }
        log.info("Connection Pool is full.");
    }

    private boolean isConnectionPoolFull() {
        //Check the pool size
        return connectionPool.size() >= maxConnections;
    }

    //Creating a connection
    private Connection createNewConnectionForPool() throws AtlasServerException {
    	log.info("Creatring new connection...");
        Connection connection;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbBaseUrl + ip + ":" + port + "/" + dbName, userName, password);
            log.info(DriverManager.getLoginTimeout());
            log.info("Created new connection: " + connection);
        } catch (SQLException sqle) {
            log.error("SQLException: " + sqle);
            log.info("Unable to connect - " + sqle.getMessage());
            throw new AtlasServerException("Unable to create connection pool");
        } catch (ClassNotFoundException cnfe) {
            log.error("ClassNotFoundException: " + cnfe);
            log.info("Unable to load the MySQL JDBC driver..");
            throw new AtlasServerException("Unable to create connection pool");
        }
        log.info("Creating new connection done.");
        return connection;
    }

    /**
     * @return connection if there is one available or null where pool is empty
     */
    public Connection checkOut() {
    	log.info("Checking out connection...");
        //Check if there is a connection available. There are times when all the connections in the pool may be used up
        Connection connection = null;
        try {
            connection = connectionPool.poll(20, TimeUnit.SECONDS); // Block if no connection is available
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Giving away the connection from the connection pool
        log.info("Checking out connection done.");
        return connection;
    }

    public void checkIn(Connection connection) {
    	log.info("Checking in connection...");
        //Adding the connection from the client back to the connection pool
        connectionPool.add(connection);
        log.info("Checking in done.");
    }

    public int getNumberOfTotalConnectionsAvailable() {
        return maxConnections;
    }

    public void close() {
        log.info("closing connection pool");
        for (Connection conn : connectionPool)
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                //log.info("error while closing DB connections+: e");
            }
    }
}
