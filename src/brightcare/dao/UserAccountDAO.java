package brightcare.dao;

import brightcare.model.UserAccount;
import brightcare.service.AuthService;
import brightcare.util.BrightCareLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAccountDAO implements AuthService.UserAccountRepository {
    private static final Logger LOGGER = BrightCareLogger.getLogger(UserAccountDAO.class);

    private final DerbyConnectionFactory connectionFactory;

    public UserAccountDAO() {
        this(new DerbyConnectionFactory());
    }

    public UserAccountDAO(DerbyConnectionFactory connectionFactory) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        this.connectionFactory = connectionFactory;
    }

    public UserAccount findByUsername(String username) {
        LOGGER.info("Looking up user account by username: " + username);
        return findOne("SELECT user_id, username, password_hash, role, status "
                + "FROM USER_ACCOUNT WHERE username = ?", username);
    }

    public UserAccount findByUserId(int userId) {
        LOGGER.info("Looking up user account by userId: " + userId);
        return findOne("SELECT user_id, username, password_hash, role, status "
                + "FROM USER_ACCOUNT WHERE user_id = ?", Integer.valueOf(userId));
    }

    public List<UserAccount> findAll() {
        List<UserAccount> accounts = new ArrayList<UserAccount>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("SELECT user_id, username, password_hash, role, status "
                    + "FROM USER_ACCOUNT ORDER BY user_id");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                accounts.add(mapUserAccount(resultSet));
            }
            return accounts;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User account list failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return accounts;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    public UserAccount create(String username, String passwordHash, String role) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            int userId = nextUserId(connection);
            statement = connection.prepareStatement("INSERT INTO USER_ACCOUNT "
                    + "(user_id, username, password_hash, role, status) VALUES (?, ?, ?, ?, 'ACTIVE')");
            statement.setInt(1, userId);
            statement.setString(2, username);
            statement.setString(3, passwordHash);
            statement.setString(4, role);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }
            return new UserAccount(userId, username, passwordHash, role, "ACTIVE");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User account create failed. username=" + username
                    + ", sqlState=" + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return null;
        } finally {
            close(statement);
            close(connection);
        }
    }

    public boolean disableByUsername(String username) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("UPDATE USER_ACCOUNT SET status = 'DISABLED' "
                    + "WHERE username = ?");
            statement.setString(1, username);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User account disable failed. username=" + username
                    + ", sqlState=" + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return false;
        } finally {
            close(statement);
            close(connection);
        }
    }

    private UserAccount findOne(String sql, Object parameter) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement(sql);
            if (parameter instanceof Integer) {
                statement.setInt(1, ((Integer) parameter).intValue());
            } else {
                statement.setString(1, String.valueOf(parameter));
            }

            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                LOGGER.info("User account lookup returned no rows.");
                return null;
            }

            UserAccount account = mapUserAccount(resultSet);
            LOGGER.info("User account found: userId=" + account.getUserId()
                    + ", username=" + account.getUsername()
                    + ", role=" + account.getRole()
                    + ", status=" + account.getStatus() + ".");
            return account;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User account lookup failed. sqlState=" + ex.getSQLState()
                    + ", errorCode=" + ex.getErrorCode(), ex);
            return null;
        } finally {
            close(resultSet);
            close(statement);
            close(connection);
        }
    }

    private UserAccount mapUserAccount(ResultSet resultSet) throws SQLException {
        return new UserAccount(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getString("role"),
                resultSet.getString("status")
        );
    }

    private int nextUserId(Connection connection) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement("SELECT COALESCE(MAX(user_id), 0) + 1 FROM USER_ACCOUNT");
            resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 1;
        } finally {
            close(resultSet);
            close(statement);
        }
    }

    private void close(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Unable to close Derby resource.", ex);
        }
    }
}
