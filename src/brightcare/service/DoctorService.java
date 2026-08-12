package brightcare.service;

import brightcare.dao.AppointmentDAO;
import brightcare.dao.ConsultationNoteDAO;
import brightcare.dao.DoctorDAO;
import brightcare.model.Doctor;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DoctorService {
    private final AppointmentDAO appointmentDAO;
    private final ConsultationNoteDAO consultationNoteDAO;
    private final DoctorDAO doctorDAO;

    public DoctorService() {
        this(new AppointmentDAO(), new ConsultationNoteDAO(), new DoctorDAO());
    }

    public DoctorService(AppointmentDAO appointmentDAO, ConsultationNoteDAO consultationNoteDAO) {
        this(appointmentDAO, consultationNoteDAO, new DoctorDAO());
    }

    public DoctorService(AppointmentDAO appointmentDAO, ConsultationNoteDAO consultationNoteDAO, DoctorDAO doctorDAO) {
        if (appointmentDAO == null || consultationNoteDAO == null || doctorDAO == null) {
            throw new IllegalArgumentException("Appointment, consultation, and doctor DAOs are required.");
        }
        this.appointmentDAO = appointmentDAO;
        this.consultationNoteDAO = consultationNoteDAO;
        this.doctorDAO = doctorDAO;
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) {
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor ID must be greater than zero.");
        }
        return appointmentDAO.findByDoctorAndDate(resolveDoctorIdForPortal(doctorId), date);
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        if (patientId <= 0) {
            throw new IllegalArgumentException("Patient ID must be greater than zero.");
        }
        return consultationNoteDAO.findByPatientId(patientId);
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        if (note == null) {
            throw new IllegalArgumentException("Consultation note is required.");
        }
        note.setDoctorId(resolveDoctorIdForPortal(note.getDoctorId()));
        ConsultationNote saved = consultationNoteDAO.save(note);
        Appointment appointment = appointmentDAO.findById(note.getAppointmentId());
        if (appointment != null) {
            appointment.setStatus("COMPLETED");
            appointmentDAO.update(appointment);
        }
        return saved;
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date,
            List<LocalTime> availableSlots) {
        return availableSlots;
    }

    private int resolveDoctorIdForPortal(int possibleDoctorOrUserId) {
        Doctor byUserId = doctorDAO.findByUserId(possibleDoctorOrUserId);
        if (byUserId != null) {
            return byUserId.getDoctorId();
        }
        Doctor byDoctorId = doctorDAO.findById(possibleDoctorOrUserId);
        if (byDoctorId != null) {
            return byDoctorId.getDoctorId();
        }
        return possibleDoctorOrUserId;
    }
}
