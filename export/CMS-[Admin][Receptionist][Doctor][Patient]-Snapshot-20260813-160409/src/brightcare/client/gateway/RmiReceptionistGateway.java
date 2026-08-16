package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import brightcare.remote.ClinicRemoteInterface;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RmiReceptionistGateway implements ReceptionistGateway {
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
