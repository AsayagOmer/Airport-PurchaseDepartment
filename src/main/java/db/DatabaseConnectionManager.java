package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {

    private static final Properties properties = new Properties();

    // Static block: runs only once when the class is loaded into memory
    static {
        // Load the configuration file from Maven's resources folder
        try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("Error: database.properties file not found in resources folder.");
                throw new RuntimeException("Database configuration file missing.");
            }
            properties.load(input);

            // Load the PostgreSQL driver to ensure it is recognized by the DriverManager
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            System.err.println("Error loading database configuration: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an active connection to the database.
     * DAO classes should call this method whenever they need to execute a query.
     * @return Connection object configured via database.properties
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        String dbUrl = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.password");

        return DriverManager.getConnection(dbUrl, user, pass);
    }
}