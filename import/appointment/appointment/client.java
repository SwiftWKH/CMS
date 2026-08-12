package ui;

import ui.Server.HospitalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Patient {
    private int patientId;
    private int userId;
    private String firstName;
    private String lastName;
    private String icPassportNo;
    private String contactNumber;
    private String medicalRecordId;

    public Patient(int patientId, int userId, String firstName, String lastName, String icPassportNo, String contactNumber, String medicalRecordId) {
        this.patientId = patientId;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.icPassportNo = icPassportNo;
        this.contactNumber = contactNumber;
        this.medicalRecordId = medicalRecordId;
    }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getIcPassportNo() { return icPassportNo; }
    public void setIcPassportNo(String icPassportNo) { this.icPassportNo = icPassportNo; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(String medicalRecordId) { this.medicalRecordId = medicalRecordId; }
}

class Appointment {
    private int appointmentId;
    private int doctorId;
    private int patientId;
    private String doctorName;
    private String dateTime;
    private String time;
    private String reason;
    private String status;

    public Appointment(int appointmentId, int doctorId, int patientId, String doctorName, String dateTime, String time, String reason, String status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.dateTime = dateTime;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public int getDoctorId() { return doctorId; }
    public int getPatientId() { return patientId; }
    public String getDoctorName() { return doctorName; }
    public String getDateTime() { return dateTime; }
    public String getTime() { return time; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

class DoctorAvailability {
    private int doctorId;
    private String doctorName;
    private String specialization;
    private String date;
    private String availableTime;

    public DoctorAvailability(int doctorId, String doctorName, String specialization, String date, String availableTime) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.date = date;
        this.availableTime = availableTime;
    }

    public int getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getSpecialization() { return specialization; }
    public String getDate() { return date; }
    public String getAvailableTime() { return availableTime; }
}

public class Client extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentCardPanel;

    private Patient currentPatient;
    private List<Appointment> appointments;
    private List<DoctorAvailability> availabilities;

    private DefaultTableModel availabilityModel;
    private DefaultTableModel activeScheduleModel;
    private DefaultTableModel historyModel;

    private JTextField txtPatientId, txtUserId, txtFirstName, txtLastName, txtIcPassport, txtContact, txtMedicalRecordId;
    private JComboBox<String> comboSlots;
    private JTextField txtReason;
    private JLabel lblWelcomeHeader;

    private final Color COLOR_DARK_TEXT = new Color(20, 20, 20);

    private final String PAGE_DASHBOARD = "DASHBOARD";
    private final String PAGE_INFO = "INFO";
    private final String PAGE_AVAILABILITY = "AVAILABILITY";
    private final String PAGE_BOOKING = "BOOKING";
    private final String PAGE_SCHEDULE = "SCHEDULE";
    private final String PAGE_HISTORY = "HISTORY";

    public Client() {
        initializeEmptyData();
        setupFrame();
    }

    private void initializeEmptyData() {
        currentPatient = new Patient(101, 1, "John", "Doe", "A12345678", "0123456789", "MRN-001");
        appointments = new ArrayList<>();
        availabilities = new ArrayList<>();
    }

    private void fetchAndParseAppointmentsFromRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1234);
            HospitalService service = (HospitalService) registry.lookup("HospitalService");

            String rawJson = service.getResultJson();
            System.out.println("Received Appointments JSON:\n" + rawJson);

            if (rawJson == null || rawJson.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Received empty response from RMI server.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            appointments.clear();

            String cleanJson = rawJson.trim();
            if (cleanJson.startsWith("[")) cleanJson = cleanJson.substring(1);
            if (cleanJson.endsWith("]")) cleanJson = cleanJson.substring(0, cleanJson.length() - 1);

            String[] objects = cleanJson.split("\\}\\s*,\\s*\\{");

            for (String obj : objects) {
                String objectContent = obj.replace("{", "").replace("}", "");

                // Extract Appointment ID
                String aptIdStr = extractJsonField(objectContent, "appointmentId");
                if (aptIdStr.isEmpty()) aptIdStr = extractJsonField(objectContent, "appointmentID");
                if (aptIdStr.isEmpty()) aptIdStr = extractJsonField(objectContent, "id");

                int aptId = appointments.size() + 1;
                try {
                    if (!aptIdStr.isEmpty()) aptId = Integer.parseInt(aptIdStr);
                } catch (NumberFormatException ignored) {}

                // Extract Doctor ID
                String doctorIdStr = extractJsonField(objectContent, "doctorId");
                if (doctorIdStr.isEmpty()) doctorIdStr = extractJsonField(objectContent, "doctorID");
                int doctorId = 1;
                try {
                    if (!doctorIdStr.isEmpty()) doctorId = Integer.parseInt(doctorIdStr);
                } catch (NumberFormatException ignored) {}

                // Extract Patient ID
                String patientIdStr = extractJsonField(objectContent, "patientId");
                if (patientIdStr.isEmpty()) patientIdStr = extractJsonField(objectContent, "patientID");
                int patientId = currentPatient.getPatientId();
                try {
                    if (!patientIdStr.isEmpty()) patientId = Integer.parseInt(patientIdStr);
                } catch (NumberFormatException ignored) {}

                // Extract Doctor Name
                String doctorName = extractJsonField(objectContent, "doctorName");
                if (doctorName.isEmpty()) doctorName = extractJsonField(objectContent, "doctor");

                // Extract Date and Time
                String date = extractJsonField(objectContent, "date");
                if (date.isEmpty()) date = extractJsonField(objectContent, "dateTime");
                if (date.isEmpty()) date = "N/A";

                String time = extractJsonField(objectContent, "time");
                if (time.isEmpty()) time = "N/A";

                // Extract Reason
                String reason = extractJsonField(objectContent, "reason");
                if (reason.isEmpty()) reason = extractJsonField(objectContent, "symptoms");

                // Extract Status
                String status = extractJsonField(objectContent, "status");
                if (status.isEmpty()) status = extractJsonField(objectContent, "state");

                appointments.add(new Appointment(
                        aptId,
                        doctorId,
                        patientId,
                        doctorName,
                        date,
                        time,
                        reason,
                        status
                ));
            }

            refreshActiveScheduleTable();
            refreshHistoryTable();

            JOptionPane.showMessageDialog(this,
                    "Successfully loaded " + appointments.size() + " appointment records!",
                    "Fetch Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch appointments from RMI Server:\n" + e.getMessage(),
                    "Fetch Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchAndParseHospitalDataFromRMI() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1234);
            HospitalService service = (HospitalService) registry.lookup("HospitalService");

            String rawJson = service.getDoctorSchedulesJson();
            System.out.println("Received Doctor JSON:\n" + rawJson);

            if (rawJson == null || rawJson.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Received empty response from RMI server.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            availabilities.clear();

            Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
            Matcher matcher = pattern.matcher(rawJson);

            while (matcher.find()) {
                String objectContent = matcher.group(1);

                String doctorIdStr = extractJsonField(objectContent, "doctorId");
                if (doctorIdStr.isEmpty()) doctorIdStr = extractJsonField(objectContent, "doctorID");
                if (doctorIdStr.isEmpty()) doctorIdStr = extractJsonField(objectContent, "id");

                int doctorId = 1;
                try {
                    if (!doctorIdStr.isEmpty()) {
                        doctorId = Integer.parseInt(doctorIdStr);
                    }
                } catch (NumberFormatException ignored) {}

                String doctorName = extractJsonField(objectContent, "doctorName");
                if (doctorName.isEmpty()) doctorName = extractJsonField(objectContent, "doctor");
                if (doctorName.isEmpty()) doctorName = extractJsonField(objectContent, "name");

                String specialization = extractJsonField(objectContent, "specialization");
                if (specialization.isEmpty()) specialization = extractJsonField(objectContent, "department");

                String date = extractJsonField(objectContent, "date");
                if (date.isEmpty()) date = extractJsonField(objectContent, "scheduleDate");

                String time = extractJsonField(objectContent, "availableTime");
                if (time.isEmpty()) time = extractJsonField(objectContent, "time");

                if (!doctorName.isEmpty() || !date.isEmpty()) {
                    availabilities.add(new DoctorAvailability(
                            doctorId,
                            doctorName.isEmpty() ? "Unknown Doctor" : doctorName,
                            specialization.isEmpty() ? "General" : specialization,
                            date.isEmpty() ? "N/A" : date,
                            time.isEmpty() ? "N/A" : time
                    ));
                }
            }

            refreshAvailabilityTable();
            populateBookingSlots();

            JOptionPane.showMessageDialog(this,
                    "Successfully loaded " + availabilities.size() + " doctor schedules from RMI Server!",
                    "RMI Sync Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to fetch or parse RMI JSON data:\n" + e.getMessage(),
                    "RMI Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String extractJsonField(String jsonBlock, String key) {
        Pattern pattern = Pattern.compile("(?i)\\b\"?" + key + "\"?\\s*:\\s*(?:\"([^\"]*)\"|([^,}]*))");
        Matcher matcher = pattern.matcher(jsonBlock);
        if (matcher.find()) {
            String val1 = matcher.group(1);
            String val2 = matcher.group(2);
            String result = (val1 != null) ? val1.trim() : (val2 != null ? val2.trim() : "");
            return result;
        }
        return "";
    }

    private void setupFrame() {
        setTitle("Patient Medical Portal - Client");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        add(createSidebarPanel(), BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentCardPanel = new JPanel(cardLayout);

        contentCardPanel.add(createDashboardPanel(), PAGE_DASHBOARD);
        contentCardPanel.add(createInfoPage(), PAGE_INFO);
        contentCardPanel.add(createAvailabilityPage(), PAGE_AVAILABILITY);
        contentCardPanel.add(createBookingPage(), PAGE_BOOKING);
        contentCardPanel.add(createSchedulePage(), PAGE_SCHEDULE);
        contentCardPanel.add(createHistoryPage(), PAGE_HISTORY);

        add(contentCardPanel, BorderLayout.CENTER);
        showPage(PAGE_DASHBOARD);
    }

    private void showPage(String pageName) {
        cardLayout.show(contentCardPanel, pageName);
    }

    private String getFormattedPatientName() {
        if (currentPatient.getFirstName().isEmpty() && currentPatient.getLastName().isEmpty()) {
            return "New Patient";
        }
        return currentPatient.getFirstName() + " " + currentPatient.getLastName();
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 600));
        sidebar.setBackground(new Color(220, 225, 230));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(180, 185, 190)));

        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel lblAppName = new JLabel("PATIENT PORTAL");
        lblAppName.setForeground(COLOR_DARK_TEXT);
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandPanel.add(lblAppName, BorderLayout.NORTH);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        JPanel menuGroupPanel = new JPanel(new GridLayout(8, 1, 0, 5));
        menuGroupPanel.setOpaque(false);
        menuGroupPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JButton btnDash = createSidebarButton(" Home Dashboard");
        btnDash.addActionListener(e -> showPage(PAGE_DASHBOARD));

        JButton btnFetchRmi = createSidebarButton(" Fetch RMI Data");
        btnFetchRmi.addActionListener(e -> fetchAndParseHospitalDataFromRMI());

        JButton btnInfo = createSidebarButton(" Profile Management");
        btnInfo.addActionListener(e -> {
            txtPatientId.setText(String.valueOf(currentPatient.getPatientId()));
            txtUserId.setText(String.valueOf(currentPatient.getUserId()));
            txtFirstName.setText(currentPatient.getFirstName());
            txtLastName.setText(currentPatient.getLastName());
            txtIcPassport.setText(currentPatient.getIcPassportNo());
            txtContact.setText(currentPatient.getContactNumber());
            txtMedicalRecordId.setText(currentPatient.getMedicalRecordId());
            showPage(PAGE_INFO);
        });

        JButton btnAvail = createSidebarButton(" Doctor Schedules");
        btnAvail.addActionListener(e -> {
            refreshAvailabilityTable();
            showPage(PAGE_AVAILABILITY);
        });

        JButton btnBook = createSidebarButton(" Book Appointment");
        btnBook.addActionListener(e -> {
            populateBookingSlots();
            showPage(PAGE_BOOKING);
        });

        JButton btnSched = createSidebarButton(" Active Schedule");
        btnSched.addActionListener(e -> {
            refreshActiveScheduleTable();
            showPage(PAGE_SCHEDULE);
        });

        JButton btnHist = createSidebarButton(" History Logs");
        btnHist.addActionListener(e -> {
            refreshHistoryTable();
            showPage(PAGE_HISTORY);
        });

        JButton btnExit = createSidebarButton(" Exit System");
        btnExit.addActionListener(e -> System.exit(0));

        menuGroupPanel.add(btnDash);
        menuGroupPanel.add(btnFetchRmi);
        menuGroupPanel.add(btnInfo);
        menuGroupPanel.add(btnAvail);
        menuGroupPanel.add(btnBook);
        menuGroupPanel.add(btnSched);
        menuGroupPanel.add(btnHist);
        menuGroupPanel.add(btnExit);

        sidebar.add(menuGroupPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(COLOR_DARK_TEXT);
        btn.setBackground(new Color(200, 205, 215));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(175, 185, 200));
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 205, 215));
                btn.setForeground(COLOR_DARK_TEXT);
            }
        });
        return btn;
    }

    private JButton createPrimaryActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setBackground(bgColor);
        btn.setForeground(COLOR_DARK_TEXT);
        btn.setFocusPainted(false);
        return btn;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panel.setBackground(Color.WHITE);

        JPanel msgPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        msgPanel.setOpaque(false);

        lblWelcomeHeader = new JLabel("Welcome, " + getFormattedPatientName());
        lblWelcomeHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcomeHeader.setForeground(new Color(43, 48, 59));

        JLabel lblSubHeader = new JLabel("Select an option from the left sidebar to navigate your medical services portal.");
        lblSubHeader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubHeader.setForeground(Color.GRAY);

        msgPanel.add(lblWelcomeHeader);
        msgPanel.add(lblSubHeader);

        panel.add(msgPanel, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createInfoPage() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        page.setBackground(Color.WHITE);

        JLabel title = new JLabel("Update Personal Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        page.add(title, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(7, 2, 10, 15));
        formGrid.setOpaque(false);

        formGrid.add(new JLabel("Patient ID Reference:"));
        txtPatientId = new JTextField();
        formGrid.add(txtPatientId);

        formGrid.add(new JLabel("User ID Link Key:"));
        txtUserId = new JTextField();
        formGrid.add(txtUserId);

        formGrid.add(new JLabel("First Name:"));
        txtFirstName = new JTextField();
        formGrid.add(txtFirstName);

        formGrid.add(new JLabel("Last Name:"));
        txtLastName = new JTextField();
        formGrid.add(txtLastName);

        formGrid.add(new JLabel("IC / Passport Identifiers:"));
        txtIcPassport = new JTextField();
        formGrid.add(txtIcPassport);

        formGrid.add(new JLabel("Active Mobile Contact:"));
        txtContact = new JTextField();
        formGrid.add(txtContact);

        formGrid.add(new JLabel("Medical Record Tracking ID:"));
        txtMedicalRecordId = new JTextField();
        formGrid.add(txtMedicalRecordId);

        page.add(formGrid, BorderLayout.CENTER);

        JButton btnSave = createPrimaryActionButton("Save Profile Modifications", new Color(180, 190, 205));
        btnSave.addActionListener(e -> {
            if (txtFirstName.getText().trim().isEmpty() || txtLastName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name and Last Name are required.", "Validation Failure", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int pId = txtPatientId.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtPatientId.getText().trim());
                int uId = txtUserId.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtUserId.getText().trim());
                currentPatient.setPatientId(pId);
                currentPatient.setUserId(uId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID records must evaluate to numeric sequences.", "Data Formatter Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentPatient.setFirstName(txtFirstName.getText().trim());
            currentPatient.setLastName(txtLastName.getText().trim());
            currentPatient.setIcPassportNo(txtIcPassport.getText().trim());
            currentPatient.setContactNumber(txtContact.getText().trim());
            currentPatient.setMedicalRecordId(txtMedicalRecordId.getText().trim());

            lblWelcomeHeader.setText("Welcome, " + getFormattedPatientName());
            JOptionPane.showMessageDialog(this, "Personal setup values confirmed.", "System Message", JOptionPane.INFORMATION_MESSAGE);
            showPage(PAGE_DASHBOARD);
        });

        page.add(btnSave, BorderLayout.SOUTH);
        return page;
    }

    private JPanel createAvailabilityPage() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        page.setBackground(Color.WHITE);

        JLabel title = new JLabel("Available Care Provider Schedules");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        page.add(title, BorderLayout.NORTH);

        String[] columns = {"Doctor ID", "Doctor Name", "Specialization Department", "Calendar Date", "Work Assignment Slot"};
        availabilityModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(availabilityModel);
        page.add(new JScrollPane(table), BorderLayout.CENTER);

        return page;
    }

    private void refreshAvailabilityTable() {
        availabilityModel.setRowCount(0);
        for (DoctorAvailability da : availabilities) {
            availabilityModel.addRow(new Object[]{
                    da.getDoctorId(), da.getDoctorName(), da.getSpecialization(), da.getDate(), da.getAvailableTime()
            });
        }
    }

    private JPanel createBookingPage() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        page.setBackground(Color.WHITE);

        JLabel title = new JLabel("Book New Consult Session");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        page.add(title, BorderLayout.NORTH);

        JPanel formGrid = new JPanel(new GridLayout(2, 2, 10, 15));
        formGrid.setOpaque(false);

        formGrid.add(new JLabel("Open Session Allocations:"));
        comboSlots = new JComboBox<>();
        formGrid.add(comboSlots);

        formGrid.add(new JLabel("Reason for Visit / Symptoms:"));
        txtReason = new JTextField();
        formGrid.add(txtReason);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(formGrid, BorderLayout.NORTH);

        page.add(centerWrapper, BorderLayout.CENTER);

        JButton btnConfirm = createPrimaryActionButton("Confirm Calendar Booking Reservation", new Color(180, 190, 205));
        btnConfirm.addActionListener(e -> {
            int selectedIndex = comboSlots.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this, "No valid opening choices are currently available.", "State Alert", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String reasonText = txtReason.getText().trim();
            if (reasonText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason for the appointment.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DoctorAvailability slot = availabilities.get(selectedIndex);
            int pId = currentPatient.getPatientId();
            int docId = slot.getDoctorId();
            String dateTimeVal = slot.getDate() + " " + slot.getAvailableTime();
            String timeVal = slot.getAvailableTime();

            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 1234);
                HospitalService service = (HospitalService) registry.lookup("HospitalService");

                boolean isSuccess = service.postAppointmentToApi(
                        docId,
                        pId,
                        slot.getDoctorName() + " (" + slot.getSpecialization() + ")",
                        dateTimeVal,
                        timeVal,
                        reasonText
                );

                if (!isSuccess) {
                    JOptionPane.showMessageDialog(this, "REST API returned failure on POST request.", "API Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "RMI Error: Could not connect to Server for POST request.\n" + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int newLocalAptId = appointments.size() + 1;
            Appointment newApt = new Appointment(
                    newLocalAptId,
                    docId,
                    pId,
                    slot.getDoctorName() + " (" + slot.getSpecialization() + ")",
                    dateTimeVal,
                    timeVal,
                    reasonText,
                    "active"
            );
            appointments.add(newApt);
            availabilities.remove(selectedIndex);

            txtReason.setText("");
            populateBookingSlots();
            JOptionPane.showMessageDialog(this, "POST Success! Appointment saved to backend REST API.", "Confirmation Success", JOptionPane.INFORMATION_MESSAGE);
            showPage(PAGE_DASHBOARD);
        });

        page.add(btnConfirm, BorderLayout.SOUTH);
        return page;
    }

    private void populateBookingSlots() {
        comboSlots.removeAllItems();
        for (DoctorAvailability da : availabilities) {
            comboSlots.addItem("[" + da.getDoctorId() + "] " + da.getDoctorName() + " (" + da.getSpecialization() + ") - " + da.getDate() + " at " + da.getAvailableTime());
        }
    }

    private JPanel createSchedulePage() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        page.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Current Pending Consultations");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        JButton btnFetchAppointments = new JButton("Fetch Remote Appointments");
        btnFetchAppointments.addActionListener(e -> fetchAndParseAppointmentsFromRMI());
        headerPanel.add(btnFetchAppointments, BorderLayout.EAST);

        page.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Appointment ID", "Doctor ID", "Patient ID", "Practitioner / Specialization", "Date Time", "Time", "Reason", "Status Info"};
        activeScheduleModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(activeScheduleModel);
        page.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnCancel = createPrimaryActionButton("Cancel Selected Appointment", new Color(240, 150, 150));
        btnCancel.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Highlight an active record row to cancel.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int aptId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString().trim());
            String doctorName = table.getValueAt(selectedRow, 3).toString();
            String dtVal = table.getValueAt(selectedRow, 4).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel appointment ID " + aptId + " with " + doctorName + " on " + dtVal + "?", "Verify Action Drop", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                for (Appointment apt : appointments) {
                    if (apt.getAppointmentId() == aptId) {
                        apt.setStatus("Cancelled");
                        break;
                    }
                }
                refreshActiveScheduleTable();
                refreshHistoryTable();
                JOptionPane.showMessageDialog(this, "Appointment cancellation processing done.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        page.add(btnCancel, BorderLayout.SOUTH);
        return page;
    }

    private void refreshActiveScheduleTable() {
        activeScheduleModel.setRowCount(0);
        // Displays ALL records fetched from the database on Schedule Page
        for (Appointment apt : appointments) {
            activeScheduleModel.addRow(new Object[]{
                    apt.getAppointmentId(), apt.getDoctorId(), apt.getPatientId(), apt.getDoctorName(), apt.getDateTime(), apt.getTime(), apt.getReason(), apt.getStatus()
            });
        }
    }

    private JPanel createHistoryPage() {
        JPanel page = new JPanel(new BorderLayout(15, 15));
        page.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        page.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Archived Encounter Log Records");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.WEST);

        JButton btnFetchAppointments = new JButton("Fetch Remote Appointments");
        btnFetchAppointments.addActionListener(e -> fetchAndParseAppointmentsFromRMI());
        headerPanel.add(btnFetchAppointments, BorderLayout.EAST);

        page.add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Appointment ID", "Doctor ID", "Patient ID", "Care Provider Unit", "Date Time", "Time", "Reason", "Resolution State"};
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(historyModel);
        page.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnDelete = createPrimaryActionButton("Delete History Record", new Color(240, 150, 150));
        btnDelete.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Highlight an archived record row to delete.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int aptId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString().trim());

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to permanently delete appointment ID " + aptId + " from history and backend REST API?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Registry registry = LocateRegistry.getRegistry("localhost", 1234);
                    HospitalService service = (HospitalService) registry.lookup("HospitalService");

                    boolean isSuccess = service.deleteAppointmentFromApi(aptId);

                    if (!isSuccess) {
                        JOptionPane.showMessageDialog(this, "REST API returned failure on DELETE request for appointment ID: " + aptId, "API Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "RMI Connection Error on DELETE:\n" + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                appointments.removeIf(apt -> apt.getAppointmentId() == aptId);
                refreshHistoryTable();
                refreshActiveScheduleTable();
                JOptionPane.showMessageDialog(this, "Appointment ID " + aptId + " successfully deleted from REST API and local state.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        page.add(btnDelete, BorderLayout.SOUTH);
        return page;
    }

    private void refreshHistoryTable() {
        historyModel.setRowCount(0);
        // Displays ALL records fetched from the database on History Page
        for (Appointment apt : appointments) {
            historyModel.addRow(new Object[]{
                    apt.getAppointmentId(), apt.getDoctorId(), apt.getPatientId(), apt.getDoctorName(), apt.getDateTime(), apt.getTime(), apt.getReason(), apt.getStatus()
            });
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new Client().setVisible(true);
        });
    }
}