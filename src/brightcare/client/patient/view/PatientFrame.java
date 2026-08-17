package brightcare.client.patient.view;

import brightcare.client.patient.controller.PatientController;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PatientFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final PatientController controller;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField icField;
    private JTextField contactField;
    private JTextField recordField;
    private JTextField doctorIdField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField reasonField;
    private JTable scheduleTable;
    private JTable historyTable;
    private JLabel appointmentStatusLabel;

    public PatientFrame() {
        this(new PatientController());
    }

    public PatientFrame(PatientController controller) {
        this.controller = controller;
        initComponents();
        buildPortal();
        setLocationRelativeTo(null);
        loadProfile();
        loadActiveSchedule();
        loadHistory();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightCare Patient");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1120, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public PatientController getController() {
        return controller;
    }

    private void buildPortal() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createProfilePanel(), "profile");
        contentPanel.add(createAppointmentsPanel(), "appointments");
        contentPanel.add(createHistoryPanel(), "history");
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "appointments");
        pack();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 720));
        sidebar.setBackground(new Color(221, 225, 229));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(sidebarTitle("PATIENT PORTAL"));
        sidebar.add(navButton("Profile Management", "profile"));
        sidebar.add(navButton("Appointments", "appointments"));
        sidebar.add(navButton("History Logs", "history"));
        JButton logout = navButton("Logout", null);
        logout.addActionListener(e -> controller.logout(this));
        sidebar.add(logout);
        return sidebar;
    }

    private JLabel sidebarTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setBorder(BorderFactory.createEmptyBorder(24, 18, 18, 18));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton navButton(String text, String card) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setBackground(new Color(185, 196, 208));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        if (card != null) {
            button.addActionListener(e -> {
                cardLayout.show(contentPanel, card);
                if ("profile".equals(card)) {
                    loadProfile();
                } else if ("appointments".equals(card)) {
                    loadActiveSchedule();
                } else if ("history".equals(card)) {
                    loadHistory();
                }
            });
        }
        return button;
    }

    private JPanel page(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = page("Profile Management");
        JPanel form = new JPanel(new java.awt.GridLayout(6, 2, 8, 8));
        form.setBackground(Color.WHITE);
        firstNameField = field("");
        lastNameField = field("");
        icField = field("");
        contactField = field("");
        recordField = field("");
        recordField.setEditable(false);
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);
        form.add(new JLabel("IC/Passport:"));
        form.add(icField);
        form.add(new JLabel("Contact Number:"));
        form.add(contactField);
        form.add(new JLabel("Medical Record ID:"));
        form.add(recordField);
        JButton save = new JButton("Update Profile");
        save.addActionListener(e -> updateProfile());
        form.add(new JLabel());
        form.add(save);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = page("Appointments");
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBackground(Color.WHITE);
        body.add(createBookingForm(), BorderLayout.NORTH);
        scheduleTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillBookingFromSelectedSchedule();
            }
        });
        body.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        JButton refresh = new JButton("Refresh Schedule");
        refresh.addActionListener(e -> loadActiveSchedule());
        JButton cancel = new JButton("Cancel Selected");
        cancel.addActionListener(e -> cancelSelectedAppointment());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(Color.WHITE);
        buttons.add(cancel);
        buttons.add(refresh);
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        appointmentStatusLabel = new JLabel("Ready.");
        appointmentStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footer.add(appointmentStatusLabel, BorderLayout.WEST);
        footer.add(buttons, BorderLayout.EAST);
        body.add(footer, BorderLayout.SOUTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookingForm() {
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBackground(Color.WHITE);
        doctorIdField = field("");
        dateField = field(LocalDate.now().toString());
        timeField = field("09:00");
        reasonField = new JTextField("", 20);
        form.add(new JLabel("Doctor ID:"));
        form.add(doctorIdField);
        form.add(new JLabel("Date:"));
        form.add(dateField);
        form.add(new JLabel("Time:"));
        form.add(timeField);
        form.add(new JLabel("Reason:"));
        form.add(reasonField);
        JButton check = new JButton("Check Availability");
        check.addActionListener(e -> showAvailability());
        JButton book = new JButton("Book");
        book.addActionListener(e -> bookAppointment());
        form.add(check);
        form.add(book);
        return form;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = page("History Logs");
        historyTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        JButton refresh = new JButton("Refresh History");
        refresh.addActionListener(e -> loadHistory());
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JTextField field(String text) {
        return new JTextField(text, 10);
    }

    private void loadProfile() {
        if (firstNameField == null) {
            return;
        }
        Patient profile = controller.viewPatientProfile();
        if (profile == null) {
            return;
        }
        firstNameField.setText(text(profile.getFirstName()));
        lastNameField.setText(text(profile.getLastName()));
        icField.setText(text(profile.getIcPassportNo()));
        contactField.setText(text(profile.getContactNumber()));
        recordField.setText(text(profile.getMedicalRecordId()));
    }

    private void updateProfile() {
        try {
            requireText(firstNameField.getText(), "First name");
            requireText(lastNameField.getText(), "Last name");
            requireText(icField.getText(), "IC/Passport");
            requireText(contactField.getText(), "Contact number");
            Patient patient = new Patient(controller.getCurrentPatientId(), 0, firstNameField.getText(),
                    lastNameField.getText(), icField.getText(), contactField.getText(), recordField.getText());
            Patient updated = controller.updatePersonalInfo(patient);
            JOptionPane.showMessageDialog(this, updated == null
                    ? "Profile update failed."
                    : "Profile update submitted.");
            loadProfile();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showAvailability() {
        try {
            int doctorId = parseRequiredInt(doctorIdField.getText(), "Doctor ID");
            LocalDate date = parseRequiredDate(dateField.getText(), "Date");
            List<LocalTime> slots = controller.checkDoctorAvailability(doctorId, date);
            String message = slots.isEmpty() ? "No available slots returned." : "Available slots: " + slots;
            appointmentStatusLabel.setText(message);
            JOptionPane.showMessageDialog(this, message);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void bookAppointment() {
        try {
            int patientId = controller.getCurrentPatientId();
            parseRequiredInt(String.valueOf(patientId), "Patient ID");
            int doctorId = parseRequiredInt(doctorIdField.getText(), "Doctor ID");
            LocalDate date = parseRequiredDate(dateField.getText(), "Date");
            LocalTime time = parseRequiredTime(timeField.getText(), "Time");
            requireText(reasonField.getText(), "Reason");
            Appointment appointment = new Appointment(0, patientId, doctorId, date, time,
                    "BOOKED", reasonField.getText());
            Appointment booked = controller.bookAppointment(appointment);
            appointmentStatusLabel.setText(booked == null ? "Appointment booking failed." : "Appointment booking submitted.");
            JOptionPane.showMessageDialog(this, booked == null
                    ? "Appointment booking failed."
                    : "Appointment booking submitted.");
            loadActiveSchedule();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void cancelSelectedAppointment() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an active appointment to cancel.");
            return;
        }
        int modelRow = scheduleTable.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        int appointmentId = parseRequiredInt(text(model.getValueAt(modelRow, 0)), "Appointment ID");
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Cancel appointment #" + appointmentId + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION
        );
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        Appointment cancelled = controller.cancelAppointment(appointmentId);
        appointmentStatusLabel.setText(cancelled == null
                ? "Appointment cancellation failed."
                : "Appointment cancellation submitted.");
        JOptionPane.showMessageDialog(this, cancelled == null
                ? "Appointment cancellation failed."
                : "Appointment cancellation submitted.");
        loadActiveSchedule();
        loadHistory();
    }

    private void loadActiveSchedule() {
        loadAppointments(false);
    }

    private void loadHistory() {
        loadAppointments(true);
    }

    private void loadAppointments(boolean history) {
        int patientId;
        try {
            patientId = parseRequiredInt(String.valueOf(controller.getCurrentPatientId()), "Patient ID");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        List<Appointment> appointments = history
                ? controller.viewAppointmentHistory(patientId)
                : controller.viewAppointmentSchedule(patientId);
        JTable table = history ? historyTable : scheduleTable;
        if (table == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            boolean isHistory = "COMPLETED".equalsIgnoreCase(appointment.getStatus())
                    || "CANCELLED".equalsIgnoreCase(appointment.getStatus());
            if (history != isHistory) {
                continue;
            }
            model.addRow(new Object[] {appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                appointment.getStatus(), appointment.getReason()});
        }
    }

    private void fillBookingFromSelectedSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int modelRow = scheduleTable.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        doctorIdField.setText(text(model.getValueAt(modelRow, 2)));
        dateField.setText(text(model.getValueAt(modelRow, 3)));
        timeField.setText(shortTime(text(model.getValueAt(modelRow, 4))));
        reasonField.setText(text(model.getValueAt(modelRow, 6)));
    }

    private void requireText(String value, String label) {
        if (value == null || value.trim().length() == 0) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }

    private int parseRequiredInt(String value, String label) {
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed <= 0) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " must be a positive number.");
        }
    }

    private LocalDate parseRequiredDate(String value, String label) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " must use yyyy-MM-dd.");
        }
    }

    private LocalTime parseRequiredTime(String value, String label) {
        try {
            return LocalTime.parse(value.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException(label + " must use HH:mm.");
        }
    }

    private String shortTime(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.length() >= 5 ? trimmed.substring(0, 5) : trimmed;
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
