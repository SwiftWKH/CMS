package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.model.Report;

public class PatientVisitSummaryFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public PatientVisitSummaryFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        patientIdLabel = new javax.swing.JLabel();
        patientIdField = new javax.swing.JTextField();
        generateButton = new javax.swing.JButton();
        resultScrollPane = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Patient Visit Summary");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Patient Visit Summary");

        patientIdLabel.setText("Patient ID:");

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
                    .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
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
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(patientIdLabel)
                    .addComponent(patientIdField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generateButton))
                .addGap(18, 18, 18)
                .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        try {
            Report report = controller.generatePatientVisitSummary(Integer.parseInt(patientIdField.getText()));
            resultArea.setText(formatReport(report));
            resultArea.setCaretPosition(0);
        } catch (NumberFormatException ex) {
            resultArea.setText("Patient ID must be a number.");
        }
    }//GEN-LAST:event_generateButtonActionPerformed

    public AdminController getController() {
        return controller;
    }

    private String formatReport(Report report) {
        return ReportDisplayHelper.formatReport(report);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateButton;
    private javax.swing.JTextField patientIdField;
    private javax.swing.JLabel patientIdLabel;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
