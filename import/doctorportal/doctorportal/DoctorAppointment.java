package com.example.hospitalapitest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int appointmentId;
    private int doctorId;
    private int patientId;
    private String patientName;
    private String doctorName;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
    private String status;
    private String stage;
    private String diagnosis;
    private String prescription;
    private String notes;
    private boolean isInHistory;
    
    private List<Consultation> consultations;

    public DoctorAppointment(int appointmentId, int doctorId, int patientId, String patientName, 
                             String doctorName, String appointmentDate, String appointmentTime, 
                             String reason, String status, String stage) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
        this.status = status;
        this.stage = stage;
        this.diagnosis = "";
        this.prescription = "";
        this.notes = "";
        this.isInHistory = false;
        this.consultations = new ArrayList<>();
    }

    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isInHistory() { return isInHistory; }
    public void setInHistory(boolean inHistory) { isInHistory = inHistory; }
    public List<Consultation> getConsultations() { return consultations; }
    public void setConsultations(List<Consultation> consultations) { this.consultations = consultations; }
    public void addConsultation(Consultation consultation) { this.consultations.add(consultation); }
}
