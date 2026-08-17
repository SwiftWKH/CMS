package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import brightcare.client.gateway.SessionSummary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
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
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        sessionsTabbedPane = new javax.swing.JTabbedPane();
        activePanel = new javax.swing.JPanel();
        activeSessionsScrollPane = new javax.swing.JScrollPane();
        activeSessionsTable = new javax.swing.JTable();
        historyPanel = new javax.swing.JPanel();
        historyDateLabel = new javax.swing.JLabel();
        historyDateSpinner = new javax.swing.JSpinner();
        filterHistoryButton = new javax.swing.JButton();
        showAllHistoryButton = new javax.swing.JButton();
        historySessionsScrollPane = new javax.swing.JScrollPane();
        historySessionsTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sessions");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18));
        titleLabel.setText("Sessions");
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(this::refreshButtonActionPerformed);

        activeSessionsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Username", "Login Time", "Role"}) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        activeSessionsScrollPane.setViewportView(activeSessionsTable);
        buildActivePanel();

        historyDateLabel.setText("Date:");
        historyDateSpinner.setModel(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        historyDateSpinner.setEditor(new JSpinner.DateEditor(historyDateSpinner, "yyyy-MM-dd"));
        filterHistoryButton.setText("Filter Date");
        filterHistoryButton.addActionListener(this::filterHistoryButtonActionPerformed);
        showAllHistoryButton.setText("Show All");
        showAllHistoryButton.addActionListener(this::showAllHistoryButtonActionPerformed);
        historySessionsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Username", "Login Time", "Logout Time", "Status", "Role"}) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        historySessionsScrollPane.setViewportView(historySessionsTable);
        buildHistoryPanel();

        sessionsTabbedPane.addTab("Active Sessions", activePanel);
        sessionsTabbedPane.addTab("Past Sessions", historyPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(sessionsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 780,
                                                Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(titleLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(refreshButton)))
                                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(titleLabel)
                                        .addComponent(refreshButton))
                                .addGap(18, 18, 18)
                                .addComponent(sessionsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 420,
                                        Short.MAX_VALUE)
                                .addGap(24, 24, 24))
        );

        pack();
    }

    private void buildActivePanel() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(activePanel);
        activePanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(activeSessionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
                .addContainerGap());
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(activeSessionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addContainerGap());
    }

    private void buildHistoryPanel() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(historyPanel);
        historyPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(historySessionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 740,
                                        Short.MAX_VALUE)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(historyDateLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(historyDateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 130,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(filterHistoryButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(showAllHistoryButton)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(historyDateLabel)
                        .addComponent(historyDateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(filterHistoryButton)
                        .addComponent(showAllHistoryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(historySessionsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap());
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
        loadSessions();
    }

    private void filterHistoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date) historyDateSpinner.getValue());
        LocalDate selectedDate = LocalDate.of(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        loadHistory(selectedDate);
    }

    private void showAllHistoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        loadHistory(null);
    }

    public AdminController getController() {
        return controller;
    }

    private void loadSessions() {
        loadActiveSessions();
        loadHistory(null);
    }

    private void loadActiveSessions() {
        DefaultTableModel model = (DefaultTableModel) activeSessionsTable.getModel();
        model.setRowCount(0);
        for (SessionSummary session : controller.getActiveSessions()) {
            model.addRow(new Object[] {session.getUsername(), session.getLoginTime(), session.getRole()});
        }
    }

    private void loadHistory(LocalDate filterDate) {
        DefaultTableModel model = (DefaultTableModel) historySessionsTable.getModel();
        model.setRowCount(0);
        for (SessionSummary session : controller.getSessionHistory()) {
            if (filterDate != null && !matchesDate(session, filterDate)) {
                continue;
            }
            model.addRow(new Object[] {session.getUsername(), session.getLoginTime(),
                session.getLogoutTime(), session.getStatus(), session.getRole()});
        }
    }

    private boolean matchesDate(SessionSummary session, LocalDate filterDate) {
        return sameDate(session.getLoginTime(), filterDate) || sameDate(session.getLogoutTime(), filterDate);
    }

    private boolean sameDate(String value, LocalDate filterDate) {
        if (value == null || value.trim().length() < 10) {
            return false;
        }
        try {
            return LocalDateTime.parse(value).toLocalDate().equals(filterDate);
        } catch (RuntimeException ex) {
            try {
                return LocalDate.parse(value.substring(0, 10)).equals(filterDate);
            } catch (RuntimeException ignored) {
                return false;
            }
        }
    }

    private javax.swing.JPanel activePanel;
    private javax.swing.JScrollPane activeSessionsScrollPane;
    private javax.swing.JTable activeSessionsTable;
    private javax.swing.JButton filterHistoryButton;
    private javax.swing.JLabel historyDateLabel;
    private javax.swing.JSpinner historyDateSpinner;
    private javax.swing.JPanel historyPanel;
    private javax.swing.JScrollPane historySessionsScrollPane;
    private javax.swing.JTable historySessionsTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTabbedPane sessionsTabbedPane;
    private javax.swing.JButton showAllHistoryButton;
    private javax.swing.JLabel titleLabel;
}
