package doctorportal;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DoctorService extends Remote {
    // Doctor operations
    Doctor getDoctor(int doctorId) throws RemoteException;
    boolean updateDoctor(Doctor doctor) throws RemoteException;
    List<Doctor> getAllDoctors() throws RemoteException;
    
    // Appointment operations
    List<DoctorAppointment> getAllAppointments() throws RemoteException;
    List<DoctorAppointment> getDoctorAppointments(int doctorId) throws RemoteException;
    List<DoctorAppointment> getMedicalHistory(int doctorId) throws RemoteException;
    List<DoctorAppointment> getPendingAppointments(int doctorId) throws RemoteException;
    boolean updateAppointment(DoctorAppointment appointment) throws RemoteException;
    boolean cancelAppointment(int appointmentId) throws RemoteException;
    boolean saveToMedicalHistory(DoctorAppointment appointment) throws RemoteException;
    
    // Patient operations
    List<Patient> getPatients(int doctorId) throws RemoteException;
    Patient getPatient(int patientId) throws RemoteException;
List<PatientConsultationSummary> getConsultationHistory() throws RemoteException;
// Data sync operations
    String fetchAppointmentswithconsFromApi() throws RemoteException;
    String fetchAppointmentsFromApi() throws RemoteException;
    boolean syncDataFromApi() throws RemoteException;
    boolean FromappwconApi() throws RemoteException;
}