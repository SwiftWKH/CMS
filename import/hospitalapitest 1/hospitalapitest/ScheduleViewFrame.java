package com.example.hospitalapitest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ScheduleViewFrame extends JFrame {

    //====================================================
    // Components
    //====================================================

    private JTable appointmentTable;

    private DefaultTableModel appointmentModel;

    private JButton btnRefresh;

    private JButton btnClose;

    //====================================================
    // Patient ID -> Patient Name
    //====================================================

    private Map<Integer, String> patientMap =
            new HashMap<>();

    //====================================================
    // Doctor ID -> Doctor Name
    //====================================================

    private Map<Integer, String> doctorMap =
            new HashMap<>();

    //====================================================
    // Constructor
    //====================================================

    public ScheduleViewFrame() {

        initializeUI();

    }

    //====================================================
    // Initialize UI
    //====================================================

    private void initializeUI() {

        setTitle("Daily Appointment Schedule");

        setSize(1000, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        //====================================================
        // Title
        //====================================================

        JLabel lblTitle = new JLabel(

                "Daily Appointment Schedule",

                SwingConstants.CENTER

        );

        lblTitle.setFont(

                new Font("Arial", Font.BOLD, 22)

        );

        add(lblTitle, BorderLayout.NORTH);

        //====================================================
        // Appointment Table
        //====================================================

        appointmentModel = new DefaultTableModel();

        appointmentModel.setColumnIdentifiers(new Object[]{

                "Appointment ID",

                "Patient Name",

                "Doctor Name",

                "Appointment Date",

                "Appointment Time",

                "Status",

                "Stage",

                "Reason"

        });

        appointmentTable =
                new JTable(appointmentModel);

        appointmentTable.setRowHeight(24);

        JScrollPane scrollPane =
                new JScrollPane(appointmentTable);

        add(

                scrollPane,

                BorderLayout.CENTER

        );

        //====================================================
        // Bottom Buttons
        //====================================================

        JPanel buttonPanel =
                new JPanel(new FlowLayout());

        btnRefresh =
                new JButton("Refresh");

        btnClose =
                new JButton("Close");

        buttonPanel.add(btnRefresh);

        buttonPanel.add(btnClose);

        add(

                buttonPanel,

                BorderLayout.SOUTH

        );

        //====================================================
        // Events
        //====================================================

        btnRefresh.addActionListener(

                e -> loadAppointments()

        );

        btnClose.addActionListener(

                e -> dispose()

        );

        //====================================================
        // Load Data
        //====================================================

        loadAppointments();

        setVisible(true);

    }

    //====================================================
    // Load Appointment Data
    //====================================================

    private void loadAppointments() {

        try {

            HospitalService service =
                    client.getHospitalService();

            if (service == null) {

                JOptionPane.showMessageDialog(

                        this,

                        "Unable to connect to the RMI Server.",

                        "Connection Error",

                        JOptionPane.ERROR_MESSAGE

                );

                return;

            }

            //================================================
            // Load Patients
            //================================================

            loadPatients(service);

            //================================================
            // Load Doctors
            //================================================

            loadDoctors(service);

            //================================================
            // Load Appointments
            //================================================

            String json =
                    service.getAppointments();

            loadAppointmentTable(json);

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "Error",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }

    //====================================================
    // Load Patients
    //====================================================

    private void loadPatients(
            HospitalService service)
            throws Exception {

        patientMap.clear();

        String json =
                service.getPatients();

        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode root =
                mapper.readTree(json);

        for (JsonNode patient : root) {

            int patientID =
                    patient
                            .get("patientID")
                            .asInt();

            String patientName =
                    patient
                            .get("patientName")
                            .asText();

            patientMap.put(

                    patientID,

                    patientName

            );

        }

    }

    //====================================================
    // Load Doctors
    //====================================================

    private void loadDoctors(
            HospitalService service)
            throws Exception {

        doctorMap.clear();

        String json =
                service.getDoctors();

        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode root =
                mapper.readTree(json);

        for (JsonNode doctor : root) {

            int doctorID =
                    doctor
                            .get("doctorID")
                            .asInt();

            String doctorName =
                    doctor
                            .get("doctorName")
                            .asText();

            doctorMap.put(

                    doctorID,

                    doctorName

            );

        }

    }

    //====================================================
    // Load Appointment Table
    //====================================================

    private void loadAppointmentTable(
            String json) {

        try {

            appointmentModel.setRowCount(0);

            ObjectMapper mapper =
                    new ObjectMapper();

            JsonNode root =
                    mapper.readTree(json);

            for (JsonNode appointment : root) {

                //================================================
                // Appointment ID
                //================================================

                int appointmentID =
                        appointment
                                .get("appointmentID")
                                .asInt();

                //================================================
                // Patient ID
                //================================================

                int patientID =
                        appointment
                                .get("patientID")
                                .asInt();

                //================================================
                // Doctor ID
                //================================================

                int doctorID =
                        appointment
                                .get("doctorID")
                                .asInt();

                //================================================
                // Find Patient Name
                //================================================

                String patientName =
                        patientMap.get(patientID);

                if (patientName == null) {

                    patientName =
                            "Unknown Patient";

                }

                //================================================
                // Find Doctor Name
                //================================================

                String doctorName =
                        doctorMap.get(doctorID);

                if (doctorName == null) {

                    doctorName =
                            "Unknown Doctor";

                }

                //================================================
                // Other Appointment Information
                //================================================

                String appointmentDate =
                        appointment
                                .get("appointmentDate")
                                .asText();

                String appointmentTime =
                        appointment
                                .get("appointmentTime")
                                .asText();

                String status =
                        appointment
                                .get("status")
                                .asText();

                String stage =
                        appointment
                                .get("stage")
                                .asText();

                String reason =
                        appointment
                                .get("reason")
                                .asText();

                //================================================
                // Add Row
                //================================================

                appointmentModel.addRow(

                        new Object[]{

                                appointmentID,

                                patientName,

                                doctorName,

                                appointmentDate,

                                appointmentTime,

                                status,

                                stage,

                                reason

                        }

                );

            }

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "JSON Error",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }

}