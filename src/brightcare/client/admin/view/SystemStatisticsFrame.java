package brightcare.client.admin.view;

import brightcare.client.admin.controller.AdminController;
import java.time.LocalDateTime;
import javax.swing.table.DefaultTableModel;

public class SystemStatisticsFrame extends javax.swing.JFrame {
    private static final long serialVersionUID = 1L;

    private final AdminController controller;

    public SystemStatisticsFrame(AdminController controller) {
        this.controller = controller;
        initComponents();
        loadStatistics();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        lastUpdatedLabel = new javax.swing.JLabel();
        metricsScrollPane = new javax.swing.JScrollPane();
        metricsTable = new javax.swing.JTable();
        detailsScrollPane = new javax.swing.JScrollPane();
        statisticsArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("System Statistics");

        titleLabel.setFont(new java.awt.Font("SansSerif", 1, 18));
        titleLabel.setText("System Statistics");
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(this::refreshButtonActionPerformed);
        lastUpdatedLabel.setText("Last refreshed: -");

        metricsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {"Metric", "Value"}) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        });
        metricsScrollPane.setViewportView(metricsTable);

        statisticsArea.setColumns(20);
        statisticsArea.setEditable(false);
        statisticsArea.setRows(5);
        detailsScrollPane.setViewportView(statisticsArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(detailsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 760,
                                                Short.MAX_VALUE)
                                        .addComponent(metricsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 760,
                                                Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(titleLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lastUpdatedLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(refreshButton)))
                                .addGap(24, 24, 24))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(titleLabel)
                                        .addComponent(lastUpdatedLabel)
                                        .addComponent(refreshButton))
                                .addGap(18, 18, 18)
                                .addComponent(metricsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 170,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(detailsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 250,
                                        Short.MAX_VALUE)
                                .addGap(24, 24, 24))
        );

        pack();
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
        loadStatistics();
    }

    public AdminController getController() {
        return controller;
    }

    private void loadStatistics() {
        String statistics = controller.viewSystemStatistics();
        statisticsArea.setText(statistics);
        populateMetricTable(statistics);
        lastUpdatedLabel.setText("Last refreshed: " + LocalDateTime.now().withNano(0));
    }

    private void populateMetricTable(String statistics) {
        DefaultTableModel model = (DefaultTableModel) metricsTable.getModel();
        model.setRowCount(0);
        if (statistics == null || statistics.trim().length() == 0) {
            return;
        }
        String[] lines = statistics.split("\\R");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            int colonIndex = line.indexOf(':');
            if (colonIndex <= 0 || colonIndex == line.length() - 1) {
                continue;
            }
            model.addRow(new Object[] {
                line.substring(0, colonIndex).trim(),
                line.substring(colonIndex + 1).trim()
            });
        }
    }

    private javax.swing.JScrollPane detailsScrollPane;
    private javax.swing.JLabel lastUpdatedLabel;
    private javax.swing.JScrollPane metricsScrollPane;
    private javax.swing.JTable metricsTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTextArea statisticsArea;
    private javax.swing.JLabel titleLabel;
}
