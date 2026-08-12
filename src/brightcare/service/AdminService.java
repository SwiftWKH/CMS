package brightcare.service;

import brightcare.dao.UserAccountDAO;
import brightcare.model.ActiveSessionInfo;
import brightcare.model.UserAccount;
import brightcare.security.PermissionChecker;
import brightcare.security.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private final UserAccountDAO userAccountDAO;
    private final SessionManager sessionManager;
    private final AuthService.PasswordHasher passwordHasher;
    private final PermissionChecker permissionChecker;

    public AdminService(UserAccountDAO userAccountDAO, SessionManager sessionManager) {
        this(userAccountDAO, sessionManager, new AuthService.Sha256PasswordHasher(), new PermissionChecker());
    }

    public AdminService(UserAccountDAO userAccountDAO, SessionManager sessionManager,
            AuthService.PasswordHasher passwordHasher, PermissionChecker permissionChecker) {
        if (userAccountDAO == null) {
            throw new IllegalArgumentException("User account DAO is required.");
        }
        if (sessionManager == null) {
            throw new IllegalArgumentException("Session manager is required.");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher is required.");
        }
        if (permissionChecker == null) {
            throw new IllegalArgumentException("Permission checker is required.");
        }
        this.userAccountDAO = userAccountDAO;
        this.sessionManager = sessionManager;
        this.passwordHasher = passwordHasher;
        this.permissionChecker = permissionChecker;
    }

    public List<UserAccount> viewUsers() {
        return userAccountDAO.findAll();
    }

    public UserAccount createUser(String username, String password, String role) {
        if (isBlank(username) || isBlank(password) || isBlank(role)) {
            return null;
        }
        String normalizedRole = role.trim().toUpperCase();
        if (!permissionChecker.isValidRole(normalizedRole)) {
            return null;
        }
        if (userAccountDAO.findByUsername(username.trim()) != null) {
            return null;
        }
        return userAccountDAO.create(username.trim(), passwordHasher.hash(password), normalizedRole);
    }

    public boolean disableUser(String username) {
        if (isBlank(username)) {
            return false;
        }
        UserAccount account = userAccountDAO.findByUsername(username.trim());
        if (account == null) {
            return false;
        }
        boolean disabled = userAccountDAO.disableByUsername(username.trim());
        if (disabled) {
            sessionManager.removeSessionByUserId(account.getUserId());
        }
        return disabled;
    }

    public List<ActiveSessionInfo> viewActiveSessions() {
        List<ActiveSessionInfo> activeSessions = new ArrayList<ActiveSessionInfo>();
        List<SessionManager.SessionInfo> sessions = sessionManager.getActiveSessions();
        for (SessionManager.SessionInfo session : sessions) {
            activeSessions.add(new ActiveSessionInfo(
                    session.getUsername(),
                    session.getCreatedAt(),
                    session.getRole()
            ));
        }
        return activeSessions;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
