package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.gateway.UserSummary;
import brightcare.model.Report;
import brightcare.security.PermissionChecker;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class DoctorConsultationReportFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public DoctorConsultationReportFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        loadDoctors();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        doctorsScrollPane = new javax.swing.JScrollPane();
        doctorsTable = new javax.swing.JTable();
        doctorIdLabel = new javax.swing.JLabel();
        doctorIdField = new javax.swing.JTextField();
        reportMonthLabel = new javax.swing.JLabel();
        reportMonthSpinner = new javax.swing.JSpinner();
        generateButton = new javax.swing.JButton();
        resultScrollPane = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Doctor Consultation Report");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18));
        titleLabel.setText("Doctor Consultation Report");

        doctorsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Doctor ID", "User ID", "Username", "Status"}) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        doctorsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        doctorsTable.getSelectionModel().addListSelectionListener(this::doctorsTableSelectionChanged);
        doctorsScrollPane.setViewportView(doctorsTable);

        doctorIdLabel.setText("Selected Doctor ID:");
        doctorIdField.setEditable(false);

        reportMonthLabel.setText("Report Month:");
        reportMonthSpinner.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.MONTH));
        reportMonthSpinner.setEditor(new JSpinner.DateEditor(reportMonthSpinner, "MMMM yyyy"));

        generateButton.setText("Generate");
        generateButton.addActionListener(this::generateButtonActionPerformed);

        resultArea.setColumns(20);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setRows(5);
        resultArea.setWrapStyleWord(true);
        resultScrollPane.setViewportView(resultArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 820,
                                                Short.MAX_VALUE)
                                        .addComponent(doctorsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 820,
                                                Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(titleLabel)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(doctorIdLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(doctorIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(reportMonthLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(reportMonthSpinner,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 170,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(generateButton)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(titleLabel)
                                .addGap(12, 12, 12)
                                .addComponent(doctorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(doctorIdLabel)
                                        .addComponent(doctorIdField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(reportMonthLabel)
                                        .addComponent(reportMonthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(generateButton))
                                .addGap(18, 18, 18)
                                .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300,
                                        Short.MAX_VALUE)
                                .addGap(24, 24, 24))
        );

        pack();
    }

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (doctorIdField.getText().trim().length() == 0) {
            resultArea.setText("Select a doctor from the table first.");
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) reportMonthSpinner.getValue());
        Report report = controller.generateDoctorConsultationReport(
                Integer.parseInt(doctorIdField.getText()),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );
        resultArea.setText(formatReport(report));
        resultArea.setCaretPosition(0);
    }

    private void doctorsTableSelectionChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting() || doctorsTable.getSelectedRow() < 0) {
            return;
        }
        int modelRow = doctorsTable.convertRowIndexToModel(doctorsTable.getSelectedRow());
        Object doctorId = doctorsTable.getModel().getValueAt(modelRow, 0);
        doctorIdField.setText(doctorId == null ? "" : doctorId.toString());
    }

    public AdminController getController() {
        return controller;
    }

    private void loadDoctors() {
        DefaultTableModel model = (DefaultTableModel) doctorsTable.getModel();
        model.setRowCount(0);
        for (UserSummary user : controller.getUsers()) {
            if (!PermissionChecker.ROLE_DOCTOR.equalsIgnoreCase(user.getRole())) {
                continue;
            }
            int doctorId = user.getRoleId() > 0 ? user.getRoleId() : user.getUserId();
            model.addRow(new Object[] {doctorId, user.getUserId(), user.getUsername(), user.getStatus()});
        }
        if (model.getRowCount() > 0) {
            doctorsTable.setRowSelectionInterval(0, 0);
        }
    }

    private String formatReport(Report report) {
        return ReportDisplayHelper.formatReport(report);
    }

    private javax.swing.JTextField doctorIdField;
    private javax.swing.JLabel doctorIdLabel;
    private javax.swing.JScrollPane doctorsScrollPane;
    private javax.swing.JTable doctorsTable;
    private javax.swing.JButton generateButton;
    private javax.swing.JLabel reportMonthLabel;
    private javax.swing.JSpinner reportMonthSpinner;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JLabel titleLabel;
}
