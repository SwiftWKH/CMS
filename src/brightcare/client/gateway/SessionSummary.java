package brightcare.client.gateway;

public class SessionSummary {
    private final String username;
    private final String loginTime;
    private final String role;

    public SessionSummary(String username, String loginTime, String role) {
        this.username = username;
        this.loginTime = loginTime;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public String getRole() {
        return role;
    }
}
