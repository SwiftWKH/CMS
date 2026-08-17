package brightcare.client.gateway;

import brightcare.model.Report;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UnavailableAdminGateway implements AdminGateway {
    public List<UserSummary> getUsers() {
        return new ArrayList<UserSummary>();
    }

    public UserAccount createUser(String username, String password, String role) {
        return null;
    }

    public UserAccount createUser(UserProfileInput input) {
        return null;
    }

    public UserAccount updateUser(UserProfileInput input) {
        return null;
    }

    public boolean disableUser(String username) {
        return false;
    }

    public Report generateMonthlyAppointmentReport(int month, int year) {
        return new Report(0, "MONTHLY_APPOINTMENT", 0, LocalDateTime.now(), null);
    }

    public Report generateDoctorConsultationReport(int doctorId, int month, int year) {
        return new Report(0, "DOCTOR_CONSULTATION", 0, LocalDateTime.now(), null);
    }

    public Report generatePatientVisitSummary(int patientId) {
        return new Report(0, "PATIENT_VISIT_SUMMARY", 0, LocalDateTime.now(), null);
    }

    public String viewSystemStatistics() {
        return "System statistics gateway is not connected yet.";
    }

    public List<SessionSummary> getActiveSessions() {
        return new ArrayList<SessionSummary>();
    }

    public List<SessionSummary> getSessionHistory() {
        return new ArrayList<SessionSummary>();
    }
}
