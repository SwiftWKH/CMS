package doctorportal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class DoctorServer implements DoctorService {
    
    private Map<Integer, Doctor> doctors;
    private Map<Integer, List<DoctorAppointment>> doctorAppointments;
    private Map<Integer, List<DoctorAppointment>> medicalHistories;
    private Map<Integer, List<DoctorAppointment>> pendingAppointments;
    private Map<Integer, Patient> patients;
    
    // REST API Base URL
    private static final String API_BASE_URL = "https://192.168.137.1:7230";
    
    public DoctorServer() throws RemoteException {
        super();
        initializeData();
    }
    
    private void initializeData() {
        doctors = new HashMap<>();
        doctorAppointments = new HashMap<>();
        medicalHistories = new HashMap<>();
        pendingAppointments = new HashMap<>();
        patients = new HashMap<>();
        
                
        // No need to pre-initialize - let the API data populate dynamically
        // Remove the for loop entirely and let doctorAppointments be populated from API
        
        // Sync data from API on startup
        try {
            syncDataFromApi();
        } catch (RemoteException e) {
            System.err.println("Failed to sync data from API on startup: " + e.getMessage());
        }
    }
    
    private void disableSslCheck() {
        try {
            TrustManager[] trustAllManager = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllManager, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //123456
    @Override
    public String fetchAppointmentsFromApi() throws RemoteException {
        disableSslCheck();
        
        try {
            String apiUrl = API_BASE_URL + "/hospital/appointment";
            System.out.println("Fetching from API: " + apiUrl);
            
            URL url = new URL(apiUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("API Response Code: " + responseCode);
            
            InputStream stream = (responseCode >= 200 && responseCode < 300) ? 
                conn.getInputStream() : conn.getErrorStream();

            if (stream == null) {
                return "[]";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            System.out.println("Fetched " + jsonResponse.length() + " bytes from API");
            return jsonResponse;

        } catch (Exception e) {
            System.err.println("Error fetching from API: " + e.getMessage());
            return "[]";
        }
    }
    @Override
    public List<PatientConsultationSummary> getConsultationHistory() throws RemoteException {
        // Group appointments with consultations by patient
        Map<Integer, List<Consultation>> map = new HashMap<>();
        for (List<DoctorAppointment> list : doctorAppointments.values()) {
            for (DoctorAppointment apt : list) {
                if (apt.getConsultations() != null && !apt.getConsultations().isEmpty()) {
                    map.computeIfAbsent(apt.getPatientId(), k -> new ArrayList<>())
                        .addAll(apt.getConsultations());
                }
            }
        }
        // Build summaries
        List<PatientConsultationSummary> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Consultation>> entry : map.entrySet()) {
            int patientId = entry.getKey();
            String patientName = getPatient(patientId) != null ? getPatient(patientId).getPatientName() : "Unknown";
            result.add(new PatientConsultationSummary(patientId, patientName, entry.getValue()));
        }
        return result;
    }
  
    
    //here is is rmi path





    @Override
    public boolean syncDataFromApi() throws RemoteException {
        try {
            String jsonData = fetchAppointmentsFromApi();
            
            if (jsonData == null || jsonData.trim().isEmpty() || jsonData.equals("[]")) {
                System.out.println("No data from API, keeping existing data");
                return false;
            }
            
            parseAppointmentData(jsonData);
            System.out.println("Data synced successfully from API");
            return true;
            
        } catch (Exception e) {
            System.err.println("Error syncing data from API: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    
    private void parseAppointmentData(String json) {
        System.out.println("Parsing appointment data...");
        
        // Clear existing data - use doctorAppointments keys instead of doctors
        for (Integer doctorId : doctorAppointments.keySet()) {
            doctorAppointments.put(doctorId, new ArrayList<>());
            pendingAppointments.put(doctorId, new ArrayList<>());
            medicalHistories.put(doctorId, new ArrayList<>());
}
        
        // Pattern to match each appointment
        Pattern aptPattern = Pattern.compile(
            "\\{\\s*\"appointmentID\"\\s*:\\s*(\\d+)\\s*," +
            "\\s*\"doctorID\"\\s*:\\s*(\\d+)\\s*," +
            "\\s*\"patientID\"\\s*:\\s*(\\d+)\\s*," +
            "\\s*\"appointmentDate\"\\s*:\\s*\"([^\"]*)\"\\s*," +
            "\\s*\"appointmentTime\"\\s*:\\s*\"([^\"]*)\"\\s*," +
            "\\s*\"status\"\\s*:\\s*([^,]*)\\s*," +
            "\\s*\"reason\"\\s*:\\s*\"([^\"]*)\"\\s*," +
            "\\s*\"stage\"\\s*:\\s*([^,]*)\\s*," +
            "\\s*\"consultation\"\\s*:\\s*\\[[^\\]]*\\]\\s*," +
            "\\s*\"doctor\"\\s*:\\s*\\{[^}]*\\}\\s*," +
            "\\s*\"patient\"\\s*:\\s*\\{([^}]*)\\}\\s*\\}"
        );
        
        Matcher aptMatcher = aptPattern.matcher(json);
        int appointmentCount = 0;

        while (aptMatcher.find()) {
            try {
                int appointmentId = Integer.parseInt(aptMatcher.group(1));
                int doctorId = Integer.parseInt(aptMatcher.group(2));
                int patientId = Integer.parseInt(aptMatcher.group(3));
                String appointmentDate = aptMatcher.group(4).replace("T00:00:00", "").trim();
                String appointmentTime = aptMatcher.group(5);
                String status = aptMatcher.group(6);
                String reason = aptMatcher.group(7);
                String stage = aptMatcher.group(8);
                String patientJson = aptMatcher.group(9);

                // Clean up status and stage
                if (status != null && !status.isEmpty()) {
                    status = status.replace("\"", "").trim();
                    if (status.equals("null")) status = null;
                }
                if (stage != null && !stage.isEmpty()) {
                    stage = stage.replace("\"", "").trim();
                    if (stage.equals("null")) stage = null;
                }

                // Extract patient name from patient JSON
                String patientName = extractPatientName(patientJson);
                String doctorName = extractDoctorName(aptMatcher.group(0));
                
                if (!doctors.containsKey(doctorId)) {
                    doctors.put(doctorId, new Doctor(doctorId, doctorName, "General", "N/A", "active"));
                }
                
                // Update patient info
                if (!patients.containsKey(patientId)) {
                    patients.put(patientId, new Patient(patientId, patientName, "N/A"));
                }

                DoctorAppointment appointment = new DoctorAppointment(
                    appointmentId, doctorId, patientId, patientName,
                    doctorName, appointmentDate, appointmentTime,
                    reason, status, stage
                );

                // Add to doctor's appointments
                List<DoctorAppointment> appointments = doctorAppointments.get(doctorId);
                if (appointments == null) {
                    appointments = new ArrayList<>();
                    doctorAppointments.put(doctorId, appointments);
                }
                appointments.add(appointment);

                // Check if completed
                boolean isCompleted = (status != null && 
                    (status.equalsIgnoreCase("completed") || 
                     status.equalsIgnoreCase("done") ||
                     status.equalsIgnoreCase("cancelled"))) ||
                    (stage != null && stage.equalsIgnoreCase("completed"));

                if (isCompleted) {
                    List<DoctorAppointment> history = medicalHistories.get(doctorId);
                    if (history == null) {
                        history = new ArrayList<>();
                        medicalHistories.put(doctorId, history);
                    }
                    history.add(appointment);
                } else {
                    List<DoctorAppointment> pending = pendingAppointments.get(doctorId);
                    if (pending == null) {
                        pending = new ArrayList<>();
                        pendingAppointments.put(doctorId, pending);
                    }
                    pending.add(appointment);
                }

                appointmentCount++;
                
            } catch (Exception e) {
                System.err.println("Error parsing appointment: " + e.getMessage());
            }
        }
        
        System.out.println("Parsed " + appointmentCount + " appointments");
        System.out.println("Doctors: " + doctors.size());
        System.out.println("Patients: " + patients.size());
    }
    
    private String extractPatientName(String patientJson) {
        Pattern namePattern = Pattern.compile("\"patientName\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = namePattern.matcher(patientJson);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown Patient";
    }
    
    private String extractDoctorName(String appointmentJson) {
        Pattern doctorNamePattern = Pattern.compile("\"doctor\"\\s*:\\s*\\{[^}]*\"doctorName\"\\s*:\\s*\"([^\"]*)\"");
        Matcher matcher = doctorNamePattern.matcher(appointmentJson);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown Doctor";
    }
    
    private boolean saveConsultationToApi(int appointmentId, int doctorId, int patientId, String diagnosis, String prescription, String notes) {
        disableSslCheck();
        
        try {
            String apiUrl = API_BASE_URL + "/hospital/consultation";
            System.out.println("Saving consultation to API: " + apiUrl);
            
            URL url = new URL(apiUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            String jsonPayload = String.format(
            "{\"appointmentId\":%d,\"doctorId\":%d,\"patientId\":%d,\"diagnosis\":\"%s\",\"prescription\":\"%s\",\"notes\":\"%s\"}",
            appointmentId,
            doctorId,
            patientId,
            escapeJson(diagnosis), 
            escapeJson(prescription), 
            escapeJson(notes)
            );
            
            System.out.println("Sending POST Payload: " + jsonPayload);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("POST Response Code: " + responseCode);

            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Consultation saved successfully to API!");
                return true;
            } else {
                System.err.println("API returned error: " + responseCode);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error saving consultation to API: " + e.getMessage());
            return false;
        }
    }
    
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
    
    @Override
    public Doctor getDoctor(int doctorId) throws RemoteException {
        return doctors.get(doctorId);
    }
    
    @Override
    public boolean updateDoctor(Doctor doctor) throws RemoteException {
        doctors.put(doctor.getDoctorId(), doctor);
        return true;
    }
    
    @Override
    public List<Doctor> getAllDoctors() throws RemoteException {
        return new ArrayList<>(doctors.values());
    }
    
    @Override
    public List<DoctorAppointment> getDoctorAppointments(int doctorId) throws RemoteException {
        List<DoctorAppointment> result = doctorAppointments.get(doctorId);
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }
    
    @Override
    public List<DoctorAppointment> getMedicalHistory(int doctorId) throws RemoteException {
        List<DoctorAppointment> result = medicalHistories.get(doctorId);
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }
    
    @Override
    public List<DoctorAppointment> getPendingAppointments(int doctorId) throws RemoteException {
        List<DoctorAppointment> result = pendingAppointments.get(doctorId);
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }
    
    @Override
    public boolean updateAppointment(DoctorAppointment appointment) throws RemoteException {
        int doctorId = appointment.getDoctorId();
        List<DoctorAppointment> appointments = doctorAppointments.get(doctorId);
        if (appointments != null) {
            for (int i = 0; i < appointments.size(); i++) {
                if (appointments.get(i).getAppointmentId() == appointment.getAppointmentId()) {
                    appointments.set(i, appointment);
                    return true;
                }
            }
        }
        return false;
    }
    
    //cp2
    @Override
    public boolean saveToMedicalHistory(DoctorAppointment appointment) throws RemoteException {
        int doctorId = appointment.getDoctorId();

        // Save to REST API
        boolean apiSaved = saveConsultationToApi(
            appointment.getAppointmentId(),
            appointment.getDoctorId(),    // <--- doctorID
            appointment.getPatientId(),   // <--- patientID
            appointment.getDiagnosis(),
            appointment.getPrescription(),
            appointment.getNotes()
        );
        
        // Update local data
        appointment.setStatus("completed");
        appointment.setStage("completed");
        appointment.setInHistory(true);
        
        // Remove from pending
        List<DoctorAppointment> pending = pendingAppointments.get(doctorId);
        if (pending != null) {
            pending.removeIf(a -> a.getAppointmentId() == appointment.getAppointmentId());
        }
        
        // Add to medical history
        List<DoctorAppointment> history = medicalHistories.get(doctorId);
        if (history == null) {
            history = new ArrayList<>();
            medicalHistories.put(doctorId, history);
        }
        history.add(0, appointment);
        
        // Update all appointments list
        List<DoctorAppointment> all = doctorAppointments.get(doctorId);
        if (all != null) {
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getAppointmentId() == appointment.getAppointmentId()) {
                    all.set(i, appointment);
                    break;
                }
            }
        }
        
        System.out.println("Appointment " + appointment.getAppointmentId() + " saved to medical history");
        System.out.println("API Save: " + (apiSaved ? "SUCCESS" : "FAILED"));
        
        return true;
    }
    
    @Override
    public boolean cancelAppointment(int appointmentId) throws RemoteException {
        for (List<DoctorAppointment> appointments : doctorAppointments.values()) {
            appointments.removeIf(a -> a.getAppointmentId() == appointmentId);
        }
        for (List<DoctorAppointment> pending : pendingAppointments.values()) {
            pending.removeIf(a -> a.getAppointmentId() == appointmentId);
        }
        return true;
    }
    
    @Override
    public List<DoctorAppointment> getAllAppointments() throws RemoteException {
        List<DoctorAppointment> all = new ArrayList<>();
        for (List<DoctorAppointment> list : doctorAppointments.values()) {
            all.addAll(list);
        }
        return all;
    }
    
    @Override
    public List<Patient> getPatients(int doctorId) throws RemoteException {
        List<Patient> doctorPatients = new ArrayList<>();
        List<DoctorAppointment> appointments = doctorAppointments.get(doctorId);
        if (appointments != null) {
            Set<Integer> patientIds = new HashSet<>();
            for (DoctorAppointment apt : appointments) {
                if (!patientIds.contains(apt.getPatientId())) {
                    patientIds.add(apt.getPatientId());
                    Patient patient = patients.get(apt.getPatientId());
                    if (patient != null) {
                        doctorPatients.add(patient);
                    }
                }
            }
        }
        return doctorPatients;
    }
    
    @Override
    public Patient getPatient(int patientId) throws RemoteException {
        return patients.get(patientId);
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("Starting Doctor RMI Server...");
            System.out.println("REST API Base URL: " + API_BASE_URL);
            
            DoctorServer server = new DoctorServer();
            DoctorService stub = (DoctorService) UnicastRemoteObject.exportObject(server, 0);
            
            Registry registry = LocateRegistry.createRegistry(1235);
            registry.rebind("DoctorService", stub);
            
            
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }





    @Override
    public String fetchAppointmentswithconsFromApi() throws RemoteException {
        disableSslCheck();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(API_BASE_URL + "/hospital/appwcon").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int code = conn.getResponseCode();
            System.out.println("API Response Code: " + code);

            InputStream stream = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();
            if (stream == null) return "[]";

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();

            return sb.toString();
        } catch (Exception e) {
            System.err.println("Error fetching from API: " + e.getMessage());
            return "[]";
        }
    }
    
    public boolean FromappwconApi() throws RemoteException {
        String json = fetchAppointmentswithconsFromApi();
        if (json == null || json.trim().isEmpty()) return false;

        String[] blocks = json.split("\"appointmentID\"\\s*:\\s*");
        for (int i = 1; i < blocks.length; i++) {
            String b = blocks[i];
            int id = Integer.parseInt(b.split(",")[0].trim());
            String patient = extract(b, "patientName");
            String doctor = extract(b, "doctorName");
            String date = extract(b, "appointmentDate").replace("T00:00:00", "");
            String time = extract(b, "appointmentTime");
            String status = extract(b, "status");
            String reason = extract(b, "reason");
            System.out.printf("%d, %s, %s, %s, %s, %s, %s%n", id, patient, doctor, date, time, status, reason);
        }
        return true;
    }

    private String extract(String json, String key) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : "";
    }
}