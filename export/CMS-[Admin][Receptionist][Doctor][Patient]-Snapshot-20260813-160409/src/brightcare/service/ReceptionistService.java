package brightcare.service;

import brightcare.concurrency.AppointmentLockManager;
import brightcare.dao.AppointmentDAO;
import brightcare.dao.PatientDAO;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.util.List;

public class ReceptionistService {
    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;
    private final AppointmentLockManager appointmentLockManager;

    public ReceptionistService() {
        this(new PatientDAO(), new AppointmentDAO(), new AppointmentLockManager());
    }

    public ReceptionistService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this(patientDAO, appointmentDAO, new AppointmentLockManager());
    }

    public ReceptionistService(PatientDAO patientDAO, AppointmentDAO appointmentDAO,
            AppointmentLockManager appointmentLockManager) {
        if (patientDAO == null || appointmentDAO == null || appointmentLockManager == null) {
            throw new IllegalArgumentException("Patient, appointment DAO, and lock manager are required.");
        }
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
        this.appointmentLockManager = appointmentLockManager;
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
        if (!appointmentLockManager.acquireSlot(appointment)) {
            throw new IllegalStateException("Appointment slot is currently being booked by another user.");
        }
        try {
            ensureSlotAvailable(appointment);
            return appointmentDAO.save(appointment);
        } finally {
            appointmentLockManager.releaseSlot(appointment);
        }
    }

    public Appointment modifyAppointment(Appointment appointment) {
        validateAppointment(appointment);
        if (!appointmentLockManager.acquireSlot(appointment)) {
            throw new IllegalStateException("Appointment slot is currently being updated by another user.");
        }
        try {
            ensureSlotAvailableForUpdate(appointment);
            return appointmentDAO.update(appointment);
        } finally {
            appointmentLockManager.releaseSlot(appointment);
        }
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

    private void ensureSlotAvailable(Appointment appointment) {
        for (Appointment existing : appointmentDAO.findByDoctorAndDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            if (sameTime(existing, appointment) && !"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("Appointment slot is already booked.");
            }
        }
    }

    private void ensureSlotAvailableForUpdate(Appointment appointment) {
        for (Appointment existing : appointmentDAO.findByDoctorAndDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            boolean sameAppointment = existing.getAppointmentId() == appointment.getAppointmentId();
            if (!sameAppointment && sameTime(existing, appointment)
                    && !"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("Appointment slot is already booked.");
            }
        }
    }

    private boolean sameTime(Appointment left, Appointment right) {
        return left.getAppointmentTime() != null && left.getAppointmentTime().equals(right.getAppointmentTime());
    }
}
