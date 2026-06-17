package brightcare.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PermissionChecker {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_DOCTOR = "DOCTOR";
    public static final String ROLE_RECEPTIONIST = "RECEPTIONIST";
    public static final String ROLE_PATIENT = "PATIENT";

    public static final String PERMISSION_REPORTS = "REPORTS";
    public static final String PERMISSION_ADMIN = "ADMIN";
    public static final String PERMISSION_SECURITY = "SECURITY";
    public static final String PERMISSION_PATIENT_MODULE = "PATIENT_MODULE";
    public static final String PERMISSION_DOCTOR_MODULE = "DOCTOR_MODULE";
    public static final String PERMISSION_RECEPTIONIST_MODULE = "RECEPTIONIST_MODULE";

    private final Set<String> validRoles;

    public PermissionChecker() {
        this.validRoles = new HashSet<String>(Arrays.asList(
                ROLE_ADMIN,
                ROLE_DOCTOR,
                ROLE_RECEPTIONIST,
                ROLE_PATIENT
        ));
    }

    public boolean isValidRole(String role) {
        return validRoles.contains(normalize(role));
    }

    public boolean hasRole(String actualRole, String requiredRole) {
        String normalizedActual = normalize(actualRole);
        String normalizedRequired = normalize(requiredRole);

        if (!isValidRole(normalizedActual) || !isValidRole(normalizedRequired)) {
            return false;
        }

        if (ROLE_ADMIN.equals(normalizedActual)) {
            return true;
        }

        return normalizedActual.equals(normalizedRequired);
    }

    public boolean hasPermission(String role, String permission) {
        String normalizedRole = normalize(role);
        String normalizedPermission = normalize(permission);

        if (ROLE_ADMIN.equals(normalizedRole)) {
            return true;
        }

        if (ROLE_DOCTOR.equals(normalizedRole)) {
            return PERMISSION_DOCTOR_MODULE.equals(normalizedPermission);
        }

        if (ROLE_RECEPTIONIST.equals(normalizedRole)) {
            return PERMISSION_RECEPTIONIST_MODULE.equals(normalizedPermission)
                    || PERMISSION_PATIENT_MODULE.equals(normalizedPermission);
        }

        if (ROLE_PATIENT.equals(normalizedRole)) {
            return PERMISSION_PATIENT_MODULE.equals(normalizedPermission);
        }

        return false;
    }

    public boolean canAccessAdminModule(String role) {
        return hasPermission(role, PERMISSION_ADMIN);
    }

    public boolean canGenerateReports(String role) {
        return hasPermission(role, PERMISSION_REPORTS);
    }

    public boolean canManageSecurity(String role) {
        return hasPermission(role, PERMISSION_SECURITY);
    }

    public boolean canAccessDoctorModule(String role) {
        return hasPermission(role, PERMISSION_DOCTOR_MODULE);
    }

    public boolean canAccessReceptionistModule(String role) {
        return hasPermission(role, PERMISSION_RECEPTIONIST_MODULE);
    }

    public boolean canAccessPatientModule(String role) {
        return hasPermission(role, PERMISSION_PATIENT_MODULE);
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase();
    }
}
