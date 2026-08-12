package com.example.hospitalapitest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CreateAppointmentFrame extends JFrame {

    //====================================================
    // Current Patient
    //====================================================

    private int patientID;

    //====================================================
    // Doctor Name -> Doctor ID
    //====================================================

    private Map<String, Integer> doctorMap =
            new HashMap<>();

    //====================================================
    // Patient Name -> Patient ID
    //====================================================

    private Map<String, Integer> patientMap =
            new HashMap<>();

    //====================================================
    // Components
    //====================================================

    private JComboBox<String> cmbPatient;

    private JComboBox<String> cmbDoctor;

    private JTextField txtDate;

    private JComboBox<String> cmbTimeSlot;

    private JTextField txtReason;

    //====================================================
    // Constructor
    //====================================================

    public CreateAppointmentFrame() {

        this(-1);

    }

    public CreateAppointmentFrame(int patientID) {

        this.patientID = patientID;

        initializeUI();

    }

    //====================================================
    // Initialize UI
    //====================================================

    private void initializeUI() {

        setTitle("Create Appointment");

        setSize(550, 420);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        //====================================================
        // Title
        //====================================================

        JLabel lblTitle = new JLabel(

                "New Appointment Reservation",

                SwingConstants.CENTER

        );

        lblTitle.setFont(

                new Font("Arial", Font.BOLD, 22)

        );

        add(lblTitle, BorderLayout.NORTH);

        //====================================================
        // Form Panel
        //====================================================

        JPanel formPanel = new JPanel();

        formPanel.setBorder(

                BorderFactory.createEmptyBorder(

                        20,
                        20,
                        20,
                        20

                )

        );

        if (patientID == -1) {

            formPanel.setLayout(

                    new GridLayout(5, 2, 10, 10)

            );

        } else {

            formPanel.setLayout(

                    new GridLayout(4, 2, 10, 10)

            );

        }

        //====================================================
        // Patient
        //====================================================

        cmbPatient = new JComboBox<>();

        if (patientID == -1) {

            formPanel.add(

                    new JLabel("Patient :")

            );

            formPanel.add(

                    cmbPatient

            );

        }

        //====================================================
        // Doctor
        //====================================================

        cmbDoctor = new JComboBox<>();

        formPanel.add(

                new JLabel("Doctor :")

        );

        formPanel.add(

                cmbDoctor

        );

        //====================================================
        // Appointment Date
        //====================================================

        txtDate = new JTextField();

        txtDate.setToolTipText("yyyy-MM-dd");

        formPanel.add(

                new JLabel("Appointment Date :")

        );

        formPanel.add(

                txtDate

        );

        //====================================================
        // Appointment Time
        //====================================================

        cmbTimeSlot = new JComboBox<>();

        formPanel.add(

                new JLabel("Appointment Time :")

        );

        formPanel.add(

                cmbTimeSlot

        );

        //====================================================
        // Reason
        //====================================================

        txtReason = new JTextField();

        formPanel.add(

                new JLabel("Reason :")

        );

        formPanel.add(

                txtReason

        );

        add(

                formPanel,

                BorderLayout.CENTER

        );

        //====================================================
        // Buttons
        //====================================================

        JPanel buttonPanel = new JPanel();

        JButton btnSubmit =
                new JButton("Submit Reservation");

        JButton btnReset =
                new JButton("Reset");

        JButton btnClose =
                new JButton("Close");

        buttonPanel.add(btnSubmit);

        buttonPanel.add(btnReset);

        buttonPanel.add(btnClose);

        add(

                buttonPanel,

                BorderLayout.SOUTH

        );

        //====================================================
        // Events
        //====================================================

        btnSubmit.addActionListener(

                e -> submitAppointment()

        );

        btnReset.addActionListener(

                e -> clearForm()

        );

        btnClose.addActionListener(

                e -> dispose()

        );

        //====================================================
        // Load Data
        //====================================================

        loadData();

        setVisible(true);

    }

    //====================================================
    // Load Data
    //====================================================

    private void loadData() {

        loadDoctors();

        loadTimeSlots();

        if (patientID == -1) {

            loadPatients();

        }

    }

    //====================================================
    // Load Doctors
    //====================================================

    private void loadDoctors() {

        try {

            HospitalService service =
                    client.getHospitalService();

            if (service == null) {

                return;

            }

            cmbDoctor.removeAllItems();

            doctorMap.clear();

            //================================================
            // Empty Default Option
            //================================================

            cmbDoctor.addItem("");

            String json =
                    service.getDoctors();

            ObjectMapper mapper =
                    new ObjectMapper();

            JsonNode root =
                    mapper.readTree(json);

            for (JsonNode doctor : root) {

                int doctorID =
                        doctor.get("doctorID").asInt();

                String doctorName =
                        doctor.get("doctorName").asText();

                doctorMap.put(

                        doctorName,

                        doctorID

                );

                cmbDoctor.addItem(

                        doctorName

                );

            }

            //================================================
            // Keep Default Selection Empty
            //================================================

            cmbDoctor.setSelectedIndex(0);

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "Doctor Loading Error",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }

    //====================================================
    // Load Patients
    //====================================================

    private void loadPatients() {

        try {

            HospitalService service =
                    client.getHospitalService();

            if (service == null) {

                return;

            }

            cmbPatient.removeAllItems();

            patientMap.clear();

            //================================================
            // Empty Default Option
            //================================================

            cmbPatient.addItem("");

            String json =
                    service.getPatients();

            ObjectMapper mapper =
                    new ObjectMapper();

            JsonNode root =
                    mapper.readTree(json);

            for (JsonNode patient : root) {

                int id =
                        patient.get("patientID").asInt();

                String name =
                        patient.get("patientName").asText();

                patientMap.put(

                        name,

                        id

                );

                cmbPatient.addItem(

                        name

                );

            }

            //================================================
            // Keep Default Selection Empty
            //================================================

            cmbPatient.setSelectedIndex(0);

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "Patient Loading Error",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }

    //====================================================
    // Load Time Slots
    //====================================================

    private void loadTimeSlots() {

        cmbTimeSlot.removeAllItems();

        //====================================================
        // Empty Default Option
        //====================================================

        cmbTimeSlot.addItem("");

        //====================================================
        // Available Time Slots
        //====================================================

        cmbTimeSlot.addItem("09:30:00");

        cmbTimeSlot.addItem("11:30:00");

        cmbTimeSlot.addItem("14:00:00");

        cmbTimeSlot.addItem("16:00:00");

        //====================================================
        // Keep Default Selection Empty
        //====================================================

        cmbTimeSlot.setSelectedIndex(0);

    }

    //====================================================
    // Submit Appointment
    //====================================================

    private void submitAppointment() {

        //====================================================
        // Get Doctor
        //====================================================

        String doctor =
                (String) cmbDoctor.getSelectedItem();

        //====================================================
        // Get Date
        //====================================================

        String date =
                txtDate.getText().trim();

        //====================================================
        // Get Time
        //====================================================

        String time =
                (String) cmbTimeSlot.getSelectedItem();

        //====================================================
        // Get Reason
        //====================================================

        String reason =
                txtReason.getText().trim();

        //====================================================
        // Validate Doctor
        //====================================================

        if (doctor == null ||
                doctor.trim().isEmpty()) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please select a doctor.",

                    "Warning",

                    JOptionPane.WARNING_MESSAGE

            );

            return;

        }

        //====================================================
        // Validate Date
        //====================================================

        if (date.isEmpty()) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please enter the appointment date.",

                    "Warning",

                    JOptionPane.WARNING_MESSAGE

            );

            return;

        }

        //====================================================
        // Validate Time
        //====================================================

        if (time == null ||
                time.trim().isEmpty()) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please select an appointment time.",

                    "Warning",

                    JOptionPane.WARNING_MESSAGE

            );

            return;

        }

        //====================================================
        // Validate Reason
        //====================================================

        if (reason.isEmpty()) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please enter the reason for the appointment.",

                    "Warning",

                    JOptionPane.WARNING_MESSAGE

            );

            return;

        }

        //====================================================
        // Get Doctor ID
        //====================================================

        Integer doctorID =
                doctorMap.get(doctor);

        if (doctorID == null) {

            JOptionPane.showMessageDialog(

                    this,

                    "Invalid doctor.",

                    "Error",

                    JOptionPane.ERROR_MESSAGE

            );

            return;

        }

        //====================================================
        // Get Patient ID
        //====================================================

        int currentPatientID =
                patientID;

        if (patientID == -1) {

            String patientName =
                    (String) cmbPatient.getSelectedItem();

            //================================================
            // Validate Patient Selection
            //================================================

            if (patientName == null ||
                    patientName.trim().isEmpty()) {

                JOptionPane.showMessageDialog(

                        this,

                        "Please select a patient.",

                        "Warning",

                        JOptionPane.WARNING_MESSAGE

                );

                return;

            }

            Integer id =
                    patientMap.get(patientName);

            if (id == null) {

                JOptionPane.showMessageDialog(

                        this,

                        "Invalid patient.",

                        "Warning",

                        JOptionPane.WARNING_MESSAGE

                );

                return;

            }

            currentPatientID = id;

        }

        //====================================================
        // RMI Service
        //====================================================

        try {

            HospitalService service =
                    client.getHospitalService();

            if (service == null) {

                JOptionPane.showMessageDialog(

                        this,

                        "Unable to connect to RMI Server.",

                        "Connection Error",

                        JOptionPane.ERROR_MESSAGE

                );

                return;

            }

            //================================================
            // Create Appointment
            //================================================

            String result =
                    service.createAppointment(

                            doctorID,

                            currentPatientID,

                            date,

                            time,

                            reason

                    );

            //================================================
            // Show Result
            //================================================

            JOptionPane.showMessageDialog(

                    this,

                    result,

                    "Information",

                    JOptionPane.INFORMATION_MESSAGE

            );

            //================================================
            // Clear Form After Successful Submission
            //================================================

            clearForm();

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "RMI Error",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }

    //====================================================
    // Clear Form
    //====================================================

    private void clearForm() {

        //====================================================
        // Clear Patient Selection
        //====================================================

        if (cmbPatient != null &&
                cmbPatient.getItemCount() > 0) {

            cmbPatient.setSelectedIndex(0);

        }

        //====================================================
        // Clear Doctor Selection
        //====================================================

        if (cmbDoctor != null &&
                cmbDoctor.getItemCount() > 0) {

            cmbDoctor.setSelectedIndex(0);

        }

        //====================================================
        // Clear Time Selection
        //====================================================

        if (cmbTimeSlot != null &&
                cmbTimeSlot.getItemCount() > 0) {

            cmbTimeSlot.setSelectedIndex(0);

        }

        //====================================================
        // Clear Date
        //====================================================

        if (txtDate != null) {

            txtDate.setText("");

        }

        //====================================================
        // Clear Reason
        //====================================================

        if (txtReason != null) {

            txtReason.setText("");

        }

    }

}