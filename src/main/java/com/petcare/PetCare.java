package com.petcare;

import com.petcare.util.DatabaseConnection;
import com.petcare.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;
import com.petcare.util.UITheme;

/**
 * Main Application Entry Point for PetCare Enterprise Application.
 */
public class PetCare {

    public static void main(String[] args) {
        // Initialize modern, colorful FlatLaf Look and Feel
        try {
            FlatLightLaf.setup();
            UITheme.configureGlobalLafDefaults();
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fallback
            }
        }

        // Test database connection
        boolean dbConnected = DatabaseConnection.testConnection();
        if (dbConnected) {
            System.out.println("[PetCare] Connected to MySQL database: " + DatabaseConnection.getDbUrl());
        } else {
            System.err.println("[PetCare] Warning: Could not connect to MySQL database at " + DatabaseConnection.getDbUrl());
        }

        // Open Login Form
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
