package brightcare.client.receptionist.controller;

import brightcare.client.common.controller.NavigationController;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.ReceptionistGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UnavailableReceptionistGateway;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JFrame;

public class ReceptionistController {
    private final ReceptionistGateway gateway;
    private final AuthenticationGateway authenticationGateway;
    private final NavigationController navigationController;
    private final int currentUserId;

    public ReceptionistController() {
        this(new UnavailableReceptionistGateway());
    }

    public ReceptionistController(ReceptionistGateway gateway) {
        this(gateway, new UnavailableAuthenticationGateway(), new NavigationController(), 0);
    }

    public ReceptionistController(ReceptionistGateway gateway, AuthenticationGateway authenticationGateway,
            NavigationController navigationController, int currentUserId) {
        if (gateway == null) {
            throw new IllegalArgumentException("Receptionist gateway is required.");
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
        this.currentUserId = currentUserId;
    }

    public Patient registerPatient(Patient patient) {
        return gateway.registerPatient(patient);
    }

    public Patient updatePatientDetails(Patient patient) {
        return gateway.updatePatientDetails(patient);
    }

    public Appointment createAppointment(Appointment appointment) {
        return gateway.createAppointment(appointment);
    }

    public Appointment modifyAppointment(Appointment appointment) {
        return gateway.modifyAppointment(appointment);
    }

    public Appointment cancelAppointment(int appointmentId) {
        return gateway.cancelAppointment(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) {
        return gateway.viewAppointmentSchedule(date);
    }

    public void logout(JFrame currentFrame) {
        if (currentUserId > 0) {
            authenticationGateway.logout(currentUserId);
        }
        navigationController.openLogin(currentFrame);
    }
}
