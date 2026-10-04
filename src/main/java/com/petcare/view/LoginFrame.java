package com.petcare.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.exception.AuthenticationException;
import com.petcare.exception.ValidationException;
import com.petcare.model.User;
import com.petcare.service.AuthService;
import com.petcare.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 * Modern, colorful & pet care themed Login Screen.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;

    public LoginFrame() {
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Pet Care & Veterinary Clinic - System Login");
        setSize(480, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BG_MAIN);

        // Top Pet Care Header Banner
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.TEAL_PRIMARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("Pet Care & Veterinary Clinic", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.HERO_FONT);
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Veterinary Appointment & Clinical Management System", SwingConstants.CENTER);
        lblSub.setFont(UITheme.REGULAR_FONT);
        lblSub.setForeground(UITheme.TEAL_LIGHT);

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Card Panel
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 16));
        centerWrapper.setOpaque(false);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(UITheme.SURFACE_WHITE);
        cardPanel.setPreferredSize(new Dimension(420, 210));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LINE, 1, true),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(UITheme.BOLD_FONT);
        lblUser.setForeground(UITheme.TEXT_DARK);
        cardPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtUsername = new JTextField("admin", 15);
        txtUsername.setFont(UITheme.REGULAR_FONT);
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        cardPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(UITheme.BOLD_FONT);
        lblPass.setForeground(UITheme.TEXT_DARK);
        cardPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPassword = new JPasswordField("admin123", 15);
        txtPassword.setFont(UITheme.REGULAR_FONT);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter password");
        cardPanel.add(txtPassword, gbc);

        // Role Dropdown
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblRole = new JLabel("User Role:");
        lblRole.setFont(UITheme.BOLD_FONT);
        lblRole.setForeground(UITheme.TEXT_DARK);
        cardPanel.add(lblRole, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cmbRole = new JComboBox<>(new String[]{"Manager", "Customer"});
        cmbRole.setFont(UITheme.REGULAR_FONT);
        cardPanel.add(cmbRole, gbc);

        // Quick demo fills for viva presentation
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        quickPanel.setOpaque(false);

        JButton btnFillManager = new JButton("Demo: Manager");
        btnFillManager.setFont(UITheme.BADGE_FONT);
        btnFillManager.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFillManager.addActionListener(e -> {
            txtUsername.setText("admin");
            txtPassword.setText("admin123");
            cmbRole.setSelectedItem("Manager");
        });

        JButton btnFillCustomer = new JButton("Demo: Customer");
        btnFillCustomer.setFont(UITheme.BADGE_FONT);
        btnFillCustomer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnFillCustomer.addActionListener(e -> {
            txtUsername.setText("john_doe");
            txtPassword.setText("user123");
            cmbRole.setSelectedItem("Customer");
        });

        quickPanel.add(new JLabel("Quick Demo:"));
        quickPanel.add(btnFillManager);
        quickPanel.add(btnFillCustomer);
        cardPanel.add(quickPanel, gbc);

        centerWrapper.add(cardPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Action Buttons at Bottom
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        btnPanel.setOpaque(false);

        JButton btnLogin = UITheme.createPrimaryButton("  Sign In  ");
        btnLogin.addActionListener(e -> performLogin());

        JButton btnReset = UITheme.createSecondaryButton("Reset Form");
        btnReset.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            cmbRole.setSelectedIndex(0);
        });

        JButton btnExit = UITheme.createDangerButton("Exit System");
        btnExit.addActionListener(e -> System.exit(0));

        btnPanel.add(btnLogin);
        btnPanel.add(btnReset);
        btnPanel.add(btnExit);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String selectedRole = (String) cmbRole.getSelectedItem();

        try {
            User user = authService.login(username, password);

            if (!user.getRole().equalsIgnoreCase(selectedRole)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Access Denied: The account '" + username + "' is registered as '" + user.getRole() + 
                    "', not as '" + selectedRole + "'. Please select the correct role.",
                    "Role Mismatch",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                this,
                "Welcome to Pet Care Clinic, " + user.getFullName() + "!\nLogged in successfully as " + user.getRole(),
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE
            );

            this.dispose();
            SwingUtilities.invokeLater(() -> {
                MainDashboardFrame dashboard = new MainDashboardFrame();
                dashboard.setVisible(true);
            });
        } catch (AuthenticationException | ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Could not connect to database: " + ex.getMessage() + "\nPlease make sure MySQL is running in XAMPP.",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
