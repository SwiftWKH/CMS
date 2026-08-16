package brightcare.client.gateway;

public class UserSummary {
    private final String username;
    private final String role;
    private final String status;

    public UserSummary(String username, String role, String status) {
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }
}
