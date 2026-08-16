package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.remote.ClinicRemoteInterface;
import brightcare.util.BrightCareLogger;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiDoctorGateway implements DoctorGateway {
    private static final Logger LOGGER = BrightCareLogger.getLogger(RmiDoctorGateway.class);

    private final ClinicRemoteInterface remote;

    public RmiDoctorGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) {
        long started = System.currentTimeMillis();
        LOGGER.info("Doctor RMI gateway request started. method=viewAppointmentList, doctorId="
                + doctorId + ", date=" + date + ".");
        try {
            List<Appointment> result = listOrEmpty(remote.viewAppointmentList(doctorId, date));
            LOGGER.info("Doctor RMI gateway request succeeded. method=viewAppointmentList, rows="
                    + result.size() + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException | RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Doctor RMI gateway request failed. method=viewAppointmentList, doctorId="
                    + doctorId + ", date=" + date + ", durationMs=" + elapsed(started) + ".", ex);
            return new ArrayList<Appointment>();
        }
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        long started = System.currentTimeMillis();
        LOGGER.info("Doctor RMI gateway request started. method=viewMedicalHistory, patientId="
                + patientId + ".");
        try {
            List<ConsultationNote> result = listOrEmpty(remote.viewMedicalHistory(patientId));
            LOGGER.info("Doctor RMI gateway request succeeded. method=viewMedicalHistory, rows="
                    + result.size() + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException | RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Doctor RMI gateway request failed. method=viewMedicalHistory, patientId="
                    + patientId + ", durationMs=" + elapsed(started) + ".", ex);
            return new ArrayList<ConsultationNote>();
        }
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        long started = System.currentTimeMillis();
        LOGGER.info("Doctor RMI gateway request started. method=updateConsultationNotes, appointmentId="
                + (note == null ? "<null>" : note.getAppointmentId()) + ".");
        try {
            ConsultationNote result = remote.updateConsultationNotes(note);
            LOGGER.info("Doctor RMI gateway request succeeded. method=updateConsultationNotes, saved="
                    + (result != null) + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException | RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Doctor RMI gateway request failed. method=updateConsultationNotes, appointmentId="
                    + (note == null ? "<null>" : note.getAppointmentId()) + ", durationMs=" + elapsed(started) + ".", ex);
            return null;
        }
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date, List<LocalTime> availableSlots) {
        long started = System.currentTimeMillis();
        LOGGER.info("Doctor RMI gateway request started. method=manageAppointmentSchedule, doctorId="
                + doctorId + ", date=" + date + ", slots=" + sizeOf(availableSlots) + ".");
        try {
            List<LocalTime> result = listOrEmpty(remote.manageAppointmentSchedule(doctorId, date, availableSlots));
            LOGGER.info("Doctor RMI gateway request succeeded. method=manageAppointmentSchedule, slots="
                    + sizeOf(result) + ", durationMs=" + elapsed(started) + ".");
            return result;
        } catch (RuntimeException | RemoteException ex) {
            LOGGER.log(Level.SEVERE, "Doctor RMI gateway request failed. method=manageAppointmentSchedule, doctorId="
                    + doctorId + ", date=" + date + ", durationMs=" + elapsed(started) + ".", ex);
            return availableSlots == null ? new ArrayList<LocalTime>() : availableSlots;
        }
    }

    private long elapsed(long started) {
        return System.currentTimeMillis() - started;
    }

    private int sizeOf(List<?> values) {
        return values == null ? 0 : values.size();
    }

    private <T> List<T> listOrEmpty(List<T> values) {
        return values == null ? new ArrayList<T>() : values;
    }
}
