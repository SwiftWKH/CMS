package com.example.hospitalapitest;

import javax.swing.*;
import java.awt.*;

public class recreationmain extends JFrame {

    public recreationmain() {

        initializeUI();

    }

    private void initializeUI() {

        setTitle("Hospital Appointment System");

        setSize(700, 500);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // ================= Title =================

        JLabel lblTitle = new JLabel(
                "Hospital Appointment System",
                SwingConstants.CENTER
        );

        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));

        add(lblTitle, BorderLayout.NORTH);

        // ================= Center Panel =================

        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new GridLayout(5, 1, 15, 15));

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(30, 180, 30, 180)
        );

        JButton btnRegister =
                new JButton("Patient Registration");

        JButton btnManagement =
                new JButton("Patient Management");

        JButton btnCreate =
                new JButton("Create Appointment");

        JButton btnSchedule =
                new JButton("View Daily Appointment Schedule");

        JButton btnExit =
                new JButton("Exit System");

        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        JButton[] buttons = {

                btnRegister,

                btnManagement,

                btnCreate,

                btnSchedule,

                btnExit

        };

        for (JButton button : buttons) {

            button.setFont(buttonFont);

            button.setPreferredSize(new Dimension(260, 45));

            centerPanel.add(button);

        }

        add(centerPanel, BorderLayout.CENTER);

        // ================= Events =================

        btnRegister.addActionListener(e ->
                new RegisterPatientFrame());

        btnManagement.addActionListener(e ->
                new PatientManagementFrame());

        btnCreate.addActionListener(e ->
                new CreateAppointmentFrame());

        btnSchedule.addActionListener(e ->
                new ScheduleViewFrame());

        btnExit.addActionListener(e -> exitSystem());

        setVisible(true);

    }

    private void exitSystem() {

        int option = JOptionPane.showConfirmDialog(

                this,

                "Do you want to exit the system?",

                "Exit Confirmation",

                JOptionPane.YES_NO_OPTION,

                JOptionPane.QUESTION_MESSAGE

        );

        if (option == JOptionPane.YES_OPTION) {

            System.exit(0);

        }

    }

}