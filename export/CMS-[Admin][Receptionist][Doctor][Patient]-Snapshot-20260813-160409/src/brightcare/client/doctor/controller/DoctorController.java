package brightcare.client.doctor.controller;

import brightcare.client.common.controller.NavigationController;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.DoctorGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UnavailableDoctorGateway;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.util.BrightCareLogger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class DoctorController {
    private static final Logger LOGGER = BrightCareLogger.getLogger(DoctorController.class);

    private final DoctorGateway gateway;
    private final AuthenticationGateway authenticationGateway;
    private final NavigationController navigationController;
    private final int currentDoctorId;

    public DoctorController() {
        this(new UnavailableDoctorGateway(), 0);
    }

    public DoctorController(DoctorGateway gateway, int currentDoctorId) {
        this(gateway, new UnavailableAuthenticationGateway(), new NavigationController(), currentDoctorId);
    }

    public DoctorController(DoctorGateway gateway, AuthenticationGateway authenticationGateway,
            NavigationController navigationController, int currentDoctorId) {
        if (gateway == null) {
            throw new IllegalArgumentException("Doctor gateway is required.");
        }
        if (authenticationGateway == null) {
            throw new IllegalArgumentException("Authentication gateway is required.");
        }
        if (navigationController == null) {
            throw new IllegalArgumentException("Navigation controller is required.");
        }
        this.gateway = gateway;
        this.authenticationGateway = authenticationGateway;
        this.navigationController = navigationController;
        this.currentDoctorId = currentDoctorId;
    }

    public int getCurrentDoctorId() {
        return currentDoctorId;
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorController.viewAppointmentList called. currentDoctorId=" + currentDoctorId
                + ", requestedDoctorId=" + doctorId + ", date=" + date + ".");
        try {
            List<Appointment> result = gateway.viewAppointmentList(doctorId, date);
            LOGGER.info("DoctorController.viewAppointmentList returned. rows=" + result.size()
                    + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorController.viewAppointmentList failed. requestedDoctorId="
                    + doctorId + ", date=" + date + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorController.viewMedicalHistory called. patientId=" + patientId + ".");
        try {
            List<ConsultationNote> result = gateway.viewMedicalHistory(patientId);
            LOGGER.info("DoctorController.viewMedicalHistory returned. rows=" + result.size()
                    + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorController.viewMedicalHistory failed. patientId="
                    + patientId + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorController.updateConsultationNotes called. appointmentId="
                + (note == null ? "<null>" : note.getAppointmentId()) + ".");
        try {
            ConsultationNote result = gateway.updateConsultationNotes(note);
            LOGGER.info("DoctorController.updateConsultationNotes returned. saved=" + (result != null)
                    + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorController.updateConsultationNotes failed. appointmentId="
                    + (note == null ? "<null>" : note.getAppointmentId()) + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date, List<LocalTime> availableSlots) {
        long started = System.currentTimeMillis();
        LOGGER.info("DoctorController.manageAppointmentSchedule called. doctorId=" + doctorId
                + ", date=" + date + ", slots=" + (availableSlots == null ? 0 : availableSlots.size()) + ".");
        try {
            List<LocalTime> result = gateway.manageAppointmentSchedule(doctorId, date, availableSlots);
            LOGGER.info("DoctorController.manageAppointmentSchedule returned. slots="
                    + (result == null ? 0 : result.size()) + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "DoctorController.manageAppointmentSchedule failed. doctorId="
                    + doctorId + ", date=" + date + ", durationMs=" + elapsed(started) + ".", ex);
            throw ex;
        }
    }

    public void logout(JFrame currentFrame) {
        LOGGER.info("DoctorController.logout called. currentDoctorId=" + currentDoctorId + ".");
        if (currentDoctorId > 0) {
            authenticationGateway.logout(currentDoctorId);
        }
        navigationController.openLogin(currentFrame);
    }

    private long elapsed(long started) {
        return System.currentTimeMillis() - started;
    }
}
