package brightcare.security;

import brightcare.model.UserAccount;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class SessionManager {
    public static final long DEFAULT_SESSION_TIMEOUT_MILLIS = 30L * 60L * 1000L;

    private final Map<String, SessionInfo> sessionsByToken;
    private final Map<Integer, String> tokensByUserId;
    private long sessionTimeoutMillis;

    public SessionManager() {
        this(DEFAULT_SESSION_TIMEOUT_MILLIS);
    }

    public SessionManager(long sessionTimeoutMillis) {
        this.sessionsByToken = new HashMap<String, SessionInfo>();
        this.tokensByUserId = new HashMap<Integer, String>();
        this.sessionTimeoutMillis = sessionTimeoutMillis;
    }

    public synchronized String createSession(UserAccount userAccount) {
        if (userAccount == null) {
            throw new IllegalArgumentException("User account is required.");
        }

        removeSessionByUserId(userAccount.getUserId());

        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusNanos(sessionTimeoutMillis * 1000000L);

        SessionInfo sessionInfo = new SessionInfo(
                token,
                userAccount.getUserId(),
                userAccount.getUsername(),
                userAccount.getRole(),
                now,
                expiresAt
        );

        sessionsByToken.put(token, sessionInfo);
        tokensByUserId.put(userAccount.getUserId(), token);
        return token;
    }

    public synchronized boolean validateSession(String token) {
        SessionInfo sessionInfo = sessionsByToken.get(token);
        if (sessionInfo == null) {
            return false;
        }

        if (sessionInfo.isExpired()) {
            removeSession(token);
            return false;
        }

        return true;
    }

    public synchronized boolean isUserLoggedIn(int userId) {
        String token = tokensByUserId.get(userId);
        return token != null && validateSession(token);
    }

    public synchronized boolean removeSession(String token) {
        SessionInfo removed = sessionsByToken.remove(token);
        if (removed == null) {
            return false;
        }

        tokensByUserId.remove(removed.getUserId());
        return true;
    }

    public synchronized boolean removeSessionByUserId(int userId) {
        String token = tokensByUserId.remove(userId);
        if (token == null) {
            return false;
        }

        sessionsByToken.remove(token);
        return true;
    }

    public synchronized String getTokenForUser(int userId) {
        String token = tokensByUserId.get(userId);
        if (token == null || !validateSession(token)) {
            return null;
        }
        return token;
    }

    public synchronized SessionInfo getSession(String token) {
        if (!validateSession(token)) {
            return null;
        }
        return sessionsByToken.get(token);
    }

    public synchronized int cleanupExpiredSessions() {
        int removedCount = 0;
        Iterator<Map.Entry<String, SessionInfo>> iterator = sessionsByToken.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, SessionInfo> entry = iterator.next();
            SessionInfo sessionInfo = entry.getValue();
            if (sessionInfo.isExpired()) {
                iterator.remove();
                tokensByUserId.remove(sessionInfo.getUserId());
                removedCount++;
            }
        }

        return removedCount;
    }

    public synchronized int getActiveSessionCount() {
        cleanupExpiredSessions();
        return sessionsByToken.size();
    }

    public long getSessionTimeoutMillis() {
        return sessionTimeoutMillis;
    }

    public void setSessionTimeoutMillis(long sessionTimeoutMillis) {
        if (sessionTimeoutMillis <= 0) {
            throw new IllegalArgumentException("Session timeout must be greater than zero.");
        }
        this.sessionTimeoutMillis = sessionTimeoutMillis;
    }

    public static class SessionInfo {
        private final String token;
        private final int userId;
        private final String username;
        private final String role;
        private final LocalDateTime createdAt;
        private final LocalDateTime expiresAt;

        public SessionInfo(String token, int userId, String username, String role,
                LocalDateTime createdAt, LocalDateTime expiresAt) {
            this.token = token;
            this.userId = userId;
            this.username = username;
            this.role = role;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
        }

        public String getToken() {
            return token;
        }

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }
}
