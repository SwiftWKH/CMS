package com.example.hospitalapitest;

import java.io.Serializable;

public class Consultation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int consultationId;
    private int appointmentId;
    private String diagnosis;
    private String prescription;
    private String notes;
    private String dateCreated;
    
    public Consultation(int consultationId, int appointmentId, String diagnosis, String prescription, String notes) {
        this.consultationId = consultationId;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
        this.dateCreated = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }
    
    // Getters and Setters
    public int getConsultationId() { return consultationId; }
    public void setConsultationId(int consultationId) { this.consultationId = consultationId; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }
}