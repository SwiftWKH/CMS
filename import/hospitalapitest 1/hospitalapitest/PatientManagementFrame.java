package com.example.hospitalapitest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PatientManagementFrame extends JFrame {

    //====================================================
    // Search
    //====================================================

    private JTextField txtSearchName;

    private JButton btnSearch;


    //====================================================
    // Patient Table
    //====================================================

    private JTable patientTable;

    private DefaultTableModel patientModel;


    //====================================================
    // Appointment Table
    //====================================================

    private JTable appointmentTable;

    private DefaultTableModel appointmentModel;


    //====================================================
    // Buttons
    //====================================================

    private JButton btnUpdatePatient;

    private JButton btnModifyAppointment;

    private JButton btnCancelAppointment;

    private JButton btnRefresh;


    //====================================================
    // Current Selected Patient
    //====================================================

    private int selectedPatientID = -1;


    //====================================================
    // Doctor / Patient Name Mapping
    //====================================================

    private Map<Integer, String> doctorNameMap =
            new HashMap<>();

    private Map<Integer, String> patientNameMap =
            new HashMap<>();


    //====================================================
    // Constructor
    //====================================================

    public PatientManagementFrame() {

        initializeUI();

    }


    //====================================================
    // Initialize UI
    //====================================================

    private void initializeUI() {

        setTitle("Patient Management");

        setSize(1000, 700);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));


        //====================================================
        // Search Panel
        //====================================================

        JPanel searchPanel =
                new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Search Patient"
                )
        );

        searchPanel.add(
                new JLabel("Patient Name :")
        );

        txtSearchName =
                new JTextField(20);

        searchPanel.add(txtSearchName);

        btnSearch =
                new JButton("Search");

        searchPanel.add(btnSearch);

        add(
                searchPanel,
                BorderLayout.NORTH
        );


        //====================================================
        // Patient Table
        //====================================================

        patientModel =
                new DefaultTableModel();

        patientModel.setColumnIdentifiers(
                new Object[]{

                        "Patient ID",

                        "Patient Name",

                        "Contact Number"

                }
        );


        patientTable =
                new JTable(patientModel) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;

                    }

                };


        patientTable.setRowHeight(24);


        JScrollPane patientScroll =
                new JScrollPane(patientTable);

        patientScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Patient Information"
                )
        );


        //====================================================
        // Appointment Table
        //====================================================

        appointmentModel =
                new DefaultTableModel();


        appointmentModel.setColumnIdentifiers(
                new Object[]{

                        "Appointment ID",

                        "Doctor Name",

                        "Patient Name",

                        "Appointment Date",

                        "Appointment Time",

                        "Status",

                        "Stage",

                        "Reason"

                }
        );


        appointmentTable =
                new JTable(appointmentModel) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;

                    }

                };


        appointmentTable.setRowHeight(24);


        JScrollPane appointmentScroll =
                new JScrollPane(appointmentTable);

        appointmentScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Appointment Information"
                )
        );


        //====================================================
        // Center Panel
        //====================================================

        JSplitPane splitPane =
                new JSplitPane(

                        JSplitPane.VERTICAL_SPLIT,

                        patientScroll,

                        appointmentScroll

                );


        splitPane.setDividerLocation(250);


        add(
                splitPane,
                BorderLayout.CENTER
        );


        //====================================================
        // Button Panel
        //====================================================

        JPanel buttonPanel =
                new JPanel(new FlowLayout());


        btnUpdatePatient =
                new JButton("Update Patient");


        btnModifyAppointment =
                new JButton("Modify Appointment");


        btnCancelAppointment =
                new JButton("Cancel Appointment");


        btnRefresh =
                new JButton("Refresh");


        JButton btnClose =
                new JButton("Close");


        buttonPanel.add(
                btnUpdatePatient
        );


        buttonPanel.add(
                btnModifyAppointment
        );


        buttonPanel.add(
                btnCancelAppointment
        );


        buttonPanel.add(
                btnRefresh
        );


        buttonPanel.add(
                btnClose
        );


        add(
                buttonPanel,
                BorderLayout.SOUTH
        );


        //====================================================
        // Events
        //====================================================

        btnSearch.addActionListener(
                e -> searchPatient()
        );


        btnRefresh.addActionListener(e -> {

            searchPatient();

            if (selectedPatientID != -1) {

                loadAppointments();

            }

        });


        btnUpdatePatient.addActionListener(
                e -> updatePatient()
        );


        btnCancelAppointment.addActionListener(
                e -> deleteAppointment()
        );


        btnModifyAppointment.addActionListener(
                e -> modifyAppointment()
        );


        patientTable
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        loadAppointments();

                    }

                });


        btnClose.addActionListener(
                e -> dispose()
        );


        //====================================================
        // Load Initial Data
        //====================================================

        loadNameMaps();


        //====================================================
        // Display Window
        //====================================================

        setVisible(true);

    }


    //====================================================
    // Search Patient
    //====================================================

    private void searchPatient() {

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


            String json =
                    service.getPatients();


            loadPatientTable(json);

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
    // Load Patient Table
    //====================================================

    private void loadPatientTable(String json) {

        try {

            patientModel.setRowCount(0);

            appointmentModel.setRowCount(0);


            ObjectMapper mapper =
                    new ObjectMapper();


            JsonNode root =
                    mapper.readTree(json);


            String keyword =
                    txtSearchName
                            .getText()
                            .trim()
                            .toLowerCase();


            //================================================
            // Clear Patient Name Map
            //================================================

            patientNameMap.clear();


            for (JsonNode patient : root) {

                int patientID =
                        patient
                                .get("patientID")
                                .asInt();


                String patientName =
                        patient
                                .get("patientName")
                                .asText();


                String phone =
                        patient
                                .get("patientContactNumber")
                                .asText();


                //================================================
                // Store Patient ID -> Patient Name
                //================================================

                patientNameMap.put(

                        patientID,

                        patientName

                );


                //================================================
                // Search Filter
                //================================================

                if (!keyword.isEmpty()) {

                    if (!patientName
                            .toLowerCase()
                            .contains(keyword)) {

                        continue;

                    }

                }


                //================================================
                // Add Patient to Table
                //================================================

                patientModel.addRow(

                        new Object[]{

                                patientID,

                                patientName,

                                phone

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


    //====================================================
    // Load Doctor and Patient Name Maps
    //====================================================

    private void loadNameMaps() {

        try {

            HospitalService service =
                    client.getHospitalService();


            if (service == null) {

                return;

            }


            ObjectMapper mapper =
                    new ObjectMapper();


            //================================================
            // Load Patients
            //================================================

            String patientJson =
                    service.getPatients();


            JsonNode patients =
                    mapper.readTree(patientJson);


            patientNameMap.clear();


            for (JsonNode patient : patients) {

                int patientID =
                        patient
                                .get("patientID")
                                .asInt();


                String patientName =
                        patient
                                .get("patientName")
                                .asText();


                patientNameMap.put(

                        patientID,

                        patientName

                );

            }


            //================================================
            // Load Doctors
            //================================================

            String doctorJson =
                    service.getDoctors();


            JsonNode doctors =
                    mapper.readTree(doctorJson);


            doctorNameMap.clear();


            for (JsonNode doctor : doctors) {

                int doctorID =
                        doctor
                                .get("doctorID")
                                .asInt();


                String doctorName =
                        doctor
                                .get("doctorName")
                                .asText();


                doctorNameMap.put(

                        doctorID,

                        doctorName

                );

            }

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "Error Loading Names",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }


    //====================================================
    // Load Appointments
    //====================================================

    private void loadAppointments() {

        try {

            int row =
                    patientTable.getSelectedRow();


            if (row == -1) {

                return;

            }


            //================================================
            // Get Selected Patient ID
            //================================================

            selectedPatientID =
                    Integer.parseInt(

                            patientModel
                                    .getValueAt(row, 0)
                                    .toString()

                    );


            HospitalService service =
                    client.getHospitalService();


            if (service == null) {

                return;

            }


            //================================================
            // Reload Doctor / Patient Names
            //================================================

            loadNameMaps();


            //================================================
            // Get Appointments
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
    // Load Appointment Table
    //====================================================

    private void loadAppointmentTable(String json) {

        try {

            appointmentModel.setRowCount(0);


            ObjectMapper mapper =
                    new ObjectMapper();


            JsonNode root =
                    mapper.readTree(json);


            for (JsonNode appointment : root) {

                //================================================
                // Get Patient ID
                //================================================

                int patientID =
                        appointment
                                .get("patientID")
                                .asInt();


                //================================================
                // Only Display Selected Patient's Appointments
                //================================================

                if (patientID != selectedPatientID) {

                    continue;

                }


                //================================================
                // Get Doctor ID
                //================================================

                int doctorID =
                        appointment
                                .get("doctorID")
                                .asInt();


                //================================================
                // Convert ID to Name
                //================================================

                String doctorName =
                        doctorNameMap.getOrDefault(

                                doctorID,

                                "Unknown Doctor"

                        );


                String patientName =
                        patientNameMap.getOrDefault(

                                patientID,

                                "Unknown Patient"

                        );


                //================================================
                // Add Appointment
                //================================================

                appointmentModel.addRow(

                        new Object[]{

                                appointment
                                        .get("appointmentID")
                                        .asInt(),

                                doctorName,

                                patientName,

                                appointment
                                        .get("appointmentDate")
                                        .asText(),

                                appointment
                                        .get("appointmentTime")
                                        .asText(),

                                appointment
                                        .get("status")
                                        .asText(),

                                appointment
                                        .get("stage")
                                        .asText(),

                                appointment
                                        .get("reason")
                                        .asText()

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


    //====================================================
    // Update Patient
    //====================================================

    private void updatePatient() {

        int row =
                patientTable.getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please select a patient."

            );

            return;

        }


        try {

            int patientID =
                    Integer.parseInt(

                            patientModel
                                    .getValueAt(row, 0)
                                    .toString()

                    );


            String patientName =
                    JOptionPane.showInputDialog(

                            this,

                            "Patient Name",

                            patientModel
                                    .getValueAt(row, 1)

                    );


            String phone =
                    JOptionPane.showInputDialog(

                            this,

                            "Contact Number",

                            patientModel
                                    .getValueAt(row, 2)

                    );


            if (patientName == null ||
                    phone == null) {

                return;

            }


            patientName =
                    patientName.trim();


            phone =
                    phone.trim();


            if (patientName.isEmpty() ||
                    phone.isEmpty()) {

                JOptionPane.showMessageDialog(

                        this,

                        "Patient information cannot be empty.",

                        "Input Error",

                        JOptionPane.WARNING_MESSAGE

                );

                return;

            }


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


            String result =
                    service.updatePatient(

                            patientID,

                            patientName,

                            phone

                    );


            JOptionPane.showMessageDialog(

                    this,

                    result

            );


            searchPatient();

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage()

            );

        }

    }


    //====================================================
    // Delete Appointment
    //====================================================

    private void deleteAppointment() {

        int row =
                appointmentTable.getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please select an appointment."

            );

            return;

        }


        try {

            int appointmentID =
                    Integer.parseInt(

                            appointmentModel
                                    .getValueAt(row, 0)
                                    .toString()

                    );


            int option =
                    JOptionPane.showConfirmDialog(

                            this,

                            "Cancel this appointment?",

                            "Confirm",

                            JOptionPane.YES_NO_OPTION

                    );


            if (option != JOptionPane.YES_OPTION) {

                return;

            }


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


            String result =
                    service.deleteAppointment(

                            appointmentID

                    );


            JOptionPane.showMessageDialog(

                    this,

                    result

            );


            loadAppointments();

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage()

            );

        }

    }


    //====================================================
    // Modify Appointment
    //====================================================

    private void modifyAppointment() {

        int row =
                appointmentTable.getSelectedRow();


        if (row == -1) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please select an appointment."

            );

            return;

        }


        try {

            //================================================
            // Appointment ID
            //================================================

            int appointmentID =
                    Integer.parseInt(

                            appointmentModel
                                    .getValueAt(row, 0)
                                    .toString()

                    );


            //================================================
            // Get Doctor Name from Table
            //================================================

            String doctorName =
                    appointmentModel
                            .getValueAt(row, 1)
                            .toString();


            //================================================
            // Get Patient Name from Table
            //================================================

            String patientName =
                    appointmentModel
                            .getValueAt(row, 2)
                            .toString();


            //================================================
            // Convert Doctor Name -> Doctor ID
            //================================================

            int doctorID = -1;


            for (Map.Entry<Integer, String> entry
                    : doctorNameMap.entrySet()) {

                if (entry
                        .getValue()
                        .equals(doctorName)) {

                    doctorID =
                            entry.getKey();

                    break;

                }

            }


            //================================================
            // Patient ID
            //================================================

            int patientID =
                    selectedPatientID;


            //================================================
            // Validate Doctor ID
            //================================================

            if (doctorID == -1) {

                JOptionPane.showMessageDialog(

                        this,

                        "Unable to find Doctor ID.",

                        "Error",

                        JOptionPane.ERROR_MESSAGE

                );

                return;

            }


            //================================================
            // Appointment Date
            //================================================

            String appointmentDate =
                    JOptionPane.showInputDialog(

                            this,

                            "Appointment Date",

                            appointmentModel
                                    .getValueAt(row, 3)

                    );


            //================================================
            // Appointment Time
            //================================================

            String appointmentTime =
                    JOptionPane.showInputDialog(

                            this,

                            "Appointment Time",

                            appointmentModel
                                    .getValueAt(row, 4)

                    );


            //================================================
            // Status
            //================================================

            String status =
                    JOptionPane.showInputDialog(

                            this,

                            "Status",

                            appointmentModel
                                    .getValueAt(row, 5)

                    );


            //================================================
            // Stage
            //================================================

            String stage =
                    JOptionPane.showInputDialog(

                            this,

                            "Stage",

                            appointmentModel
                                    .getValueAt(row, 6)

                    );


            //================================================
            // Reason
            //================================================

            String reason =
                    JOptionPane.showInputDialog(

                            this,

                            "Reason",

                            appointmentModel
                                    .getValueAt(row, 7)

                    );


            //================================================
            // Cancel Check
            //================================================

            if (appointmentDate == null ||
                    appointmentTime == null ||
                    status == null ||
                    stage == null ||
                    reason == null) {

                return;

            }


            //================================================
            // Trim Input
            //================================================

            appointmentDate =
                    appointmentDate.trim();


            appointmentTime =
                    appointmentTime.trim();


            status =
                    status.trim();


            stage =
                    stage.trim();


            reason =
                    reason.trim();


            //================================================
            // Validate Input
            //================================================

            if (appointmentDate.isEmpty()
                    || appointmentTime.isEmpty()
                    || status.isEmpty()
                    || stage.isEmpty()
                    || reason.isEmpty()) {

                JOptionPane.showMessageDialog(

                        this,

                        "Please complete all appointment information.",

                        "Input Error",

                        JOptionPane.WARNING_MESSAGE

                );

                return;

            }


            //================================================
            // Connect RMI Service
            //================================================

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
            // Update Appointment
            //================================================

            String result =
                    service.updateAppointment(

                            appointmentID,

                            doctorID,

                            patientID,

                            appointmentDate,

                            appointmentTime,

                            status,

                            stage,

                            reason

                    );


            JOptionPane.showMessageDialog(

                    this,

                    result

            );


            //================================================
            // Reload Appointments
            //================================================

            loadAppointments();

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

}