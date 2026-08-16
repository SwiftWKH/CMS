package brightcare.service;

import brightcare.concurrency.AppointmentLockManager;
import brightcare.dao.AppointmentDAO;
import brightcare.dao.PatientDAO;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PatientService {
    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;
    private final AppointmentLockManager appointmentLockManager;

    public PatientService() {
        this(new PatientDAO(), new AppointmentDAO(), new AppointmentLockManager());
    }

    public PatientService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this(patientDAO, appointmentDAO, new AppointmentLockManager());
    }

    public PatientService(PatientDAO patientDAO, AppointmentDAO appointmentDAO,
            AppointmentLockManager appointmentLockManager) {
        if (patientDAO == null || appointmentDAO == null || appointmentLockManager == null) {
            throw new IllegalArgumentException("Patient, appointment DAO, and lock manager are required.");
        }
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
        this.appointmentLockManager = appointmentLockManager;
    }

    public Patient updatePersonalInfo(Patient patient) {
        validatePatient(patient);
        patient.setPatientId(resolvePatientId(patient.getPatientId()));
        return patientDAO.update(patient);
    }

    public Appointment bookAppointment(Appointment appointment) {
        validateAppointment(appointment);
        appointment.setPatientId(resolvePatientId(appointment.getPatientId()));
        appointment.setStatus(defaultStatus(appointment.getStatus()));
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

    public Appointment cancelAppointment(int appointmentId) {
        validateId(appointmentId, "Appointment ID");
        return appointmentDAO.cancel(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(int patientId) {
        validateId(patientId, "Patient ID");
        return appointmentDAO.findByPatientId(resolvePatientId(patientId));
    }

    public List<Appointment> viewAppointmentHistory(int patientId) {
        List<Appointment> history = new ArrayList<Appointment>();
        for (Appointment appointment : viewAppointmentSchedule(patientId)) {
            if ("COMPLETED".equalsIgnoreCase(appointment.getStatus())
                    || "CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
                history.add(appointment);
            }
        }
        return history;
    }

    public List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) {
        validateId(doctorId, "Doctor ID");
        List<LocalTime> available = defaultClinicSlots();
        for (Appointment appointment : appointmentDAO.findByDoctorAndDate(doctorId, date)) {
            available.remove(appointment.getAppointmentTime());
        }
        return available;
    }

    private List<LocalTime> defaultClinicSlots() {
        List<LocalTime> slots = new ArrayList<LocalTime>();
        slots.add(LocalTime.of(9, 0));
        slots.add(LocalTime.of(10, 0));
        slots.add(LocalTime.of(11, 0));
        slots.add(LocalTime.of(14, 0));
        slots.add(LocalTime.of(15, 0));
        slots.add(LocalTime.of(16, 0));
        return slots;
    }

    private int resolvePatientId(int possiblePatientOrUserId) {
        Patient byPatientId = patientDAO.findById(possiblePatientOrUserId);
        if (byPatientId != null) {
            return byPatientId.getPatientId();
        }
        Patient byUserId = patientDAO.findByUserId(possiblePatientOrUserId);
        if (byUserId != null) {
            return byUserId.getPatientId();
        }
        return possiblePatientOrUserId;
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
        validateId(appointment.getPatientId(), "Patient ID");
        validateId(appointment.getDoctorId(), "Doctor ID");
    }

    private void validateId(int id, String label) {
        if (id <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
    }

    private String defaultStatus(String status) {
        return status == null || status.trim().length() == 0 ? "BOOKED" : status;
    }

    private void ensureSlotAvailable(Appointment appointment) {
        for (Appointment existing : appointmentDAO.findByDoctorAndDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            if (existing.getAppointmentTime() != null
                    && existing.getAppointmentTime().equals(appointment.getAppointmentTime())
                    && !"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("Appointment slot is already booked.");
            }
        }
    }
}
