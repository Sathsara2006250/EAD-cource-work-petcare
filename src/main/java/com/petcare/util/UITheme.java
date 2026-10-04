package com.petcare.util;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Modern, colorful, pet-care themed UI design system & component factory.
 */
public class UITheme {

    // Primary Brand Palette - Medical Teal & Vet Colors
    public static final Color TEAL_PRIMARY = new Color(13, 148, 136);    // Rich Medical Teal
    public static final Color TEAL_DARK = new Color(15, 118, 110);      // Dark Teal
    public static final Color TEAL_LIGHT = new Color(240, 253, 250);    // Ice Teal
    public static final Color TEAL_BORDER = new Color(153, 246, 228);   // Soft Mint Border

    // Functional & Stat Accent Colors
    public static final Color BLUE_ACCENT = new Color(37, 99, 235);     // Royal Blue
    public static final Color BLUE_LIGHT = new Color(239, 246, 255);    // Light Blue Tint

    public static final Color GREEN_SUCCESS = new Color(16, 185, 129);  // Emerald Green
    public static final Color GREEN_LIGHT = new Color(236, 253, 245);   // Mint Green Tint

    public static final Color ORANGE_WARN = new Color(245, 158, 11);    // Warm Amber
    public static final Color ORANGE_LIGHT = new Color(254, 243, 199);  // Amber Tint

    public static final Color RED_DANGER = new Color(239, 68, 68);      // Coral Red
    public static final Color RED_LIGHT = new Color(254, 242, 242);     // Coral Tint

    public static final Color PURPLE_CARD = new Color(139, 92, 246);    // Violet Purple
    public static final Color PURPLE_LIGHT = new Color(245, 243, 255);  // Violet Tint

    // Surfaces & Backgrounds
    public static final Color BG_MAIN = new Color(245, 247, 250);       // Soft Clinical Light Slate
    public static final Color SURFACE_WHITE = Color.WHITE;               // Crisp White Cards
    public static final Color TEXT_DARK = new Color(15, 23, 42);        // Deep Slate Charcoal
    public static final Color TEXT_MUTED = new Color(100, 116, 139);    // Slate Grey
    public static final Color BORDER_LINE = new Color(226, 232, 240);   // Subtle Clean Border

    // Typography
    public static final Font HERO_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font STAT_NUMBER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font BADGE_FONT = new Font("Segoe UI", Font.BOLD, 11);

    /**
     * Configures FlatLaf global UI settings for rounded modern desktop elegance.
     */
    public static void configureGlobalLafDefaults() {
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.tabHeight", 38);
        UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
        UIManager.put("TabbedPane.underlineColor", TEAL_PRIMARY);
        UIManager.put("TabbedPane.tabInsets", new Insets(6, 16, 6, 16));
        UIManager.put("TableHeader.height", 34);
        UIManager.put("Table.rowHeight", 28);
    }

    /**
     * Primary Teal Action Button.
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(TEAL_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        return btn;
    }

    /**
     * Success Emerald Green Button (Save, Book, Generate Bill, Confirm).
     */
    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(GREEN_SUCCESS);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        return btn;
    }

    /**
     * Danger Coral Red Button (Delete, Cancel).
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(RED_DANGER);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        return btn;
    }

    /**
     * Secondary / Outline Button (Clear, Reset, Refresh).
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(REGULAR_FONT);
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LINE, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        return btn;
    }

    /**
     * Accent Button with customizable color.
     */
    public static JButton createAccentButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        return btn;
    }

    /**
     * Creates a modern Card Panel with clean white surface, rounded border, and padding.
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE_WHITE);
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(TEAL_BORDER, 1, true),
            " " + title + " ",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            BOLD_FONT,
            TEAL_DARK
        );
        panel.setBorder(BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    /**
     * Creates a colorful, high-impact Dashboard Metric Card.
     */
    public static JPanel createStatCard(String title, JLabel valueLabel, String subtitle, Color themeColor, Color bgLight, String iconEmoji) {
        JPanel card = new JPanel(new BorderLayout(8, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card Background
                g2.setColor(SURFACE_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Left accent bar
                g2.setColor(themeColor);
                g2.fillRoundRect(0, 0, 6, getHeight(), 6, 6);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LINE, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 14)
        ));

        // Top Row: Title
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(BOLD_FONT);
        lblTitle.setForeground(TEXT_DARK);
        topRow.add(lblTitle, BorderLayout.WEST);

        // Center: Bold colorful count
        valueLabel.setFont(STAT_NUMBER_FONT);
        valueLabel.setForeground(themeColor);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Bottom: Descriptive Subtitle
        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(TEXT_MUTED);

        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Styles a JTable with colorful modern headers, row height, and zebra striping.
     */
    public static void styleTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(30);
        table.setSelectionBackground(TEAL_LIGHT);
        table.setSelectionForeground(TEXT_DARK);
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));

        JTableHeader header = table.getTableHeader();
        header.setFont(BOLD_FONT);
        header.setBackground(TEAL_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 34));
        header.setReorderingAllowed(false);

        // Cell alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i).toLowerCase();
            if (name.contains("id") || name.contains("date") || name.contains("time") || name.contains("status") || name.contains("age") || name.contains("code")) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
}
