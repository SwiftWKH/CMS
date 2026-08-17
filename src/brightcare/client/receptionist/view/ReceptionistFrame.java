package brightcare.client.receptionist.view;

import brightcare.client.receptionist.controller.ReceptionistController;
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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ReceptionistFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final ReceptionistController controller;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JTextField patientIdField;
    private JTextField patientUsernameField;
    private JPasswordField patientPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField icField;
    private JTextField contactField;
    private JTextField appointmentIdField;
    private JTextField appointmentPatientIdField;
    private JTextField appointmentDoctorIdField;
    private JTextField appointmentDateField;
    private JTextField appointmentTimeField;
    private JCheckBox appointmentActiveBox;
    private JTextField reasonField;
    private JTextField scheduleDateField;
    private JTable patientTable;
    private JTable appointmentTable;
    private JTable scheduleTable;

    public ReceptionistFrame() {
        this(new ReceptionistController());
    }

    public ReceptionistFrame(ReceptionistController controller) {
        this.controller = controller;
        initComponents();
        buildDesk();
        setLocationRelativeTo(null);
        refreshPatients();
        refreshAppointmentManagementTable();
        loadAllSchedule();
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
            .addGap(0, 1120, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 720, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public ReceptionistController getController() {
        return controller;
    }

    private void buildDesk() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createPatientPanel(), "patients");
        contentPanel.add(createAppointmentPanel(), "appointments");
        contentPanel.add(createSchedulePanel(), "schedule");
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "patients");
        pack();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 720));
        sidebar.setBackground(new Color(221, 225, 229));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.add(sidebarTitle("RECEPTIONIST"));
        sidebar.add(navButton("Patient Registration", "patients"));
        sidebar.add(navButton("Appointment Management", "appointments"));
        sidebar.add(navButton("Daily Schedule", "schedule"));
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
                if ("patients".equals(card)) {
                    refreshPatients();
                } else if ("appointments".equals(card)) {
                    refreshAppointmentManagementTable();
                } else if ("schedule".equals(card)) {
                    loadAllSchedule();
                }
            });
        }
        return button;
    }

    private JPanel createPatientPanel() {
        JPanel panel = page("Patient Registration");
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBackground(Color.WHITE);
        body.add(createPatientTablePanel(), BorderLayout.CENTER);
        body.add(createPatientForm(), BorderLayout.SOUTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPatientTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        JButton refresh = new JButton("Refresh Patients");
        refresh.addActionListener(e -> refreshPatients());
        JButton newPatient = new JButton("New Patient");
        newPatient.addActionListener(e -> clearPatientFormForRegistration());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBackground(Color.WHITE);
        controls.add(refresh);
        controls.add(newPatient);
        patientTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "First Name", "Last Name", "IC/Passport", "Contact"}, 0));
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillPatientFromSelectedRow();
            }
        });
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(860, 300));
        return panel;
    }

    private JPanel createPatientForm() {
        JPanel form = new JPanel(new java.awt.GridLayout(8, 2, 8, 8));
        form.setBackground(Color.WHITE);
        patientIdField = new JTextField(8);
        patientIdField.setEditable(false);
        patientIdField.setFocusable(false);
        patientUsernameField = new JTextField(14);
        patientPasswordField = new JPasswordField(14);
        patientUsernameField.setToolTipText("Registration only. Existing patient rows do not return username.");
        patientPasswordField.setToolTipText("Registration only. Existing patient rows do not return password.");
        firstNameField = new JTextField(14);
        lastNameField = new JTextField(14);
        icField = new JTextField(14);
        contactField = new JTextField(14);
        form.add(new JLabel("Patient ID:"));
        form.add(patientIdField);
        form.add(new JLabel("Username (new only):"));
        form.add(patientUsernameField);
        form.add(new JLabel("Password (new only):"));
        form.add(patientPasswordField);
        form.add(new JLabel("First Name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last Name:"));
        form.add(lastNameField);
        form.add(new JLabel("IC/Passport:"));
        form.add(icField);
        form.add(new JLabel("Contact Number:"));
        form.add(contactField);
        JButton register = new JButton("Register Patient");
        register.addActionListener(e -> registerPatient());
        JButton update = new JButton("Update Patient");
        update.addActionListener(e -> updatePatient());
        form.add(register);
        form.add(update);
        return form;
    }

    private JPanel createAppointmentPanel() {
        JPanel panel = page("Appointment Management");
        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setBackground(Color.WHITE);
        body.add(createAppointmentTablePanel(), BorderLayout.CENTER);
        body.add(createAppointmentForm(), BorderLayout.SOUTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAppointmentTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        JButton refresh = new JButton("Refresh Appointments");
        refresh.addActionListener(e -> refreshAppointmentManagementTable());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBackground(Color.WHITE);
        controls.add(refresh);
        appointmentTable = new JTable(appointmentTableModel());
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillAppointmentFromSelectedRow(appointmentTable);
            }
        });
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(860, 300));
        return panel;
    }

    private JPanel createAppointmentForm() {
        JPanel form = new JPanel(new java.awt.GridLayout(8, 2, 8, 8));
        form.setBackground(Color.WHITE);
        appointmentIdField = new JTextField(8);
        appointmentPatientIdField = new JTextField(8);
        appointmentDoctorIdField = new JTextField(8);
        appointmentDateField = new JTextField(LocalDate.now().toString(), 10);
        appointmentTimeField = new JTextField("09:00", 8);
        appointmentActiveBox = new JCheckBox("Active / booked", true);
        appointmentActiveBox.setBackground(Color.WHITE);
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
        form.add(appointmentActiveBox);
        form.add(new JLabel("Reason:"));
        form.add(reasonField);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setBackground(Color.WHITE);
        JButton create = new JButton("Create");
        create.addActionListener(e -> createAppointment());
        JButton modify = new JButton("Modify");
        modify.addActionListener(e -> modifyAppointment());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> cancelAppointment());
        actions.add(create);
        actions.add(modify);
        actions.add(cancel);
        form.add(new JLabel());
        form.add(actions);
        return form;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = page("Daily Schedule");
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBackground(Color.WHITE);
        scheduleDateField = new JTextField(LocalDate.now().toString(), 10);
        JButton filter = new JButton("Filter Date");
        filter.addActionListener(e -> loadScheduleByDate());
        JButton showAll = new JButton("Show All");
        showAll.addActionListener(e -> loadAllSchedule());
        controls.add(new JLabel("Date:"));
        controls.add(scheduleDateField);
        controls.add(filter);
        controls.add(showAll);
        scheduleTable = new JTable(appointmentTableModel());
        scheduleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillAppointmentFromSelectedRow(scheduleTable);
            }
        });
        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel appointmentTableModel() {
        return new DefaultTableModel(new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Active", "Reason"}, 0) {
            private static final long serialVersionUID = 1L;

            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : Object.class;
            }

            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

    private Patient patientFromFields() {
        return new Patient(parseInt(patientIdField.getText()), 0, firstNameField.getText(),
                lastNameField.getText(), icField.getText(), contactField.getText(), "");
    }

    private Appointment appointmentFromFields() {
        return new Appointment(parseInt(appointmentIdField.getText()),
                parseInt(appointmentPatientIdField.getText()),
                parseInt(appointmentDoctorIdField.getText()),
                parseDate(appointmentDateField.getText()), parseTime(appointmentTimeField.getText()),
                appointmentStatus(), reasonField.getText());
    }

    private void registerPatient() {
        try {
            validatePatientFields(false);
            String username = patientUsernameField.getText();
            String password = new String(patientPasswordField.getPassword());
            Patient patient = controller.registerPatient(patientFromFields(), username, password);
            JOptionPane.showMessageDialog(this, patient == null
                    ? "Patient registration failed."
                    : "Patient registration submitted.");
            refreshPatients();
            if (patient != null) {
                clearPatientFormForRegistration();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updatePatient() {
        try {
            validatePatientFields(true);
            Patient patient = controller.updatePatientDetails(patientFromFields());
            JOptionPane.showMessageDialog(this, patient == null
                    ? "Patient update failed."
                    : "Patient update submitted.");
            refreshPatients();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void createAppointment() {
        try {
            validateAppointmentFields(false);
            Appointment appointment = controller.createAppointment(appointmentFromFields());
            JOptionPane.showMessageDialog(this, appointment == null
                    ? "Appointment creation failed."
                    : "Appointment creation submitted.");
            refreshAppointmentTables();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void modifyAppointment() {
        try {
            validateAppointmentFields(true);
            Appointment appointment = controller.modifyAppointment(appointmentFromFields());
            JOptionPane.showMessageDialog(this, appointment == null
                    ? "Appointment modification failed."
                    : "Appointment modification submitted.");
            refreshAppointmentTables();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void cancelAppointment() {
        try {
            int appointmentId = parseRequiredInt(appointmentIdField.getText(), "Appointment ID");
            Appointment appointment = controller.cancelAppointment(appointmentId);
            appointmentActiveBox.setSelected(false);
            JOptionPane.showMessageDialog(this, appointment == null
                    ? "Appointment cancellation failed."
                    : "Appointment cancellation submitted.");
            refreshAppointmentTables();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshPatients() {
        if (patientTable == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        model.setRowCount(0);
        for (Patient patient : controller.viewPatients()) {
            model.addRow(new Object[] {patient.getPatientId(), patient.getFirstName(), patient.getLastName(),
                patient.getIcPassportNo(), patient.getContactNumber()});
        }
    }

    private void refreshAppointmentManagementTable() {
        if (appointmentTable == null) {
            return;
        }
        loadAppointmentsInto(appointmentTable, null);
    }

    private void loadAllSchedule() {
        if (scheduleTable == null) {
            return;
        }
        loadAppointmentsInto(scheduleTable, null);
    }

    private void loadScheduleByDate() {
        if (scheduleTable == null || scheduleDateField == null) {
            return;
        }
        LocalDate scheduleDate;
        try {
            scheduleDate = parseRequiredDate(scheduleDateField.getText(), "Schedule date");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        loadAppointmentsInto(scheduleTable, scheduleDate);
    }

    private void loadAppointmentsInto(JTable table, LocalDate date) {
        List<Appointment> appointments = controller.viewAppointmentSchedule(date);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[] {appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                isActiveStatus(appointment.getStatus()), appointment.getReason()});
        }
    }

    private void refreshAppointmentTables() {
        refreshAppointmentManagementTable();
        loadAllSchedule();
    }

    private void fillPatientFromSelectedRow() {
        int row = patientTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        int modelRow = patientTable.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        patientIdField.setText(text(model.getValueAt(modelRow, 0)));
        firstNameField.setText(text(model.getValueAt(modelRow, 1)));
        lastNameField.setText(text(model.getValueAt(modelRow, 2)));
        icField.setText(text(model.getValueAt(modelRow, 3)));
        contactField.setText(text(model.getValueAt(modelRow, 4)));
        patientUsernameField.setText("");
        patientPasswordField.setText("");
        setCredentialFieldsEditable(false);
    }

    private void clearPatientFormForRegistration() {
        if (patientTable != null) {
            patientTable.clearSelection();
        }
        patientIdField.setText("");
        patientUsernameField.setText("");
        patientPasswordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        icField.setText("");
        contactField.setText("");
        setCredentialFieldsEditable(true);
        patientUsernameField.requestFocusInWindow();
    }

    private void setCredentialFieldsEditable(boolean editable) {
        patientUsernameField.setEditable(editable);
        patientUsernameField.setEnabled(editable);
        patientPasswordField.setEditable(editable);
        patientPasswordField.setEnabled(editable);
    }

    private void fillAppointmentFromSelectedRow(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0 || appointmentIdField == null) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        appointmentIdField.setText(text(model.getValueAt(modelRow, 0)));
        appointmentPatientIdField.setText(text(model.getValueAt(modelRow, 1)));
        appointmentDoctorIdField.setText(text(model.getValueAt(modelRow, 2)));
        appointmentDateField.setText(text(model.getValueAt(modelRow, 3)));
        appointmentTimeField.setText(shortTime(text(model.getValueAt(modelRow, 4))));
        appointmentActiveBox.setSelected(Boolean.TRUE.equals(model.getValueAt(modelRow, 5)));
        reasonField.setText(text(model.getValueAt(modelRow, 6)));
    }

    private void validatePatientFields(boolean requirePatientId) {
        if (requirePatientId) {
            parseRequiredInt(patientIdField.getText(), "Patient ID");
        } else {
            if (!patientUsernameField.isEnabled() || !patientPasswordField.isEnabled()) {
                throw new IllegalArgumentException("Click New Patient before registering a new patient.");
            }
            requireText(patientUsernameField.getText(), "Username");
            requireText(new String(patientPasswordField.getPassword()), "Password");
        }
        requireText(firstNameField.getText(), "First name");
        requireText(lastNameField.getText(), "Last name");
        requireText(icField.getText(), "IC/Passport");
        requireText(contactField.getText(), "Contact number");
    }

    private void validateAppointmentFields(boolean requireAppointmentId) {
        if (requireAppointmentId) {
            parseRequiredInt(appointmentIdField.getText(), "Appointment ID");
        }
        parseRequiredInt(appointmentPatientIdField.getText(), "Patient ID");
        parseRequiredInt(appointmentDoctorIdField.getText(), "Doctor ID");
        parseRequiredDate(appointmentDateField.getText(), "Appointment date");
        parseRequiredTime(appointmentTimeField.getText(), "Appointment time");
        requireText(reasonField.getText(), "Reason");
    }

    private String appointmentStatus() {
        return appointmentActiveBox.isSelected() ? "BOOKED" : "CANCELLED";
    }

    private boolean isActiveStatus(String status) {
        return status == null || !"CANCELLED".equalsIgnoreCase(status);
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
