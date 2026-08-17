package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.gateway.UserSummary;
import brightcare.model.UserAccount;
import brightcare.model.UserProfileInput;
import brightcare.security.PermissionChecker;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class ViewUsersFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;
    private static final String ROLE_ALL = "ALL";

    private final AdminController controller;
    private List<UserSummary> users = new ArrayList<UserSummary>();

    public ViewUsersFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        loadUsers();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        roleFilterLabel = new javax.swing.JLabel();
        roleFilterComboBox = new javax.swing.JComboBox<String>();
        refreshButton = new javax.swing.JButton();
        usersScrollPane = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();
        userActionsTabbedPane = new javax.swing.JTabbedPane();

        createPanel = new javax.swing.JPanel();
        createUsernameLabel = new javax.swing.JLabel();
        createUsernameField = new javax.swing.JTextField();
        createPasswordLabel = new javax.swing.JLabel();
        createPasswordField = new javax.swing.JTextField();
        createRoleLabel = new javax.swing.JLabel();
        createRoleComboBox = new javax.swing.JComboBox<String>();
        optionalHeaderLabel = new javax.swing.JLabel();
        optionalCardsPanel = new javax.swing.JPanel();
        noOptionalPanel = new javax.swing.JPanel();
        noOptionalLabel = new javax.swing.JLabel();
        doctorOptionalPanel = new javax.swing.JPanel();
        doctorNameLabel = new javax.swing.JLabel();
        doctorNameField = new javax.swing.JTextField();
        specializationLabel = new javax.swing.JLabel();
        specializationField = new javax.swing.JTextField();
        doctorContactLabel = new javax.swing.JLabel();
        doctorContactField = new javax.swing.JTextField();
        patientOptionalPanel = new javax.swing.JPanel();
        patientFirstNameLabel = new javax.swing.JLabel();
        patientFirstNameField = new javax.swing.JTextField();
        patientLastNameLabel = new javax.swing.JLabel();
        patientLastNameField = new javax.swing.JTextField();
        patientIcLabel = new javax.swing.JLabel();
        patientIcField = new javax.swing.JTextField();
        patientContactLabel = new javax.swing.JLabel();
        patientContactField = new javax.swing.JTextField();
        createButton = new javax.swing.JButton();
        resetCreateButton = new javax.swing.JButton();

        updatePanel = new javax.swing.JPanel();
        updateInfoLabel = new javax.swing.JLabel();
        updateUserIdLabel = new javax.swing.JLabel();
        updateUserIdField = new javax.swing.JTextField();
        updateUsernameLabel = new javax.swing.JLabel();
        updateUsernameField = new javax.swing.JTextField();
        updatePasswordLabel = new javax.swing.JLabel();
        updatePasswordField = new javax.swing.JTextField();
        updateRoleLabel = new javax.swing.JLabel();
        updateRoleComboBox = new javax.swing.JComboBox<String>();
        updateStatusLabel = new javax.swing.JLabel();
        updateStatusComboBox = new javax.swing.JComboBox<String>();
        updateButton = new javax.swing.JButton();
        resetUpdateButton = new javax.swing.JButton();

        disablePanel = new javax.swing.JPanel();
        disableInfoLabel = new javax.swing.JLabel();
        disableUsernameLabel = new javax.swing.JLabel();
        disableUsernameField = new javax.swing.JTextField();
        disableButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("User Management");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18));
        titleLabel.setText("User Management");
        roleFilterLabel.setText("Role:");

        roleFilterComboBox.setModel(roleModel(true));
        roleFilterComboBox.addActionListener(this::roleFilterComboBoxActionPerformed);

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(this::refreshButtonActionPerformed);

        usersTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"User ID", "Username", "Role", "Role ID", "Status"}) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        usersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        usersTable.getSelectionModel().addListSelectionListener(this::usersTableSelectionChanged);
        usersScrollPane.setViewportView(usersTable);

        createUsernameLabel.setText("Username:");
        createPasswordLabel.setText("Password:");
        createRoleLabel.setText("Role:");
        createRoleComboBox.setModel(roleModel(false));
        createRoleComboBox.addActionListener(this::createRoleComboBoxActionPerformed);

        optionalHeaderLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        optionalHeaderLabel.setText("Optional role profile fields");
        optionalCardsPanel.setLayout(new CardLayout());
        noOptionalLabel.setText("No additional profile fields are needed for this role.");
        noOptionalPanel.add(noOptionalLabel);
        optionalCardsPanel.add(noOptionalPanel, "NONE");
        buildDoctorOptionalPanel();
        buildPatientOptionalPanel();

        createButton.setText("Create User");
        createButton.addActionListener(this::createButtonActionPerformed);
        resetCreateButton.setText("Reset");
        resetCreateButton.addActionListener(this::resetCreateButtonActionPerformed);
        buildCreatePanel();

        updateInfoLabel.setText("Select a user from the table. New password is optional and is saved hashed.");
        updateUserIdLabel.setText("User ID:");
        updateUserIdField.setEditable(false);
        updateUsernameLabel.setText("Username:");
        updatePasswordLabel.setText("New Password:");
        updateRoleLabel.setText("Role:");
        updateRoleComboBox.setModel(roleModel(false));
        updateStatusLabel.setText("Status:");
        updateStatusComboBox.setModel(new javax.swing.DefaultComboBoxModel<String>(
                new String[] {"ACTIVE", "DISABLED"}));
        updateButton.setText("Update User");
        updateButton.addActionListener(this::updateButtonActionPerformed);
        resetUpdateButton.setText("Clear");
        resetUpdateButton.addActionListener(this::resetUpdateButtonActionPerformed);
        buildUpdatePanel();

        disableInfoLabel.setText("Select a user from the table or type a username.");
        disableUsernameLabel.setText("Username:");
        disableButton.setText("Disable User");
        disableButton.addActionListener(this::disableButtonActionPerformed);
        buildDisablePanel();

        userActionsTabbedPane.addTab("Create User", createPanel);
        userActionsTabbedPane.addTab("Update User", updatePanel);
        userActionsTabbedPane.addTab("Disable User", disablePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(usersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 980, Short.MAX_VALUE)
                                        .addComponent(userActionsTabbedPane)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(titleLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(roleFilterLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(roleFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(refreshButton)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(titleLabel)
                                        .addComponent(roleFilterLabel)
                                        .addComponent(roleFilterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(refreshButton))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(usersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userActionsTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        showOptionalFieldsForSelectedRole();
        pack();
    }

    private javax.swing.DefaultComboBoxModel<String> roleModel(boolean includeAll) {
        if (includeAll) {
            return new javax.swing.DefaultComboBoxModel<String>(new String[] {
                ROLE_ALL,
                PermissionChecker.ROLE_ADMIN,
                PermissionChecker.ROLE_DOCTOR,
                PermissionChecker.ROLE_RECEPTIONIST,
                PermissionChecker.ROLE_PATIENT
            });
        }
        return new javax.swing.DefaultComboBoxModel<String>(new String[] {
            PermissionChecker.ROLE_ADMIN,
            PermissionChecker.ROLE_DOCTOR,
            PermissionChecker.ROLE_RECEPTIONIST,
            PermissionChecker.ROLE_PATIENT
        });
    }

    private void buildDoctorOptionalPanel() {
        doctorNameLabel.setText("Doctor Name:");
        specializationLabel.setText("Specialization:");
        doctorContactLabel.setText("Contact Number:");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(doctorOptionalPanel);
        doctorOptionalPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(doctorNameLabel)
                        .addComponent(specializationLabel)
                        .addComponent(doctorContactLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(doctorNameField)
                        .addComponent(specializationField)
                        .addComponent(doctorContactField))
                .addContainerGap());
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(doctorNameLabel)
                        .addComponent(doctorNameField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(specializationLabel)
                        .addComponent(specializationField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(doctorContactLabel)
                        .addComponent(doctorContactField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap());
        optionalCardsPanel.add(doctorOptionalPanel, PermissionChecker.ROLE_DOCTOR);
    }

    private void buildPatientOptionalPanel() {
        patientFirstNameLabel.setText("First Name:");
        patientLastNameLabel.setText("Last Name:");
        patientIcLabel.setText("IC/Passport:");
        patientContactLabel.setText("Contact Number:");
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(patientOptionalPanel);
        patientOptionalPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(patientFirstNameLabel)
                        .addComponent(patientLastNameLabel)
                        .addComponent(patientIcLabel)
                        .addComponent(patientContactLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(patientFirstNameField)
                        .addComponent(patientLastNameField)
                        .addComponent(patientIcField)
                        .addComponent(patientContactField))
                .addContainerGap());
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(patientFirstNameLabel)
                        .addComponent(patientFirstNameField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(patientLastNameLabel)
                        .addComponent(patientLastNameField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(patientIcLabel)
                        .addComponent(patientIcField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(patientContactLabel)
                        .addComponent(patientContactField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap());
        optionalCardsPanel.add(patientOptionalPanel, PermissionChecker.ROLE_PATIENT);
    }

    private void buildCreatePanel() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(createPanel);
        createPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(createUsernameLabel)
                        .addComponent(createPasswordLabel)
                        .addComponent(createRoleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(createUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(createPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(createRoleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(createButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(resetCreateButton)))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(optionalHeaderLabel)
                        .addComponent(optionalCardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE))
                .addContainerGap());
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(createUsernameLabel)
                        .addComponent(createUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(optionalHeaderLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(createPasswordLabel)
                                        .addComponent(createPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(createRoleLabel)
                                        .addComponent(createRoleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(createButton)
                                        .addComponent(resetCreateButton)))
                        .addComponent(optionalCardsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }

    private void buildUpdatePanel() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(updatePanel);
        updatePanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(updateInfoLabel)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(updateUserIdLabel)
                                        .addComponent(updateUsernameLabel)
                                        .addComponent(updatePasswordLabel)
                                        .addComponent(updateRoleLabel)
                                        .addComponent(updateStatusLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(updateUserIdField, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(updateUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(updatePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(updateRoleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(updateStatusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(updateButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(resetUpdateButton)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updateInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updateUserIdLabel)
                        .addComponent(updateUserIdField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updateUsernameLabel)
                        .addComponent(updateUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updatePasswordLabel)
                        .addComponent(updatePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updateRoleLabel)
                        .addComponent(updateRoleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updateStatusLabel)
                        .addComponent(updateStatusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(updateButton)
                        .addComponent(resetUpdateButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }

    private void buildDisablePanel() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(disablePanel);
        disablePanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(disableInfoLabel)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(disableUsernameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(disableUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(disableButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(disableInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(disableUsernameLabel)
                        .addComponent(disableUsernameField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(disableButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }

    private void roleFilterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        applyUserFilter();
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
        loadUsers();
    }

    private void createRoleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        showOptionalFieldsForSelectedRole();
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (isBlank(createUsernameField.getText())) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }
        if (isBlank(createPasswordField.getText())) {
            JOptionPane.showMessageDialog(this, "Password is required.");
            return;
        }

        UserProfileInput input = new UserProfileInput();
        input.setUsername(createUsernameField.getText());
        input.setPassword(createPasswordField.getText());
        input.setRole(String.valueOf(createRoleComboBox.getSelectedItem()));
        input.setStatus("ACTIVE");
        input.setDoctorName(doctorNameField.getText());
        input.setSpecialization(specializationField.getText());
        input.setDoctorContactNumber(doctorContactField.getText());
        input.setPatientFirstName(patientFirstNameField.getText());
        input.setPatientLastName(patientLastNameField.getText());
        input.setPatientIcPassportNo(patientIcField.getText());
        input.setPatientContactNumber(patientContactField.getText());

        UserAccount created = controller.createUser(input);
        if (created == null) {
            JOptionPane.showMessageDialog(this,
                    "User was not created. Check for duplicate username or server/API errors.");
            return;
        }

        JOptionPane.showMessageDialog(this, createdMessage(created));
        clearCreateForm();
        loadUsers();
    }

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (isBlank(updateUserIdField.getText())) {
            JOptionPane.showMessageDialog(this, "Select a user to update.");
            return;
        }
        if (isBlank(updateUsernameField.getText())) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }

        UserProfileInput input = new UserProfileInput();
        input.setUserId(Integer.parseInt(updateUserIdField.getText()));
        input.setUsername(updateUsernameField.getText());
        input.setPassword(updatePasswordField.getText());
        input.setRole(String.valueOf(updateRoleComboBox.getSelectedItem()));
        input.setStatus(String.valueOf(updateStatusComboBox.getSelectedItem()));

        UserAccount updated = controller.updateUser(input);
        if (updated == null) {
            JOptionPane.showMessageDialog(this,
                    "User was not updated. Check for duplicate username or server/API errors.");
            return;
        }
        JOptionPane.showMessageDialog(this, "User updated.");
        clearUpdateForm();
        loadUsers();
    }

    private void resetCreateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        clearCreateForm();
    }

    private void resetUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        clearUpdateForm();
    }

    private void disableButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (isBlank(disableUsernameField.getText())) {
            JOptionPane.showMessageDialog(this, "Username is required.");
            return;
        }
        boolean disabled = controller.disableUser(disableUsernameField.getText());
        JOptionPane.showMessageDialog(this,
                disabled ? "User disabled." : "User was not disabled. Check the username or server/API errors.");
        loadUsers();
    }

    private void usersTableSelectionChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting() || usersTable.getSelectedRow() < 0) {
            return;
        }
        int modelRow = usersTable.convertRowIndexToModel(usersTable.getSelectedRow());
        int userId = parseInt(usersTable.getModel().getValueAt(modelRow, 0));
        UserSummary user = findUserById(userId);
        if (user == null) {
            return;
        }
        fillUpdateForm(user);
        disableUsernameField.setText(user.getUsername());
    }

    public AdminController getController() {
        return controller;
    }

    private void loadUsers() {
        users = controller.getUsers();
        applyUserFilter();
    }

    private void applyUserFilter() {
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setRowCount(0);
        String selectedRole = String.valueOf(roleFilterComboBox.getSelectedItem());
        for (UserSummary user : users) {
            if (!ROLE_ALL.equals(selectedRole) && !selectedRole.equalsIgnoreCase(user.getRole())) {
                continue;
            }
            model.addRow(new Object[] {user.getUserId(), user.getUsername(),
                user.getRole(), user.getRoleId(), user.getStatus()});
        }
    }

    private void showOptionalFieldsForSelectedRole() {
        String role = String.valueOf(createRoleComboBox.getSelectedItem());
        CardLayout layout = (CardLayout) optionalCardsPanel.getLayout();
        if (PermissionChecker.ROLE_DOCTOR.equals(role)) {
            layout.show(optionalCardsPanel, PermissionChecker.ROLE_DOCTOR);
        } else if (PermissionChecker.ROLE_PATIENT.equals(role)) {
            layout.show(optionalCardsPanel, PermissionChecker.ROLE_PATIENT);
        } else {
            layout.show(optionalCardsPanel, "NONE");
        }
    }

    private void fillUpdateForm(UserSummary user) {
        updateUserIdField.setText(String.valueOf(user.getUserId()));
        updateUsernameField.setText(user.getUsername());
        updatePasswordField.setText("");
        updateRoleComboBox.setSelectedItem(user.getRole());
        updateStatusComboBox.setSelectedItem(user.getStatus());
    }

    private UserSummary findUserById(int userId) {
        for (UserSummary user : users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    private void clearCreateForm() {
        createUsernameField.setText("");
        createPasswordField.setText("");
        createRoleComboBox.setSelectedIndex(0);
        doctorNameField.setText("");
        specializationField.setText("");
        doctorContactField.setText("");
        patientFirstNameField.setText("");
        patientLastNameField.setText("");
        patientIcField.setText("");
        patientContactField.setText("");
        showOptionalFieldsForSelectedRole();
    }

    private void clearUpdateForm() {
        updateUserIdField.setText("");
        updateUsernameField.setText("");
        updatePasswordField.setText("");
        updateRoleComboBox.setSelectedIndex(0);
        updateStatusComboBox.setSelectedIndex(0);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    private int parseInt(Object value) {
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private String createdMessage(UserAccount account) {
        StringBuilder message = new StringBuilder();
        message.append("User created.");
        if (account.getUserId() > 0) {
            message.append(System.lineSeparator()).append("User ID: ").append(account.getUserId());
        }
        if (account.getRoleId() > 0) {
            message.append(System.lineSeparator()).append("Role ID: ").append(account.getRoleId());
        }
        return message.toString();
    }

    private javax.swing.JButton createButton;
    private javax.swing.JLabel createPasswordLabel;
    private javax.swing.JTextField createPasswordField;
    private javax.swing.JPanel createPanel;
    private javax.swing.JLabel createRoleLabel;
    private javax.swing.JComboBox<String> createRoleComboBox;
    private javax.swing.JLabel createUsernameLabel;
    private javax.swing.JTextField createUsernameField;
    private javax.swing.JButton disableButton;
    private javax.swing.JLabel disableInfoLabel;
    private javax.swing.JPanel disablePanel;
    private javax.swing.JLabel disableUsernameLabel;
    private javax.swing.JTextField disableUsernameField;
    private javax.swing.JLabel doctorContactLabel;
    private javax.swing.JTextField doctorContactField;
    private javax.swing.JLabel doctorNameLabel;
    private javax.swing.JTextField doctorNameField;
    private javax.swing.JPanel doctorOptionalPanel;
    private javax.swing.JLabel noOptionalLabel;
    private javax.swing.JPanel noOptionalPanel;
    private javax.swing.JPanel optionalCardsPanel;
    private javax.swing.JLabel optionalHeaderLabel;
    private javax.swing.JLabel patientContactLabel;
    private javax.swing.JTextField patientContactField;
    private javax.swing.JLabel patientFirstNameLabel;
    private javax.swing.JTextField patientFirstNameField;
    private javax.swing.JLabel patientIcLabel;
    private javax.swing.JTextField patientIcField;
    private javax.swing.JLabel patientLastNameLabel;
    private javax.swing.JTextField patientLastNameField;
    private javax.swing.JPanel patientOptionalPanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton resetCreateButton;
    private javax.swing.JButton resetUpdateButton;
    private javax.swing.JComboBox<String> roleFilterComboBox;
    private javax.swing.JLabel roleFilterLabel;
    private javax.swing.JLabel specializationLabel;
    private javax.swing.JTextField specializationField;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JButton updateButton;
    private javax.swing.JLabel updateInfoLabel;
    private javax.swing.JLabel updatePasswordLabel;
    private javax.swing.JTextField updatePasswordField;
    private javax.swing.JPanel updatePanel;
    private javax.swing.JLabel updateRoleLabel;
    private javax.swing.JComboBox<String> updateRoleComboBox;
    private javax.swing.JLabel updateStatusLabel;
    private javax.swing.JComboBox<String> updateStatusComboBox;
    private javax.swing.JLabel updateUserIdLabel;
    private javax.swing.JTextField updateUserIdField;
    private javax.swing.JLabel updateUsernameLabel;
    private javax.swing.JTextField updateUsernameField;
    private javax.swing.JTabbedPane userActionsTabbedPane;
    private javax.swing.JScrollPane usersScrollPane;
    private javax.swing.JTable usersTable;
}
