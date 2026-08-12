package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import brightcare.remote.ClinicRemoteInterface;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RmiPatientGateway implements PatientGateway {
    private final ClinicRemoteInterface remote;

    public RmiPatientGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public Patient updatePersonalInfo(Patient patient) {
        try {
            return remote.updatePersonalInfo(patient);
        } catch (RemoteException ex) {
            return patient;
        }
    }

    public Appointment bookAppointment(Appointment appointment) {
        try {
            return remote.bookAppointment(appointment);
        } catch (RemoteException ex) {
            return appointment;
        }
    }

    public Appointment cancelAppointment(int appointmentId) {
        try {
            return remote.cancelAppointment(appointmentId);
        } catch (RemoteException ex) {
            return null;
        }
    }

    public List<Appointment> viewAppointmentSchedule(int patientId) {
        try {
            return remote.viewAppointmentSchedule(patientId);
        } catch (RemoteException ex) {
            return new ArrayList<Appointment>();
        }
    }

    public List<Appointment> viewAppointmentHistory(int patientId) {
        try {
            return remote.viewAppointmentHistory(patientId);
        } catch (RemoteException ex) {
            return new ArrayList<Appointment>();
        }
    }

    public List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) {
        try {
            return remote.checkDoctorAvailability(doctorId, date);
        } catch (RemoteException ex) {
            return new ArrayList<LocalTime>();
        }
    }
}
