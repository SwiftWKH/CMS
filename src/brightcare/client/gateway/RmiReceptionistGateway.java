package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import brightcare.remote.ClinicRemoteInterface;
import brightcare.util.BrightCareLogger;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RmiReceptionistGateway implements ReceptionistGateway {
    private static final Logger LOGGER = BrightCareLogger.getLogger(RmiReceptionistGateway.class);

    private final ClinicRemoteInterface remote;

    public RmiReceptionistGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public Patient registerPatient(Patient patient) {
        try {
            return remote.registerPatient(patient);
        } catch (RuntimeException | RemoteException ex) {
            return null;
        }
    }

    public Patient registerPatient(Patient patient, String username, String password) {
        try {
            return remote.registerPatientWithAccount(patient, username, password);
        } catch (RuntimeException | RemoteException ex) {
            return null;
        }
    }

    public List<Patient> viewPatients() {
        try {
            return remote.viewPatients();
        } catch (RuntimeException | RemoteException ex) {
            return new ArrayList<Patient>();
        }
    }

    public Patient updatePatientDetails(Patient patient) {
        try {
            return remote.updatePatientDetails(patient);
        } catch (RuntimeException | RemoteException ex) {
            return null;
        }
    }

    public Appointment createAppointment(Appointment appointment) {
        try {
            return remote.createAppointment(appointment);
        } catch (RuntimeException | RemoteException ex) {
            return null;
        }
    }

    public Appointment modifyAppointment(Appointment appointment) {
        try {
            return remote.modifyAppointment(appointment);
        } catch (RuntimeException | RemoteException ex) {
            LOGGER.log(Level.WARNING, "Remote modify appointment failed. appointmentId="
                    + (appointment == null ? "<null>" : appointment.getAppointmentId()) + ".", ex);
            return null;
        }
    }

    public Appointment cancelAppointment(int appointmentId) {
        try {
            return remote.cancelAppointment(appointmentId);
        } catch (RuntimeException | RemoteException ex) {
            return null;
        }
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) {
        try {
            return remote.viewAppointmentSchedule(date);
        } catch (RuntimeException | RemoteException ex) {
            return new ArrayList<Appointment>();
        }
    }
}
