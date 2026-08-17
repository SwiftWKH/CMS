package brightcare.service;

import brightcare.dao.AppointmentDAO;
import brightcare.dao.ConsultationNoteDAO;
import brightcare.dao.DataSourceConfig;
import brightcare.dao.DoctorDAO;
import brightcare.model.Doctor;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.util.BrightCareLogger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorService {
    private static final Logger LOGGER = BrightCareLogger.getLogger(DoctorService.class);

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
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorService.viewAppointmentList started. inputDoctorId=" + doctorId
                + ", date=" + date + ", dataSource=" + DataSourceConfig.resolveMode() + ".");
        if (doctorId <= 0) {
            throw new IllegalArgumentException("Doctor ID must be greater than zero.");
        }
        try {
            int resolvedDoctorId = resolveDoctorIdForPortal(doctorId);
            List<Appointment> result = appointmentDAO.findByDoctorAndDate(resolvedDoctorId, date);
            LOGGER.info("DoctorService.viewAppointmentList finished. inputDoctorId=" + doctorId
                    + ", resolvedDoctorId=" + resolvedDoctorId + ", rows=" + result.size()
                    + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorService.viewAppointmentList failed. inputDoctorId="
                    + doctorId + ", date=" + date + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public List<Doctor> viewDoctors() {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorService.viewDoctors started. dataSource=" + DataSourceConfig.resolveMode() + ".");
        try {
            List<Doctor> doctors = doctorDAO.findAll();
            LOGGER.info("DoctorService.viewDoctors finished. rows=" + doctors.size()
                    + ", durationMs=" + elapsed(started) + ".");
            return doctors;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorService.viewDoctors failed. durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorService.viewMedicalHistory started. patientId=" + patientId
                + ", dataSource=" + DataSourceConfig.resolveMode() + ".");
        if (patientId <= 0) {
            throw new IllegalArgumentException("Patient ID must be greater than zero.");
        }
        try {
            List<ConsultationNote> result = consultationNoteDAO.findByPatientId(patientId);
            LOGGER.info("DoctorService.viewMedicalHistory finished. patientId=" + patientId
                    + ", rows=" + result.size() + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorService.viewMedicalHistory failed. patientId="
                    + patientId + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorService.updateConsultationNotes started. appointmentId="
                + (note == null ? "<null>" : note.getAppointmentId()) + ", dataSource="
                + DataSourceConfig.resolveMode() + ".");
        if (note == null) {
            throw new IllegalArgumentException("Consultation note is required.");
        }
        try {
            int inputDoctorId = note.getDoctorId();
            note.setDoctorId(resolveDoctorIdForPortal(note.getDoctorId()));
            Appointment appointment = appointmentDAO.findById(note.getAppointmentId());
            if (note.getPatientId() <= 0 && appointment != null) {
                note.setPatientId(appointment.getPatientId());
            }
            validateConsultationPatient(note);
            ConsultationNote saved = consultationNoteDAO.save(note);
            if (appointment != null) {
                appointment.setStatus("COMPLETED");
                appointmentDAO.update(appointment);
            }
            LOGGER.info("DoctorService.updateConsultationNotes finished. appointmentId="
                    + note.getAppointmentId() + ", inputDoctorId=" + inputDoctorId
                    + ", resolvedDoctorId=" + note.getDoctorId() + ", patientId=" + note.getPatientId()
                    + ", saved=" + (saved != null)
                    + ", appointmentFound=" + (appointment != null) + ", durationMs=" + elapsed(started) + ".");
            return saved;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorService.updateConsultationNotes failed. appointmentId="
                    + note.getAppointmentId() + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date,
            List<LocalTime> availableSlots) {
        LOGGER.info("DoctorService.manageAppointmentSchedule called. doctorId=" + doctorId
                + ", date=" + date + ", slots=" + (availableSlots == null ? 0 : availableSlots.size()) + ".");
        return availableSlots;
    }

    private void validateConsultationPatient(ConsultationNote note) {
        if (note.getPatientId() <= 0) {
            throw new IllegalArgumentException("Patient ID is required for consultation notes.");
        }
    }

    private int resolveDoctorIdForPortal(int possibleDoctorOrUserId) {
        LOGGER.info("DoctorService resolving doctor portal id. input=" + possibleDoctorOrUserId + ".");
        Doctor byUserId = doctorDAO.findByUserId(possibleDoctorOrUserId);
        if (byUserId != null) {
            LOGGER.info("DoctorService resolved doctor portal id by user id. input="
                    + possibleDoctorOrUserId + ", resolvedDoctorId=" + byUserId.getDoctorId() + ".");
            return byUserId.getDoctorId();
        }
        Doctor byDoctorId = doctorDAO.findById(possibleDoctorOrUserId);
        if (byDoctorId != null) {
            LOGGER.info("DoctorService resolved doctor portal id by doctor id. input="
                    + possibleDoctorOrUserId + ", resolvedDoctorId=" + byDoctorId.getDoctorId() + ".");
            return byDoctorId.getDoctorId();
        }
        LOGGER.warning("DoctorService could not resolve doctor portal id; using raw input="
                + possibleDoctorOrUserId + ".");
        return possibleDoctorOrUserId;
    }

    private long elapsed(long started) {
        return System.currentTimeMillis() - started;
    }
}
