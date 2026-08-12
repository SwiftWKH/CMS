package com.example.hospitalapitest;

import java.io.Serializable;

public class Patient implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int patientId;
    private String patientName;
    private String patientContactNumber;

    public Patient(int patientId, String patientName, String patientContactNumber) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientContactNumber = patientContactNumber;
    }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientContactNumber() { return patientContactNumber; }
    public void setPatientContactNumber(String patientContactNumber) { this.patientContactNumber = patientContactNumber; }
}
