package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.model.Report;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

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
        reportMonthLabel = new javax.swing.JLabel();
        reportMonthSpinner = new javax.swing.JSpinner();
        generateButton = new javax.swing.JButton();
        resultScrollPane = new javax.swing.JScrollPane();
        resultArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Monthly Appointment Report");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Monthly Appointment Report");

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
                    .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 752, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(reportMonthLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reportMonthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(reportMonthLabel)
                    .addComponent(reportMonthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generateButton))
                .addGap(18, 18, 18)
                .addComponent(resultScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void generateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateButtonActionPerformed
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) reportMonthSpinner.getValue());
        Report report = controller.generateMonthlyAppointmentReport(
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );
        resultArea.setText(formatReport(report));
        resultArea.setCaretPosition(0);
    }//GEN-LAST:event_generateButtonActionPerformed

    public AdminController getController() {
        return controller;
    }

    private String formatReport(Report report) {
        return ReportDisplayHelper.formatReport(report);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton generateButton;
    private javax.swing.JLabel reportMonthLabel;
    private javax.swing.JSpinner reportMonthSpinner;
    private javax.swing.JTextArea resultArea;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
