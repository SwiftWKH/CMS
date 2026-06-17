package brightcare.client.gateway;

import brightcare.model.Report;
import brightcare.remote.ClinicRemoteInterface;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RmiAdminGateway implements AdminGateway {
    private final ClinicRemoteInterface remote;

    public RmiAdminGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public List<UserSummary> getUsers() {
        return new ArrayList<UserSummary>();
    }

    public boolean createUser(String username, String password, String role) {
        return false;
    }

    public boolean disableUser(String username) {
        return false;
    }

    public Report generateMonthlyAppointmentReport(int month, int year) {
        try {
            return remote.generateMonthlyAppointmentReport(month, year);
        } catch (RemoteException ex) {
            return unavailableReport("MONTHLY_APPOINTMENT");
        }
    }

    public Report generateDoctorConsultationReport(int doctorId, int month, int year) {
        try {
            return remote.generateDoctorConsultationReport(doctorId, month, year);
        } catch (RemoteException ex) {
            return unavailableReport("DOCTOR_CONSULTATION");
        }
    }

    public Report generatePatientVisitSummary(int patientId) {
        try {
            return remote.generatePatientVisitSummary(patientId);
        } catch (RemoteException ex) {
            return unavailableReport("PATIENT_VISIT_SUMMARY");
        }
    }

    public String viewSystemStatistics() {
        try {
            return remote.viewSystemStatistics();
        } catch (RemoteException ex) {
            return "System statistics gateway is not connected.";
        }
    }

    public List<SessionSummary> getActiveSessions() {
        return new ArrayList<SessionSummary>();
    }

    private Report unavailableReport(String reportType) {
        return new Report(0, reportType, 0, LocalDateTime.now(), null);
    }
}
