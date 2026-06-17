package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.model.Report;

public class MonthlyAppointmentReportFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public MonthlyAppointmentReportFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        monthLabel = new javax.swing.JLabel();
        monthField = new javax.swing.JTextField();
        yearLabel = new javax.swing.JLabel();
        yearField = new javax.swing.JTextField();
        generateButton = new javax.swing.JButton();
        resultScrollPane = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Monthly Appointment Report");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Monthly Appointment Report");

        monthLabel.setText("Month:");

        yearLabel.setText("Year:");

        generateButton.setText("Generate");
        generateButton.addActionListener(this::generateButtonActionPerformed);

        resultArea.setColumns(20);
        resultArea.setEditable(false);
        resultArea.setRows(5);
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
                        .addComponent(monthLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(monthField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(yearLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(monthLabel)
                    .addComponent(monthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yearLabel)
                    .addComponent(yearField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generateButton))
                .addGap(18, 18, 18)
                .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        try {
            Report report = controller.generateMonthlyAppointmentReport(
                    Integer.parseInt(monthField.getText()),
                    Integer.parseInt(yearField.getText())
            );
            resultArea.setText(formatReport(report));
        } catch (NumberFormatException ex) {
            resultArea.setText("Month and year must be numbers.");
        }
    }//GEN-LAST:event_generateButtonActionPerformed

    public AdminController getController() {
        return controller;
    }

    private String formatReport(Report report) {
        return "Report Type: " + report.getReportType() + System.lineSeparator()
                + "Generated At: " + report.getGeneratedAt() + System.lineSeparator()
                + "File Path: " + report.getFilePath();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateButton;
    private javax.swing.JTextField monthField;
    private javax.swing.JLabel monthLabel;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField yearField;
    private javax.swing.JLabel yearLabel;
    // End of variables declaration//GEN-END:variables
}
