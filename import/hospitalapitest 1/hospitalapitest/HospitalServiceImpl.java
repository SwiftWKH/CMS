package com.example.hospitalapitest;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.cert.X509Certificate;

public class HospitalServiceImpl extends UnicastRemoteObject implements HospitalService {

    //====================================================
    // API Base URL
    //====================================================

    private static final String BASE_URL =
            "https://192.168.137.1:7230/hospital";

    private final RestTemplate restTemplate;

    //====================================================
    // Constructor
    //====================================================

    public HospitalServiceImpl() throws RemoteException {

        super();

        disableSslCheck();

        restTemplate = new RestTemplate();

    }

    //====================================================
    // Disable SSL
    //====================================================

    private void disableSslCheck() {

        try {

            TrustManager[] trustAllManager = new TrustManager[]{

                    new X509TrustManager() {

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {

                            return null;

                        }

                        @Override
                        public void checkClientTrusted(
                                X509Certificate[] certs,
                                String authType) {
                        }

                        @Override
                        public void checkServerTrusted(
                                X509Certificate[] certs,
                                String authType) {
                        }

                    }

            };

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(
                    null,
                    trustAllManager,
                    new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(
                    sslContext.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier(
                    (hostname, session) -> true);

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }

    //====================================================
    // Get All Patients
    //====================================================

    @Override
    public String getPatients() throws RemoteException {

        try {

            String url = BASE_URL + "/patient";

            String json =
                    restTemplate.getForObject(
                            url,
                            String.class);

            System.out.println();

            System.out.println("========== GET Patients ==========");

            System.out.println(json);

            return json;

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }
    //====================================================
    // Register Patient
    //====================================================

    @Override
    public String registerPatient(
            String patientName,
            String patientContactNumber)
            throws RemoteException {

        try {

            String url = BASE_URL + "/patient";

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonBody =
                    "{"
                            + "\"patientName\":\"" + patientName + "\","
                            + "\"patientContactNumber\":\"" + patientContactNumber + "\""
                            + "}";

            HttpEntity<String> request =
                    new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class);

            System.out.println();

            System.out.println("========== Register Patient ==========");

            System.out.println(jsonBody);

            return response.getBody();

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }

    //====================================================
    // Update Patient
    //====================================================

    @Override
    public String updatePatient(

            int patientID,

            String patientName,

            String patientContactNumber)

            throws RemoteException {

        try {

            String url =
                    BASE_URL + "/patient/" + patientID;

            HttpHeaders headers =
                    new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON);

            String jsonBody =
                    "{"
                            + "\"patientName\":\"" + patientName + "\","
                            + "\"patientContactNumber\":\"" + patientContactNumber + "\""
                            + "}";

            HttpEntity<String> request =
                    new HttpEntity<>(
                            jsonBody,
                            headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(

                            url,

                            HttpMethod.PUT,

                            request,

                            String.class

                    );

            System.out.println();

            System.out.println("========== Update Patient ==========");

            System.out.println(jsonBody);

            return response.getBody();

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }
    //====================================================
    // Get All Appointments
    //====================================================

    @Override
    public String getAppointments() throws RemoteException {

        try {

            String url = BASE_URL + "/appointment";

            String json =
                    restTemplate.getForObject(
                            url,
                            String.class);

            System.out.println();

            System.out.println("========== GET Appointments ==========");

            System.out.println(json);

            return json;

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }

    //====================================================
    // Create Appointment
    //====================================================

    @Override
    public String createAppointment(

            int doctorID,
            int patientID,
            String appointmentDate,
            String appointmentTime,
            String reason)

            throws RemoteException {

        try {

            String url = BASE_URL + "/appointment";

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonBody =
                    "{"
                            + "\"doctorID\":" + doctorID + ","
                            + "\"patientID\":" + patientID + ","
                            + "\"appointmentDate\":\"" + appointmentDate + "\","
                            + "\"appointmentTime\":\"" + appointmentTime + "\","
                            + "\"status\":\"active\","
                            + "\"stage\":\"scheduled\","
                            + "\"reason\":\"" + reason + "\""
                            + "}";

            HttpEntity<String> request =
                    new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            String.class);

            System.out.println();

            System.out.println("========== Create Appointment ==========");

            System.out.println(jsonBody);

            return response.getBody();

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }

    //====================================================
    // Update Appointment
    //====================================================

    @Override
    public String updateAppointment(

            int appointmentID,
            int doctorID,
            int patientID,
            String appointmentDate,
            String appointmentTime,
            String status,
            String stage,
            String reason)

            throws RemoteException {

        try {

            String url =
                    BASE_URL + "/appointment/" + appointmentID;

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonBody =
                    "{"
                            + "\"doctorID\":" + doctorID + ","
                            + "\"patientID\":" + patientID + ","
                            + "\"appointmentDate\":\"" + appointmentDate + "\","
                            + "\"appointmentTime\":\"" + appointmentTime + "\","
                            + "\"status\":\"" + status + "\","
                            + "\"stage\":\"" + stage + "\","
                            + "\"reason\":\"" + reason + "\""
                            + "}";

            HttpEntity<String> request =
                    new HttpEntity<>(jsonBody, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.PUT,
                            request,
                            String.class);

            System.out.println();

            System.out.println("========== Update Appointment ==========");

            System.out.println(jsonBody);

            return response.getBody();

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }

    //====================================================
    // Delete Appointment
    //====================================================

    @Override
    public String deleteAppointment(
            int appointmentID)
            throws RemoteException {

        try {

            String url =
                    BASE_URL + "/appointment/" + appointmentID;

            restTemplate.delete(url);

            System.out.println();

            System.out.println("========== Delete Appointment ==========");

            System.out.println("Appointment ID : " + appointmentID);

            return "Appointment cancelled successfully.";

        }

        catch (Exception e) {
            return "Appointment cancellation failed.";
            //e.printStackTrace();

          /* return e.getMessage();*/

        }


    }
    @Override
    public String getDoctors() throws RemoteException {

        try {

            String url = BASE_URL + "/doctor";

            String json = restTemplate.getForObject(
                    url,
                    String.class
            );

            System.out.println();
            System.out.println("========== GET Doctors ==========");
            System.out.println(json);

            return json;

        }

        catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();

        }

    }

}