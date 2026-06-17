package brightcare.service;

import brightcare.model.Report;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private final ReportDataProvider reportDataProvider;

    public ReportService() {
        this(new EmptyReportDataProvider());
    }

    public ReportService(ReportDataProvider reportDataProvider) {
        if (reportDataProvider == null) {
            throw new IllegalArgumentException("Report data provider is required.");
        }
        this.reportDataProvider = reportDataProvider;
    }

    public Report generateMonthlyAppointmentReport(int month, int year) {
        validateMonth(month);
        validateYear(year);

        List<AppointmentSummary> appointments = reportDataProvider.findAppointmentsByMonth(month, year);
        StringBuilder content = new StringBuilder();
        content.append("Monthly Appointment Report").append(System.lineSeparator());
        content.append("Month: ").append(month).append(System.lineSeparator());
        content.append("Year: ").append(year).append(System.lineSeparator());
        content.append("Total appointments: ").append(appointments.size()).append(System.lineSeparator());
        content.append(System.lineSeparator());

        for (int i = 0; i < appointments.size(); i++) {
            AppointmentSummary appointment = appointments.get(i);
            content.append(i + 1).append(". ")
                    .append(appointment.getAppointmentDate()).append(" ")
                    .append(appointment.getAppointmentTime()).append(" | Patient #")
                    .append(appointment.getPatientId()).append(" | Doctor #")
                    .append(appointment.getDoctorId()).append(" | ")
                    .append(appointment.getStatus()).append(System.lineSeparator());
        }

        return createReport("MONTHLY_APPOINTMENT", content.toString());
    }

    public Report generateDoctorConsultationReport(int doctorId, int month, int year) {
        validatePositiveId(doctorId, "Doctor ID");
        validateMonth(month);
        validateYear(year);

        List<ConsultationSummary> consultations =
                reportDataProvider.findConsultationsByDoctorAndMonth(doctorId, month, year);

        StringBuilder content = new StringBuilder();
        content.append("Doctor Consultation Report").append(System.lineSeparator());
        content.append("Doctor ID: ").append(doctorId).append(System.lineSeparator());
        content.append("Month: ").append(month).append(System.lineSeparator());
        content.append("Year: ").append(year).append(System.lineSeparator());
        content.append("Total consultations: ").append(consultations.size()).append(System.lineSeparator());
        content.append(System.lineSeparator());

        for (int i = 0; i < consultations.size(); i++) {
            ConsultationSummary consultation = consultations.get(i);
            content.append(i + 1).append(". Appointment #")
                    .append(consultation.getAppointmentId()).append(" | Patient #")
                    .append(consultation.getPatientId()).append(" | Diagnosis: ")
                    .append(consultation.getDiagnosis()).append(System.lineSeparator());
        }

        return createReport("DOCTOR_CONSULTATION", content.toString());
    }

    public Report generatePatientVisitSummary(int patientId) {
        validatePositiveId(patientId, "Patient ID");

        List<VisitSummary> visits = reportDataProvider.findVisitsByPatient(patientId);
        StringBuilder content = new StringBuilder();
        content.append("Patient Visit Summary").append(System.lineSeparator());
        content.append("Patient ID: ").append(patientId).append(System.lineSeparator());
        content.append("Total visits: ").append(visits.size()).append(System.lineSeparator());
        content.append(System.lineSeparator());

        for (int i = 0; i < visits.size(); i++) {
            VisitSummary visit = visits.get(i);
            content.append(i + 1).append(". ")
                    .append(visit.getVisitDate()).append(" | Doctor #")
                    .append(visit.getDoctorId()).append(" | ")
                    .append(visit.getSummary()).append(System.lineSeparator());
        }

        return createReport("PATIENT_VISIT_SUMMARY", content.toString());
    }

    public String viewSystemStatistics() {
        SystemStatistics statistics = reportDataProvider.getSystemStatistics();
        StringBuilder content = new StringBuilder();
        content.append("System Statistics").append(System.lineSeparator());
        content.append("Total patients: ").append(statistics.getTotalPatients()).append(System.lineSeparator());
        content.append("Total doctors: ").append(statistics.getTotalDoctors()).append(System.lineSeparator());
        content.append("Total appointments: ").append(statistics.getTotalAppointments()).append(System.lineSeparator());
        content.append("Completed appointments: ").append(statistics.getCompletedAppointments()).append(System.lineSeparator());
        content.append("Cancelled appointments: ").append(statistics.getCancelledAppointments());
        return content.toString();
    }

    private Report createReport(String reportType, String content) {
        return new Report(0, reportType, 0, LocalDateTime.now(), null);
    }

    private void validateMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
    }

    private void validateYear(int year) {
        if (year < 2000) {
            throw new IllegalArgumentException("Year must be 2000 or later.");
        }
    }

    private void validatePositiveId(int id, String label) {
        if (id <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
    }

    public interface ReportDataProvider {
        List<AppointmentSummary> findAppointmentsByMonth(int month, int year);

        List<ConsultationSummary> findConsultationsByDoctorAndMonth(int doctorId, int month, int year);

        List<VisitSummary> findVisitsByPatient(int patientId);

        SystemStatistics getSystemStatistics();
    }

    private static class EmptyReportDataProvider implements ReportDataProvider {
        public List<AppointmentSummary> findAppointmentsByMonth(int month, int year) {
            return new ArrayList<AppointmentSummary>();
        }

        public List<ConsultationSummary> findConsultationsByDoctorAndMonth(int doctorId, int month, int year) {
            return new ArrayList<ConsultationSummary>();
        }

        public List<VisitSummary> findVisitsByPatient(int patientId) {
            return new ArrayList<VisitSummary>();
        }

        public SystemStatistics getSystemStatistics() {
            return new SystemStatistics(0, 0, 0, 0, 0);
        }
    }

    public static class AppointmentSummary {
        private final int appointmentId;
        private final int patientId;
        private final int doctorId;
        private final LocalDate appointmentDate;
        private final String appointmentTime;
        private final String status;

        public AppointmentSummary(int appointmentId, int patientId, int doctorId,
                LocalDate appointmentDate, String appointmentTime, String status) {
            this.appointmentId = appointmentId;
            this.patientId = patientId;
            this.doctorId = doctorId;
            this.appointmentDate = appointmentDate;
            this.appointmentTime = appointmentTime;
            this.status = status;
        }

        public int getAppointmentId() {
            return appointmentId;
        }

        public int getPatientId() {
            return patientId;
        }

        public int getDoctorId() {
            return doctorId;
        }

        public LocalDate getAppointmentDate() {
            return appointmentDate;
        }

        public String getAppointmentTime() {
            return appointmentTime;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class ConsultationSummary {
        private final int appointmentId;
        private final int patientId;
        private final String diagnosis;

        public ConsultationSummary(int appointmentId, int patientId, String diagnosis) {
            this.appointmentId = appointmentId;
            this.patientId = patientId;
            this.diagnosis = diagnosis;
        }

        public int getAppointmentId() {
            return appointmentId;
        }

        public int getPatientId() {
            return patientId;
        }

        public String getDiagnosis() {
            return diagnosis;
        }
    }

    public static class VisitSummary {
        private final LocalDate visitDate;
        private final int doctorId;
        private final String summary;

        public VisitSummary(LocalDate visitDate, int doctorId, String summary) {
            this.visitDate = visitDate;
            this.doctorId = doctorId;
            this.summary = summary;
        }

        public LocalDate getVisitDate() {
            return visitDate;
        }

        public int getDoctorId() {
            return doctorId;
        }

        public String getSummary() {
            return summary;
        }
    }

    public static class SystemStatistics {
        private final int totalPatients;
        private final int totalDoctors;
        private final int totalAppointments;
        private final int completedAppointments;
        private final int cancelledAppointments;

        public SystemStatistics(int totalPatients, int totalDoctors, int totalAppointments,
                int completedAppointments, int cancelledAppointments) {
            this.totalPatients = totalPatients;
            this.totalDoctors = totalDoctors;
            this.totalAppointments = totalAppointments;
            this.completedAppointments = completedAppointments;
            this.cancelledAppointments = cancelledAppointments;
        }

        public int getTotalPatients() {
            return totalPatients;
        }

        public int getTotalDoctors() {
            return totalDoctors;
        }

        public int getTotalAppointments() {
            return totalAppointments;
        }

        public int getCompletedAppointments() {
            return completedAppointments;
        }

        public int getCancelledAppointments() {
            return cancelledAppointments;
        }
    }
}
