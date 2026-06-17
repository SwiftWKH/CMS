package brightcare.client.admin.controller;

import brightcare.client.gateway.AdminGateway;
import brightcare.client.gateway.SessionSummary;
import brightcare.client.gateway.UnavailableAdminGateway;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UserSummary;
import brightcare.client.admin.view.ActiveSessionsFrame;
import brightcare.client.admin.view.CreateUserFrame;
import brightcare.client.admin.view.DisableUserFrame;
import brightcare.client.admin.view.DoctorConsultationReportFrame;
import brightcare.client.admin.view.MonthlyAppointmentReportFrame;
import brightcare.client.admin.view.PatientVisitSummaryFrame;
import brightcare.client.admin.view.SystemStatisticsFrame;
import brightcare.client.admin.view.ViewUsersFrame;
import brightcare.client.common.controller.NavigationController;
import brightcare.model.Report;
import javax.swing.JFrame;
import java.util.List;

public class AdminController {
    private final NavigationController navigationController;
    private final AdminGateway adminGateway;
    private final AuthenticationGateway authenticationGateway;
    private int currentUserId;

    public AdminController() {
        this(new NavigationController(), new UnavailableAdminGateway(), new UnavailableAuthenticationGateway(), 0);
    }

    public AdminController(NavigationController navigationController) {
        this(navigationController, new UnavailableAdminGateway(), new UnavailableAuthenticationGateway(), 0);
    }

    public AdminController(NavigationController navigationController, AdminGateway adminGateway,
            AuthenticationGateway authenticationGateway, int currentUserId) {
        if (navigationController == null) {
            throw new IllegalArgumentException("Navigation controller is required.");
        }
        if (adminGateway == null) {
            throw new IllegalArgumentException("Admin gateway is required.");
        }
        if (authenticationGateway == null) {
            throw new IllegalArgumentException("Authentication gateway is required.");
        }
        this.navigationController = navigationController;
        this.adminGateway = adminGateway;
        this.authenticationGateway = authenticationGateway;
        this.currentUserId = currentUserId;
    }

    public void logout(JFrame currentFrame) {
        if (currentUserId > 0) {
            authenticationGateway.logout(currentUserId);
        }
        navigationController.openLogin(currentFrame);
    }

    public void openCreateUser(JFrame currentFrame) {
        show(new CreateUserFrame(this));
    }

    public void openViewUsers(JFrame currentFrame) {
        show(new ViewUsersFrame(this));
    }

    public void openDisableUser(JFrame currentFrame) {
        show(new DisableUserFrame(this));
    }

    public void openMonthlyAppointmentReport(JFrame currentFrame) {
        show(new MonthlyAppointmentReportFrame(this));
    }

    public void openDoctorConsultationReport(JFrame currentFrame) {
        show(new DoctorConsultationReportFrame(this));
    }

    public void openPatientVisitSummary(JFrame currentFrame) {
        show(new PatientVisitSummaryFrame(this));
    }

    public void openSystemStatistics(JFrame currentFrame) {
        show(new SystemStatisticsFrame(this));
    }

    public void openActiveSessions(JFrame currentFrame) {
        show(new ActiveSessionsFrame(this));
    }

    private void show(JFrame frame) {
        frame.setVisible(true);
    }

    public List<UserSummary> getUsers() {
        return adminGateway.getUsers();
    }

    public boolean createUser(String username, String password, String role) {
        return adminGateway.createUser(username, password, role);
    }

    public boolean disableUser(String username) {
        return adminGateway.disableUser(username);
    }

    public Report generateMonthlyAppointmentReport(int month, int year) {
        return adminGateway.generateMonthlyAppointmentReport(month, year);
    }

    public Report generateDoctorConsultationReport(int doctorId, int month, int year) {
        return adminGateway.generateDoctorConsultationReport(doctorId, month, year);
    }

    public Report generatePatientVisitSummary(int patientId) {
        return adminGateway.generatePatientVisitSummary(patientId);
    }

    public String viewSystemStatistics() {
        return adminGateway.viewSystemStatistics();
    }

    public List<SessionSummary> getActiveSessions() {
        return adminGateway.getActiveSessions();
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}
