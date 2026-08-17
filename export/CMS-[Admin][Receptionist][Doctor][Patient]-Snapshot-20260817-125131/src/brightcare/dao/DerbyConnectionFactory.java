package brightcare.dao;

import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DerbyConnectionFactory {
    public static final String DEFAULT_URL = "jdbc:derby://localhost:1527/BRIGHTCARE_DB";
    public static final String DEFAULT_USER = "app";
    public static final String DEFAULT_PASSWORD = "app";

    private static final Logger LOGGER = BrightCareLogger.getLogger(DerbyConnectionFactory.class);

    private final String url;
    private final String username;
    private final String password;

    public DerbyConnectionFactory() {
        this(
                System.getProperty("brightcare.db.url", DEFAULT_URL),
                System.getProperty("brightcare.db.user", DEFAULT_USER),
                System.getProperty("brightcare.db.password", DEFAULT_PASSWORD)
        );
    }

    public DerbyConnectionFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        LOGGER.info("Opening Derby connection to " + url + " as user " + username + ".");
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            LOGGER.info("Derby connection opened successfully.");
            return connection;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Derby connection failed. url=" + url + ", user=" + username
                    + ", sqlState=" + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            throw ex;
        }
    }
}
