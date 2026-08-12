package brightcare.service;

import brightcare.dao.AppointmentDAO;
import brightcare.dao.PatientDAO;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.util.List;

public class ReceptionistService {
    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;

    public ReceptionistService() {
        this(new PatientDAO(), new AppointmentDAO());
    }

    public ReceptionistService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        if (patientDAO == null || appointmentDAO == null) {
            throw new IllegalArgumentException("Patient and appointment DAOs are required.");
        }
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
    }

    public Patient registerPatient(Patient patient) {
        validatePatient(patient);
        return patientDAO.save(patient);
    }

    public Patient updatePatientDetails(Patient patient) {
        validatePatient(patient);
        return patientDAO.update(patient);
    }

    public Appointment createAppointment(Appointment appointment) {
        validateAppointment(appointment);
        if (appointment.getStatus() == null || appointment.getStatus().trim().length() == 0) {
            appointment.setStatus("BOOKED");
        }
        return appointmentDAO.save(appointment);
    }

    public Appointment modifyAppointment(Appointment appointment) {
        validateAppointment(appointment);
        return appointmentDAO.update(appointment);
    }

    public Appointment cancelAppointment(int appointmentId) {
        if (appointmentId <= 0) {
            throw new IllegalArgumentException("Appointment ID must be greater than zero.");
        }
        return appointmentDAO.cancel(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) {
        return appointmentDAO.findByDate(date);
    }

    public List<Patient> viewPatients() {
        return patientDAO.findAll();
    }

    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is required.");
        }
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment is required.");
        }
        if (appointment.getPatientId() <= 0 || appointment.getDoctorId() <= 0) {
            throw new IllegalArgumentException("Patient ID and Doctor ID are required.");
        }
    }
}
