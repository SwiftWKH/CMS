package brightcare.client.receptionist.view;

import brightcare.client.receptionist.controller.ReceptionistController;
import brightcare.model.Appointment;
import brightcare.model.Patient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ReceptionistFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final ReceptionistController controller;
    private JTextField patientIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField icField;
    private JTextField contactField;
    private JTextField recordField;
    private JTextField appointmentIdField;
    private JTextField appointmentPatientIdField;
    private JTextField appointmentDoctorIdField;
    private JTextField appointmentDateField;
    private JTextField appointmentTimeField;
    private JTextField statusField;
    private JTextField reasonField;
    private JTextField scheduleDateField;
    private JTable scheduleTable;

    public ReceptionistFrame() {
        this(new ReceptionistController());
    }

    public ReceptionistFrame(ReceptionistController controller) {
        this.controller = controller;
        initComponents();
        buildDesk();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightCare Receptionist");

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

    public ReceptionistController getController() {
        return controller;
    }

    private void buildDesk() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 12, 24));
        JLabel title = new JLabel("Receptionist Desk");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> controller.logout(this));
        header.add(title, BorderLayout.WEST);
        header.add(logout, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patient Registration", createPatientPanel());
        tabs.addTab("Appointment Management", createAppointmentPanel());
        tabs.addTab("Daily Schedule", createSchedulePanel());
        add(tabs, BorderLayout.CENTER);
        pack();
    }

    private JPanel createPatientPanel() {
        JPanel panel = page();
        JPanel form = new JPanel(new java.awt.GridLayout(8, 2, 8, 8));
        patientIdField = new JTextField(8);
        firstNameField = new JTextField(14);
        lastNameField = new JTextField(14);
        icField = new JTextField(14);
        contactField = new JTextField(14);
        recordField = new JTextField(14);
        form.add(new JLabel("Patient ID for update:"));
        form.add(patientIdField);
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
        JButton register = new JButton("Register Patient");
        register.addActionListener(e -> registerPatient());
        JButton update = new JButton("Update Patient");
        update.addActionListener(e -> updatePatient());
        form.add(register);
        form.add(update);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = page();
        JPanel form = new JPanel(new java.awt.GridLayout(9, 2, 8, 8));
        appointmentIdField = new JTextField(8);
        appointmentPatientIdField = new JTextField(8);
        appointmentDoctorIdField = new JTextField(8);
        appointmentDateField = new JTextField(LocalDate.now().toString(), 10);
        appointmentTimeField = new JTextField("09:00", 8);
        statusField = new JTextField("BOOKED", 10);
        reasonField = new JTextField(20);
        form.add(new JLabel("Appointment ID for update/cancel:"));
        form.add(appointmentIdField);
        form.add(new JLabel("Patient ID:"));
        form.add(appointmentPatientIdField);
        form.add(new JLabel("Doctor ID:"));
        form.add(appointmentDoctorIdField);
        form.add(new JLabel("Date:"));
        form.add(appointmentDateField);
        form.add(new JLabel("Time:"));
        form.add(appointmentTimeField);
        form.add(new JLabel("Status:"));
        form.add(statusField);
        form.add(new JLabel("Reason:"));
        form.add(reasonField);
        JButton create = new JButton("Create Appointment");
        create.addActionListener(e -> createAppointment());
        JButton modify = new JButton("Modify Appointment");
        modify.addActionListener(e -> modifyAppointment());
        JButton cancel = new JButton("Cancel Appointment");
        cancel.addActionListener(e -> cancelAppointment());
        form.add(create);
        form.add(modify);
        form.add(cancel);
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = page();
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        scheduleDateField = new JTextField(LocalDate.now().toString(), 10);
        JButton refresh = new JButton("Refresh Schedule");
        refresh.addActionListener(e -> loadSchedule());
        controls.add(new JLabel("Date:"));
        controls.add(scheduleDateField);
        controls.add(refresh);
        scheduleTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        return panel;
    }

    private Patient patientFromFields() {
        return new Patient(parseInt(patientIdField.getText()), 0, firstNameField.getText(),
                lastNameField.getText(), icField.getText(), contactField.getText(), recordField.getText());
    }

    private Appointment appointmentFromFields() {
        return new Appointment(parseInt(appointmentIdField.getText()),
                parseInt(appointmentPatientIdField.getText()),
                parseInt(appointmentDoctorIdField.getText()),
                parseDate(appointmentDateField.getText()), parseTime(appointmentTimeField.getText()),
                statusField.getText(), reasonField.getText());
    }

    private void registerPatient() {
        controller.registerPatient(patientFromFields());
        JOptionPane.showMessageDialog(this, "Patient registration submitted.");
    }

    private void updatePatient() {
        controller.updatePatientDetails(patientFromFields());
        JOptionPane.showMessageDialog(this, "Patient update submitted.");
    }

    private void createAppointment() {
        controller.createAppointment(appointmentFromFields());
        JOptionPane.showMessageDialog(this, "Appointment creation submitted.");
    }

    private void modifyAppointment() {
        controller.modifyAppointment(appointmentFromFields());
        JOptionPane.showMessageDialog(this, "Appointment modification submitted.");
    }

    private void cancelAppointment() {
        controller.cancelAppointment(parseInt(appointmentIdField.getText()));
        JOptionPane.showMessageDialog(this, "Appointment cancellation submitted.");
    }

    private void loadSchedule() {
        List<Appointment> appointments = controller.viewAppointmentSchedule(parseDate(scheduleDateField.getText()));
        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[] {appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                appointment.getStatus(), appointment.getReason()});
        }
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
