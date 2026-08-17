package brightcare.client.common.view;

import brightcare.client.common.controller.LoginController;
import brightcare.client.common.controller.LoginController.LoginResult;
import javax.swing.JOptionPane;
import java.util.prefs.Preferences;

public class LoginFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private static final String PREF_USERNAME = "brightcare.login.username";
    private static final String PREF_PASSWORD = "brightcare.login.password";
    private static final String PREF_REMEMBER = "brightcare.login.remember";
    private static final String PREF_SERVER_HOST = "brightcare.login.serverHost";

    private final LoginController controller;
    private final Preferences preferences = Preferences.userNodeForPackage(LoginFrame.class);

    public LoginFrame() {
        this(new LoginController());
    }

    public LoginFrame(LoginController controller) {
        this.controller = controller;
        initComponents();
        loadRememberedCredentials();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        serverHostLabel = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JPasswordField();
        serverHostField = new javax.swing.JTextField();
        rememberCheckBox = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BrightCare Login");

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setText("LOGIN");

        jLabel2.setText("Username:");

        jLabel3.setText("Password:");

        serverHostLabel.setText("Server:");

        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jTextField2.addActionListener(this::jTextField2ActionPerformed);

        serverHostField.addActionListener(this::serverHostFieldActionPerformed);

        rememberCheckBox.setText("Remember username and password");

        jButton1.setText("Sign in");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton1)
                            .addComponent(rememberCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(serverHostLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                                    .addComponent(jTextField2)
                                    .addComponent(serverHostField)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(213, 213, 213)
                        .addComponent(jLabel1)))
                .addContainerGap(88, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverHostLabel)
                    .addComponent(serverHostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rememberCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(139, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        attemptLogin();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        attemptLogin();
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void serverHostFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverHostFieldActionPerformed
        attemptLogin();
    }//GEN-LAST:event_serverHostFieldActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        attemptLogin();
    }//GEN-LAST:event_jButton1ActionPerformed

    public LoginController getController() {
        return controller;
    }

    private void attemptLogin() {
        String username = jTextField1.getText();
        String password = new String(jTextField2.getPassword());
        String serverHost = serverHostField.getText();
        if (isBlank(serverHost)) {
            JOptionPane.showMessageDialog(this, "Server host is required.");
            return;
        }
        saveServerHost(serverHost);
        if (!controller.connectToServer(serverHost)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to connect to BrightCare server at " + serverHost.trim() + ".",
                    "Server Connection Failed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        LoginResult result = controller.login(username, password);
        if (result.isSuccessful()) {
            saveRememberedCredentials(username, password);
            controller.openRoleFrame(result.getUserAccount(), this);
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                result.getMessage(),
                "Login Failed",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void loadRememberedCredentials() {
        serverHostField.setText(preferences.get(PREF_SERVER_HOST,
                System.getProperty("brightcare.rmi.host", "localhost")));
        boolean remember = preferences.getBoolean(PREF_REMEMBER, false);
        rememberCheckBox.setSelected(remember);
        if (remember) {
            jTextField1.setText(preferences.get(PREF_USERNAME, ""));
            jTextField2.setText(preferences.get(PREF_PASSWORD, ""));
        }
    }

    private void saveRememberedCredentials(String username, String password) {
        if (rememberCheckBox.isSelected()) {
            preferences.put(PREF_USERNAME, username);
            preferences.put(PREF_PASSWORD, password);
            preferences.putBoolean(PREF_REMEMBER, true);
            return;
        }
        preferences.remove(PREF_USERNAME);
        preferences.remove(PREF_PASSWORD);
        preferences.putBoolean(PREF_REMEMBER, false);
    }

    private void saveServerHost(String serverHost) {
        String trimmed = serverHost == null || serverHost.trim().length() == 0 ? "localhost" : serverHost.trim();
        preferences.put(PREF_SERVER_HOST, trimmed);
        System.setProperty("brightcare.rmi.host", trimmed);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPasswordField jTextField2;
    private javax.swing.JCheckBox rememberCheckBox;
    private javax.swing.JTextField serverHostField;
    private javax.swing.JLabel serverHostLabel;
    // End of variables declaration//GEN-END:variables
}
