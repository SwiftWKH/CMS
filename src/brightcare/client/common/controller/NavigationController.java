package brightcare.client.common.controller;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.admin.view.AdminFrame;
import brightcare.client.common.view.AccessDeniedDialog;
import brightcare.client.common.view.LoginFrame;
import brightcare.client.common.view.MainMenuFrame;
import brightcare.client.common.view.SessionExpiredDialog;
import brightcare.client.gateway.AdminGateway;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.DoctorGateway;
import brightcare.client.gateway.PatientGateway;
import brightcare.client.gateway.ReceptionistGateway;
import brightcare.client.gateway.UnavailableAdminGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UnavailableDoctorGateway;
import brightcare.client.gateway.UnavailablePatientGateway;
import brightcare.client.gateway.UnavailableReceptionistGateway;
import brightcare.client.doctor.view.DoctorFrame;
import brightcare.client.doctor.controller.DoctorController;
import brightcare.model.UserAccount;
import brightcare.client.patient.view.PatientFrame;
import brightcare.client.patient.controller.PatientController;
import brightcare.client.receptionist.view.ReceptionistFrame;
import brightcare.client.receptionist.controller.ReceptionistController;
import brightcare.security.PermissionChecker;
import javax.swing.JFrame;

public class NavigationController {
    private final AuthenticationGateway authenticationGateway;
    private final AdminGateway adminGateway;
    private final PatientGateway patientGateway;
    private final DoctorGateway doctorGateway;
    private final ReceptionistGateway receptionistGateway;

    public NavigationController() {
        this(new UnavailableAuthenticationGateway(), new UnavailableAdminGateway(),
                new UnavailablePatientGateway(), new UnavailableDoctorGateway(),
                new UnavailableReceptionistGateway());
    }

    public NavigationController(AuthenticationGateway authenticationGateway, AdminGateway adminGateway) {
        this(authenticationGateway, adminGateway, new UnavailablePatientGateway(),
                new UnavailableDoctorGateway(), new UnavailableReceptionistGateway());
    }

    public NavigationController(AuthenticationGateway authenticationGateway, AdminGateway adminGateway,
            PatientGateway patientGateway, DoctorGateway doctorGateway, ReceptionistGateway receptionistGateway) {
        if (authenticationGateway == null) {
            throw new IllegalArgumentException("Authentication gateway is required.");
        }
        if (adminGateway == null) {
            throw new IllegalArgumentException("Admin gateway is required.");
        }
        if (patientGateway == null || doctorGateway == null || receptionistGateway == null) {
            throw new IllegalArgumentException("Role gateways are required.");
        }
        this.authenticationGateway = authenticationGateway;
        this.adminGateway = adminGateway;
        this.patientGateway = patientGateway;
        this.doctorGateway = doctorGateway;
        this.receptionistGateway = receptionistGateway;
    }

    public void openFrameForUser(UserAccount userAccount, JFrame currentFrame) {
        if (userAccount == null) {
            showAccessDenied(currentFrame);
            return;
        }
        openFrameForRole(userAccount.getRole(), userAccount.getUsername(), userAccount.getUserId(),
                userAccount.getRoleId(), currentFrame);
    }

    public void openFrameForRole(String role, JFrame currentFrame) {
        openFrameForRole(role, 0, currentFrame);
    }

    public void openFrameForRole(String role, int userId, JFrame currentFrame) {
        openFrameForRole(role, null, userId, currentFrame);
    }

    private void openFrameForRole(String role, String username, int userId, JFrame currentFrame) {
        openFrameForRole(role, username, userId, 0, currentFrame);
    }

    private void openFrameForRole(String role, String username, int userId, int roleId, JFrame currentFrame) {
        JFrame nextFrame;

        if (PermissionChecker.ROLE_ADMIN.equalsIgnoreCase(role)) {
            AdminController adminController = new AdminController(
                    this,
                    adminGateway,
                    authenticationGateway,
                    userId
            );
            nextFrame = new AdminFrame(adminController);
        } else if (PermissionChecker.ROLE_DOCTOR.equalsIgnoreCase(role)) {
            nextFrame = new DoctorFrame(new DoctorController(doctorGateway, authenticationGateway, this,
                    resolveRoleRecordId("D", username, userId, roleId), userId));
        } else if (PermissionChecker.ROLE_RECEPTIONIST.equalsIgnoreCase(role)) {
            nextFrame = new ReceptionistFrame(new ReceptionistController(
                    receptionistGateway,
                    authenticationGateway,
                    this,
                    userId
            ));
        } else if (PermissionChecker.ROLE_PATIENT.equalsIgnoreCase(role)) {
            nextFrame = new PatientFrame(new PatientController(patientGateway, authenticationGateway, this,
                    resolveRoleRecordId("P", username, userId, roleId), userId));
        } else {
            showAccessDenied(currentFrame);
            return;
        }

        showNextFrame(currentFrame, nextFrame);
    }

    private int resolveRoleRecordId(String expectedPrefix, String username, int fallbackUserId, int apiRoleId) {
        if (apiRoleId > 0) {
            return apiRoleId;
        }
        if (username == null || expectedPrefix == null) {
            return fallbackUserId;
        }
        String trimmed = username.trim();
        if (trimmed.length() <= expectedPrefix.length()
                || !trimmed.toUpperCase().startsWith(expectedPrefix.toUpperCase())) {
            return fallbackUserId;
        }
        try {
            int parsed = Integer.parseInt(trimmed.substring(expectedPrefix.length()).trim());
            return parsed > 0 ? parsed : fallbackUserId;
        } catch (NumberFormatException ex) {
            return fallbackUserId;
        }
    }

    public void openMainMenu(JFrame currentFrame) {
        showNextFrame(currentFrame, new MainMenuFrame(this));
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
