package brightcare.server;

import brightcare.concurrency.AppointmentLockManager;
import brightcare.dao.AppointmentDAO;
import brightcare.dao.DerbyReportDataProvider;
import brightcare.dao.DataSourceConfig;
import brightcare.dao.HospitalApiReportDataProvider;
import brightcare.dao.PatientDAO;
import brightcare.dao.UserAccountDAO;
import brightcare.model.ActiveSessionInfo;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.model.Doctor;
import brightcare.model.Patient;
import brightcare.model.Report;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
import brightcare.remote.ClinicRemoteInterface;
import brightcare.security.SSLConfig;
import brightcare.service.AdminService;
import brightcare.service.AuthService;
import brightcare.service.DoctorService;
import brightcare.service.PatientService;
import brightcare.service.ReceptionistService;
import brightcare.service.ReportService;
import brightcare.util.BrightCareLogger;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

public class ClinicServerImplementation extends UnicastRemoteObject implements ClinicRemoteInterface {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = BrightCareLogger.getLogger(ClinicServerImplementation.class);

    private final AuthService authService;
    private final AdminService adminService;
    private final ReceptionistService receptionistService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final ReportService reportService;

    public ClinicServerImplementation() throws RemoteException {
        this(createDefaultServices());
    }

    private ClinicServerImplementation(ServiceBundle services) throws RemoteException {
        super(0, SSLConfig.rmiClientSocketFactoryIfEnabled(), SSLConfig.rmiServerSocketFactoryIfEnabled());
        this.authService = require(services.authService, "Auth service");
        this.adminService = new AdminService(new UserAccountDAO(), this.authService.getSessionManager());
        this.receptionistService = require(services.receptionistService, "Receptionist service");
        this.patientService = require(services.patientService, "Patient service");
        this.doctorService = require(services.doctorService, "Doctor service");
        this.reportService = require(services.reportService, "Report service");
    }

    public ClinicServerImplementation(AuthService authService, AdminService adminService,
            ReceptionistService receptionistService, PatientService patientService,
            DoctorService doctorService, ReportService reportService) throws RemoteException {
        super(0, SSLConfig.rmiClientSocketFactoryIfEnabled(), SSLConfig.rmiServerSocketFactoryIfEnabled());
        this.authService = require(authService, "Auth service");
        this.adminService = require(adminService, "Admin service");
        this.receptionistService = require(receptionistService, "Receptionist service");
        this.patientService = require(patientService, "Patient service");
        this.doctorService = require(doctorService, "Doctor service");
        this.reportService = require(reportService, "Report service");
    }

    public UserAccount login(String username, String password) throws RemoteException {
        logRemoteCall("login", "username=" + (username == null ? "<null>" : username.trim()));
        UserAccount account = authService.login(username, password);
        LOGGER.info("RMI login result for username=" + (username == null ? "<null>" : username.trim())
                + ": " + (account == null ? "FAILED" : "SUCCESS role=" + account.getRole()) + ".");
        return account;
    }

    public boolean logout(int userId) throws RemoteException {
        logRemoteCall("logout", "userId=" + userId);
        return authService.logout(userId);
    }

    public boolean checkPermission(int userId, String requiredRole) throws RemoteException {
        logRemoteCall("checkPermission", "userId=" + userId + ", requiredRole=" + requiredRole);
        return authService.checkPermission(userId, requiredRole);
    }

    public List<UserAccount> viewUsers() throws RemoteException {
        logRemoteCall("viewUsers", "");
        return adminService.viewUsers();
    }

    public UserAccount createUser(String username, String password, String role) throws RemoteException {
        logRemoteCall("createUser", "username=" + username + ", role=" + role);
        return adminService.createUser(username, password, role);
    }

    public UserAccount createUserWithProfile(UserProfileInput input) throws RemoteException {
        String username = input == null ? "" : input.getUsername();
        String role = input == null ? "" : input.getRole();
        logRemoteCall("createUserWithProfile", "username=" + username + ", role=" + role);
        return adminService.createUser(input);
    }

