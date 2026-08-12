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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class PatientFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final PatientController controller;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JTextField patientIdField;
    private JTextField doctorIdField;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField reasonField;
    private JTable scheduleTable;
    private JTable historyTable;
    private JLabel statusLabel;

    public PatientFrame() {
        this(new PatientController());
    }

    public PatientFrame(PatientController controller) {
        this.controller = controller;
        initComponents();
        buildPortal();
        setLocationRelativeTo(null);
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
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 680, Short.MAX_VALUE)
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
        contentPanel.add(createDashboard(), "dashboard");
        contentPanel.add(createProfilePanel(), "profile");
        contentPanel.add(createBookPanel(), "book");
        contentPanel.add(createSchedulePanel(false), "schedule");
        contentPanel.add(createSchedulePanel(true), "history");
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "dashboard");
        pack();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 680));
        sidebar.setBackground(new Color(221, 225, 229));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(sidebarTitle("PATIENT PORTAL"));
        sidebar.add(navButton("Home Dashboard", "dashboard"));
        sidebar.add(navButton("Profile Management", "profile"));
        sidebar.add(navButton("Book Appointment", "book"));
        sidebar.add(navButton("Active Schedule", "schedule"));
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
            button.addActionListener(e -> cardLayout.show(contentPanel, card));
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

    private JPanel createDashboard() {
        JPanel panel = page("Home Dashboard");
        statusLabel = new JLabel("Use the sidebar to manage profile, appointments, and history.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = page("Profile Management");
        JPanel form = new JPanel(new java.awt.GridLayout(8, 2, 8, 8));
        JTextField id = field(defaultPatientId());
        JTextField first = field("");
        JTextField last = field("");
        JTextField ic = field("");
        JTextField contact = field("");
        JTextField record = field("");
        form.add(new JLabel("Patient ID:"));
        form.add(id);
        form.add(new JLabel("First Name:"));
        form.add(first);
        form.add(new JLabel("Last Name:"));
        form.add(last);
        form.add(new JLabel("IC/Passport:"));
        form.add(ic);
        form.add(new JLabel("Contact Number:"));
        form.add(contact);
        form.add(new JLabel("Medical Record ID:"));
        form.add(record);
        JButton save = new JButton("Update Profile");
        save.addActionListener(e -> {
            Patient patient = new Patient(parseInt(id.getText()), 0, first.getText(), last.getText(),
                    ic.getText(), contact.getText(), record.getText());
            controller.updatePersonalInfo(patient);
            JOptionPane.showMessageDialog(this, "Profile update submitted.");
        });
        form.add(new JLabel());
        form.add(save);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBookPanel() {
        JPanel panel = page("Book Appointment");
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientIdField = field(defaultPatientId());
        doctorIdField = field("");
        dateField = field("2026-08-12");
        timeField = field("09:00");
        reasonField = field("");
        form.add(new JLabel("Patient ID:"));
        form.add(patientIdField);
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
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSchedulePanel(boolean history) {
        JPanel panel = page(history ? "History Logs" : "Active Schedule");
        JTable table = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        if (history) {
            historyTable = table;
        } else {
            scheduleTable = table;
        }
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadAppointments(history));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        return panel;
    }

    private JTextField field(String text) {
        return new JTextField(text, 10);
    }

    private void showAvailability() {
        List<LocalTime> slots = controller.checkDoctorAvailability(parseInt(doctorIdField.getText()),
                parseDate(dateField.getText()));
        JOptionPane.showMessageDialog(this, slots.isEmpty()
                ? "No available slots returned."
                : "Available slots: " + slots);
    }

    private void bookAppointment() {
        Appointment appointment = new Appointment(0, parseInt(patientIdField.getText()),
                parseInt(doctorIdField.getText()), parseDate(dateField.getText()),
                parseTime(timeField.getText()), "BOOKED", reasonField.getText());
        controller.bookAppointment(appointment);
        JOptionPane.showMessageDialog(this, "Appointment booking submitted.");
    }

    private void loadAppointments(boolean history) {
        int patientId = parseInt(defaultPatientId());
        List<Appointment> appointments = history
                ? controller.viewAppointmentHistory(patientId)
                : controller.viewAppointmentSchedule(patientId);
        DefaultTableModel model = (DefaultTableModel) (history ? historyTable : scheduleTable).getModel();
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[] {appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                appointment.getStatus(), appointment.getReason()});
        }
    }

    private String defaultPatientId() {
        return controller.getCurrentPatientId() > 0 ? String.valueOf(controller.getCurrentPatientId()) : "";
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception ex) {
            return LocalDate.now();
        }
    }

    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value.trim());
        } catch (Exception ex) {
            return LocalTime.of(9, 0);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
