package brightcare.client.patient.controller;

import brightcare.client.common.controller.NavigationController;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.PatientGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UnavailablePatientGateway;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.JFrame;

public class PatientController {
    private final PatientGateway gateway;
    private final AuthenticationGateway authenticationGateway;
    private final NavigationController navigationController;
    private final int currentPatientId;

    public PatientController() {
        this(new UnavailablePatientGateway(), 0);
    }

    public PatientController(PatientGateway gateway, int currentPatientId) {
        this(gateway, new UnavailableAuthenticationGateway(), new NavigationController(), currentPatientId);
    }

    public PatientController(PatientGateway gateway, AuthenticationGateway authenticationGateway,
            NavigationController navigationController, int currentPatientId) {
        if (gateway == null) {
            throw new IllegalArgumentException("Patient gateway is required.");
        }
        if (authenticationGateway == null) {
            throw new IllegalArgumentException("Authentication gateway is required.");
        }
        if (navigationController == null) {
            throw new IllegalArgumentException("Navigation controller is required.");
        }
        this.gateway = gateway;
        this.authenticationGateway = authenticationGateway;
        this.navigationController = navigationController;
        this.currentPatientId = currentPatientId;
    }

    public int getCurrentPatientId() {
        return currentPatientId;
    }

    public Patient updatePersonalInfo(Patient patient) {
        return gateway.updatePersonalInfo(patient);
    }

    public Appointment bookAppointment(Appointment appointment) {
        return gateway.bookAppointment(appointment);
    }

    public Appointment cancelAppointment(int appointmentId) {
        return gateway.cancelAppointment(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(int patientId) {
        return gateway.viewAppointmentSchedule(patientId);
    }

    public List<Appointment> viewAppointmentHistory(int patientId) {
        return gateway.viewAppointmentHistory(patientId);
    }

    public List<LocalTime> checkDoctorAvailability(int doctorId, LocalDate date) {
        return gateway.checkDoctorAvailability(doctorId, date);
    }

    public void logout(JFrame currentFrame) {
        if (currentPatientId > 0) {
            authenticationGateway.logout(currentPatientId);
        }
        navigationController.openLogin(currentFrame);
    }
}
