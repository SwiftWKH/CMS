package brightcare.dao;

import brightcare.model.Doctor;
import brightcare.model.Patient;
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
    private final HospitalApiClient apiClient;
    private final AuthService.PasswordHasher passwordHasher;

    public UserAccountDAO() {
        this(new DerbyConnectionFactory(), new HospitalApiClient(), new AuthService.Sha256PasswordHasher());
    }

    public UserAccountDAO(DerbyConnectionFactory connectionFactory) {
        this(connectionFactory, new HospitalApiClient(), new AuthService.Sha256PasswordHasher());
    }

    public UserAccountDAO(DerbyConnectionFactory connectionFactory, HospitalApiClient apiClient,
            AuthService.PasswordHasher passwordHasher) {
        if (connectionFactory == null) {
            throw new IllegalArgumentException("Connection factory is required.");
        }
        if (apiClient == null) {
            throw new IllegalArgumentException("API client is required.");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher is required.");
        }
        this.connectionFactory = connectionFactory;
        this.apiClient = apiClient;
        this.passwordHasher = passwordHasher;
    }

    public UserAccount findByUsername(String username) {
        LOGGER.info("Looking up user account by username: " + username);
        UserAccount account = findApiUserByUsername(username);
        if (account != null) {
            return account;
        }

        account = findOne("SELECT user_id, username, password_hash, role, status "
                + "FROM USER_ACCOUNT WHERE username = ?", username);
        return account == null ? findExternalUserByUsername(username) : account;
    }

    public UserAccount findByUserId(int userId) {
        LOGGER.info("Looking up user account by userId: " + userId);
        UserAccount account = findApiUserById(userId);
        if (account != null) {
            return account;
        }

        account = findOne("SELECT user_id, username, password_hash, role, status "
                + "FROM USER_ACCOUNT WHERE user_id = ?", Integer.valueOf(userId));
        return account == null ? findExternalUserById(userId) : account;
    }

    public List<UserAccount> findAll() {
        List<UserAccount> apiAccounts = findAllFromApi();
        if (!apiAccounts.isEmpty()) {
            return apiAccounts;
        }

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
        UserAccount account = new UserAccount(0, username, passwordHash, role, "ACTIVE");
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                LOGGER.info("Creating user account through hospital API. username=" + username
                        + ", role=" + role + ".");
                String response = apiClient.post("/user", HospitalJsonMapper.userJson(account));
                List<UserAccount> accounts = HospitalJsonMapper.userAccounts(response);
                if (!accounts.isEmpty()) {
                    UserAccount created = accounts.get(0);
                    LOGGER.info("Hospital API user create returned userId=" + created.getUserId()
                            + ", username=" + created.getUsername()
                            + ", role=" + created.getRole()
                            + ", status=" + created.getStatus() + ".");
                    return created;
                }
                LOGGER.info("Hospital API user create returned no parseable user; returning submitted account.");
                return account;
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Hospital API user create failed; falling back to Derby. username="
                        + username + ".", ex);
            }
        }

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
        if (DataSourceConfig.preferHospitalApi()) {
            UserAccount account = findApiUserByUsername(username);
            if (account != null) {
                try {
                    LOGGER.info("Disabling user account through hospital API. username=" + username
                            + ", userId=" + account.getUserId() + ".");
                    account.setStatus("DISABLED");
                    apiClient.put("/user/" + account.getUserId(), HospitalJsonMapper.userJson(account));
                    return true;
                } catch (RuntimeException ex) {
                    LOGGER.log(Level.WARNING, "Hospital API user disable failed; falling back to Derby. username="
                            + username + ".", ex);
                }
            } else {
                LOGGER.info("Hospital API user disable found no matching user; Derby fallback will be used. username="
                        + username + ".");
            }
        }

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

    public UserAccount update(UserAccount account) {
        if (account == null || account.getUserId() <= 0) {
            return null;
        }
        if (DataSourceConfig.preferHospitalApi()) {
            try {
                LOGGER.info("Updating user account through hospital API. userId=" + account.getUserId()
                        + ", username=" + account.getUsername() + ".");
                String response = apiClient.put("/user/" + account.getUserId(), HospitalJsonMapper.userJson(account));
                List<UserAccount> accounts = HospitalJsonMapper.userAccounts(response);
                return accounts.isEmpty() ? account : accounts.get(0);
            } catch (RuntimeException ex) {
                LOGGER.log(Level.WARNING, "Hospital API user update failed; falling back to Derby. userId="
                        + account.getUserId() + ".", ex);
            }
        }

        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = connectionFactory.getConnection();
            statement = connection.prepareStatement("UPDATE USER_ACCOUNT SET username = ?, password_hash = ?, "
                    + "role = ?, status = ? WHERE user_id = ?");
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getPasswordHash());
            statement.setString(3, account.getRole());
            statement.setString(4, account.getStatus());
            statement.setInt(5, account.getUserId());
            if (statement.executeUpdate() == 0) {
                return null;
            }
            return account;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "User account update failed. userId=" + account.getUserId()
                    + ", sqlState=" + ex.getSQLState() + ", errorCode=" + ex.getErrorCode(), ex);
            return null;
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
                0,
                resultSet.getString("status")
        );
    }

    private UserAccount findApiUserByUsername(String username) {
        if (username == null || username.trim().length() == 0) {
            return null;
        }
        List<UserAccount> accounts = findAllFromApi();
        for (int i = 0; i < accounts.size(); i++) {
            UserAccount account = accounts.get(i);
            if (username.trim().equalsIgnoreCase(account.getUsername())) {
                LOGGER.info("Hospital API user account matched username. userId=" + account.getUserId()
                        + ", username=" + account.getUsername()
                        + ", role=" + account.getRole()
                        + ", status=" + account.getStatus() + ".");
                return account;
            }
        }
        if (DataSourceConfig.preferHospitalApi()) {
            LOGGER.info("Hospital API user lookup returned no match; Derby fallback will be used. username="
                    + username + ".");
        }
        return null;
    }

    private UserAccount findApiUserById(int userId) {
        if (userId <= 0) {
            return null;
        }
        List<UserAccount> accounts = findAllFromApi();
        for (int i = 0; i < accounts.size(); i++) {
            UserAccount account = accounts.get(i);
            if (account.getUserId() == userId) {
                LOGGER.info("Hospital API user account matched userId. userId=" + account.getUserId()
                        + ", username=" + account.getUsername()
                        + ", role=" + account.getRole()
                        + ", status=" + account.getStatus() + ".");
                return account;
            }
        }
        if (DataSourceConfig.preferHospitalApi()) {
            LOGGER.info("Hospital API user lookup returned no match; Derby fallback will be used. userId="
                    + userId + ".");
        }
        return null;
    }

    private List<UserAccount> findAllFromApi() {
        List<UserAccount> accounts = new ArrayList<UserAccount>();
        if (!DataSourceConfig.preferHospitalApi()) {
            return accounts;
        }
        try {
            LOGGER.info("Loading user accounts from hospital API endpoint /user.");
            accounts = HospitalJsonMapper.userAccounts(apiClient.get("/user"));
            LOGGER.info("Hospital API user endpoint returned " + accounts.size() + " account(s).");
            if (accounts.isEmpty()) {
                LOGGER.info("Hospital API user endpoint returned no accounts; Derby fallback will be used.");
            }
            return accounts;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Hospital API user endpoint failed; Derby fallback will be used.", ex);
            return accounts;
        }
    }

    private UserAccount findExternalUserByUsername(String username) {
        if (!isRoleCodeFallbackEnabled() || !DataSourceConfig.preferHospitalApi() || username == null) {
            return null;
        }
        String trimmed = username.trim();
        if (trimmed.length() < 2) {
            return null;
        }

        char prefix = Character.toUpperCase(trimmed.charAt(0));
        int id = parsePositiveId(trimmed.substring(1));
        if (id <= 0) {
            LOGGER.info("External user lookup skipped; username is not a role-code identity. username=" + trimmed + ".");
            return null;
        }

        if (prefix == 'D') {
            return findExternalDoctorUser(trimmed, id);
        }
        if (prefix == 'P') {
            return findExternalPatientUser(trimmed, id);
        }

        LOGGER.info("External user lookup skipped; unsupported role-code prefix. username=" + trimmed + ".");
        return null;
    }

    private UserAccount findExternalUserById(int userId) {
        if (!isRoleCodeFallbackEnabled() || !DataSourceConfig.preferHospitalApi() || userId <= 0) {
            return null;
        }
        UserAccount doctor = findExternalDoctorUser("D" + userId, userId);
        if (doctor != null) {
            return doctor;
        }
        return findExternalPatientUser("P" + userId, userId);
    }

    private UserAccount findExternalDoctorUser(String username, int doctorId) {
        try {
            Doctor doctor = new DoctorDAO(apiClient).findById(doctorId);
            if (doctor == null) {
                LOGGER.warning("External doctor login rejected because doctor was not found. username="
                        + username + ", doctorId=" + doctorId + ".");
                return null;
            }
            LOGGER.info("External doctor user resolved through hospital API. username="
                    + username + ", doctorId=" + doctorId + ".");
            return externalUser(doctorId, username, "DOCTOR");
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "External doctor user lookup failed. username="
                    + username + ", doctorId=" + doctorId + ".", ex);
            return null;
        }
    }

    private UserAccount findExternalPatientUser(String username, int patientId) {
        try {
            Patient patient = new PatientDAO(apiClient).findById(patientId);
            if (patient == null) {
                LOGGER.warning("External patient login rejected because patient was not found. username="
                        + username + ", patientId=" + patientId + ".");
                return null;
            }
            LOGGER.info("External patient user resolved through hospital API. username="
                    + username + ", patientId=" + patientId + ".");
            return externalUser(patientId, username, "PATIENT");
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "External patient user lookup failed. username="
                    + username + ", patientId=" + patientId + ".", ex);
            return null;
        }
    }

    private UserAccount externalUser(int userId, String username, String role) {
        return new UserAccount(userId, username, defaultPasswordHash(role, username),
                role, userId, "ACTIVE");
    }

    private String defaultPasswordHash(String role, String username) {
        StringBuilder hashes = new StringBuilder();
        appendHash(hashes, defaultPassword(role, username));
        if ("DOCTOR".equals(role)) {
            appendHash(hashes, "doctor123");
        } else if ("PATIENT".equals(role)) {
            appendHash(hashes, "patient123");
        }
        appendHash(hashes, username);
        return hashes.toString();
    }

    private void appendHash(StringBuilder hashes, String password) {
        if (!hasText(password)) {
            return;
        }
        String hash = passwordHasher.hash(password.trim());
        if (hashes.indexOf(hash) >= 0) {
            return;
        }
        if (hashes.length() > 0) {
            hashes.append('|');
        }
        hashes.append(hash);
    }

    private String defaultPassword(String role, String username) {
        String rolePassword = System.getProperty("brightcare.external." + role.toLowerCase() + "Password");
        if (hasText(rolePassword)) {
            return rolePassword.trim();
        }
        String sharedPassword = System.getProperty("brightcare.external.defaultPassword");
        if (hasText(sharedPassword)) {
            return sharedPassword.trim();
        }
        if (Boolean.parseBoolean(System.getProperty("brightcare.external.allowUsernamePassword", "true"))) {
            return username;
        }
        if ("DOCTOR".equals(role)) {
            return "doctor123";
        }
        if ("PATIENT".equals(role)) {
            return "patient123";
        }
        return "password";
    }

    private int parsePositiveId(String text) {
        try {
            int parsed = Integer.parseInt(text.trim());
            return parsed > 0 ? parsed : 0;
        } catch (Exception ex) {
            return 0;
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private boolean isRoleCodeFallbackEnabled() {
        return Boolean.parseBoolean(System.getProperty("brightcare.external.roleCodeFallback", "false"));
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
