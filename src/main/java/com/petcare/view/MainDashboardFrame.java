package com.petcare.view;

import com.petcare.model.User;
import com.petcare.util.SessionManager;
import com.petcare.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Main Pet Care System Container Window with colorful modern header & tab navigation.
 */
public class MainDashboardFrame extends JFrame {

    private JTabbedPane tabbedPane;

    private DashboardPanel dashboardPanel;
    private CustomerManagementPanel customerPanel;
    private PetManagementPanel petPanel;
    private AppointmentManagementPanel appointmentPanel;
    private TreatmentBillingPanel treatmentPanel;
    private ReportManagementPanel reportPanel;

    public MainDashboardFrame() {
        initComponents();
    }

    private void initComponents() {
        User currentUser = SessionManager.getCurrentUser();
        String role = (currentUser != null) ? currentUser.getRole() : "User";
        String userName = (currentUser != null) ? currentUser.getFullName() : "Guest";

        setTitle("Pet Care & Veterinary Services System - [" + role + ": " + userName + "]");
        setSize(1160, 740);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 8));
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top Header Bar - Medical Teal with Clinic Branding
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.TEAL_PRIMARY);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.TEAL_DARK, 1, true),
            BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));

        // Left Branding
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brandPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Pet Care & Veterinary Clinic");
        lblTitle.setFont(UITheme.HERO_FONT);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblTag = new JLabel("  CLINICAL ERP  ");
        lblTag.setFont(UITheme.BADGE_FONT);
        lblTag.setOpaque(true);
        lblTag.setBackground(UITheme.TEAL_DARK);
        lblTag.setForeground(UITheme.TEAL_LIGHT);
        lblTag.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        brandPanel.add(lblTitle);
        brandPanel.add(lblTag);
        topBar.add(brandPanel, BorderLayout.WEST);

        // Right User Pill + Logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userPanel.setOpaque(false);

        JPanel userPill = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        userPill.setBackground(UITheme.TEAL_DARK);
        userPill.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        JLabel lblUser = new JLabel(userName + " (" + role + ")");
        lblUser.setFont(UITheme.BOLD_FONT);
        lblUser.setForeground(Color.WHITE);
        userPill.add(lblUser);

        JButton btnLogout = UITheme.createDangerButton("Sign Out");
        btnLogout.addActionListener(e -> performLogout());

        userPanel.add(userPill);
        userPanel.add(btnLogout);
        topBar.add(userPanel, BorderLayout.EAST);

        mainPanel.add(topBar, BorderLayout.NORTH);

        // Center Modern Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.BOLD_FONT);

        dashboardPanel = new DashboardPanel();
        customerPanel = new CustomerManagementPanel();
        petPanel = new PetManagementPanel();
        appointmentPanel = new AppointmentManagementPanel();
        treatmentPanel = new TreatmentBillingPanel();
        reportPanel = new ReportManagementPanel();

        // Add tabs with clean text headers
        tabbedPane.addTab("Clinic Dashboard", dashboardPanel);

        if (SessionManager.isManager()) {
            tabbedPane.addTab("Pet Owners", customerPanel);
            tabbedPane.addTab("Patient Pets", petPanel);
            tabbedPane.addTab("Appointments (Tx 1)", appointmentPanel);
            tabbedPane.addTab("Treatments & Billing (Tx 2)", treatmentPanel);
            tabbedPane.addTab("Clinical Reports", reportPanel);
        } else {
            // Customer Role Tabs
            tabbedPane.addTab("My Patient Pets", petPanel);
            tabbedPane.addTab("Book Appointment", appointmentPanel);
        }

        // Listener to refresh data when user switches tabs
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex == -1) return;
                String title = tabbedPane.getTitleAt(selectedIndex);

                if (title.contains("Dashboard")) {
                    dashboardPanel.refreshMetrics();
                } else if (title.contains("Pet")) {
                    petPanel.loadOwnerComboBox();
                } else if (title.contains("Appointment")) {
                    appointmentPanel.loadCustomers();
                    appointmentPanel.loadAppointmentData();
                } else if (title.contains("Treatment")) {
                    treatmentPanel.loadAppointmentsDropdown();
                    treatmentPanel.loadTreatmentData();
                } else if (title.contains("Reports")) {
                    reportPanel.loadReportData();
                }
            }
        });

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out of Pet Care System?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            this.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }
}
