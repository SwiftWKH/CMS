package brightcare.service;

import brightcare.concurrency.AppointmentLockManager;
import brightcare.dao.AppointmentDAO;
import brightcare.dao.PatientDAO;
import brightcare.dao.UserAccountDAO;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import brightcare.model.UserAccount;
import brightcare.security.PermissionChecker;
import java.time.LocalDate;
import java.util.List;

public class ReceptionistService {
    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;
    private final AppointmentLockManager appointmentLockManager;
    private final UserAccountDAO userAccountDAO;
    private final AuthService.PasswordHasher passwordHasher;

    public ReceptionistService() {
        this(new PatientDAO(), new AppointmentDAO(), new AppointmentLockManager());
    }

    public ReceptionistService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this(patientDAO, appointmentDAO, new AppointmentLockManager());
    }

    public ReceptionistService(PatientDAO patientDAO, AppointmentDAO appointmentDAO,
            AppointmentLockManager appointmentLockManager) {
        this(patientDAO, appointmentDAO, appointmentLockManager,
                new UserAccountDAO(), new AuthService.Sha256PasswordHasher());
    }

    public ReceptionistService(PatientDAO patientDAO, AppointmentDAO appointmentDAO,
            AppointmentLockManager appointmentLockManager, UserAccountDAO userAccountDAO,
            AuthService.PasswordHasher passwordHasher) {
        if (patientDAO == null || appointmentDAO == null || appointmentLockManager == null
                || userAccountDAO == null || passwordHasher == null) {
            throw new IllegalArgumentException(
                    "Patient DAO, appointment DAO, lock manager, user account DAO, and password hasher are required.");
        }
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
        this.appointmentLockManager = appointmentLockManager;
        this.userAccountDAO = userAccountDAO;
        this.passwordHasher = passwordHasher;
    }

    public Patient registerPatient(Patient patient) {
        validatePatient(patient);
        return patientDAO.save(patient);
    }

    public Patient registerPatientWithAccount(Patient patient, String username, String password) {
        validatePatient(patient);
        requireText(username, "Username");
        requireText(password, "Password");

        String normalizedUsername = username.trim();
        if (userAccountDAO.findByUsername(normalizedUsername) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        String passwordHash = passwordHasher.hash(password);
        Patient apiPatient = patientDAO.saveWithAccount(patient, normalizedUsername, passwordHash, "ACTIVE");
        if (apiPatient != null) {
            return apiPatient;
        }

        UserAccount account = userAccountDAO.create(normalizedUsername, passwordHash, PermissionChecker.ROLE_PATIENT);
        if (account == null) {
            return null;
        }

        patient.setUserId(account.getUserId());
        if (account.getRoleId() > 0) {
            patient.setPatientId(account.getRoleId());
            return patientDAO.update(patient);
        }
        return patientDAO.save(patient);
    }

    public Patient updatePatientDetails(Patient patient) {
        validatePatient(patient);
        return patientDAO.update(patient);
    }

    public Appointment createAppointment(Appointment appointment) {
        validateAppointment(appointment);
        if (appointment.getStatus() == null || appointment.getStatus().trim().length() == 0) {
            appointment.setStatus("BOOKED");
        }
        if (!appointmentLockManager.acquireSlot(appointment)) {
            throw new IllegalStateException("Appointment slot is currently being booked by another user.");
        }
        try {
            ensureSlotAvailable(appointment);
            return appointmentDAO.save(appointment);
        } finally {
            appointmentLockManager.releaseSlot(appointment);
        }
    }

    public Appointment modifyAppointment(Appointment appointment) {
        validateAppointment(appointment);
        Appointment original = appointmentDAO.findById(appointment.getAppointmentId());
        if (!appointmentLockManager.acquireSlot(appointment)) {
            throw new IllegalStateException("Appointment slot is currently being updated by another user.");
        }
        try {
            if (original == null || !sameSlot(original, appointment)) {
                ensureSlotAvailableForUpdate(appointment);
            }
            return appointmentDAO.update(appointment);
        } finally {
            appointmentLockManager.releaseSlot(appointment);
        }
    }

    public Appointment cancelAppointment(int appointmentId) {
        if (appointmentId <= 0) {
            throw new IllegalArgumentException("Appointment ID must be greater than zero.");
        }
        return appointmentDAO.cancel(appointmentId);
    }

    public List<Appointment> viewAppointmentSchedule(LocalDate date) {
        return appointmentDAO.findByDate(date);
    }

    public List<Patient> viewPatients() {
        return patientDAO.findAll();
    }

    private void validatePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is required.");
        }
    }

    private void requireText(String value, String label) {
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment is required.");
        }
        if (appointment.getPatientId() <= 0 || appointment.getDoctorId() <= 0) {
            throw new IllegalArgumentException("Patient ID and Doctor ID are required.");
        }
    }

    private void ensureSlotAvailable(Appointment appointment) {
        for (Appointment existing : appointmentDAO.findByDoctorAndDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            if (sameTime(existing, appointment) && !"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("Appointment slot is already booked.");
            }
        }
    }

    private void ensureSlotAvailableForUpdate(Appointment appointment) {
        for (Appointment existing : appointmentDAO.findByDoctorAndDate(
                appointment.getDoctorId(), appointment.getAppointmentDate())) {
            boolean sameAppointment = existing.getAppointmentId() == appointment.getAppointmentId();
            if (!sameAppointment && sameTime(existing, appointment)
                    && !"CANCELLED".equalsIgnoreCase(existing.getStatus())) {
                throw new IllegalStateException("Appointment slot is already booked.");
            }
        }
    }

    private boolean sameTime(Appointment left, Appointment right) {
        return left.getAppointmentTime() != null && left.getAppointmentTime().equals(right.getAppointmentTime());
    }

    private boolean sameSlot(Appointment left, Appointment right) {
        if (left == null || right == null) {
            return false;
        }
        boolean sameDoctor = left.getDoctorId() == right.getDoctorId();
        boolean sameDate = left.getAppointmentDate() != null
                && left.getAppointmentDate().equals(right.getAppointmentDate());
        return sameDoctor && sameDate && sameTime(left, right);
    }
}
