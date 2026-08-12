package brightcare.service;

import brightcare.model.UserAccount;
import brightcare.security.PermissionChecker;
import brightcare.security.SessionManager;
import brightcare.util.BrightCareLogger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger LOGGER = BrightCareLogger.getLogger(AuthService.class);

    private final UserAccountRepository userAccountRepository;
    private final SessionManager sessionManager;
    private final PermissionChecker permissionChecker;
    private final PasswordHasher passwordHasher;

    public AuthService() {
        this(new EmptyUserAccountRepository(), new SessionManager(), new PermissionChecker(), new Sha256PasswordHasher());
    }

    public AuthService(UserAccountRepository userAccountRepository) {
        this(userAccountRepository, new SessionManager(), new PermissionChecker(), new Sha256PasswordHasher());
    }

    public AuthService(UserAccountRepository userAccountRepository, SessionManager sessionManager,
            PermissionChecker permissionChecker, PasswordHasher passwordHasher) {
        if (userAccountRepository == null) {
            throw new IllegalArgumentException("User account repository is required.");
        }
        if (sessionManager == null) {
            throw new IllegalArgumentException("Session manager is required.");
        }
        if (permissionChecker == null) {
            throw new IllegalArgumentException("Permission checker is required.");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher is required.");
        }

        this.userAccountRepository = userAccountRepository;
        this.sessionManager = sessionManager;
        this.permissionChecker = permissionChecker;
        this.passwordHasher = passwordHasher;
    }

    public UserAccount login(String username, String password) {
        LOGGER.info("AuthService.login called for username=" + safeUsername(username) + ".");
        if (isBlank(username) || isBlank(password)) {
            LOGGER.warning("Login rejected because username or password was blank.");
            return null;
        }

        UserAccount userAccount = userAccountRepository.findByUsername(username.trim());
        if (userAccount == null || !userAccount.isActive()) {
            LOGGER.warning("Login rejected because account was not found or inactive. username="
                    + safeUsername(username) + ".");
            return null;
        }

        if (!passwordHasher.matches(password, userAccount.getPasswordHash())) {
            LOGGER.warning("Login rejected because password hash did not match. username="
                    + safeUsername(username) + ", userId=" + userAccount.getUserId() + ".");
            return null;
        }

        sessionManager.createSession(userAccount);
        LOGGER.info("Login accepted. username=" + userAccount.getUsername()
                + ", userId=" + userAccount.getUserId()
                + ", role=" + userAccount.getRole() + ".");
        return userAccount;
    }

    public boolean logout(int userId) {
        return sessionManager.removeSessionByUserId(userId);
    }

    public boolean checkPermission(int userId, String requiredRole) {
        UserAccount userAccount = userAccountRepository.findByUserId(userId);
        if (userAccount == null || !userAccount.isActive()) {
            return false;
        }

        if (!sessionManager.isUserLoggedIn(userId)) {
            return false;
        }

        return permissionChecker.hasRole(userAccount.getRole(), requiredRole);
    }

    public String getSessionTokenForUser(int userId) {
        return sessionManager.getTokenForUser(userId);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public PermissionChecker getPermissionChecker() {
        return permissionChecker;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    private String safeUsername(String username) {
        return username == null ? "<null>" : username.trim();
    }

    public interface UserAccountRepository {
        UserAccount findByUsername(String username);

        UserAccount findByUserId(int userId);
    }

    public interface PasswordHasher {
        String hash(String plainTextPassword);

        boolean matches(String plainTextPassword, String storedPasswordHash);
    }

    private static class EmptyUserAccountRepository implements UserAccountRepository {
        public UserAccount findByUsername(String username) {
            return null;
        }

        public UserAccount findByUserId(int userId) {
            return null;
        }
    }

    public static class Sha256PasswordHasher implements PasswordHasher {
        public String hash(String plainTextPassword) {
            if (plainTextPassword == null) {
                throw new IllegalArgumentException("Password is required.");
            }

            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(plainTextPassword.getBytes("UTF-8"));
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < hashBytes.length; i++) {
                    String hex = Integer.toHexString(0xff & hashBytes[i]);
                    if (hex.length() == 1) {
                        builder.append('0');
                    }
                    builder.append(hex);
                }

                return builder.toString();
            } catch (NoSuchAlgorithmException ex) {
                throw new IllegalStateException("SHA-256 is not available.", ex);
            } catch (java.io.UnsupportedEncodingException ex) {
                throw new IllegalStateException("UTF-8 is not available.", ex);
            }
        }

        public boolean matches(String plainTextPassword, String storedPasswordHash) {
            if (plainTextPassword == null || storedPasswordHash == null) {
                return false;
            }

            return hash(plainTextPassword).equalsIgnoreCase(storedPasswordHash);
        }
    }
}
