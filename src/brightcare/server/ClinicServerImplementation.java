package brightcare.server;

import brightcare.dao.DerbyReportDataProvider;
import brightcare.dao.UserAccountDAO;
import brightcare.model.ActiveSessionInfo;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.model.Patient;
import brightcare.model.Report;
import brightcare.model.UserAccount;
import brightcare.remote.ClinicRemoteInterface;
import brightcare.service.AdminService;
import brightcare.service.AuthService;
import brightcare.service.DoctorService;
import brightcare.service.PatientService;
import brightcare.service.ReceptionistService;
import brightcare.service.ReportService;
import brightcare.util.BrightCareLogger;
import java.rmi.RemoteException;
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
        this(createDefaultAuthService());
    }

    private ClinicServerImplementation(AuthService authService) throws RemoteException {
        this(authService, new AdminService(new UserAccountDAO(), authService.getSessionManager()),
                new ReceptionistService(), new PatientService(), new DoctorService(),
                new ReportService(new DerbyReportDataProvider()));
    }

    public ClinicServerImplementation(AuthService authService, AdminService adminService,
            ReceptionistService receptionistService, PatientService patientService,
            DoctorService doctorService, ReportService reportService) throws RemoteException {
        super();
        this.authService = require(authService, "Auth service");
        this.adminService = require(adminService, "Admin service");
        this.receptionistService = require(receptionistService, "Receptionist service");
        this.patientService = require(patientService, "Patient service");
        this.doctorService = require(doctorService, "Doctor service");
        this.reportService = require(reportService, "Report service");
    }

    public UserAccount login(String username, String password) throws RemoteException {
        LOGGER.info("RMI login request received for username=" + (username == null ? "<null>" : username.trim()) + ".");
        UserAccount account = authService.login(username, password);
        LOGGER.info("RMI login result for username=" + (username == null ? "<null>" : username.trim())
                + ": " + (account == null ? "FAILED" : "SUCCESS role=" + account.getRole()) + ".");
        return account;
    }

    public boolean logout(int userId) throws RemoteException {
        return authService.logout(userId);
    }

    public boolean checkPermission(int userId, String requiredRole) throws RemoteException {
        return authService.checkPermission(userId, requiredRole);
    }

    public List<UserAccount> viewUsers() throws RemoteException {
        return adminService.viewUsers();
    }

    public UserAccount createUser(String username, String password, String role) throws RemoteException {
        return adminService.createUser(username, password, role);
    }

    public boolean disableUser(String username) throws RemoteException {
        return adminService.disableUser(username);
    }

    public List<ActiveSessionInfo> viewActiveSessions() throws RemoteException {
        return adminService.viewActiveSessions();
    }

    public Patient registerPatient(Patient patient) throws RemoteException {
        return receptionistService.registerPatient(patient);
    }

    public Patient updatePatientDetails(Patient patient) throws RemoteException {
        return receptionistService.updatePatientDetails(patient);
    }

    public Appointment createAppointment(Appointment appointment) throws RemoteException {
        return receptionistService.createAppointment(appointment);
    }

    public Appointment modifyAppointment(Appointment appointment) throws RemoteException {
        return receptionistService.modifyAppointment(appointment);
    }

    public Appointment cancelAppointment(int appointmentId) throws RemoteException {
        return receptionistService.cancelAppointment(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) throws RemoteException {
        return receptionistService.viewAppointmentSchedule(date);
    }

    public Patient updatePersonalInfo(Patient patient) throws RemoteException {
        return patientService.updatePersonalInfo(patient);
    }

    public Appointment bookAppointment(Appointment appointment) throws RemoteException {
        return patientService.bookAppointment(appointment);
    }

    public List<Appointment> viewAppointmentSchedule(int patientId) throws RemoteException {
        return patientService.viewAppointmentSchedule(patientId);
    }

    public List<Appointment> viewAppointmentHistory(int patientId) throws RemoteException {
        return patientService.viewAppointmentHistory(patientId);
    }

    public List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) throws RemoteException {
        return patientService.checkDoctorAvailability(doctorId, date);
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) throws RemoteException {
        return doctorService.viewAppointmentList(doctorId, date);
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) throws RemoteException {
        return doctorService.viewMedicalHistory(patientId);
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) throws RemoteException {
        return doctorService.updateConsultationNotes(note);
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date,
            List<LocalTime> availableSlots) throws RemoteException {
        return doctorService.manageAppointmentSchedule(doctorId, date, availableSlots);
    }

    public Report generateMonthlyAppointmentReport(int month, int year) throws RemoteException {
        return reportService.generateMonthlyAppointmentReport(month, year);
    }

    public Report generateDoctorConsultationReport(int doctorId, int month, int year) throws RemoteException {
        return reportService.generateDoctorConsultationReport(doctorId, month, year);
    }

    public Report generatePatientVisitSummary(int patientId) throws RemoteException {
        return reportService.generatePatientVisitSummary(patientId);
    }

    public String viewSystemStatistics() throws RemoteException {
        return reportService.viewSystemStatistics();
    }

    private <T> T require(T value, String label) {
        if (value == null) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value;
    }

    private static AuthService createDefaultAuthService() {
        return new AuthService(new UserAccountDAO());
    }
}
