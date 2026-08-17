package brightcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ActiveSessionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String role;
    private String status;

    public ActiveSessionInfo() {
    }

    public ActiveSessionInfo(String username, LocalDateTime loginTime, String role) {
        this(username, loginTime, null, role, "ACTIVE");
    }

    public ActiveSessionInfo(String username, LocalDateTime loginTime, LocalDateTime logoutTime,
            String role, String status) {
        this.username = username;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.role = role;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
