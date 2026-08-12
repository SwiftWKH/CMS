package brightcare.dao;

import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.model.Doctor;
import brightcare.model.Patient;
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
            doctor.setSpecialization(defaultText(stringValue(object, "specialization"), "General"));
            doctor.setContactNumber(stringValue(object, "contactNumber", "doctorContactNumber"));
            doctors.add(doctor);
        }
        return doctors;
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
            note.setDoctorId(intValue(object, "doctorID", "doctorId"));
            note.setDiagnosis(stringValue(object, "diagnosis"));
            note.setPrescription(stringValue(object, "prescription"));
            note.setNotes(stringValue(object, "notes"));
            note.setCreatedAt(LocalDateTime.now());
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
                + "\"appointmentDate\":\"" + text(appointment.getAppointmentDate()) + "\","
                + "\"appointmentTime\":\"" + text(appointment.getAppointmentTime()) + "\","
                + "\"status\":\"" + escape(defaultText(appointment.getStatus(), "BOOKED")) + "\","
                + "\"stage\":\"scheduled\","
                + "\"reason\":\"" + escape(appointment.getReason()) + "\""
                + "}";
    }

    static String consultationJson(ConsultationNote note) {
        return "{"
                + "\"appointmentId\":" + note.getAppointmentId() + ","
                + "\"doctorId\":" + note.getDoctorId() + ","
                + "\"diagnosis\":\"" + escape(note.getDiagnosis()) + "\","
                + "\"prescription\":\"" + escape(note.getPrescription()) + "\","
                + "\"notes\":\"" + escape(note.getNotes()) + "\""
                + "}";
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
}
