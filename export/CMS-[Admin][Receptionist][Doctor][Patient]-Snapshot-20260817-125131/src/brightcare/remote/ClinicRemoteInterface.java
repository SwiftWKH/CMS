package brightcare.remote;

import brightcare.model.Appointment;
import brightcare.model.ActiveSessionInfo;
import brightcare.model.ConsultationNote;
import brightcare.model.Doctor;
import brightcare.model.Patient;
import brightcare.model.Report;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ClinicRemoteInterface extends Remote {
    UserAccount login(String username, String password) throws RemoteException;

    boolean logout(int userId) throws RemoteException;

    boolean checkPermission(int userId, String requiredRole) throws RemoteException;

    List<UserAccount> viewUsers() throws RemoteException;

    UserAccount createUser(String username, String password, String role) throws RemoteException;

    UserAccount createUserWithProfile(UserProfileInput input) throws RemoteException;

    UserAccount updateUser(UserProfileInput input) throws RemoteException;

    boolean disableUser(String username) throws RemoteException;

    List<ActiveSessionInfo> viewActiveSessions() throws RemoteException;

    List<ActiveSessionInfo> viewSessionHistory() throws RemoteException;

    Patient registerPatient(Patient patient) throws RemoteException;

    Patient registerPatientWithAccount(Patient patient, String username, String password) throws RemoteException;

    List<Patient> viewPatients() throws RemoteException;

    Patient updatePatientDetails(Patient patient) throws RemoteException;

    Appointment createAppointment(Appointment appointment) throws RemoteException;

    Appointment modifyAppointment(Appointment appointment) throws RemoteException;

    Appointment cancelAppointment(int appointmentId) throws RemoteException;

    List<Appointment> viewAppointmentSchedule(LocalDate date) throws RemoteException;

    Patient updatePersonalInfo(Patient patient) throws RemoteException;

    Patient viewPatientProfile(int patientId) throws RemoteException;

    List<Doctor> viewDoctors() throws RemoteException;

    Appointment bookAppointment(Appointment appointment) throws RemoteException;

    List<Appointment> viewAppointmentSchedule(int patientId) throws RemoteException;

    List<Appointment> viewAppointmentHistory(int patientId) throws RemoteException;

    List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) throws RemoteException;

    List<Appointment> viewAppointmentList(int doctorId, LocalDate date) throws RemoteException;

    List<ConsultationNote> viewMedicalHistory(int patientId) throws RemoteException;

    ConsultationNote updateConsultationNotes(ConsultationNote note) throws RemoteException;

    List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date,
            List<LocalTime> availableSlots) throws RemoteException;

    Report generateMonthlyAppointmentReport(int month, int year) throws RemoteException;

    Report generateDoctorConsultationReport(int doctorId, int month, int year) throws RemoteException;

    Report generatePatientVisitSummary(int patientId) throws RemoteException;

    String viewSystemStatistics() throws RemoteException;
}
