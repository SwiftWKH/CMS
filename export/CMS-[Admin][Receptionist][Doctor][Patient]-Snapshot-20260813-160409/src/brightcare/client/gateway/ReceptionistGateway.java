package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.util.List;

public interface ReceptionistGateway {
    Patient registerPatient(Patient patient);

    Patient updatePatientDetails(Patient patient);

    Appointment createAppointment(Appointment appointment);

    Appointment modifyAppointment(Appointment appointment);

    Appointment cancelAppointment(int appointmentId);

    List<Appointment> viewAppointmentSchedule(LocalDate date);
}
