package brightcare.service;

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

    public PatientService() {
        this(new PatientDAO(), new AppointmentDAO());
    }

    public PatientService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        if (patientDAO == null || appointmentDAO == null) {
            throw new IllegalArgumentException("Patient and appointment DAOs are required.");
        }
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
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
        return appointmentDAO.save(appointment);
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
}
