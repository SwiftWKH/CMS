package brightcare.dao;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.service.ReportService;
import brightcare.util.BrightCareLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HospitalApiReportDataProvider implements ReportService.ReportDataProvider {
    private static final Logger LOGGER = BrightCareLogger.getLogger(HospitalApiReportDataProvider.class);

    private final HospitalApiClient apiClient;

    public HospitalApiReportDataProvider() {
        this(new HospitalApiClient());
    }

    public HospitalApiReportDataProvider(HospitalApiClient apiClient) {
        if (apiClient == null) {
            throw new IllegalArgumentException("API client is required.");
        }
        this.apiClient = apiClient;
    }

    public List<ReportService.AppointmentSummary> findAppointmentsByMonth(int month, int year) {
        List<ReportService.AppointmentSummary> summaries = new ArrayList<ReportService.AppointmentSummary>();
        for (Appointment appointment : appointments()) {
            if (appointment.getAppointmentDate() != null
                    && appointment.getAppointmentDate().getMonthValue() == month
                    && appointment.getAppointmentDate().getYear() == year) {
                summaries.add(new ReportService.AppointmentSummary(
                        appointment.getAppointmentId(),
                        appointment.getPatientId(),
                        appointment.getDoctorId(),
                        appointment.getAppointmentDate(),
                        appointment.getAppointmentTime() == null ? "" : appointment.getAppointmentTime().toString(),
                        appointment.getStatus()
                ));
            }
        }
        return summaries;
    }

    public List<ReportService.ConsultationSummary> findConsultationsByDoctorAndMonth(
            int doctorId, int month, int year) {
        List<ReportService.ConsultationSummary> summaries = new ArrayList<ReportService.ConsultationSummary>();
        for (ConsultationNote note : consultations()) {
            if (note.getDoctorId() != doctorId) {
                continue;
            }
            Appointment appointment = findAppointment(note.getAppointmentId());
            if (appointment != null && appointment.getAppointmentDate() != null
                    && appointment.getAppointmentDate().getMonthValue() == month
                    && appointment.getAppointmentDate().getYear() == year) {
                summaries.add(new ReportService.ConsultationSummary(
                        note.getAppointmentId(),
                        appointment.getPatientId(),
                        note.getDiagnosis()
                ));
            }
        }
        return summaries;
    }

    public List<ReportService.VisitSummary> findVisitsByPatient(int patientId) {
        List<ReportService.VisitSummary> visits = new ArrayList<ReportService.VisitSummary>();
        for (Appointment appointment : appointments()) {
            if (appointment.getPatientId() == patientId) {
                visits.add(new ReportService.VisitSummary(
                        appointment.getAppointmentDate(),
                        appointment.getDoctorId(),
                        visitSummary(appointment)
                ));
            }
        }
        return visits;
    }

    public ReportService.SystemStatistics getSystemStatistics() {
        List<Appointment> appointments = appointments();
        int completed = 0;
        int cancelled = 0;
        for (Appointment appointment : appointments) {
            if ("COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
                completed++;
            } else if ("CANCELLED".equalsIgnoreCase(appointment.getStatus())) {
                cancelled++;
            }
        }
        return new ReportService.SystemStatistics(
                HospitalJsonMapper.patients(get("/patient")).size(),
                HospitalJsonMapper.doctors(get("/doctor")).size(),
                appointments.size(),
                completed,
                cancelled
        );
    }

    private List<Appointment> appointments() {
        return HospitalJsonMapper.appointments(get("/appointment"));
    }

    private List<ConsultationNote> consultations() {
        return HospitalJsonMapper.consultations(get("/consultation"));
    }

    private Appointment findAppointment(int appointmentId) {
        for (Appointment appointment : appointments()) {
            if (appointment.getAppointmentId() == appointmentId) {
                return appointment;
            }
        }
        return null;
    }

    private String visitSummary(Appointment appointment) {
        for (ConsultationNote note : consultations()) {
            if (note.getAppointmentId() == appointment.getAppointmentId()
                    && note.getDiagnosis() != null
                    && note.getDiagnosis().trim().length() > 0) {
                return "Diagnosis: " + note.getDiagnosis();
            }
        }
        return "Appointment status: " + appointment.getStatus();
    }

    private String get(String path) {
        try {
            return apiClient.get(path);
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Report API request failed. path=" + path, ex);
            return "[]";
        }
    }
}
