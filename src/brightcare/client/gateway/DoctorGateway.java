package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorGateway {
    List<Appointment> viewAppointmentList(int doctorId, LocalDate date);

    List<ConsultationNote> viewMedicalHistory(int patientId);

    ConsultationNote updateConsultationNotes(ConsultationNote note);

    List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date, List<LocalTime> availableSlots);
}
