package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.gateway.SessionSummary;
import javax.swing.table.DefaultTableModel;

public class ActiveSessionsFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public ActiveSessionsFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        loadSessions();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        sessionsScrollPane = new javax.swing.JScrollPane();
        sessionsTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Active Sessions");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Active Sessions");

        sessionsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] {"Username", "Login Time", "Role"}
        ));
        sessionsScrollPane.setViewportView(sessionsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sessionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addComponent(sessionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public AdminController getController() {
        return controller;
    }

    private void loadSessions() {
        DefaultTableModel model = (DefaultTableModel) sessionsTable.getModel();
        model.setRowCount(0);
        for (SessionSummary session : controller.getActiveSessions()) {
            model.addRow(new Object[] {session.getUsername(), session.getLoginTime(), session.getRole()});
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane sessionsScrollPane;
    private javax.swing.JTable sessionsTable;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
}
