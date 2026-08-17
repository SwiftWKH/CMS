package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UnavailableReceptionistGateway implements ReceptionistGateway {
    public Patient registerPatient(Patient patient) {
        return patient;
    }

    public Patient registerPatient(Patient patient, String username, String password) {
        return patient;
    }

    public List<Patient> viewPatients() {
        return new ArrayList<Patient>();
    }

    public Patient updatePatientDetails(Patient patient) {
        return patient;
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointment;
    }

    public Appointment modifyAppointment(Appointment appointment) {
        return appointment;
    }

    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus("CANCELLED");
        return appointment;
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) {
        return new ArrayList<Appointment>();
    }
}
