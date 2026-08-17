package brightcare.client.gateway;

import brightcare.model.ActiveSessionInfo;
import brightcare.model.Report;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
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
        List<UserSummary> users = new ArrayList<UserSummary>();
        try {
            List<UserAccount> accounts = remote.viewUsers();
            for (UserAccount account : accounts) {
                users.add(new UserSummary(account.getUserId(), account.getUsername(), account.getPasswordHash(),
                        account.getRole(), account.getRoleId(), account.getStatus()));
            }
        } catch (RemoteException ex) {
            return users;
        }
        return users;
    }

    public UserAccount createUser(String username, String password, String role) {
        try {
            return remote.createUser(username, password, role);
        } catch (RemoteException ex) {
            return null;
        }
    }

    public UserAccount createUser(UserProfileInput input) {
        try {
            return remote.createUserWithProfile(input);
        } catch (RemoteException ex) {
            return null;
        }
    }

    public UserAccount updateUser(UserProfileInput input) {
        try {
            return remote.updateUser(input);
        } catch (RemoteException ex) {
            return null;
        }
    }

    public boolean disableUser(String username) {
        try {
            return remote.disableUser(username);
        } catch (RemoteException ex) {
            return false;
        }
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
        List<SessionSummary> summaries = new ArrayList<SessionSummary>();
        try {
            List<ActiveSessionInfo> sessions = remote.viewActiveSessions();
            for (ActiveSessionInfo session : sessions) {
                summaries.add(new SessionSummary(
                        session.getUsername(),
                        session.getLoginTime() == null ? "" : session.getLoginTime().toString(),
                        session.getLogoutTime() == null ? "" : session.getLogoutTime().toString(),
                        session.getStatus(),
                        session.getRole()
                ));
            }
        } catch (RemoteException ex) {
            return summaries;
        }
        return summaries;
    }

    public List<SessionSummary> getSessionHistory() {
        List<SessionSummary> summaries = new ArrayList<SessionSummary>();
        try {
            List<ActiveSessionInfo> sessions = remote.viewSessionHistory();
            for (ActiveSessionInfo session : sessions) {
                summaries.add(new SessionSummary(
                        session.getUsername(),
                        session.getLoginTime() == null ? "" : session.getLoginTime().toString(),
                        session.getLogoutTime() == null ? "" : session.getLogoutTime().toString(),
                        session.getStatus(),
                        session.getRole()
                ));
            }
        } catch (RemoteException ex) {
            return summaries;
        }
        return summaries;
    }

    private Report unavailableReport(String reportType) {
        return new Report(0, reportType, 0, LocalDateTime.now(), null);
    }
}
