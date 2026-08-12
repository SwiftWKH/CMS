package brightcare.client.doctor.controller;

import brightcare.client.common.controller.NavigationController;
import brightcare.client.gateway.AuthenticationGateway;
import brightcare.client.gateway.DoctorGateway;
import brightcare.client.gateway.UnavailableAuthenticationGateway;
import brightcare.client.gateway.UnavailableDoctorGateway;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.JFrame;

public class DoctorController {
    private final DoctorGateway gateway;
    private final AuthenticationGateway authenticationGateway;
    private final NavigationController navigationController;
    private final int currentDoctorId;

    public DoctorController() {
        this(new UnavailableDoctorGateway(), 0);
    }

    public DoctorController(DoctorGateway gateway, int currentDoctorId) {
        this(gateway, new UnavailableAuthenticationGateway(), new NavigationController(), currentDoctorId);
    }

    public DoctorController(DoctorGateway gateway, AuthenticationGateway authenticationGateway,
            NavigationController navigationController, int currentDoctorId) {
        if (gateway == null) {
            throw new IllegalArgumentException("Doctor gateway is required.");
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
        this.currentDoctorId = currentDoctorId;
    }

    public int getCurrentDoctorId() {
        return currentDoctorId;
    }

    public List<Appointment> viewAppointmentList(int doctorId, LocalDate date) {
        return gateway.viewAppointmentList(doctorId, date);
    }

    public List<ConsultationNote> viewMedicalHistory(int patientId) {
        return gateway.viewMedicalHistory(patientId);
    }

    public ConsultationNote updateConsultationNotes(ConsultationNote note) {
        return gateway.updateConsultationNotes(note);
    }

    public List<LocalTime> manageAppointmentSchedule(int doctorId, LocalDate date, List<LocalTime> availableSlots) {
        return gateway.manageAppointmentSchedule(doctorId, date, availableSlots);
    }

    public void logout(JFrame currentFrame) {
        if (currentDoctorId > 0) {
            authenticationGateway.logout(currentDoctorId);
        }
        navigationController.openLogin(currentFrame);
    }
}
