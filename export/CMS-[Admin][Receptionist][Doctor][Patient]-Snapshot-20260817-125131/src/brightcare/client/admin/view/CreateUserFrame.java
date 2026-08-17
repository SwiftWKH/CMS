package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.model.UserAccount;
import brightcare.security.PermissionChecker;
import javax.swing.JOptionPane;

public class CreateUserFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public CreateUserFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        roleLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        roleComboBox = new javax.swing.JComboBox<>();
        roleIdInfoLabel = new javax.swing.JLabel();
        createButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Create User");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        titleLabel.setText("Create User");

        usernameLabel.setText("Username:");
        passwordLabel.setText("Password:");
        roleLabel.setText("Role:");

        roleComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] {
            PermissionChecker.ROLE_ADMIN,
            PermissionChecker.ROLE_DOCTOR,
            PermissionChecker.ROLE_RECEPTIONIST,
            PermissionChecker.ROLE_PATIENT
        }));

        roleIdInfoLabel.setText("Role ID is assigned automatically by the API.");

        createButton.setText("Create");
        createButton.addActionListener(this::createButtonActionPerformed);

        resetButton.setText("Reset");
        resetButton.addActionListener(this::resetButtonActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(usernameLabel)
                            .addComponent(passwordLabel)
                            .addComponent(roleLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(usernameField)
                            .addComponent(passwordField)
                            .addComponent(roleComboBox, 0, 260, Short.MAX_VALUE)
                            .addComponent(roleIdInfoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(createButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetButton)))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roleLabel)
                    .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roleIdInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createButton)
                    .addComponent(resetButton))
                .addContainerGap(252, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        if (isBlank(usernameField.getText())) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Password is required.");
            return;
        }

        UserAccount created = controller.createUser(
                usernameField.getText(),
                new String(passwordField.getPassword()),
                String.valueOf(roleComboBox.getSelectedItem())
        );
        if (created == null) {
            JOptionPane.showMessageDialog(this,
                    "User was not created. Check for blank fields, duplicate username, or server errors.");
            return;
        }

        JOptionPane.showMessageDialog(this, createdMessage(created));
    }//GEN-LAST:event_createButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
    }//GEN-LAST:event_resetButtonActionPerformed

    public AdminController getController() {
        return controller;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    private String createdMessage(UserAccount account) {
        StringBuilder message = new StringBuilder();
        message.append("User created.");
        if (account.getUserId() > 0) {
            message.append(System.lineSeparator()).append("User ID: ").append(account.getUserId());
        }
        if (account.getRoleId() > 0) {
            message.append(System.lineSeparator()).append("Role ID: ").append(account.getRoleId());
        } else {
            message.append(System.lineSeparator()).append("Role ID will be assigned by the role table.");
        }
        return message.toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton resetButton;
    private javax.swing.JComboBox<String> roleComboBox;
    private javax.swing.JLabel roleIdInfoLabel;
    private javax.swing.JLabel roleLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JLabel usernameLabel;
    // End of variables declaration//GEN-END:variables
}
