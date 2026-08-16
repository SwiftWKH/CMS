package brightcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ActiveSessionInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private LocalDateTime loginTime;
    private String role;

    public ActiveSessionInfo() {
    }

    public ActiveSessionInfo(String username, LocalDateTime loginTime, String role) {
        this.username = username;
        this.loginTime = loginTime;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
