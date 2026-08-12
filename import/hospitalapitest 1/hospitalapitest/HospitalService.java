package com.example.hospitalapitest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HospitalService extends Remote {

    // ======================================================
    // Doctor
    // ======================================================

    // Get all doctors (JSON)
    String getDoctors() throws RemoteException;


    // ======================================================
    // Patient
    // ======================================================

    // Get all patients (JSON)
    String getPatients() throws RemoteException;

    // Register new patient
    String registerPatient(
            String patientName,
            String patientContactNumber
    ) throws RemoteException;

    // Update patient information
    String updatePatient(
            int patientID,
            String patientName,
            String patientContactNumber
    ) throws RemoteException;


    // ======================================================
    // Appointment
    // ======================================================

    // Get all appointments (JSON)
    String getAppointments() throws RemoteException;

    // Create appointment
    String createAppointment(
            int doctorID,
            int patientID,
            String appointmentDate,
            String appointmentTime,
            String reason
    ) throws RemoteException;

    // Update appointment
    String updateAppointment(
            int appointmentID,
            int doctorID,
            int patientID,
            String appointmentDate,
            String appointmentTime,
            String status,
            String stage,
            String reason
    ) throws RemoteException;

    // Delete appointment
    String deleteAppointment(
            int appointmentID
    ) throws RemoteException;

}