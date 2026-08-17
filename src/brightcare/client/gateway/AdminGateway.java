package brightcare.client.gateway;

import brightcare.model.Report;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
import java.util.List;

public interface AdminGateway {
    List<UserSummary> getUsers();

    UserAccount createUser(String username, String password, String role);

    UserAccount createUser(UserProfileInput input);

    UserAccount updateUser(UserProfileInput input);

    boolean disableUser(String username);

    Report generateMonthlyAppointmentReport(int month, int year);

    Report generateDoctorConsultationReport(int doctorId, int month, int year);

    Report generatePatientVisitSummary(int patientId);

    String viewSystemStatistics();

    List<SessionSummary> getActiveSessions();

    List<SessionSummary> getSessionHistory();
}
