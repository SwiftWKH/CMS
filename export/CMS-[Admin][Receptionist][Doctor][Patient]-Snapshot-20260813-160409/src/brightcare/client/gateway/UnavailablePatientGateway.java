package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UnavailablePatientGateway implements PatientGateway {
    public Patient updatePersonalInfo(Patient patient) {
        return patient;
    }

    public Appointment bookAppointment(Appointment appointment) {
        return appointment;
    }

    public Appointment cancelAppointment(int appointmentId) {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus("CANCELLED");
        return appointment;
    }

    public List<Appointment> viewAppointmentSchedule(int patientId) {
        return new ArrayList<Appointment>();
    }

    public List<Appointment> viewAppointmentHistory(int patientId) {
        return new ArrayList<Appointment>();
    }

    public List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) {
        return new ArrayList<LocalTime>();
    }
}
