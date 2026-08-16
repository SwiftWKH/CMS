package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PatientGateway {
    Patient updatePersonalInfo(Patient patient);

    Appointment bookAppointment(Appointment appointment);

    Appointment cancelAppointment(int appointmentId);

    List<Appointment> viewAppointmentSchedule(int patientId);

    List<Appointment> viewAppointmentHistory(int patientId);

    List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date);
}
