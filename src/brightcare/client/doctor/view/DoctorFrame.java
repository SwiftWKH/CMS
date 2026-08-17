package brightcare.client.doctor.view;

import brightcare.client.doctor.controller.DoctorController;
import brightcare.model.Appointment;
import brightcare.model.ConsultationNote;
import brightcare.util.BrightCareLogger;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class DoctorFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = BrightCareLogger.getLogger(DoctorFrame.class);
    private static final Color SIDEBAR_BG = new Color(221, 225, 229);
    private static final Color BUTTON_BG = new Color(185, 196, 208);
    private static final Color BUTTON_HOVER = new Color(165, 178, 192);
    private static final Color BUTTON_ACTIVE = new Color(145, 160, 176);
    private static final Color COLOR_DARK_TEXT = new Color(20, 20, 20);
    private static final Color COLOR_PRIMARY = new Color(66, 133, 244);
    private static final Color COLOR_SUCCESS = new Color(52, 168, 83);
    private static final Color COLOR_WARNING = new Color(234, 67, 53);

    private final DoctorController controller;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JTextField doctorIdField;
    private JTextField dateField;
    private JTable appointmentTable;
    private JTextField patientIdField;
    private JTable historyTable;
    private JTable consultationAppointmentTable;
    private JTextField appointmentIdField;
    private JTextField consultationPatientIdField;
    private JTextField consultationDoctorIdField;
    private JTextArea diagnosisArea;
    private JTextArea prescriptionArea;
    private JTextArea notesArea;
    private JLabel statusLabel;
    private JLabel appointmentSummaryLabel;
    private int busyOperations;

    public DoctorFrame() {
        this(new DoctorController());
    }

    public DoctorFrame(DoctorController controller) {
        this.controller = controller;
        initComponents();
        buildPortal();
        setLocationRelativeTo(null);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loadAppointments();
            }
        });
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
        setTitle("Doctor Medical Portal");
        setSize(1200, 700);
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(createAppointmentPage(), "appointments");
        contentPanel.add(createHistoryPage(), "history");
        contentPanel.add(createConsultationPage(), "consultation");
        add(contentPanel, BorderLayout.CENTER);
        statusLabel = new JLabel("Ready.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        add(statusLabel, BorderLayout.SOUTH);
        cardLayout.show(contentPanel, "appointments");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(180, 185, 190)));

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        JLabel title = new JLabel("DOCTOR PORTAL");
        title.setForeground(COLOR_DARK_TEXT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandPanel.add(title, BorderLayout.NORTH);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel(new GridLayout(6, 1, 0, 3));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        menuPanel.add(navButton(" Appointments", "appointments"));
        menuPanel.add(navButton(" Medical History", "history"));
        menuPanel.add(navButton(" Consultation Note", "consultation"));
        JButton refresh = sidebarButton(" Refresh Data");
        refresh.addActionListener(e -> loadAppointments());
        menuPanel.add(refresh);
        menuPanel.add(new JLabel());
        JButton logout = navButton("Logout", null);
        logout.addActionListener(e -> controller.logout(this));
        menuPanel.add(logout);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JButton navButton(String text, String card) {
        JButton button = sidebarButton(text);
        if (card != null) {
            button.addActionListener(e -> cardLayout.show(contentPanel, card));
        }
        return button;
    }

    private JButton sidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(COLOR_DARK_TEXT);
        button.setBackground(BUTTON_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                button.setBackground(BUTTON_HOVER);
            }

            public void mouseExited(MouseEvent event) {
                button.setBackground(BUTTON_BG);
            }
        });
        return button;
    }

    private JPanel page(String title) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(new Color(43, 48, 59));
        panel.add(heading, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createAppointmentPage() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JPanel topPanel = new JPanel(new BorderLayout(0, 8));
        topPanel.setOpaque(false);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Appointments - Double-click to Create Consultation Note");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(43, 48, 59));
        headerPanel.add(title, BorderLayout.WEST);
        JLabel connectionLabel = new JLabel("BrightCare RMI Session");
        connectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        connectionLabel.setForeground(COLOR_SUCCESS);
        headerPanel.add(connectionLabel, BorderLayout.EAST);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        doctorIdField = new JTextField(defaultDoctorId(), 8);
        if (controller.getCurrentDoctorId() > 0) {
            doctorIdField.setEditable(false);
            doctorIdField.setFocusable(false);
            doctorIdField.setBackground(new Color(235, 235, 235));
        }
        dateField = new JTextField("", 10);
        JButton refresh = primaryButton("Refresh", COLOR_PRIMARY);
        refresh.addActionListener(e -> loadAppointments());
        JButton createNote = primaryButton("Create Consultation Note", COLOR_SUCCESS);
        createNote.addActionListener(e -> useSelectedAppointmentForConsultationOrWarn());
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        controls.add(new JLabel("Doctor ID:"));
        controls.add(doctorIdField);
        controls.add(new JLabel("Date:"));
        controls.add(dateField);
        controls.add(refresh);
        controls.add(createNote);
        topPanel.add(controls, BorderLayout.CENTER);
        JLabel hint = new JLabel("Double-click any appointment row to prepare a consultation note.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        hint.setForeground(Color.GRAY);
        topPanel.add(hint, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);

        appointmentTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        styleTable(appointmentTable);
        appointmentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    useSelectedAppointmentForConsultation();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        appointmentSummaryLabel = new JLabel("Total: 0 appointments");
        appointmentSummaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(appointmentSummaryLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHistoryPage() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Medical History - Select a Patient to View Consultations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(43, 48, 59));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setOpaque(false);
        patientIdField = new JTextField(8);
        JButton load = primaryButton("Load History", COLOR_PRIMARY);
        load.addActionListener(e -> loadHistory());
        controls.add(new JLabel("Patient ID:"));
        controls.add(patientIdField);
        controls.add(load);
        headerPanel.add(controls, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        historyTable = new JTable(new DefaultTableModel(
                new Object[] {"Note ID", "Appointment", "Doctor", "Diagnosis", "Prescription", "Created"}, 0));
        styleTable(historyTable);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Consultation Notes",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createConsultationPage() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Create Consultation Note");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(43, 48, 59));
        panel.add(title, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setBackground(Color.WHITE);
        consultationAppointmentTable = new JTable(new DefaultTableModel(
                new Object[] {"ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"}, 0));
        styleTable(consultationAppointmentTable);
        consultationAppointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillConsultationFromSelectedAppointment(consultationAppointmentTable, false);
            }
        });
        JScrollPane appointmentScrollPane = new JScrollPane(consultationAppointmentTable);
        appointmentScrollPane.setPreferredSize(new Dimension(850, 170));
        appointmentScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Select Appointment",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)));
        body.add(appointmentScrollPane, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Consultation Details",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14)));
        appointmentIdField = new JTextField(8);
        consultationPatientIdField = new JTextField(8);
        consultationDoctorIdField = new JTextField(defaultDoctorId(), 8);
        makeReadOnly(appointmentIdField);
        makeReadOnly(consultationPatientIdField);
        makeReadOnly(consultationDoctorIdField);
        diagnosisArea = new JTextArea(3, 30);
        prescriptionArea = new JTextArea(3, 30);
        notesArea = new JTextArea(3, 30);
        addFormRow(form, "Appointment ID:", appointmentIdField, 0);
        addFormRow(form, "Patient ID:", consultationPatientIdField, 1);
        addFormRow(form, "Doctor ID:", consultationDoctorIdField, 2);
        addFormRow(form, "Diagnosis:*", new JScrollPane(diagnosisArea), 3);
        addFormRow(form, "Prescription:", new JScrollPane(prescriptionArea), 4);
        addFormRow(form, "Notes:", new JScrollPane(notesArea), 5);
        JButton save = primaryButton("Save Consultation Note", COLOR_SUCCESS);
        save.addActionListener(e -> saveConsultation());
        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.gridx = 1;
        buttonGbc.gridy = 6;
        buttonGbc.anchor = GridBagConstraints.EAST;
        buttonGbc.insets = new Insets(12, 10, 5, 10);
        form.add(save, buttonGbc);
        body.add(form, BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JButton primaryButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(180, 35));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void addFormRow(JPanel panel, String label, java.awt.Component component, int row) {
        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.gridy = row;
        labelGbc.anchor = GridBagConstraints.NORTHEAST;
        labelGbc.insets = new Insets(7, 10, 7, 10);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 13));
        if (label.indexOf('*') >= 0) {
            labelComponent.setForeground(COLOR_WARNING);
        }
        panel.add(labelComponent, labelGbc);

        GridBagConstraints fieldGbc = new GridBagConstraints();
        fieldGbc.gridx = 1;
        fieldGbc.gridy = row;
        fieldGbc.weightx = 1.0;
        fieldGbc.fill = GridBagConstraints.HORIZONTAL;
        fieldGbc.insets = new Insets(7, 0, 7, 10);
        component.setPreferredSize(row >= 3 ? new Dimension(420, 70) : new Dimension(220, 28));
        panel.add(component, fieldGbc);
    }

    private void makeReadOnly(JTextField field) {
        field.setEditable(false);
        field.setFocusable(false);
        field.setBackground(new Color(235, 235, 235));
    }

    private void loadAppointments() {
        int doctorId;
        LocalDate date;
        try {
            doctorId = doctorIdForAppointmentLookup();
            date = parseOptionalDate(dateField.getText(), "Date");
        } catch (IllegalArgumentException ex) {
            LOGGER.warning("Doctor appointment load rejected: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        final int requestedDoctorId = doctorId;
        final LocalDate requestedDate = date;
        LOGGER.info("Doctor appointment load started from UI. doctorId=" + requestedDoctorId
                + ", date=" + requestedDate + ".");
        setBusy("Loading appointments...");
        new SwingWorker<List<Appointment>, Void>() {
            protected List<Appointment> doInBackground() {
                return controller.viewAppointmentList(requestedDoctorId, requestedDate);
            }

            protected void done() {
                try {
                    List<Appointment> appointments = get();
                    loadAppointmentsIntoTable(appointmentTable, appointments);
                    loadAppointmentsIntoTable(consultationAppointmentTable, appointments);
                    LOGGER.info("Doctor appointment load finished in UI. doctorId=" + requestedDoctorId
                            + ", date=" + requestedDate + ", rows=" + appointments.size() + ".");
                    clearBusy("Appointments loaded: " + appointments.size() + ".");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    handleDoctorActionFailure("Appointment load interrupted", ex);
                } catch (ExecutionException ex) {
                    handleDoctorActionFailure("Appointment load failed", rootCause(ex));
                }
            }
        }.execute();
    }

    private void loadAppointmentsIntoTable(JTable table, List<Appointment> appointments) {
        if (table == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Appointment appointment : appointments) {
            model.addRow(new Object[] {appointment.getAppointmentId(), appointment.getPatientId(),
                appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                appointment.getStatus(), appointment.getReason()});
        }
    }

    private void loadHistory() {
        int patientId;
        try {
            patientId = parseRequiredInt(patientIdField.getText(), "Patient ID");
        } catch (IllegalArgumentException ex) {
            LOGGER.warning("Doctor medical history load rejected: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        final int requestedPatientId = patientId;
        LOGGER.info("Doctor medical history load started from UI. patientId=" + requestedPatientId + ".");
        setBusy("Loading medical history...");
        new SwingWorker<List<ConsultationNote>, Void>() {
            protected List<ConsultationNote> doInBackground() {
                return controller.viewMedicalHistory(requestedPatientId);
            }

            protected void done() {
                try {
                    List<ConsultationNote> notes = get();
                    DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
                    model.setRowCount(0);
                    for (ConsultationNote note : notes) {
                        model.addRow(new Object[] {note.getNoteId(), note.getAppointmentId(), note.getDoctorId(),
                            note.getDiagnosis(), note.getPrescription(), note.getCreatedAt()});
                    }
                    LOGGER.info("Doctor medical history load finished in UI. patientId=" + requestedPatientId
                            + ", rows=" + notes.size() + ".");
                    clearBusy("Medical history loaded: " + notes.size() + ".");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    handleDoctorActionFailure("Medical history load interrupted", ex);
                } catch (ExecutionException ex) {
                    handleDoctorActionFailure("Medical history load failed", rootCause(ex));
                }
            }
        }.execute();
    }

    private void saveConsultation() {
        try {
            int appointmentId = parseRequiredInt(appointmentIdField.getText(), "Appointment ID");
            int patientId = parseRequiredInt(consultationPatientIdField.getText(), "Patient ID");
            int doctorId = parseRequiredInt(consultationDoctorIdField.getText(), "Doctor ID");
            requireText(diagnosisArea.getText(), "Diagnosis");
            final ConsultationNote note = new ConsultationNote(0, appointmentId, patientId, doctorId, notesArea.getText(),
                    diagnosisArea.getText(), prescriptionArea.getText(), LocalDateTime.now());
            LOGGER.info("Doctor consultation save started from UI. appointmentId=" + appointmentId
                    + ", patientId=" + patientId + ", doctorId=" + doctorId + ".");
            setBusy("Saving consultation note...");
            new SwingWorker<ConsultationNote, Void>() {
                protected ConsultationNote doInBackground() {
                    return controller.updateConsultationNotes(note);
                }

                protected void done() {
                    try {
                        ConsultationNote saved = get();
                        LOGGER.info("Doctor consultation save finished in UI. appointmentId="
                                + note.getAppointmentId() + ", saved=" + (saved != null) + ".");
                        clearBusy(saved == null ? "Consultation note failed." : "Consultation note submitted.");
                        JOptionPane.showMessageDialog(DoctorFrame.this, saved == null
                                ? "Consultation note failed."
                                : "Consultation note submitted.");
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        handleDoctorActionFailure("Consultation note save interrupted", ex);
                    } catch (ExecutionException ex) {
                        handleDoctorActionFailure("Consultation note save failed", rootCause(ex));
                    }
                }
            }.execute();
        } catch (IllegalArgumentException ex) {
            LOGGER.warning("Doctor consultation save rejected: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void setBusy(String message) {
        busyOperations++;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus(message);
    }

    private void clearBusy(String message) {
        busyOperations = Math.max(0, busyOperations - 1);
        if (busyOperations == 0) {
            setCursor(Cursor.getDefaultCursor());
        }
        setStatus(message);
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    private void handleDoctorActionFailure(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, message + ".", throwable);
        clearBusy(message + ".");
        JOptionPane.showMessageDialog(this, message + ". Check logs/brightcare*.log for details.");
    }

    private Throwable rootCause(ExecutionException ex) {
        return ex.getCause() == null ? ex : ex.getCause();
    }

    private String defaultDoctorId() {
        return controller.getCurrentDoctorId() > 0 ? String.valueOf(controller.getCurrentDoctorId()) : "";
    }

    private int doctorIdForAppointmentLookup() {
        if (controller.getCurrentDoctorId() > 0) {
            return controller.getCurrentDoctorId();
        }
        return parseRequiredInt(doctorIdField.getText(), "Doctor ID");
    }

    private void useSelectedAppointmentForConsultation() {
        fillConsultationFromSelectedAppointment(appointmentTable, true);
    }

    private void fillConsultationFromSelectedAppointment(JTable table, boolean showConsultationPage) {
        if (table == null || appointmentIdField == null) {
            return;
        }
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);
        String appointmentId = valueAt(table, modelRow, 0);
        String patientId = valueAt(table, modelRow, 1);
        String doctorId = valueAt(table, modelRow, 2);
        appointmentIdField.setText(appointmentId);
        consultationPatientIdField.setText(patientId);
        consultationDoctorIdField.setText(doctorId);
        if (showConsultationPage) {
            cardLayout.show(contentPanel, "consultation");
            selectConsultationAppointmentById(appointmentId);
        }
        setStatus("Selected appointment " + appointmentId + " for patient " + patientId + ".");
        LOGGER.info("Doctor appointment selected for consultation. appointmentId=" + appointmentId
                + ", patientId=" + patientId + ", doctorId=" + doctorId + ".");
    }

    private void useSelectedAppointmentForConsultationOrWarn() {
        if (appointmentTable.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.");
            return;
        }
        useSelectedAppointmentForConsultation();
    }

    private void selectConsultationAppointmentById(String appointmentId) {
        if (consultationAppointmentTable == null || appointmentId == null || appointmentId.trim().length() == 0) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) consultationAppointmentTable.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (appointmentId.equals(valueAt(consultationAppointmentTable, row, 0))) {
                consultationAppointmentTable.setRowSelectionInterval(row, row);
                return;
            }
        }
    }

    private String valueAt(JTable table, int row, int column) {
        Object value = table.getModel().getValueAt(row, column);
        return value == null ? "" : String.valueOf(value);
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

    private LocalDate parseOptionalDate(String value, String label) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        return parseRequiredDate(value, label);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
