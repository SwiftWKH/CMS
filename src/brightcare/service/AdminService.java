package brightcare.service;

import brightcare.dao.DoctorDAO;
import brightcare.dao.PatientDAO;
import brightcare.dao.UserAccountDAO;
import brightcare.model.ActiveSessionInfo;
import brightcare.model.Doctor;
import brightcare.model.Patient;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
import brightcare.security.PermissionChecker;
import brightcare.security.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class AdminService {
    private final UserAccountDAO userAccountDAO;
    private final SessionManager sessionManager;
    private final AuthService.PasswordHasher passwordHasher;
    private final PermissionChecker permissionChecker;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    public AdminService(UserAccountDAO userAccountDAO, SessionManager sessionManager) {
        this(userAccountDAO, sessionManager, new AuthService.Sha256PasswordHasher(),
                new PermissionChecker(), new DoctorDAO(), new PatientDAO());
    }

    public AdminService(UserAccountDAO userAccountDAO, SessionManager sessionManager,
            AuthService.PasswordHasher passwordHasher, PermissionChecker permissionChecker) {
        this(userAccountDAO, sessionManager, passwordHasher, permissionChecker, new DoctorDAO(), new PatientDAO());
    }

    public AdminService(UserAccountDAO userAccountDAO, SessionManager sessionManager,
            AuthService.PasswordHasher passwordHasher, PermissionChecker permissionChecker,
            DoctorDAO doctorDAO, PatientDAO patientDAO) {
        if (userAccountDAO == null) {
            throw new IllegalArgumentException("User account DAO is required.");
        }
        if (sessionManager == null) {
            throw new IllegalArgumentException("Session manager is required.");
        }
        if (passwordHasher == null) {
            throw new IllegalArgumentException("Password hasher is required.");
        }
        if (permissionChecker == null) {
            throw new IllegalArgumentException("Permission checker is required.");
        }
        if (doctorDAO == null || patientDAO == null) {
            throw new IllegalArgumentException("Doctor and patient DAOs are required.");
        }
        this.userAccountDAO = userAccountDAO;
        this.sessionManager = sessionManager;
        this.passwordHasher = passwordHasher;
        this.permissionChecker = permissionChecker;
        this.doctorDAO = doctorDAO;
        this.patientDAO = patientDAO;
    }

    public List<UserAccount> viewUsers() {
        return userAccountDAO.findAll();
    }

    public UserAccount createUser(String username, String password, String role) {
        UserProfileInput input = new UserProfileInput();
        input.setUsername(username);
        input.setPassword(password);
        input.setRole(role);
        return createUser(input);
    }

    public UserAccount createUser(UserProfileInput input) {
        if (input == null) {
            return null;
        }
        String username = input.getUsername();
        String password = input.getPassword();
        String role = input.getRole();
        if (isBlank(username) || isBlank(password) || isBlank(role)) {
            return null;
        }
        String normalizedRole = role.trim().toUpperCase();
        if (!permissionChecker.isValidRole(normalizedRole)) {
            return null;
        }
        if (userAccountDAO.findByUsername(username.trim()) != null) {
            return null;
        }

        String normalizedUsername = username.trim();
        String passwordHash = passwordHasher.hash(password);
        if (PermissionChecker.ROLE_DOCTOR.equals(normalizedRole)) {
            return createDoctorAccount(normalizedUsername, passwordHash, input);
        }
        if (PermissionChecker.ROLE_PATIENT.equals(normalizedRole)) {
            return createPatientAccount(normalizedUsername, passwordHash, input);
        }
        return userAccountDAO.create(normalizedUsername, passwordHash, normalizedRole);
    }

    public boolean disableUser(String username) {
        if (isBlank(username)) {
            return false;
        }
        UserAccount account = userAccountDAO.findByUsername(username.trim());
        if (account == null) {
            return false;
        }
        boolean disabled = userAccountDAO.disableByUsername(username.trim());
        if (disabled) {
            sessionManager.removeSessionByUserId(account.getUserId());
        }
        return disabled;
    }

    public UserAccount updateUser(UserProfileInput input) {
        if (input == null || input.getUserId() <= 0 || isBlank(input.getUsername())
                || isBlank(input.getRole()) || isBlank(input.getStatus())) {
            return null;
        }
        String normalizedRole = input.getRole().trim().toUpperCase();
        if (!permissionChecker.isValidRole(normalizedRole)) {
            return null;
        }

        UserAccount existing = userAccountDAO.findByUserId(input.getUserId());
        if (existing == null) {
            return null;
        }
        String normalizedUsername = input.getUsername().trim();
        UserAccount usernameMatch = userAccountDAO.findByUsername(normalizedUsername);
        if (usernameMatch != null && usernameMatch.getUserId() != existing.getUserId()) {
            return null;
        }

        String passwordHash = isBlank(input.getPassword())
                ? existing.getPasswordHash()
                : passwordHasher.hash(input.getPassword());
        UserAccount updated = new UserAccount(
                existing.getUserId(),
                normalizedUsername,
                passwordHash,
                normalizedRole,
                existing.getRoleId(),
                input.getStatus().trim().toUpperCase()
        );
        UserAccount saved = userAccountDAO.update(updated);
        if (saved != null && !"ACTIVE".equalsIgnoreCase(saved.getStatus())) {
            sessionManager.removeSessionByUserId(saved.getUserId());
        }
        return saved;
    }

    public List<ActiveSessionInfo> viewActiveSessions() {
        List<ActiveSessionInfo> activeSessions = new ArrayList<ActiveSessionInfo>();
        List<SessionManager.SessionInfo> sessions = sessionManager.getActiveSessions();
        for (SessionManager.SessionInfo session : sessions) {
            activeSessions.add(new ActiveSessionInfo(
                    session.getUsername(),
                    session.getCreatedAt(),
                    session.getEndedAt(),
                    session.getRole(),
                    session.getStatus()
            ));
        }
        return activeSessions;
    }

    public List<ActiveSessionInfo> viewSessionHistory() {
        List<ActiveSessionInfo> history = new ArrayList<ActiveSessionInfo>();
        List<SessionManager.SessionInfo> sessions = sessionManager.getSessionHistory();
        for (SessionManager.SessionInfo session : sessions) {
            history.add(new ActiveSessionInfo(
                    session.getUsername(),
                    session.getCreatedAt(),
                    session.getEndedAt(),
                    session.getRole(),
                    session.getStatus()
            ));
        }
        return history;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    private UserAccount createDoctorAccount(String username, String passwordHash, UserProfileInput input) {
        Doctor doctor = new Doctor(0, 0,
                defaultText(input.getDoctorName(), username),
                defaultText(input.getSpecialization(), "General"),
                defaultText(input.getDoctorContactNumber(), ""));
        Doctor createdDoctor = doctorDAO.saveWithAccount(doctor, username, passwordHash, "ACTIVE");
        if (createdDoctor == null) {
            return userAccountDAO.create(username, passwordHash, PermissionChecker.ROLE_DOCTOR);
        }

        UserAccount account = userAccountDAO.findByUsername(username);
        if (account != null) {
            return account;
        }
        return new UserAccount(0, username, passwordHash, PermissionChecker.ROLE_DOCTOR,
                createdDoctor.getDoctorId(), "ACTIVE");
    }

    private UserAccount createPatientAccount(String username, String passwordHash, UserProfileInput input) {
        String firstName = defaultText(input.getPatientFirstName(), username);
        Patient patient = new Patient(0, 0, firstName,
                defaultText(input.getPatientLastName(), ""),
                defaultText(input.getPatientIcPassportNo(), ""),
                defaultText(input.getPatientContactNumber(), ""), "");
        Patient createdPatient = patientDAO.saveWithAccount(patient, username, passwordHash, "ACTIVE");
        if (createdPatient == null) {
            return userAccountDAO.create(username, passwordHash, PermissionChecker.ROLE_PATIENT);
        }

        UserAccount account = userAccountDAO.findByUsername(username);
        if (account != null) {
            return account;
        }
        return new UserAccount(0, username, passwordHash, PermissionChecker.ROLE_PATIENT,
                createdPatient.getPatientId(), "ACTIVE");
    }

    private String defaultText(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }
}
