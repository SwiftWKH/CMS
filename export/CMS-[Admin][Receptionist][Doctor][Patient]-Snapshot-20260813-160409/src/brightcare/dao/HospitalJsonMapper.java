package brightcare.dao;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.model.Doctor;
import brightcare.model.Patient;
import brightcare.model.UserAccount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HospitalJsonMapper {
    private HospitalJsonMapper() {
    }

    static List<Patient> patients(String json) {
        List<Patient> patients = new ArrayList<Patient>();
        for (String object : objects(json)) {
            if (!object.contains("patientID") && !object.contains("patientId")) {
                continue;
            }
            int id = intValue(object, "patientID", "patientId");
            String name = stringValue(object, "patientName", "name");
            String contact = stringValue(object, "patientContactNumber", "contactNumber");
            Patient patient = new Patient();
            patient.setPatientId(id);
            patient.setFirstName(firstName(name));
            patient.setLastName(lastName(name));
            patient.setContactNumber(contact);
            patients.add(patient);
        }
        return patients;
    }

    static List<Doctor> doctors(String json) {
        List<Doctor> doctors = new ArrayList<Doctor>();
        for (String object : objects(json)) {
            if (!object.contains("doctorID") && !object.contains("doctorId")) {
                continue;
            }
            Doctor doctor = new Doctor();
            doctor.setDoctorId(intValue(object, "doctorID", "doctorId"));
            doctor.setName(stringValue(object, "doctorName", "name"));
            doctor.setSpecialization(defaultText(stringValue(object, "specialization", "special"), "General"));
            doctor.setContactNumber(stringValue(object, "contactNumber", "doctorContactNumber", "contextNumber"));
            doctors.add(doctor);
        }
        return doctors;
    }

    static List<UserAccount> userAccounts(String json) {
        List<UserAccount> accounts = new ArrayList<UserAccount>();
        for (String object : objects(json)) {
            if (!object.contains("username")) {
                continue;
            }
            UserAccount account = new UserAccount(
                    intValue(object, "user_id", "userId", "userID"),
                    stringValue(object, "username"),
                    stringValue(object, "password_hash", "passwordHash", "password"),
                    stringValue(object, "role"),
                    normalizeAccountStatus(stringValue(object, "status"))
            );
            accounts.add(account);
        }
        return accounts;
    }

    static List<Appointment> appointments(String json) {
        List<Appointment> appointments = new ArrayList<Appointment>();
        for (String object : objects(json)) {
            if (!object.contains("appointmentID") && !object.contains("appointmentId")) {
                continue;
            }
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(intValue(object, "appointmentID", "appointmentId"));
            appointment.setDoctorId(intValue(object, "doctorID", "doctorId"));
            appointment.setPatientId(intValue(object, "patientID", "patientId"));
            appointment.setAppointmentDate(localDate(stringValue(object, "appointmentDate")));
            appointment.setAppointmentTime(localTime(stringValue(object, "appointmentTime")));
            appointment.setStatus(normalizeStatus(stringValue(object, "status")));
            appointment.setReason(stringValue(object, "reason"));
            appointments.add(appointment);
        }
        return appointments;
    }

    static List<ConsultationNote> consultations(String json) {
        List<ConsultationNote> notes = new ArrayList<ConsultationNote>();
        for (String object : objects(json)) {
            if (!object.contains("consultation") && !object.contains("diagnosis")) {
                continue;
            }
            ConsultationNote note = new ConsultationNote();
            note.setNoteId(intValue(object, "noteId", "consultationId", "consultationID"));
            note.setAppointmentId(intValue(object, "appointmentID", "appointmentId"));
            note.setPatientId(intValue(object, "patientID", "patientId"));
            note.setDoctorId(intValue(object, "doctorID", "doctorId"));
            note.setDiagnosis(stringValue(object, "diagnosis"));
            note.setPrescription(stringValue(object, "prescription"));
            note.setNotes(stringValue(object, "notes"));
            note.setCreatedAt(localDateTime(stringValue(object, "createAT", "createdAt")));
            notes.add(note);
        }
        return notes;
    }

    static List<ConsultationNote> consultationsForPatient(String json, int patientId) {
        List<ConsultationNote> notes = new ArrayList<ConsultationNote>();
        for (String object : objects(json)) {
            if ((!object.contains("consultation") && !object.contains("diagnosis"))
                    || intValue(object, "patientID", "patientId") != patientId) {
                continue;
            }
            ConsultationNote note = new ConsultationNote();
            note.setNoteId(intValue(object, "noteId", "consultationId", "consultationID"));
            note.setAppointmentId(intValue(object, "appointmentID", "appointmentId"));
            note.setPatientId(intValue(object, "patientID", "patientId"));
            note.setDoctorId(intValue(object, "doctorID", "doctorId"));
            note.setDiagnosis(stringValue(object, "diagnosis"));
            note.setPrescription(stringValue(object, "prescription"));
            note.setNotes(stringValue(object, "notes"));
            note.setCreatedAt(localDateTime(stringValue(object, "createAT", "createdAt")));
            notes.add(note);
        }
        return notes;
    }

    static String patientJson(Patient patient) {
        return "{"
                + "\"patientName\":\"" + escape(patient.getFullName()) + "\","
                + "\"patientContactNumber\":\"" + escape(patient.getContactNumber()) + "\""
                + "}";
    }

    static String appointmentJson(Appointment appointment) {
        return "{"
                + "\"doctorID\":" + appointment.getDoctorId() + ","
                + "\"patientID\":" + appointment.getPatientId() + ","
                + "\"appointmentDate\":\"" + dateText(appointment.getAppointmentDate()) + "\","
                + "\"appointmentTime\":\"" + timeText(appointment.getAppointmentTime()) + "\","
                + "\"status\":\"" + escape(apiStatus(appointment.getStatus())) + "\","
                + "\"stage\":\"scheduled\","
                + "\"reason\":\"" + escape(appointment.getReason()) + "\""
                + "}";
    }

    static String consultationJson(ConsultationNote note) {
        return "{"
                + "\"appointmentID\":" + note.getAppointmentId() + ","
                + "\"appointmentId\":" + note.getAppointmentId() + ","
                + "\"doctorID\":" + note.getDoctorId() + ","
                + "\"doctorId\":" + note.getDoctorId() + ","
                + "\"patientID\":" + note.getPatientId() + ","
                + "\"patientId\":" + note.getPatientId() + ","
                + "\"diagnosis\":\"" + escape(note.getDiagnosis()) + "\","
                + "\"prescription\":\"" + escape(note.getPrescription()) + "\","
                + "\"notes\":\"" + escape(note.getNotes()) + "\""
                + "}";
    }

    static String userJson(UserAccount account) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        if (account.getUserId() > 0) {
            builder.append("\"user_id\":").append(account.getUserId()).append(',');
            builder.append("\"userId\":").append(account.getUserId()).append(',');
        }
        builder.append("\"username\":\"").append(escape(account.getUsername())).append("\",");
        builder.append("\"password_hash\":\"").append(escape(account.getPasswordHash())).append("\",");
        builder.append("\"passwordHash\":\"").append(escape(account.getPasswordHash())).append("\",");
        builder.append("\"role\":\"").append(escape(account.getRole())).append("\",");
        builder.append("\"status\":\"").append(escape(account.getStatus())).append("\"");
        builder.append('}');
        return builder.toString();
    }

    static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", " ").replace("\n", " ");
    }

    private static List<String> objects(String json) {
        List<String> objects = new ArrayList<String>();
        if (json == null) {
            return objects;
        }
        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (c == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    private static String stringValue(String object, String... names) {
        for (int i = 0; i < names.length; i++) {
            Matcher matcher = Pattern.compile("\"" + Pattern.quote(names[i]) + "\"\\s*:\\s*(\"([^\"]*)\"|null|([^,}\\]]+))")
                    .matcher(object);
            if (matcher.find()) {
                String quoted = matcher.group(2);
                String raw = quoted != null ? quoted : matcher.group(3);
                if (raw == null || "null".equalsIgnoreCase(raw.trim())) {
                    return "";
                }
                return raw.trim();
            }
        }
        return "";
    }

    private static int intValue(String object, String... names) {
        String value = stringValue(object, names);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static LocalDate localDate(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        String normalized = value.contains("T") ? value.substring(0, value.indexOf('T')) : value;
        try {
            return LocalDate.parse(normalized);
        } catch (Exception ex) {
            return null;
        }
    }

    private static LocalTime localTime(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            return LocalTime.parse(value.length() == 5 ? value : value.substring(0, Math.min(5, value.length())));
        } catch (Exception ex) {
            return null;
        }
    }

    private static LocalDateTime localDateTime(String value) {
        if (value == null || value.length() == 0) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ex) {
            return LocalDateTime.now();
        }
    }

    private static String normalizeStatus(String value) {
        if (value == null || value.trim().length() == 0) {
            return "BOOKED";
        }
        String normalized = value.trim().toUpperCase();
        if ("ACTIVE".equals(normalized) || "SCHEDULED".equals(normalized)) {
            return "BOOKED";
        }
        return normalized;
    }

    private static String normalizeAccountStatus(String value) {
        if (value == null || value.trim().length() == 0) {
            return "ACTIVE";
        }
        return value.trim().toUpperCase();
    }

    private static String firstName(String fullName) {
        if (fullName == null) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 0 ? parts[0] : "";
    }

    private static String lastName(String fullName) {
        if (fullName == null) {
            return "";
        }
        String[] parts = fullName.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    private static String defaultText(String value, String fallback) {
        return value == null || value.trim().length() == 0 ? fallback : value;
    }

    private static String text(Object value) {
        return value == null ? "" : value.toString();
    }

    private static String timeText(LocalTime value) {
        return value == null ? "" : value.toString() + (value.getSecond() == 0 ? ":00" : "");
    }

    private static String dateText(LocalDate value) {
        return value == null ? "" : value.toString() + "T00:00:00";
    }

    private static String apiStatus(String status) {
        String normalized = defaultText(status, "BOOKED").trim().toUpperCase();
        if ("BOOKED".equals(normalized)) {
            return "active";
        }
        if ("COMPLETED".equals(normalized)) {
            return "completed";
        }
        if ("CANCELLED".equals(normalized)) {
            return "cancelled";
        }
        return status;
    }
}
