package brightcare.client.common.controller;

import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.model.UserAccount;
import javax.swing.JFrame;

public class LoginController {
    private final AuthenticationGateway authenticationGateway;
    private final NavigationController navigationController;

    public LoginController() {
        this(new NavigationController());
    }

    public LoginController(NavigationController navigationController) {
        this(new UnavailableAuthenticationGateway(), navigationController);
    }

    public LoginController(AuthenticationGateway authenticationGateway, NavigationController navigationController) {
        if (authenticationGateway == null) {
            throw new IllegalArgumentException("Authentication gateway is required.");
        }
        if (navigationController == null) {
            throw new IllegalArgumentException("Navigation controller is required.");
        }
        this.authenticationGateway = authenticationGateway;
        this.navigationController = navigationController;
    }

    public LoginResult login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return LoginResult.failure("Username and password are required.");
        }

        UserAccount userAccount = authenticationGateway.login(username, password);
        if (userAccount == null) {
            return LoginResult.failure("Invalid username or password.");
        }

        return LoginResult.success(userAccount);
    }

    public void openRoleFrame(UserAccount userAccount, JFrame currentFrame) {
        if (userAccount == null) {
            navigationController.showAccessDenied(currentFrame);
            return;
        }

        navigationController.openFrameForUser(userAccount, currentFrame);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static class LoginResult {
        private final boolean successful;
        private final UserAccount userAccount;
        private final String message;

        private LoginResult(boolean successful, UserAccount userAccount, String message) {
            this.successful = successful;
            this.userAccount = userAccount;
            this.message = message;
        }

        public static LoginResult success(UserAccount userAccount) {
            return new LoginResult(true, userAccount, null);
        }

        public static LoginResult failure(String message) {
            return new LoginResult(false, null, message);
        }

        public boolean isSuccessful() {
            return successful;
        }

        public UserAccount getUserAccount() {
            return userAccount;
        }

        public String getMessage() {
            return message;
        }
    }
}
