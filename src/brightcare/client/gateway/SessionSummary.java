package brightcare.client.gateway;

public class SessionSummary {
    private final String username;
    private final String loginTime;
    private final String logoutTime;
    private final String status;
    private final String role;

    public SessionSummary(String username, String loginTime, String role) {
        this(username, loginTime, "", "ACTIVE", role);
    }

    public SessionSummary(String username, String loginTime, String logoutTime, String status, String role) {
        this.username = username;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.status = status;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }
}
