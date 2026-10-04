package com.petcare.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.exception.ValidationException;
import com.petcare.model.Customer;
import com.petcare.service.CustomerService;
import com.petcare.util.UITheme;
import com.petcare.util.ValidationHelper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Modern & colorful Pet Owner (Customer) Management Screen.
 */
public class CustomerManagementPanel extends JPanel {

    private final CustomerService customerService;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Form inputs
    private JTextField txtId;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtRegDate;
    private JTextField txtSearch;

    private int selectedCustomerId = -1;

    public CustomerManagementPanel() {
        this.customerService = new CustomerService();
        initComponents();
        loadCustomerData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Search Toolbar Card
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        topPanel.setBackground(UITheme.SURFACE_WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LINE, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        txtSearch = new JTextField(22);
        txtSearch.setFont(UITheme.REGULAR_FONT);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search by name, email, or phone...");

        JButton btnSearch = UITheme.createPrimaryButton("Search Owner");
        btnSearch.addActionListener(e -> performSearch());

        JButton btnResetSearch = UITheme.createSecondaryButton("Reset");
        btnResetSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadCustomerData();
        });

        JLabel lblSearchIcon = new JLabel("Pet Owner Search:");
        lblSearchIcon.setFont(UITheme.BOLD_FONT);
        lblSearchIcon.setForeground(UITheme.TEXT_DARK);

        topPanel.add(lblSearchIcon);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnResetSearch);
        add(topPanel, BorderLayout.NORTH);

        // Left Form Panel with clean white surface and styled titled border
        JPanel leftPanel = UITheme.createTitledPanel("Pet Owner Profile Form");
        leftPanel.setLayout(new BorderLayout(0, 8));
        leftPanel.setPreferredSize(new Dimension(380, 420));

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtFullName = new JTextField(18);
        txtFullName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Emily Watson");
        txtEmail = new JTextField(18);
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. emily@gmail.com");
        txtPhone = new JTextField(18);
        txtPhone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. 0771234567");
        txtAddress = new JTextField(18);
        txtAddress.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. 42 Main St, Colombo");
        txtRegDate = new JTextField(LocalDate.now().toString(), 18);

        addFormField(formFields, gbc, 0, "Owner ID (Auto):", txtId);
        addFormField(formFields, gbc, 1, "Full Name *:", txtFullName);
        addFormField(formFields, gbc, 2, "Email Address *:", txtEmail);
        addFormField(formFields, gbc, 3, "Phone Number *:", txtPhone);
        addFormField(formFields, gbc, 4, "Home Address *:", txtAddress);
        addFormField(formFields, gbc, 5, "Registration Date:", txtRegDate);

        leftPanel.add(formFields, BorderLayout.CENTER);

        // Form Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setOpaque(false);

        JButton btnSave = UITheme.createSuccessButton("Save / Update");
        btnSave.addActionListener(e -> saveCustomer());

        JButton btnClear = UITheme.createSecondaryButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        JButton btnDelete = UITheme.createDangerButton("Delete");
        btnDelete.addActionListener(e -> deleteCustomer());

        btnPanel.add(btnSave);
        btnPanel.add(btnClear);
        btnPanel.add(btnDelete);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        // Right Table Panel with clean styled card
        JPanel rightPanel = UITheme.createTitledPanel("Registered Pet Owners List");
        rightPanel.setLayout(new BorderLayout());

        String[] columns = {"ID", "Owner Name", "Email", "Phone", "Address", "Reg Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        customerTable = new JTable(tableModel);
        UITheme.styleTable(customerTable);
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customerTable.getSelectedRow() != -1) {
                populateFormFromTable(customerTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(380);
        splitPane.setOpaque(false);
        add(splitPane, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.BOLD_FONT);
        lbl.setForeground(UITheme.TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        field.setFont(UITheme.REGULAR_FONT);
        panel.add(field, gbc);
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = customerService.getAllCustomers();
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getFullName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress(),
                    c.getRegistrationDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading pet owners: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        List<Customer> results = customerService.searchCustomers(keyword);
        for (Customer c : results) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getFullName(),
                c.getEmail(),
                c.getPhone(),
                c.getAddress(),
                c.getRegistrationDate()
            });
        }
    }

    private void populateFormFromTable(int row) {
        selectedCustomerId = (int) tableModel.getValueAt(row, 0);
        txtId.setText(String.valueOf(selectedCustomerId));
        txtFullName.setText((String) tableModel.getValueAt(row, 1));
        txtEmail.setText((String) tableModel.getValueAt(row, 2));
        txtPhone.setText((String) tableModel.getValueAt(row, 3));
        txtAddress.setText((String) tableModel.getValueAt(row, 4));
        Object regDate = tableModel.getValueAt(row, 5);
        txtRegDate.setText(regDate != null ? regDate.toString() : "");
    }

    private void saveCustomer() {
        try {
            String name = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String address = txtAddress.getText().trim();
            String dateStr = txtRegDate.getText().trim();

            LocalDate regDate = ValidationHelper.validateDate(dateStr, "Registration Date");

            Customer c = new Customer();
            c.setId(selectedCustomerId > 0 ? selectedCustomerId : 0);
            c.setFullName(name);
            c.setEmail(email);
            c.setPhone(phone);
            c.setAddress(address);
            c.setRegistrationDate(regDate);

            boolean success = customerService.saveCustomer(c);
            if (success) {
                JOptionPane.showMessageDialog(this, "Pet owner profile saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCustomerData();
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save pet owner: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select an owner from the table to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete Pet Owner ID #" + selectedCustomerId + "?\nAll registered pets belonging to this owner will also be removed.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = customerService.deleteCustomer(selectedCustomerId);
            if (deleted) {
                JOptionPane.showMessageDialog(this, "Pet owner deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCustomerData();
            }
        }
    }

    private void clearForm() {
        selectedCustomerId = -1;
        txtId.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtRegDate.setText(LocalDate.now().toString());
        customerTable.clearSelection();
    }
}
