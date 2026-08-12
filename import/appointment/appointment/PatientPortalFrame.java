package com.example.hospitalapitest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PatientPortalFrame extends javax.swing.JFrame {

    // Theme Palette matching the image
    private final Color SIDEBAR_BG = new Color(221, 225, 229);      // Light grey background
    private final Color BUTTON_BG = new Color(185, 196, 208);       // Button normal state
    private final Color BUTTON_HOVER = new Color(165, 178, 192);    // Button hover state
    private final Color BUTTON_ACTIVE = new Color(145, 160, 176);   // Button active state
    private final Color TEXT_COLOR = new Color(0, 0, 0);            // Dark text color

    private CardLayout cardLayout;
    private JPanel cardContentPanel;
    private final Map<String, JButton> navButtons = new HashMap<>();
    private JButton currentActiveButton;

    public PatientPortalFrame() {
        initComponents();
        setLocationRelativeTo(null); // Center window on screen
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Patient Medical Portal - Client");
        setPreferredSize(new java.awt.Dimension(1000, 680));
        setMinimumSize(new java.awt.Dimension(850, 550));
        getContentPane().setLayout(new java.awt.BorderLayout());

        // ==========================================
        // LEFT SIDEBAR
        // ==========================================
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(240, 680));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        JLabel lblPortalTitle = new JLabel("PATIENT PORTAL");
        lblPortalTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPortalTitle.setForeground(Color.BLACK);
        lblPortalTitle.setBorder(new EmptyBorder(25, 20, 20, 20));
        lblPortalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(lblPortalTitle);

        // Updated Navigation Array matching image
        String[] navItems = {
            "Home Dashboard",
            "Fetch RMI Data",
            "Profile Management",
            "Doctor Schedules",
            "Book Appointment",
            "Active Schedule",
            "History Logs",
            "Exit System"
        };

        // ==========================================
        // CENTER MAIN CONTENT
        // ==========================================
        cardLayout = new CardLayout();
        cardContentPanel = new JPanel(cardLayout);

        for (String navText : navItems) {
            JButton btn = createNavButton("\u25A2  " + navText, navText);
            navButtons.put(navText, btn);
            
            // Wrap button in a container for margins/padding between buttons
            JPanel btnWrapper = new JPanel(new BorderLayout());
            btnWrapper.setOpaque(false);
            btnWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            btnWrapper.setBorder(new EmptyBorder(3, 12, 3, 12));
            btnWrapper.add(btn, BorderLayout.CENTER);
            
            sidebarPanel.add(btnWrapper);

            if (navText.equals("Exit System")) {
                btn.addActionListener(evt -> System.exit(0));
            } else {
                cardContentPanel.add(createViewPanel(navText), navText);
                btn.addActionListener(evt -> switchView(navText));
            }
        }

        getContentPane().add(sidebarPanel, java.awt.BorderLayout.WEST);
        getContentPane().add(cardContentPanel, java.awt.BorderLayout.CENTER);

        // Initial view
        switchView("Home Dashboard");

        pack();
    }

    private void switchView(String viewName) {
        cardLayout.show(cardContentPanel, viewName);

        if (currentActiveButton != null) {
            currentActiveButton.setBackground(BUTTON_BG);
        }

        JButton selectedBtn = navButtons.get(viewName);
        if (selectedBtn != null) {
            selectedBtn.setBackground(BUTTON_ACTIVE);
            currentActiveButton = selectedBtn;
        }
    }

    private JPanel createViewPanel(String titleText) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 45, 40, 45));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(titleText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTitle.setForeground(new Color(30, 35, 45));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Content panel for " + titleText.toLowerCase());
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(120, 125, 135));
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(lblSubtitle);

        return panel;
    }

    private JButton createNavButton(String text, String navKey) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(BUTTON_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != currentActiveButton) {
                    btn.setBackground(BUTTON_HOVER);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != currentActiveButton) {
                    btn.setBackground(BUTTON_BG);
                }
            }
        });

        return btn;
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(PatientPortalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new PatientPortalFrame().setVisible(true));
    }
}