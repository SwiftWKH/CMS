package brightcare.client.gateway;

public class UserSummary {
    private final int userId;
    private final String username;
    private final String passwordHash;
    private final String role;
    private final int roleId;
    private final String status;

    public UserSummary(String username, String role, String status) {
        this(0, username, "", role, 0, status);
    }

    public UserSummary(int userId, String username, String passwordHash, String role, int roleId, String status) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.roleId = roleId;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public int getRoleId() {
        return roleId;
    }

    public String getStatus() {
        return status;
    }
}
