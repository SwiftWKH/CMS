package brightcare.client.doctor.view;

import brightcare.client.doctor.controller.DoctorController;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class DoctorFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final DoctorController controller;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JTextField doctorIdField;
    private JTextField dateField;
    private JTable appointmentTable;
    private JTextField patientIdField;
    private JTable historyTable;
    private JTextField appointmentIdField;
    private JTextArea diagnosisArea;
    private JTextArea prescriptionArea;
    private JTextArea notesArea;

    public DoctorFrame() {
        this(new DoctorController());
    }

    public DoctorFrame(DoctorController controller) {
        this.controller = controller;
        initComponents();
        buildPortal();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightCare Doctor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1100, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public DoctorController getController() {
        return controller;
    }

    private void buildPortal() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createAppointmentPage(), "appointments");
        contentPanel.add(createHistoryPage(), "history");
        contentPanel.add(createConsultationPage(), "consultation");
        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "appointments");
        pack();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 700));
        sidebar.setBackground(new Color(221, 225, 229));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("DOCTOR PORTAL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(24, 18, 18, 18));
        sidebar.add(title);
        sidebar.add(navButton("Appointments", "appointments"));
        sidebar.add(navButton("Medical History", "history"));
        sidebar.add(navButton("Consultation Note", "consultation"));
        JButton logout = navButton("Logout", null);
        logout.addActionListener(e -> controller.logout(this));
        sidebar.add(logout);
        return sidebar;
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

    private JPanel createAppointmentPage() {
        JPanel panel = page("Appointments");
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        doctorIdField = new JTextField(defaultDoctorId(), 8);
        dateField = new JTextField(LocalDate.now().toString(), 10);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadAppointments());
        controls.add(new JLabel("Doctor ID:"));
        controls.add(doctorIdField);
        controls.add(new JLabel("Date:"));
        controls.add(dateField);
        controls.add(refresh);

        appointmentTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createHistoryPage() {
        JPanel panel = page("Medical History");
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientIdField = new JTextField(8);
        JButton load = new JButton("Load History");
        load.addActionListener(e -> loadHistory());
        controls.add(new JLabel("Patient ID:"));
        controls.add(patientIdField);
        controls.add(load);
        historyTable = new JTable(new DefaultTableModel(
                new Object[] {"Note ID", "Appointment", "Doctor", "Diagnosis", "Prescription", "Created"}, 0));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createConsultationPage() {
        JPanel panel = page("Create Consultation Note");
        JPanel form = new JPanel(new java.awt.GridLayout(8, 2, 8, 8));
        appointmentIdField = new JTextField(8);
        JTextField noteDoctorIdField = new JTextField(defaultDoctorId(), 8);
        diagnosisArea = new JTextArea(3, 30);
        prescriptionArea = new JTextArea(3, 30);
        notesArea = new JTextArea(3, 30);
        form.add(new JLabel("Appointment ID:"));
        form.add(appointmentIdField);
        form.add(new JLabel("Doctor ID:"));
        form.add(noteDoctorIdField);
        form.add(new JLabel("Diagnosis:"));
        form.add(new JScrollPane(diagnosisArea));
        form.add(new JLabel("Prescription:"));
        form.add(new JScrollPane(prescriptionArea));
        form.add(new JLabel("Notes:"));
        form.add(new JScrollPane(notesArea));
        JButton save = new JButton("Save Consultation Note");
        save.addActionListener(e -> saveConsultation(noteDoctorIdField.getText()));
        form.add(new JLabel());
        form.add(save);
        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private void loadAppointments() {
        List<Appointment> appointments = controller.viewAppointmentList(parseInt(doctorIdField.getText()),
                parseDate(dateField.getText()));
        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[] {appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                appointment.getStatus(), appointment.getReason()});
        }
    }

    private void loadHistory() {
        List<ConsultationNote> notes = controller.viewMedicalHistory(parseInt(patientIdField.getText()));
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        for (ConsultationNote note : notes) {
            model.addRow(new Object[] {note.getNoteId(), note.getAppointmentId(), note.getDoctorId(),
                note.getDiagnosis(), note.getPrescription(), note.getCreatedAt()});
        }
    }

    private void saveConsultation(String doctorIdText) {
        if (diagnosisArea.getText().trim().length() == 0) {
            JOptionPane.showMessageDialog(this, "Diagnosis is required.");
            return;
        }
        ConsultationNote note = new ConsultationNote(0, parseInt(appointmentIdField.getText()),
                parseInt(doctorIdText), notesArea.getText(), diagnosisArea.getText(),
                prescriptionArea.getText(), LocalDateTime.now());
        controller.updateConsultationNotes(note);
        JOptionPane.showMessageDialog(this, "Consultation note submitted.");
    }

    private String defaultDoctorId() {
        return controller.getCurrentDoctorId() > 0 ? String.valueOf(controller.getCurrentDoctorId()) : "";
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
