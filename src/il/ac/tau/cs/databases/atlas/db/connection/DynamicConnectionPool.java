package il.ac.tau.cs.databases.atlas.db.connection;

import il.ac.tau.cs.databases.atlas.core.exception.AtlasServerException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * TODO
 */
public class DynamicConnectionPool implements ConnectionPool {
    
    private static final Logger log = Logger.getLogger(ConnectionPool.class);

    private String userName;
    private String password;
    private String ip;
    private String port;
    private String dbName;
    private Map<Connection, Long> locked, unlocked;
    private long expirationTime;

    @Override
    public void initialize(String userName, String password, String ip, String port, String dbName) throws AtlasServerException {
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.dbName = dbName;
        locked = new ConcurrentHashMap<>();
        unlocked = new ConcurrentHashMap<>();
        expirationTime = DriverConstants.EXPIRATION_TIME;
    }

    @Override
    public synchronized Connection checkOut() throws AtlasServerException {
    	log.info("Checking out connection...");
        long now = System.currentTimeMillis();
        Connection connection;
        if (unlocked.size() > 0) {
            Set<Connection> connections = unlocked.keySet();
            final Iterator<Connection> connectionIterator = connections.iterator();
            while (connectionIterator.hasNext()) {
                connection = connectionIterator.next();
                if ((now - unlocked.get(connection)) > expirationTime) {
                    // object has expired
                    unlocked.remove(connection);
                    closeConnection(connection);
                    connection = null;
                } else {
                    if (isValid(connection)) {
                        unlocked.remove(connection);
                        locked.put(connection, now);
                        return connection;
                    } else {
                        // object failed validation
                        unlocked.remove(connection);
                        closeConnection(connection);
                    }
                }
            }
        }
        // no objects available, create a new one
        connection = createConnection();
        locked.put(connection, now);
        log.info("Checking out connection done.");
        return connection;
    }

    private Connection createConnection() throws AtlasServerException {
        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // connection = DriverManager.getConnection(dbBaseUrl + ip + ":" + port + "/" + dbName, userName, password);
            connection = DriverManager.getConnection(dbBaseUrl + ip + ":" + port + "/" + dbName + "?rewriteBatchedStatements=true", userName, password);
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
        return connection;
    }

    private void closeConnection(Connection connection){
        log.info("closing connection: " + connection);
        try {
            connection.close();
            log.info("closing connection done");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid(Connection connection) {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void checkIn(Connection connection) {
    	log.info("Checking in connection...");
        locked.remove(connection);
        unlocked.put(connection, System.currentTimeMillis());
        log.info("Checking in connection done.");
    }

    @Override
    public synchronized void close() {
        log.info("Closing connection pool");
        for (Connection connection : locked.keySet()) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        locked.clear();
        for (Connection connection : unlocked.keySet()) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        unlocked.clear();
    }
}
