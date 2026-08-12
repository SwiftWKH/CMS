package brightcare.client.common.controller;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.admin.view.AdminFrame;
import brightcare.client.common.view.AccessDeniedDialog;
import brightcare.client.common.view.LoginFrame;
import brightcare.client.common.view.SessionExpiredDialog;
import brightcare.client.gateway.AdminGateway;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.UnavailableAdminGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.model.UserAccount;
import brightcare.security.PermissionChecker;
import javax.swing.JFrame;

public class NavigationController {
    private final AuthenticationGateway authenticationGateway;
    private final AdminGateway adminGateway;

    public NavigationController() {
        this(new UnavailableAuthenticationGateway(), new UnavailableAdminGateway());
    }

    public NavigationController(AuthenticationGateway authenticationGateway, AdminGateway adminGateway) {
        if (authenticationGateway == null) {
            throw new IllegalArgumentException("Authentication gateway is required.");
        }
        if (adminGateway == null) {
            throw new IllegalArgumentException("Admin gateway is required.");
        }
        this.authenticationGateway = authenticationGateway;
        this.adminGateway = adminGateway;
    }

    public void openFrameForUser(UserAccount userAccount, JFrame currentFrame) {
        if (userAccount == null) {
            showAccessDenied(currentFrame);
            return;
        }
        openFrameForRole(userAccount.getRole(), userAccount.getUserId(), currentFrame);
    }

    public void openFrameForRole(String role, JFrame currentFrame) {
        openFrameForRole(role, 0, currentFrame);
    }

    public void openFrameForRole(String role, int userId, JFrame currentFrame) {
        JFrame nextFrame;

        if (PermissionChecker.ROLE_ADMIN.equalsIgnoreCase(role)) {
            AdminController adminController = new AdminController(
                    this,
                    adminGateway,
                    authenticationGateway,
                    userId
            );
            nextFrame = new AdminFrame(adminController);
        } else {
            showAccessDenied(currentFrame);
            return;
        }

        showNextFrame(currentFrame, nextFrame);
    }

    public void openMainMenu(JFrame currentFrame) {
        openLogin(currentFrame);
    }

    public void openLogin(JFrame currentFrame) {
        showNextFrame(currentFrame, new LoginFrame(new LoginController(authenticationGateway, this)));
    }

    public void showAccessDenied(JFrame parentFrame) {
        AccessDeniedDialog dialog = new AccessDeniedDialog(parentFrame, true);
        dialog.setVisible(true);
    }

    public void showSessionExpired(JFrame parentFrame) {
        SessionExpiredDialog dialog = new SessionExpiredDialog(parentFrame, true);
        dialog.setVisible(true);
        openLogin(parentFrame);
    }

    private void showNextFrame(JFrame currentFrame, JFrame nextFrame) {
        nextFrame.setVisible(true);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
    }
}
