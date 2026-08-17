package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.gateway.UserSummary;
import brightcare.model.Report;
import brightcare.security.PermissionChecker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class PatientVisitSummaryFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public PatientVisitSummaryFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        loadPatients();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        patientsScrollPane = new javax.swing.JScrollPane();
        patientsTable = new javax.swing.JTable();
        patientIdLabel = new javax.swing.JLabel();
        patientIdField = new javax.swing.JTextField();
        generateButton = new javax.swing.JButton();
        resultScrollPane = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Patient Visit Summary");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Patient Visit Summary");

        patientsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Patient ID", "User ID", "Username", "Status"}
        ) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        patientsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        patientsTable.getSelectionModel().addListSelectionListener(this::patientsTableSelectionChanged);
        patientsScrollPane.setViewportView(patientsTable);

        patientIdLabel.setText("Selected Patient ID:");

        patientIdField.setEditable(false);

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
                    .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                    .addComponent(patientsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 820, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(patientIdLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(patientIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addComponent(patientsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(patientIdLabel)
                    .addComponent(patientIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generateButton))
                .addGap(18, 18, 18)
                .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        if (patientIdField.getText().trim().length() == 0) {
            resultArea.setText("Select a patient from the table first.");
            return;
        }
        Report report = controller.generatePatientVisitSummary(Integer.parseInt(patientIdField.getText()));
        resultArea.setText(formatReport(report));
        resultArea.setCaretPosition(0);
    }//GEN-LAST:event_generateButtonActionPerformed

    private void patientsTableSelectionChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting() || patientsTable.getSelectedRow() < 0) {
            return;
        }
        int modelRow = patientsTable.convertRowIndexToModel(patientsTable.getSelectedRow());
        Object patientId = patientsTable.getModel().getValueAt(modelRow, 0);
        patientIdField.setText(patientId == null ? "" : patientId.toString());
    }

    public AdminController getController() {
        return controller;
    }

    private void loadPatients() {
        DefaultTableModel model = (DefaultTableModel) patientsTable.getModel();
        model.setRowCount(0);
        for (UserSummary user : controller.getUsers()) {
            if (!PermissionChecker.ROLE_PATIENT.equalsIgnoreCase(user.getRole())) {
                continue;
            }
            int patientId = user.getRoleId() > 0 ? user.getRoleId() : user.getUserId();
            model.addRow(new Object[] {patientId, user.getUserId(), user.getUsername(), user.getStatus()});
        }
        if (model.getRowCount() > 0) {
            patientsTable.setRowSelectionInterval(0, 0);
        }
    }

    private String formatReport(Report report) {
        return ReportDisplayHelper.formatReport(report);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateButton;
    private javax.swing.JTextField patientIdField;
    private javax.swing.JLabel patientIdLabel;
    private javax.swing.JScrollPane patientsScrollPane;
    private javax.swing.JTable patientsTable;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
