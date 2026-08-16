package doctorportal;

import java.io.Serializable;
import java.util.List;

public class PatientConsultationSummary implements Serializable {
    private int patientId;
    private String patientName;
    private String doctorName;
    private List<Consultation> consultations;

    public PatientConsultationSummary(int patientId, String patientName, List<Consultation> consultations) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.consultations = consultations;
    }

    // Getters & Setters
    public int getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public List<Consultation> getConsultations() { return consultations; }
}