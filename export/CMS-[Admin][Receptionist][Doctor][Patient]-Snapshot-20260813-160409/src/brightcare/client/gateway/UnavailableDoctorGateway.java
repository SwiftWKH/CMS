package brightcare.client.gateway;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UnavailableDoctorGateway implements DoctorGateway {
    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) {
        return new ArrayList<Appointment>();
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        return new ArrayList<ConsultationNote>();
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        return note;
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date, List<LocalTime> availableSlots) {
        return availableSlots == null ? new ArrayList<LocalTime>() : availableSlots;
    }
}
