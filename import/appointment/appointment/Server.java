package ui;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

    public interface HospitalService extends Remote {
        String getResultJson() throws RemoteException;
        String getDoctorSchedulesJson() throws RemoteException;
        boolean postAppointmentToApi(int doctorId, int patientId, String doctorName, String dateTime, String time, String reason) throws RemoteException;
        boolean deleteAppointmentFromApi(int appointmentId) throws RemoteException;
    }

    public static class HospitalServiceImpl extends UnicastRemoteObject implements HospitalService {

        protected HospitalServiceImpl() throws RemoteException {
            super();
        }

        private String executeHttpGet(String apiUrl) throws RemoteException {
            disableSslCheck();
            try {
                URL url = new URL(apiUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                InputStream stream = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();

                if (stream == null) return "[]";

                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                System.out.println("GET [" + responseCode + "] Response for " + apiUrl + ": " + response.toString());
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RemoteException("Error reaching REST API (" + apiUrl + "): " + e.getMessage(), e);
            }
        }

        @Override
        public String getResultJson() throws RemoteException {
            return executeHttpGet("https://192.168.137.1:7230/hospital/appointment");
        }

        @Override
        public String getDoctorSchedulesJson() throws RemoteException {
            return executeHttpGet("https://192.168.137.1:7230/hospital/doctor");
        }

        @Override
        public boolean postAppointmentToApi(int doctorId, int patientId, String doctorName, String dateTime, String time, String reason) throws RemoteException {
            disableSslCheck();
            String apiUrl = "https://192.168.137.1:7230/hospital/appointment";

            try {
                URL url = new URL(apiUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                String jsonInputString = String.format(
                    "{\"doctorId\":%d,\"patientId\":%d,\"doctorName\":\"%s\",\"date\":\"%s\",\"time\":\"%s\",\"reason\":\"%s\"}",
                    doctorId, patientId, escapeJson(doctorName), escapeJson(dateTime), escapeJson(time), escapeJson(reason)
                );

                System.out.println("Sending POST Payload: " + jsonInputString);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                System.out.println("HTTP POST Response Code: " + responseCode);

                return (responseCode >= 200 && responseCode < 300);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RemoteException("Error sending POST request to API: " + e.getMessage(), e);
            }
        }

        @Override
        public boolean deleteAppointmentFromApi(int targetAppointmentId) throws RemoteException {
            disableSslCheck();

            // 1. Fetch JSON from GET endpoint to look up appointmentID
            System.out.println("Fetching current appointments from GET endpoint...");
            String appointmentsJson = getResultJson();

            int verifiedId = findAppointmentIdInJson(appointmentsJson, targetAppointmentId);

            if (verifiedId == -1) {
                System.out.println("Target ID " + targetAppointmentId + " not explicitly matched in JSON. Forwarding ID " + targetAppointmentId + " directly to DELETE request.");
                verifiedId = targetAppointmentId;
            } else {
                System.out.println("Confirmed appointmentID " + verifiedId + " in live REST payload.");
            }

            // 2. Perform HTTP DELETE request using the verified appointmentID
            String apiUrl = "https://192.168.137.1:7230/hospital/appointment/" + verifiedId;

            try {
                URL url = new URL(apiUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                System.out.println("Sending HTTP DELETE Request to: " + apiUrl);

                int responseCode = conn.getResponseCode();
                System.out.println("HTTP DELETE Response Code: " + responseCode);

                if (responseCode >= 200 && responseCode < 300) {
                    return true;
                } else {
                    readAndPrintErrorStream(conn);
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RemoteException("Error sending DELETE request to API: " + e.getMessage(), e);
            }
        }

        private int findAppointmentIdInJson(String json, int searchId) {
            if (json == null || json.trim().isEmpty()) return -1;

            Pattern pattern = Pattern.compile("(?i)\"(?:appointmentId|appointmentID|id)\"\\s*:\\s*(\\d+)");
            Matcher matcher = pattern.matcher(json);

            while (matcher.find()) {
                try {
                    int parsedId = Integer.parseInt(matcher.group(1));
                    if (parsedId == searchId) {
                        return parsedId;
                    }
                } catch (NumberFormatException ignored) {}
            }
            return -1;
        }

        private void readAndPrintErrorStream(HttpsURLConnection conn) {
            try (InputStream errorStream = conn.getErrorStream()) {
                if (errorStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                    StringBuilder errResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errResponse.append(line);
                    }
                    System.err.println("REST API Server Error Response: " + errResponse.toString());
                }
            } catch (IOException ignored) {}
        }

        private String escapeJson(String input) {
            if (input == null) return "";
            return input.replace("\"", "\\\"").replace("\n", " ");
        }
    }

    public static void main(String[] args) {
        int rmiPort = 1234;
        try {
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            registry.rebind("HospitalService", new HospitalServiceImpl());

            System.out.println("RMI Server Started successfully.");
            System.out.println("Listening Port : " + rmiPort);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void disableSslCheck() {
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
}