package com.petcare.util;

import com.petcare.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Pattern implementation for Managing Database Connections.
 * Ensures a single shared configuration and coordinated connection lifecycle.
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "petcare_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    private static String dbHost = DEFAULT_HOST;
    private static String dbPort = DEFAULT_PORT;
    private static String dbName = DEFAULT_DB;
    private static String dbUser = DEFAULT_USER;
    private static String dbPassword = DEFAULT_PASS;

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor to enforce Singleton Pattern
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found in classpath", e);
            throw new DatabaseException("MySQL JDBC Driver missing. Please ensure mysql-connector-j is included.", e);
        }
    }

    /**
     * Thread-safe Singleton accessor.
     * @return DatabaseConnection singleton instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtains an active connection to the MySQL database.
     * Reopens connection if closed or invalid.
     * @return Connection object
     * @throws DatabaseException if connection fails
     */
    public synchronized Connection getConnection() throws DatabaseException {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                String url = String.format(
                    "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                    dbHost, dbPort, dbName
                );
                connection = DriverManager.getConnection(url, dbUser, dbPassword);
            }
            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to MySQL database: " + e.getMessage(), e);
            throw new DatabaseException(
                "Unable to connect to MySQL database at " + dbHost + ":" + dbPort + "/" + dbName + 
                ". Please ensure MySQL / XAMPP is running. Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Test connection without throwing runtime exception.
     * @return true if database is reachable
     */
    public static boolean testConnection() {
        try {
            Connection conn = getInstance().getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Allows dynamic configuration changes (e.g. if user has custom MySQL port/pass).
     */
    public static synchronized void configure(String host, String port, String database, String user, String password) {
        dbHost = (host != null && !host.trim().isEmpty()) ? host.trim() : DEFAULT_HOST;
        dbPort = (port != null && !port.trim().isEmpty()) ? port.trim() : DEFAULT_PORT;
        dbName = (database != null && !database.trim().isEmpty()) ? database.trim() : DEFAULT_DB;
        dbUser = (user != null) ? user.trim() : DEFAULT_USER;
        dbPassword = (password != null) ? password : "";

        if (instance != null && instance.connection != null) {
            try {
                instance.connection.close();
            } catch (SQLException ignored) {}
            instance.connection = null;
        }
    }

    public static String getDbUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }

    public static String getDbUser() {
        return dbUser;
    }
}
