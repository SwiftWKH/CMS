package brightcare.client.gateway;

import brightcare.model.Report;
import java.util.List;

public interface AdminGateway {
    List<UserSummary> getUsers();

    boolean createUser(String username, String password, String role);

    boolean disableUser(String username);

    Report generateMonthlyAppointmentReport(int month, int year);

    Report generateDoctorConsultationReport(int doctorId, int month, int year);

    Report generatePatientVisitSummary(int patientId);

    String viewSystemStatistics();

    List<SessionSummary> getActiveSessions();
}
