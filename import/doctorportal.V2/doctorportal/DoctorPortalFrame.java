package doctorportal;

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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class DoctorPortalFrame extends JFrame {

    // Theme Colors
    private final Color SIDEBAR_BG = new Color(221, 225, 229);
    private final Color BUTTON_BG = new Color(185, 196, 208);
    private final Color BUTTON_HOVER = new Color(165, 178, 192);
    private final Color BUTTON_ACTIVE = new Color(145, 160, 176);
    private final Color COLOR_DARK_TEXT = new Color(20, 20, 20);
    private final Color COLOR_PRIMARY = new Color(66, 133, 244);
    private final Color COLOR_SUCCESS = new Color(52, 168, 83);
    private final Color COLOR_WARNING = new Color(234, 67, 53);
private JTable patientTable;
private DefaultTableModel patientModel;
private JPanel patientListPanel; // optional
    private CardLayout cardLayout;
    private JPanel contentCardPanel;
    private List<PatientConsultationSummary> consultationHistory = new ArrayList<>();
    // RMI Service
    private DoctorService doctorService;
    private boolean connectedToRMI = false;
    
    // Data
    private List<DoctorAppointment> allAppointments;
    private List<DoctorAppointment> medicalHistory;
    private List<DoctorAppointment> pendingAppointments;
    
    
    // Table Models
    private DefaultTableModel pendingModel = new DefaultTableModel();
    private DefaultTableModel historyModel = new DefaultTableModel();
    private DefaultTableModel consultationModel = new DefaultTableModel();
    private JTable consultationTable;

    
    // UI Components
    private JTable pendingTable;
    private JTable historyTable;
    private JLabel lblTotalRecords;
    private JLabel lblStatusConnected;
    private JLabel lblDebugInfo;
    
    // Navigation constants
    private final String PAGE_APPOINTMENTS = "APPOINTMENTS";
    private final String PAGE_MEDICAL_HISTORY = "MEDICAL_HISTORY";

    public DoctorPortalFrame() {
        initializeData();
        connectToRmiServer();
        setupFrame();
        setLocationRelativeTo(null);
    }

    private void initializeData() {
        allAppointments = new ArrayList<>();
        medicalHistory = new ArrayList<>();
        pendingAppointments = new ArrayList<>();
        System.out.println("Data initialized");
    }

    private void connectToRmiServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1235);
            doctorService = (DoctorService) registry.lookup("DoctorService");
            connectedToRMI = true;
            System.out.println("Connected to RMI Server successfully!");
            
            // Fetch data from RMI server
            fetchDataFromRmi();
            
        } catch (Exception e) {
            System.err.println("Could not connect to RMI Server: " + e.getMessage());
            connectedToRMI = false;
            
        }
    }

    private void fetchDataFromRmi() {
        try {
            System.out.println("Fetching appointments from RMI server...");
            
            // Get appointments from RMI server
            List<Doctor> allDoctors = doctorService.getAllDoctors();
            List<DoctorAppointment> appointments = new ArrayList<>();
            for (Doctor doc : allDoctors) {
                appointments.addAll(doctorService.getDoctorAppointments(doc.getDoctorId()));
            }
            
            if (appointments != null && !appointments.isEmpty()) {
                allAppointments = appointments;
                System.out.println("Fetched " + allAppointments.size() + " appointments from RMI");
            } else {
                System.out.println("No appointments from RMI, syncing data...");
                doctorService.syncDataFromApi();
                appointments = doctorService.getDoctorAppointments(1);
                if (appointments != null && !appointments.isEmpty()) {
                    allAppointments = appointments;
                    System.out.println("Fetched " + allAppointments.size() + " appointments after sync");
                } 
            }
            
            // Update lists
            updateAppointmentLists();
            
        } catch (Exception e) {
            System.err.println("Error fetching data from RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
private void refreshPatientList() {
    if (patientModel == null) return;

    try {
        // Sync via RMI
        doctorService.FromappwconApi();
        consultationHistory = doctorService.getConsultationHistory();

        patientModel.setRowCount(0);

        // Build a map: patientId -> (patientName, doctorName, consultationCount)
        Map<Integer, String> patientNameMap = new HashMap<>();
        Map<Integer, String> doctorNameMap = new HashMap<>();
        Map<Integer, Integer> countMap = new HashMap<>();

        // First, fill from allAppointments (which have doctor name)
        for (DoctorAppointment apt : allAppointments) {
            int pid = apt.getPatientId();
            if (!patientNameMap.containsKey(pid)) {
                patientNameMap.put(pid, apt.getPatientName());
                doctorNameMap.put(pid, apt.getDoctorName());
                countMap.put(pid, 0);
            }
            // count consultations if any
            if (apt.getConsultations() != null) {
                countMap.put(pid, countMap.get(pid) + apt.getConsultations().size());
            }
        }

        // Also add patients from consultationHistory (if not already)
        for (PatientConsultationSummary summary : consultationHistory) {
            int pid = summary.getPatientId();
            if (!patientNameMap.containsKey(pid)) {
                patientNameMap.put(pid, summary.getPatientName());
                // doctor name not available in summary, try to find from allAppointments
                String docName = "Unknown";
                for (DoctorAppointment apt : allAppointments) {
                    if (apt.getPatientId() == pid && apt.getConsultations() != null && !apt.getConsultations().isEmpty()) {
                        docName = apt.getDoctorName();
                        break;
                    }
                }
                doctorNameMap.put(pid, docName);
                countMap.put(pid, summary.getConsultations().size());
            }
        }

        // Now populate the table
        for (Integer pid : patientNameMap.keySet()) {
            patientModel.addRow(new Object[]{
                pid,
                patientNameMap.get(pid),
                doctorNameMap.get(pid),
                countMap.get(pid)
            });
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error loading consultation history: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void updateAppointmentLists() {
        medicalHistory.clear();
        pendingAppointments.clear();
        
        System.out.println("Updating appointment lists...");
        System.out.println("Total appointments: " + allAppointments.size());
        
        for (DoctorAppointment apt : allAppointments) {
            apt.setInHistory(false);
            pendingAppointments.add(apt);

            if (apt.getConsultations() != null && !apt.getConsultations().isEmpty()) {
                apt.setInHistory(true);
                medicalHistory.add(apt);
            }
        }
        
        pendingAppointments.sort((a, b) -> b.getAppointmentDate().compareTo(a.getAppointmentDate()));
        medicalHistory.sort((a, b) -> b.getAppointmentDate().compareTo(a.getAppointmentDate()));
        
        System.out.println("Total appointments shown: " + pendingAppointments.size());
        System.out.println("Medical history records: " + medicalHistory.size());
    }

    private void setupFrame() {
        setTitle("Doctor Medical Portal (RMI)");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(createSidebarPanel(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentCardPanel = new JPanel(cardLayout);

        contentCardPanel.add(createAppointmentsPage(), PAGE_APPOINTMENTS);
        contentCardPanel.add(createMedicalHistoryPage(), PAGE_MEDICAL_HISTORY);

        add(contentCardPanel, BorderLayout.CENTER);
        
        cardLayout.show(contentCardPanel, PAGE_APPOINTMENTS);
        
        refreshPendingTable();
        refreshHistoryTable();
        updateSummaryLabels();
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 700));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(180, 185, 190)));

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel lblAppName = new JLabel("DOCTOR PORTAL");
        lblAppName.setForeground(COLOR_DARK_TEXT);
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandPanel.add(lblAppName, BorderLayout.NORTH);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        JPanel menuGroupPanel = new JPanel(new GridLayout(6, 1, 0, 3));
        menuGroupPanel.setOpaque(false);
        menuGroupPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnAppointments = createSidebarButton(" Appointments");
        btnAppointments.setBackground(BUTTON_ACTIVE);
        btnAppointments.addActionListener(e -> {
            refreshPendingTable();
            cardLayout.show(contentCardPanel, PAGE_APPOINTMENTS);
        });

        JButton btnHistory = createSidebarButton(" Medical History");
        btnHistory.addActionListener(e -> {
            refreshHistoryTable();
            refreshPatientList();  
            cardLayout.show(contentCardPanel, PAGE_MEDICAL_HISTORY);
        });

        JButton btnRefresh = createSidebarButton(" Refresh Data");
        btnRefresh.addActionListener(e -> {
            if (connectedToRMI) {
                try {
                    doctorService.syncDataFromApi();
                    fetchDataFromRmi();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error syncing data: " + ex.getMessage());
                }
            } 
            refreshPendingTable();
            refreshHistoryTable();
            updateSummaryLabels();
            JOptionPane.showMessageDialog(this, 
                "Data refreshed!\n" +
                "Total Appointments: " + allAppointments.size() + "\n" +
                "Pending: " + pendingAppointments.size() + "\n" +
                "History: " + medicalHistory.size(), 
                "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnExit = createSidebarButton(" Exit System");
        btnExit.addActionListener(e -> System.exit(0));

        menuGroupPanel.add(btnAppointments);
        menuGroupPanel.add(btnHistory);
        menuGroupPanel.add(btnRefresh);
        menuGroupPanel.add(new JLabel());
        menuGroupPanel.add(new JLabel());
        menuGroupPanel.add(btnExit);

        sidebar.add(menuGroupPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(COLOR_DARK_TEXT);
        btn.setBackground(BUTTON_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(BUTTON_BG);
            }
        });
        return btn;
    }

    private JButton createPrimaryButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createAppointmentsPage() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Appointments - Double-click to Create Consultation Note");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(43, 48, 59));
        headerPanel.add(title, BorderLayout.WEST);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setOpaque(false);
        lblStatusConnected = new JLabel(connectedToRMI ? "● Connected to RMI" : "● Offline Mode");
        lblStatusConnected.setForeground(connectedToRMI ? new Color(52, 168, 83) : new Color(234, 67, 53));
        lblStatusConnected.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusPanel.add(lblStatusConnected);
        headerPanel.add(statusPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel instructionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructionsPanel.setOpaque(false);
        instructionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel instructions = new JLabel("💡 Double-click any appointment to create a consultation note");
        instructions.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        instructions.setForeground(Color.GRAY);
        instructionsPanel.add(instructions);
        
        panel.add(instructionsPanel, BorderLayout.NORTH);

        JPanel debugPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        debugPanel.setOpaque(false);
        lblDebugInfo = new JLabel("Loading appointments...");
        lblDebugInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDebugInfo.setForeground(Color.BLUE);
        debugPanel.add(lblDebugInfo);
        panel.add(debugPanel, BorderLayout.NORTH);

        String[] pendingColumns = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"};
        pendingModel = new DefaultTableModel(pendingColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        pendingTable = new JTable(pendingModel);
        pendingTable.setRowHeight(30);
        pendingTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pendingTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        pendingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = pendingTable.getSelectedRow();
                    if (row != -1) {
                        openCreateMedicalHistoryDialog(row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pendingTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        lblTotalRecords = new JLabel("Total Pending: 0 appointments");
        lblTotalRecords.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bottomPanel.add(lblTotalRecords, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton btnCreateHistory = new JButton("Create Consultation Note");
        btnCreateHistory.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCreateHistory.setBackground(COLOR_PRIMARY);
        btnCreateHistory.setForeground(Color.WHITE);
        btnCreateHistory.setFocusPainted(false);
        btnCreateHistory.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreateHistory.addActionListener(e -> {
            int row = pendingTable.getSelectedRow();
            if (row != -1) {
                openCreateMedicalHistoryDialog(row);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select an appointment first.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        buttonPanel.add(btnCreateHistory);

        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMedicalHistoryPage() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Medical History - Select a Patient to View Consultations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(43, 48, 59));
        headerPanel.add(title, BorderLayout.WEST);
        
JButton btnRefreshHistory = new JButton("Refresh");
btnRefreshHistory.addActionListener(e -> refreshPatientList());
headerPanel.add(btnRefreshHistory, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Split pane for two tables
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.4);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Top Panel - Patients with consultations
        JPanel patientPanel = createPatientListPanel();
        splitPane.setTopComponent(patientPanel);

        // Bottom Panel - Consultation notes for selected patient
        JPanel consultationPanel = createConsultationListPanel();
        splitPane.setBottomComponent(consultationPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }
    
    // 1. Create patient list panel
private JPanel createPatientListPanel() {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        "Medical History (Appointments with Consultations)",
        TitledBorder.LEFT, TitledBorder.TOP,
        new Font("Segoe UI", Font.BOLD, 14)));

    String[] columns = {"Appointment ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    JTable table = new JTable(model);
    table.setRowHeight(30);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

    // Populate from allAppointments (filtered)
    for (DoctorAppointment apt : allAppointments) {
        if (apt.getConsultations() != null && !apt.getConsultations().isEmpty()) {
            model.addRow(new Object[]{
                apt.getAppointmentId(),
                apt.getPatientName(),
                apt.getDoctorName(),
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getStatus() != null ? apt.getStatus() : "Completed",
                apt.getReason()
            });
        }
    }

    JScrollPane scroll = new JScrollPane(table);
    panel.add(scroll, BorderLayout.CENTER);

    // Store reference to refresh later
    panel.putClientProperty("model", model);
    panel.putClientProperty("table", table);

    return panel;
}

    // 2. Create consultation list panel
    private JPanel createConsultationListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Consultation Notes",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)));

        String[] columns = {"Consultation ID", "Appointment ID", "Date", "Diagnosis", "Prescription", "Notes"};
        consultationModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        consultationTable = new JTable(consultationModel);
        consultationTable.setRowHeight(30);
        consultationTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        consultationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(consultationTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Store reference
        panel.putClientProperty("consultationModel", consultationModel);
        panel.putClientProperty("consultationTable", consultationTable);

        return panel;
    }

    // 3. Update consultation table when patient is selected
    private void updateConsultationTable(int patientId) {
        consultationModel.setRowCount(0);

        for (DoctorAppointment apt : allAppointments) {
            if (apt.getPatientId() == patientId && apt.getConsultations() != null) {
                for (Consultation cons : apt.getConsultations()) {
                    consultationModel.addRow(new Object[]{
                        cons.getConsultationId(),
                        apt.getAppointmentId(),
                        cons.getDateCreated(),
                        cons.getDiagnosis(),
                        cons.getPrescription(),
                        cons.getNotes()
                    });
                }
            }
        }
    }

    private void openCreateMedicalHistoryDialog(int row) {
        if (row < 0 || row >= pendingAppointments.size()) {
            return;
        }

        DoctorAppointment appointment = pendingAppointments.get(row);
        
        // Check if already has diagnosis
        if (appointment.getDiagnosis() != null && !appointment.getDiagnosis().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "This appointment already has a consultation note.\n" +
                "Patient: " + appointment.getPatientName() + "\n" +
                "Diagnosis: " + appointment.getDiagnosis(), 
                "Already Completed", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Create Consultation Note", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 168, 83));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerTitle = new JLabel("Create Consultation Note");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);
        
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 8));
        infoPanel.setBackground(new Color(248, 248, 248));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        infoPanel.add(new JLabel("Appointment ID:"));
        infoPanel.add(new JLabel(String.valueOf(appointment.getAppointmentId())));
        infoPanel.add(new JLabel("Patient:"));
        infoPanel.add(new JLabel(appointment.getPatientName()));
        infoPanel.add(new JLabel("Date:"));
        infoPanel.add(new JLabel(appointment.getAppointmentDate() + " " + appointment.getAppointmentTime()));
        infoPanel.add(new JLabel("Reason:"));
        infoPanel.add(new JLabel(appointment.getReason()));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(infoPanel, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel diagLabel = new JLabel("Diagnosis:*");
        diagLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        diagLabel.setForeground(COLOR_WARNING);
        contentPanel.add(diagLabel, gbc);
        
        gbc.gridx = 1;
        JTextArea txtDiagnosis = new JTextArea(3, 20);
        txtDiagnosis.setLineWrap(true);
        txtDiagnosis.setWrapStyleWord(true);
        txtDiagnosis.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        JScrollPane diagScroll = new JScrollPane(txtDiagnosis);
        diagScroll.setPreferredSize(new Dimension(0, 60));
        contentPanel.add(diagScroll, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel prescLabel = new JLabel("Prescription:");
        prescLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contentPanel.add(prescLabel, gbc);
        
        gbc.gridx = 1;
        JTextArea txtPrescription = new JTextArea(3, 20);
        txtPrescription.setLineWrap(true);
        txtPrescription.setWrapStyleWord(true);
        txtPrescription.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        JScrollPane prescScroll = new JScrollPane(txtPrescription);
        prescScroll.setPreferredSize(new Dimension(0, 60));
        contentPanel.add(prescScroll, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        contentPanel.add(notesLabel, gbc);
        
        gbc.gridx = 1;
        JTextArea txtNotes = new JTextArea(2, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        txtNotes.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        notesScroll.setPreferredSize(new Dimension(0, 40));
        contentPanel.add(notesScroll, gbc);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnSave = new JButton("Save Consultation Note");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setBackground(COLOR_SUCCESS);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnSave.addActionListener(e -> {
            
            // Check if appointment already has diagnosis
            if (appointment.getDiagnosis() != null && !appointment.getDiagnosis().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "This appointment already has a consultation note.", 
                    "Already Completed", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String diagnosis = txtDiagnosis.getText().trim();
            if (diagnosis.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Please enter a diagnosis.", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                txtDiagnosis.requestFocus();
                return;
            }

            btnSave.setEnabled(false);
            btnSave.setText("Saving...");

            try {
                
            // ===== CREATE CONSULTATION NOTE (NOT modifying appointment) =====
            Consultation newConsultation = new Consultation(
                appointment.getConsultations().size() + 1,
                appointment.getAppointmentId(),
                diagnosis,
                txtPrescription.getText().trim(),
                txtNotes.getText().trim()
            );

            // Add consultation to appointment
            appointment.addConsultation(newConsultation);
            appointment.setDiagnosis(diagnosis);
            appointment.setPrescription(txtPrescription.getText().trim());
            appointment.setNotes(txtNotes.getText().trim());
// checkpoint
                // Save via RMI to server
                if (connectedToRMI && doctorService != null) {
                    boolean saved = doctorService.saveToMedicalHistory(appointment);

                    if (saved) {
                      
                        
                        refreshPendingTable();
                        refreshHistoryTable();
                        updateSummaryLabels();
                        
                        dialog.dispose();
                        
                        JOptionPane.showMessageDialog(this, 
                            "Consultation Note record saved successfully via RMI!\n" +
                            "Patient: " + appointment.getPatientName() + "\n" +
                            "Diagnosis: " + diagnosis, 
                            
                            
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Failed to save via RMI server.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        btnSave.setEnabled(true);
                        btnSave.setText("Save to History");
                    }
                } else {
                    // Offline mode - save locally only
                    // ===== DO NOT remove appointment =====
                    // ===== DO NOT move to medical history =====
                    // Just keep the consultation added
                    
                    refreshPendingTable();
                    refreshHistoryTable();
                    updateSummaryLabels();
                    
                    dialog.dispose();
                    
                    JOptionPane.showMessageDialog(this, 
                        "Consultation note record saved locally (Offline Mode)!\n" +
                        "Patient: " + appointment.getPatientName() + "\n" +
                        "Diagnosis: " + diagnosis + "\n\n" +
                        "Start RMI server to persist data.", 
                        "Local Save", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Error saving: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                btnSave.setEnabled(true);
                btnSave.setText("Save Consultation Note");
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(new Color(150, 150, 150));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewFullHistoryRecord(int row) {
        if (row < 0 || row >= medicalHistory.size()) {
            return;
        }

        DoctorAppointment record = medicalHistory.get(row);
        
        JDialog dialog = new JDialog(this, "Medical History Record - ID: " + record.getAppointmentId(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerTitle = new JLabel("Medical History Record Details");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);
        
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.setBackground(Color.WHITE);
        
        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0;
        labelGbc.anchor = GridBagConstraints.EAST;
        labelGbc.insets = new Insets(5, 10, 5, 5);
        
        GridBagConstraints valueGbc = new GridBagConstraints();
        valueGbc.gridx = 1;
        valueGbc.anchor = GridBagConstraints.WEST;
        valueGbc.insets = new Insets(5, 5, 5, 10);
        valueGbc.weightx = 1.0;

        addDetailRow(content, labelGbc, valueGbc, "Appointment ID:", String.valueOf(record.getAppointmentId()), 0);
        addDetailRow(content, labelGbc, valueGbc, "Patient:", record.getPatientName(), 1);
        addDetailRow(content, labelGbc, valueGbc, "Doctor:", record.getDoctorName(), 2);
        addDetailRow(content, labelGbc, valueGbc, "Date:", record.getAppointmentDate(), 3);
        addDetailRow(content, labelGbc, valueGbc, "Time:", record.getAppointmentTime(), 4);
        addDetailRow(content, labelGbc, valueGbc, "Reason:", record.getReason(), 5);
        addDetailRow(content, labelGbc, valueGbc, "Status:", record.getStatus() != null ? record.getStatus() : "Completed", 6);

        JLabel diagLabel = new JLabel("Diagnosis:");
        diagLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextArea diagArea = new JTextArea(record.getDiagnosis() != null && !record.getDiagnosis().isEmpty() ? 
                                          record.getDiagnosis() : "No diagnosis recorded");
        diagArea.setEditable(false);
        diagArea.setLineWrap(true);
        diagArea.setWrapStyleWord(true);
        diagArea.setRows(3);
        diagArea.setBackground(new Color(248, 248, 248));
        JScrollPane diagScroll = new JScrollPane(diagArea);
        diagScroll.setPreferredSize(new Dimension(300, 60));

        GridBagConstraints diagLabelGbc = new GridBagConstraints();
        diagLabelGbc.gridx = 0;
        diagLabelGbc.gridy = 7;
        diagLabelGbc.anchor = GridBagConstraints.NORTHEAST;
        diagLabelGbc.insets = new Insets(5, 10, 5, 5);
        
        GridBagConstraints diagValueGbc = new GridBagConstraints();
        diagValueGbc.gridx = 1;
        diagValueGbc.gridy = 7;
        diagValueGbc.anchor = GridBagConstraints.WEST;
        diagValueGbc.insets = new Insets(5, 5, 5, 10);
        diagValueGbc.weightx = 1.0;
        diagValueGbc.fill = GridBagConstraints.HORIZONTAL;
        
        content.add(diagLabel, diagLabelGbc);
        content.add(diagScroll, diagValueGbc);

        JLabel prescLabel = new JLabel("Prescription:");
        prescLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextArea prescArea = new JTextArea(record.getPrescription() != null && !record.getPrescription().isEmpty() ? 
                                           record.getPrescription() : "No prescription recorded");
        prescArea.setEditable(false);
        prescArea.setLineWrap(true);
        prescArea.setWrapStyleWord(true);
        prescArea.setRows(3);
        prescArea.setBackground(new Color(248, 248, 248));
        JScrollPane prescScroll = new JScrollPane(prescArea);
        prescScroll.setPreferredSize(new Dimension(300, 60));

        GridBagConstraints prescLabelGbc = new GridBagConstraints();
        prescLabelGbc.gridx = 0;
        prescLabelGbc.gridy = 8;
        prescLabelGbc.anchor = GridBagConstraints.NORTHEAST;
        prescLabelGbc.insets = new Insets(5, 10, 5, 5);
        
        GridBagConstraints prescValueGbc = new GridBagConstraints();
        prescValueGbc.gridx = 1;
        prescValueGbc.gridy = 8;
        prescValueGbc.anchor = GridBagConstraints.WEST;
        prescValueGbc.insets = new Insets(5, 5, 5, 10);
        prescValueGbc.weightx = 1.0;
        prescValueGbc.fill = GridBagConstraints.HORIZONTAL;
        
        content.add(prescLabel, prescLabelGbc);
        content.add(prescScroll, prescValueGbc);

        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JTextArea notesArea = new JTextArea(record.getNotes() != null && !record.getNotes().isEmpty() ? 
                                           record.getNotes() : "No notes recorded");
        notesArea.setEditable(false);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setRows(2);
        notesArea.setBackground(new Color(248, 248, 248));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(300, 40));

        GridBagConstraints notesLabelGbc = new GridBagConstraints();
        notesLabelGbc.gridx = 0;
        notesLabelGbc.gridy = 9;
        notesLabelGbc.anchor = GridBagConstraints.NORTHEAST;
        notesLabelGbc.insets = new Insets(5, 10, 5, 5);
        
        GridBagConstraints notesValueGbc = new GridBagConstraints();
        notesValueGbc.gridx = 1;
        notesValueGbc.gridy = 9;
        notesValueGbc.anchor = GridBagConstraints.WEST;
        notesValueGbc.insets = new Insets(5, 5, 5, 10);
        notesValueGbc.weightx = 1.0;
        notesValueGbc.fill = GridBagConstraints.HORIZONTAL;
        
        content.add(notesLabel, notesLabelGbc);
        content.add(notesScroll, notesValueGbc);

        dialog.add(new JScrollPane(content), BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setBackground(new Color(150, 150, 150));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints labelGbc, GridBagConstraints valueGbc, 
                             String label, String value, int row) {
        labelGbc.gridy = row;
        valueGbc.gridy = row;
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(labelComp, labelGbc);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(valueComp, valueGbc);
    }
//123456789
    private void refreshPendingTable() {
        pendingModel.setRowCount(0);
        
        // Sort pendingAppointments by appointment ID (ascending order)
        pendingAppointments.sort((a, b) -> Integer.compare(a.getAppointmentId(), b.getAppointmentId()));
        
        for (DoctorAppointment apt : pendingAppointments) {
            String status = apt.getStatus() != null ? apt.getStatus() : "Scheduled";
            
            boolean hasDiagnosis = apt.getDiagnosis() != null && !apt.getDiagnosis().isEmpty();
            
            String statusDisplay = hasDiagnosis ? "✓ Completed" : status;
            
            pendingModel.addRow(new Object[]{
                apt.getAppointmentId(),
                apt.getPatientName(),
                apt.getDoctorName(),
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getReason(),
                statusDisplay
            });
        }
        
        if (lblDebugInfo != null) {
            lblDebugInfo.setText("Appointments loaded: " + pendingAppointments.size() + " total");
        }
        
        updateSummaryLabels();
    }

    private void refreshHistoryTable() {
        historyModel.setRowCount(0);
        for (DoctorAppointment record : medicalHistory) {
            String status = record.getStatus() != null ? record.getStatus() : "Completed";
            String diagnosis = record.getDiagnosis() != null && !record.getDiagnosis().isEmpty() ? 
                              record.getDiagnosis() : "N/A";
            String prescription = record.getPrescription() != null && !record.getPrescription().isEmpty() ? 
                                 record.getPrescription() : "N/A";
            
            historyModel.addRow(new Object[]{
                record.getAppointmentId(),
                record.getPatientName(),
                record.getDoctorName(),
                record.getAppointmentDate(),
                diagnosis,
                prescription,
                status
            });
        }
    }

    private void updateSummaryLabels() {
        if (lblTotalRecords != null) {
            lblTotalRecords.setText("Total Pending: " + pendingAppointments.size() + " appointments");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new DoctorPortalFrame().setVisible(true);
        });
    }
}