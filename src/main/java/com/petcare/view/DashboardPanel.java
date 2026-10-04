package com.petcare.view;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.CustomerDAO;
import com.petcare.dao.PetDAO;
import com.petcare.dao.TreatmentDAO;
import com.petcare.model.Appointment;
import com.petcare.util.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Modern, colorful Veterinary Clinic Dashboard with 5 styled stat cards and live schedule table.
 */
public class DashboardPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final PetDAO petDAO = new PetDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final TreatmentDAO treatmentDAO = new TreatmentDAO();

    private JLabel lblTotalCust;
    private JLabel lblTotalPets;
    private JLabel lblTodayAppts;
    private JLabel lblTotalTreatments;
    private JLabel lblTotalRevenue;

    private JTable recentTable;
    private DefaultTableModel tableModel;

    public DashboardPanel() {
        initComponents();
        refreshMetrics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 12));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top 5 Colorful Stat Cards Grid
        JPanel metricsPanel = new JPanel(new GridLayout(1, 5, 12, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(0, 105));

        lblTotalCust = new JLabel("0");
        lblTotalPets = new JLabel("0");
        lblTodayAppts = new JLabel("0");
        lblTotalTreatments = new JLabel("0");
        lblTotalRevenue = new JLabel("$0.00");

        metricsPanel.add(UITheme.createStatCard("Pet Owners", lblTotalCust, "Registered clients", UITheme.BLUE_ACCENT, UITheme.BLUE_LIGHT, ""));
        metricsPanel.add(UITheme.createStatCard("Patient Pets", lblTotalPets, "Animals on file", UITheme.GREEN_SUCCESS, UITheme.GREEN_LIGHT, ""));
        metricsPanel.add(UITheme.createStatCard("Today's Visits", lblTodayAppts, "Scheduled checkups", UITheme.ORANGE_WARN, UITheme.ORANGE_LIGHT, ""));
        metricsPanel.add(UITheme.createStatCard("Treatments", lblTotalTreatments, "Procedures done", UITheme.PURPLE_CARD, UITheme.PURPLE_LIGHT, ""));
        metricsPanel.add(UITheme.createStatCard("Total Revenue", lblTotalRevenue, "Invoiced & billed", UITheme.TEAL_PRIMARY, UITheme.TEAL_LIGHT, ""));

        add(metricsPanel, BorderLayout.NORTH);

        // Center Table Card Panel
        JPanel tableCard = UITheme.createTitledPanel("Upcoming & Recent Pet Clinic Appointments");
        tableCard.setLayout(new BorderLayout(0, 8));

        JPanel tableTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tableTop.setOpaque(false);

        JButton btnRefresh = UITheme.createPrimaryButton("Refresh Dashboard");
        btnRefresh.addActionListener(e -> refreshMetrics());
        tableTop.add(btnRefresh);
        tableCard.add(tableTop, BorderLayout.NORTH);

        String[] columns = {"Appt Code", "Date", "Time", "Pet Owner", "Pet Name", "Veterinarian", "Status", "Reason for Visit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        recentTable = new JTable(tableModel);
        UITheme.styleTable(recentTable);

        // Custom colorful status renderer
        recentTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setFont(UITheme.BOLD_FONT);
                if (value != null) {
                    String status = value.toString();
                    if ("Completed".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(16, 185, 129));
                    } else if ("Scheduled".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(37, 99, 235));
                    } else if ("In-Progress".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(245, 158, 11));
                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                        lbl.setForeground(new Color(239, 68, 68));
                    }
                }
                return lbl;
            }
        });

        JScrollPane scrollPane = new JScrollPane(recentTable);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
    }

    public void refreshMetrics() {
        try {
            int custCount = customerDAO.count();
            int petCount = petDAO.count();
            int todayCount = appointmentDAO.countTodayAppointments();
            int treatCount = treatmentDAO.count();
            double revenue = treatmentDAO.calculateTotalRevenue();

            lblTotalCust.setText(String.valueOf(custCount));
            lblTotalPets.setText(String.valueOf(petCount));
            lblTodayAppts.setText(String.valueOf(todayCount));
            lblTotalTreatments.setText(String.valueOf(treatCount));
            lblTotalRevenue.setText(String.format("$%.2f", revenue));

            tableModel.setRowCount(0);
            List<Appointment> list = appointmentDAO.findAll();
            int limit = Math.min(list.size(), 12);
            for (int i = 0; i < limit; i++) {
                Appointment a = list.get(i);
                tableModel.addRow(new Object[]{
                    a.getAppointmentCode(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getCustomerName(),
                    a.getPetName(),
                    a.getVetDoctor(),
                    a.getStatus(),
                    a.getReason()
                });
            }
        } catch (Exception ignored) {}
    }
}
