package com.example.hospitalapitest;

import javax.swing.*;
import java.awt.*;

public class RegisterPatientFrame extends JFrame {

    //====================================================
    // Input Fields
    //====================================================

    private JTextField txtPatientName;

    private JTextField txtPhoneNumber;

   /* private JTextField txtICPassport;

    private JTextField txtMedicalRecordID;*/

    //====================================================
    // Buttons
    //====================================================

    private JButton btnRegister;

    private JButton btnClear;

    private JButton btnClose;

    //====================================================
    // Constructor
    //====================================================

    public RegisterPatientFrame() {

        initializeUI();

    }

    //====================================================
    // Initialize UI
    //====================================================

    private void initializeUI() {

        setTitle("Patient Registration");

        setSize(620, 420);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10,10));

        //====================================================
        // Title
        //====================================================

        JLabel lblTitle = new JLabel(

                "New Patient Registration",

                SwingConstants.CENTER

        );

        lblTitle.setFont(

                new Font("Arial", Font.BOLD, 22)

        );

        add(lblTitle, BorderLayout.NORTH);
        //====================================================
        // Registration Form
        //====================================================

        JPanel formPanel = new JPanel();

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(20,30,20,30)
        );

        formPanel.setLayout(new GridLayout(2,2,15,15));

        //----------------------------------------------------
        // Patient Name
        //----------------------------------------------------

        formPanel.add(new JLabel("Patient Name :"));

        txtPatientName = new JTextField();

        formPanel.add(txtPatientName);

        //----------------------------------------------------
        // Phone Number
        //----------------------------------------------------

        formPanel.add(new JLabel("Phone Number :"));

        txtPhoneNumber = new JTextField();

        formPanel.add(txtPhoneNumber);

        //----------------------------------------------------
        // IC / Passport
        //----------------------------------------------------

       /* formPanel.add(new JLabel("IC / Passport :"));

        txtICPassport = new JTextField();

        formPanel.add(txtICPassport);*/

        //----------------------------------------------------
        // Medical Record ID
        //----------------------------------------------------

        /*formPanel.add(new JLabel("Medical Record ID :"));

        txtMedicalRecordID = new JTextField();

        formPanel.add(txtMedicalRecordID);*/

        add(formPanel, BorderLayout.CENTER);
        //====================================================
        // Button Panel
        //====================================================

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER,15,10)
        );

        btnRegister = new JButton("Register");

        btnClear = new JButton("Clear");

        btnClose = new JButton("Close");

        btnRegister.setPreferredSize(
                new Dimension(130,40)
        );

        btnClear.setPreferredSize(
                new Dimension(100,40)
        );

        btnClose.setPreferredSize(
                new Dimension(100,40)
        );

        buttonPanel.add(btnRegister);

        buttonPanel.add(btnClear);

        buttonPanel.add(btnClose);

        add(buttonPanel, BorderLayout.SOUTH);

        //====================================================
        // Events
        //====================================================

        btnRegister.addActionListener(e -> registerPatient());

        btnClear.addActionListener(e -> clearFields());

        btnClose.addActionListener(e -> dispose());

        setVisible(true);

    }
    //====================================================
    // Register Patient
    //====================================================

    private void registerPatient() {

        String patientName = txtPatientName.getText().trim();

        String phoneNumber = txtPhoneNumber.getText().trim();

        /*String icPassport = txtICPassport.getText().trim();

        String medicalRecordID = txtMedicalRecordID.getText().trim();*/

        //----------------------------------------------------
        // Validation
        //----------------------------------------------------

        if (patientName.isEmpty()
                || phoneNumber.isEmpty()
                /*|| icPassport.isEmpty()
                || medicalRecordID.isEmpty()*/) {

            JOptionPane.showMessageDialog(

                    this,

                    "Please complete all required information.",

                    "Input Error",

                    JOptionPane.WARNING_MESSAGE

            );

            return;

        }

        try {

            //------------------------------------------------
            // RMI Service
            //------------------------------------------------

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

            //------------------------------------------------
            // Register Patient
            //------------------------------------------------

            String result = service.registerPatient(

                    patientName,

                   /* icPassport,*/

                    phoneNumber

                    /*medicalRecordID*/

            );

            JOptionPane.showMessageDialog(

                    this,

                    result,

                    "Registration Result",

                    JOptionPane.INFORMATION_MESSAGE

            );

            clearFields();

        }

        catch (Exception e) {

            JOptionPane.showMessageDialog(

                    this,

                    e.getMessage(),

                    "Registration Error",

                    JOptionPane.ERROR_MESSAGE

            );

        }

    }

    //====================================================
    // Clear Input Fields
    //====================================================

    private void clearFields() {

        txtPatientName.setText("");

        txtPhoneNumber.setText("");

        /*txtICPassport.setText("");

        txtMedicalRecordID.setText("");*/

        txtPatientName.requestFocus();

    }

}