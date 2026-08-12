package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.remote.ClinicRemoteInterface;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RmiDoctorGateway implements DoctorGateway {
    private final ClinicRemoteInterface remote;

    public RmiDoctorGateway(ClinicRemoteInterface remote) {
        if (remote == null) {
            throw new IllegalArgumentException("Remote interface is required.");
        }
        this.remote = remote;
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) {
        try {
            return remote.viewAppointmentList(doctorId, date);
        } catch (RemoteException ex) {
            return new ArrayList<Appointment>();
        }
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        try {
            return remote.viewMedicalHistory(patientId);
        } catch (RemoteException ex) {
            return new ArrayList<ConsultationNote>();
        }
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        try {
            return remote.updateConsultationNotes(note);
        } catch (RemoteException ex) {
            return note;
        }
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date, List<LocalTime> availableSlots) {
        try {
            return remote.manageAppointmentSchedule(doctorId, date, availableSlots);
        } catch (RemoteException ex) {
            return availableSlots == null ? new ArrayList<LocalTime>() : availableSlots;
        }
    }
}
