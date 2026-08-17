package brightcare.client.common.controller;

import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.RmiAdminGateway;
import brightcare.client.gateway.RmiAuthenticationGateway;
import brightcare.client.gateway.RmiDoctorGateway;
import brightcare.client.gateway.RmiGatewayFactory;
import brightcare.client.gateway.RmiPatientGateway;
import brightcare.client.gateway.RmiReceptionistGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.model.UserAccount;
import brightcare.remote.ClinicRemoteInterface;
import brightcare.util.BrightCareLogger;
import javax.swing.JFrame;
import java.util.logging.Logger;

public class LoginController {
    private static final Logger LOGGER = BrightCareLogger.getLogger(LoginController.class);

    private AuthenticationGateway authenticationGateway;
    private NavigationController navigationController;
    private String connectedHost;

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

    public boolean connectToServer(String host) {
        String resolvedHost = host == null || host.trim().length() == 0 ? "localhost" : host.trim();
        if (resolvedHost.equalsIgnoreCase(connectedHost)
                && !(authenticationGateway instanceof UnavailableAuthenticationGateway)) {
            return true;
        }

        ClinicRemoteInterface remote = RmiGatewayFactory.lookupRemote(resolvedHost);
        if (remote == null) {
            LOGGER.warning("Unable to connect login controller to RMI host=" + resolvedHost + ".");
            authenticationGateway = new UnavailableAuthenticationGateway();
            navigationController = new NavigationController();
            connectedHost = null;
            return false;
        }

        RmiAuthenticationGateway rmiAuthenticationGateway = new RmiAuthenticationGateway(remote);
        authenticationGateway = rmiAuthenticationGateway;
        navigationController = new NavigationController(
                rmiAuthenticationGateway,
                new RmiAdminGateway(remote),
                new RmiPatientGateway(remote),
                new RmiDoctorGateway(remote),
                new RmiReceptionistGateway(remote)
        );
        connectedHost = resolvedHost;
        LOGGER.info("Login controller connected to RMI host=" + connectedHost + ".");
        return true;
    }

    public LoginResult login(String username, String password) {
        LOGGER.info("LoginController.login called for username=" + safeUsername(username) + ".");
        if (isBlank(username) || isBlank(password)) {
            LOGGER.warning("Login blocked by client validation: blank username or password.");
            return LoginResult.failure("Username and password are required.");
        }

        UserAccount userAccount = authenticationGateway.login(username, password);
        if (userAccount == null) {
            LOGGER.warning("Login failed after gateway call for username=" + safeUsername(username) + ".");
            return LoginResult.failure("Invalid username or password.");
        }

        LOGGER.info("Login successful for username=" + userAccount.getUsername()
                + ", role=" + userAccount.getRole() + ".");
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

    private String safeUsername(String username) {
        return username == null ? "<null>" : username.trim();
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
