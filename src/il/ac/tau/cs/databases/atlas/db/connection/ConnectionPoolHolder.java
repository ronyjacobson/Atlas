package il.ac.tau.cs.databases.atlas.db.connection;

/**
 * A singleton for getting and setting the connection pool
 */
public enum ConnectionPoolHolder {
    INSTANCE;

    private ConnectionPool connectionPool;


    public ConnectionPool get() {
        return connectionPool;
    }

    public void set(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
