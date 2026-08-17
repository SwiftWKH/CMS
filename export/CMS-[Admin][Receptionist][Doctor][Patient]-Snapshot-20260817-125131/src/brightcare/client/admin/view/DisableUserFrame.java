package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import javax.swing.JOptionPane;

public class DisableUserFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public DisableUserFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        disableButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Disable User");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Disable User");

        usernameLabel.setText("Username:");

        disableButton.setText("Disable");
        disableButton.addActionListener(this::disableButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(usernameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(disableButton)
                            .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(350, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usernameLabel)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(disableButton)
                .addContainerGap(320, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void disableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disableButtonActionPerformed
        if (isBlank(usernameField.getText())) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }

        boolean disabled = controller.disableUser(usernameField.getText());
        JOptionPane.showMessageDialog(this,
                disabled ? "User disabled." : "User was not disabled. Check the username or server errors.");
    }//GEN-LAST:event_disableButtonActionPerformed

    public AdminController getController() {
        return controller;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton disableButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