    public UserAccount updateUser(UserProfileInput input) throws RemoteException {
        String username = input == null ? "" : input.getUsername();
        int userId = input == null ? 0 : input.getUserId();
        logRemoteCall("updateUser", "userId=" + userId + ", username=" + username);
        return adminService.updateUser(input);
    }

    public boolean disableUser(String username) throws RemoteException {
        logRemoteCall("disableUser", "username=" + username);
        return adminService.disableUser(username);
    }

    public List<ActiveSessionInfo> viewActiveSessions() throws RemoteException {
        logRemoteCall("viewActiveSessions", "");
        return adminService.viewActiveSessions();
    }

    public List<ActiveSessionInfo> viewSessionHistory() throws RemoteException {
        logRemoteCall("viewSessionHistory", "");
        return adminService.viewSessionHistory();
    }

    public Patient registerPatient(Patient patient) throws RemoteException {
        logRemoteCall("registerPatient", patient == null ? "patient=<null>" : "patientId=" + patient.getPatientId());
        return receptionistService.registerPatient(patient);
    }

    public Patient registerPatientWithAccount(Patient patient, String username, String password)
            throws RemoteException {
        logRemoteCall("registerPatientWithAccount", patient == null ? "patient=<null>"
                : "patientId=" + patient.getPatientId() + ", username=" + username);
        return receptionistService.registerPatientWithAccount(patient, username, password);
    }

    public List<Patient> viewPatients() throws RemoteException {
        logRemoteCall("viewPatients", "");
        return receptionistService.viewPatients();
    }

    public Patient updatePatientDetails(Patient patient) throws RemoteException {
        logRemoteCall("updatePatientDetails", patient == null ? "patient=<null>" : "patientId=" + patient.getPatientId());
        return receptionistService.updatePatientDetails(patient);
    }

    public Appointment createAppointment(Appointment appointment) throws RemoteException {
        logRemoteCall("createAppointment", appointmentDetails(appointment));
        return receptionistService.createAppointment(appointment);
    }

    public Appointment modifyAppointment(Appointment appointment) throws RemoteException {
        logRemoteCall("modifyAppointment", appointmentDetails(appointment));
        return receptionistService.modifyAppointment(appointment);
    }

    public Appointment cancelAppointment(int appointmentId) throws RemoteException {
        logRemoteCall("cancelAppointment", "appointmentId=" + appointmentId);
        return receptionistService.cancelAppointment(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) throws RemoteException {
        logRemoteCall("viewAppointmentScheduleByDate", "date=" + date);
        return receptionistService.viewAppointmentSchedule(date);
    }

    public Patient updatePersonalInfo(Patient patient) throws RemoteException {
        logRemoteCall("updatePersonalInfo", patient == null ? "patient=<null>" : "patientId=" + patient.getPatientId());
        return patientService.updatePersonalInfo(patient);
    }

    public Patient viewPatientProfile(int patientId) throws RemoteException {
        logRemoteCall("viewPatientProfile", "patientId=" + patientId);
        return patientService.viewPatientProfile(patientId);
    }

    public List<Doctor> viewDoctors() throws RemoteException {
        logRemoteCall("viewDoctors", "");
        return doctorService.viewDoctors();
    }

    public Appointment bookAppointment(Appointment appointment) throws RemoteException {
        logRemoteCall("bookAppointment", appointmentDetails(appointment));
        return patientService.bookAppointment(appointment);
    }

    public List<Appointment> viewAppointmentSchedule(int patientId) throws RemoteException {
        logRemoteCall("viewAppointmentScheduleByPatient", "patientId=" + patientId);
        return patientService.viewAppointmentSchedule(patientId);
    }

    public List<Appointment> viewAppointmentHistory(int patientId) throws RemoteException {
        logRemoteCall("viewAppointmentHistory", "patientId=" + patientId);
        return patientService.viewAppointmentHistory(patientId);
    }

    public List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) throws RemoteException {
        logRemoteCall("checkDoctorAvailability", "doctorId=" + doctorId + ", date=" + date);
        return patientService.checkDoctorAvailability(doctorId, date);
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) throws RemoteException {
        logRemoteCall("viewAppointmentList", "doctorId=" + doctorId + ", date=" + date);
        return doctorService.viewAppointmentList(doctorId, date);
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) throws RemoteException {
        logRemoteCall("viewMedicalHistory", "patientId=" + patientId);
        return doctorService.viewMedicalHistory(patientId);
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) throws RemoteException {
        logRemoteCall("updateConsultationNotes", note == null ? "note=<null>"
                : "appointmentId=" + note.getAppointmentId() + ", patientId=" + note.getPatientId()
                        + ", doctorId=" + note.getDoctorId());
        return doctorService.updateConsultationNotes(note);
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date,
            List<LocalTime> availableSlots) throws RemoteException {
        logRemoteCall("manageAppointmentSchedule", "doctorId=" + doctorId + ", date=" + date);
        return doctorService.manageAppointmentSchedule(doctorId, date, availableSlots);
    }

