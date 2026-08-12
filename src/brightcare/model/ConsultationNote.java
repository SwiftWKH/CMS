package brightcare.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ConsultationNote implements Serializable {
    private static final long serialVersionUID = 1L;

    private int noteId;
    private int appointmentId;
    private int doctorId;
    private String notes;
    private String diagnosis;
    private String prescription;
    private LocalDateTime createdAt;

    public ConsultationNote() {
    }

    public ConsultationNote(int noteId, int appointmentId, int doctorId, String notes,
            String diagnosis, String prescription, LocalDateTime createdAt) {
        this.noteId = noteId;
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.createdAt = createdAt;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