    public Report generateMonthlyAppointmentReport(int month, int year) throws RemoteException {
        logRemoteCall("generateMonthlyAppointmentReport", "month=" + month + ", year=" + year);
        return reportService.generateMonthlyAppointmentReport(month, year);
    }

    public Report generateDoctorConsultationReport(int doctorId, int month, int year) throws RemoteException {
        logRemoteCall("generateDoctorConsultationReport",
                "doctorId=" + doctorId + ", month=" + month + ", year=" + year);
        return reportService.generateDoctorConsultationReport(doctorId, month, year);
    }

    public Report generatePatientVisitSummary(int patientId) throws RemoteException {
        logRemoteCall("generatePatientVisitSummary", "patientId=" + patientId);
        return reportService.generatePatientVisitSummary(patientId);
    }

    public String viewSystemStatistics() throws RemoteException {
        logRemoteCall("viewSystemStatistics", "");
        return reportService.viewSystemStatistics();
    }

    private <T> T require(T value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value;
    }

    private void logRemoteCall(String method, String details) {
        LOGGER.info("RMI call received. method=" + method + ", clientHost=" + clientHost()
                + (details == null || details.length() == 0 ? "" : ", " + details) + ".");
    }

    private String clientHost() {
        try {
            return RemoteServer.getClientHost();
        } catch (ServerNotActiveException ex) {
            return "local-or-unavailable";
        }
    }

    private String appointmentDetails(Appointment appointment) {
        if (appointment == null) {
            return "appointment=<null>";
        }
        return "appointmentId=" + appointment.getAppointmentId()
                + ", patientId=" + appointment.getPatientId()
                + ", doctorId=" + appointment.getDoctorId()
                + ", date=" + appointment.getAppointmentDate()
                + ", time=" + appointment.getAppointmentTime()
                + ", status=" + appointment.getStatus();
    }

    private static ServiceBundle createDefaultServices() {
        AuthService authService = new AuthService(new UserAccountDAO());
        AppointmentLockManager lockManager = new AppointmentLockManager();
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        PatientDAO patientDAO = new PatientDAO();
        return new ServiceBundle(
                authService,
                new ReceptionistService(patientDAO, appointmentDAO, lockManager),
                new PatientService(patientDAO, appointmentDAO, lockManager),
                new DoctorService(),
                new ReportService(DataSourceConfig.preferHospitalApi()
                        ? new HospitalApiReportDataProvider()
                        : new DerbyReportDataProvider())
        );
    }

    private static class ServiceBundle {
        private final AuthService authService;
        private final ReceptionistService receptionistService;
        private final PatientService patientService;
        private final DoctorService doctorService;
        private final ReportService reportService;

        ServiceBundle(AuthService authService, ReceptionistService receptionistService,
                PatientService patientService, DoctorService doctorService, ReportService reportService) {
            this.authService = authService;
            this.receptionistService = receptionistService;
            this.patientService = patientService;
            this.doctorService = doctorService;
            this.reportService = reportService;
        }
    }
}
