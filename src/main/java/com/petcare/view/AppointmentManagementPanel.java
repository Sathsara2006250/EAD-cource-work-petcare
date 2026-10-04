package com.petcare.view;

import com.formdev.flatlaf.FlatClientProperties;
import com.petcare.exception.InvalidAppointmentException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Appointment;
import com.petcare.model.Customer;
import com.petcare.model.Pet;
import com.petcare.model.User;
import com.petcare.service.AppointmentService;
import com.petcare.service.CustomerService;
import com.petcare.service.PetService;
import com.petcare.util.SessionManager;
import com.petcare.util.UITheme;
import com.petcare.util.ValidationHelper;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Modern & colorful Appointment Scheduling Screen (Transaction 1).
 */
public class AppointmentManagementPanel extends JPanel {

    private final AppointmentService appointmentService;
    private final CustomerService customerService;
    private final PetService petService;

    private JTable appointmentTable;
    private DefaultTableModel tableModel;

    // Form Components
    private JTextField txtId;
    private JTextField txtCode;
    private JComboBox<CustomerItem> cmbCustomer;
    private JComboBox<PetItem> cmbPet;
    private JComboBox<String> cmbVetDoctor;
    private JTextField txtDate;
    private JComboBox<String> cmbTimeSlot;
    private JComboBox<String> cmbStatus;
    private JTextField txtReason;
    private JTextField txtSearch;

    private int selectedAppointmentId = -1;

    private static class CustomerItem {
        final int id;
        final String name;

        CustomerItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (#" + id + ")";
        }
    }

    private static class PetItem {
        final int id;
        final String name;

        PetItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name + " (ID: " + id + ")";
        }
    }

    public AppointmentManagementPanel() {
        this.appointmentService = new AppointmentService();
        this.customerService = new CustomerService();
        this.petService = new PetService();
        initComponents();
        loadCustomers();
        loadAppointmentData();
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
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search by code, doctor, pet, owner...");

        JButton btnSearch = UITheme.createPrimaryButton("Search Schedule");
        btnSearch.addActionListener(e -> performSearch());

        JButton btnResetSearch = UITheme.createSecondaryButton("Reset");
        btnResetSearch.addActionListener(e -> {
            txtSearch.setText("");
            loadAppointmentData();
        });

        JLabel lblSearchIcon = new JLabel("Appointment Schedule Search:");
        lblSearchIcon.setFont(UITheme.BOLD_FONT);
        lblSearchIcon.setForeground(UITheme.TEXT_DARK);

        topPanel.add(lblSearchIcon);
        topPanel.add(txtSearch);
        topPanel.add(btnSearch);
        topPanel.add(btnResetSearch);
        add(topPanel, BorderLayout.NORTH);

        // Left Form Panel with styled titled card
        JPanel leftPanel = UITheme.createTitledPanel("Appointment Booking (Transaction 1)");
        leftPanel.setLayout(new BorderLayout(0, 8));
        leftPanel.setPreferredSize(new Dimension(390, 490));

        JPanel formFields = new JPanel(new GridBagLayout());
        formFields.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtCode = new JTextField();
        txtCode.setEditable(false);
        txtCode.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Generated automatically");

        cmbCustomer = new JComboBox<>();
        cmbCustomer.addActionListener(e -> onCustomerChanged());

        cmbPet = new JComboBox<>();
        cmbVetDoctor = new JComboBox<>(new String[]{
            "Dr. Robert Jenkins",
            "Dr. Alicia Stone",
            "Dr. David Chang",
            "Dr. Sarah Connor"
        });

        txtDate = new JTextField(LocalDate.now().plusDays(1).toString());
        cmbTimeSlot = new JComboBox<>(new String[]{
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM",
            "11:30 AM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM", "04:00 PM"
        });

        cmbStatus = new JComboBox<>(new String[]{"Scheduled", "In-Progress", "Completed", "Cancelled"});
        txtReason = new JTextField("Routine Health Checkup");
        txtReason.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. Vaccination, Fever, Dental check");

        int row = 0;
        addFormField(formFields, gbc, row++, "Appointment ID:", txtId);
        addFormField(formFields, gbc, row++, "Appt Code (Auto):", txtCode);
        addFormField(formFields, gbc, row++, "Pet Owner *:", cmbCustomer);
        addFormField(formFields, gbc, row++, "Patient Pet * (Cascading):", cmbPet);
        addFormField(formFields, gbc, row++, "Veterinarian Doctor *:", cmbVetDoctor);
        addFormField(formFields, gbc, row++, "Date (YYYY-MM-DD) *:", txtDate);
        addFormField(formFields, gbc, row++, "Time Slot *:", cmbTimeSlot);
        addFormField(formFields, gbc, row++, "Status:", cmbStatus);
        addFormField(formFields, gbc, row++, "Reason for Visit *:", txtReason);

        leftPanel.add(formFields, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnPanel.setOpaque(false);

        JButton btnBook = UITheme.createSuccessButton("Book Appointment");
        btnBook.addActionListener(e -> executeBookingTransaction());

        JButton btnClear = UITheme.createSecondaryButton("Clear");
        btnClear.addActionListener(e -> clearForm());

        JButton btnCancel = UITheme.createDangerButton("Cancel Appt");
        btnCancel.addActionListener(e -> cancelSelectedAppointment());

        btnPanel.add(btnBook);
        btnPanel.add(btnClear);
        btnPanel.add(btnCancel);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        // Right Table Panel
        JPanel rightPanel = UITheme.createTitledPanel("Clinic Appointment Schedule");
        rightPanel.setLayout(new BorderLayout());

        String[] columns = {"ID", "Code", "Date", "Time", "Pet Owner", "Pet Name", "Veterinarian", "Status", "Reason"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        appointmentTable = new JTable(tableModel);
        UITheme.styleTable(appointmentTable);

        // Status column colored styling
        appointmentTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(JLabel.CENTER);
                lbl.setFont(UITheme.BOLD_FONT);
                if (value != null) {
                    String status = value.toString();
                    if ("Completed".equalsIgnoreCase(status)) {
                        lbl.setForeground(UITheme.GREEN_SUCCESS);
                    } else if ("Scheduled".equalsIgnoreCase(status)) {
                        lbl.setForeground(UITheme.BLUE_ACCENT);
                    } else if ("In-Progress".equalsIgnoreCase(status)) {
                        lbl.setForeground(UITheme.ORANGE_WARN);
                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                        lbl.setForeground(UITheme.RED_DANGER);
                    }
                }
                return lbl;
            }
        });

        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && appointmentTable.getSelectedRow() != -1) {
                populateFormFromTable(appointmentTable.getSelectedRow());
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(390);
        splitPane.setOpaque(false);
        add(splitPane, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.BOLD_FONT);
        lbl.setForeground(UITheme.TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        comp.setFont(UITheme.REGULAR_FONT);
        panel.add(comp, gbc);
    }

    public void loadCustomers() {
        DefaultComboBoxModel<CustomerItem> model = new DefaultComboBoxModel<>();
        try {
            User user = SessionManager.getCurrentUser();
            if (SessionManager.isCustomer() && user != null) {
                Customer c = customerService.getCustomerByUserId(user.getId());
                if (c != null) {
                    model.addElement(new CustomerItem(c.getId(), c.getFullName()));
                    cmbCustomer.setModel(model);
                    cmbCustomer.setEnabled(false);
                    onCustomerChanged();
                    return;
                }
            }

            List<Customer> list = customerService.getAllCustomers();
            for (Customer c : list) {
                model.addElement(new CustomerItem(c.getId(), c.getFullName()));
            }
            cmbCustomer.setModel(model);
            if (model.getSize() > 0) {
                onCustomerChanged();
            }
        } catch (Exception ignored) {}
    }

    private void onCustomerChanged() {
        CustomerItem item = (CustomerItem) cmbCustomer.getSelectedItem();
        DefaultComboBoxModel<PetItem> petModel = new DefaultComboBoxModel<>();
        if (item != null) {
            List<Pet> pets = petService.getPetsByCustomerId(item.id);
            for (Pet p : pets) {
                petModel.addElement(new PetItem(p.getId(), p.getName()));
            }
        }
        cmbPet.setModel(petModel);
    }

    public void loadAppointmentData() {
        tableModel.setRowCount(0);
        try {
            List<Appointment> list;
            User user = SessionManager.getCurrentUser();
            if (SessionManager.isCustomer() && user != null) {
                Customer c = customerService.getCustomerByUserId(user.getId());
                list = (c != null) ? appointmentService.getAppointmentsByCustomerId(c.getId()) : appointmentService.getAllAppointments();
            } else {
                list = appointmentService.getAllAppointments();
            }

            for (Appointment a : list) {
                tableModel.addRow(new Object[]{
                    a.getId(),
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        List<Appointment> results = appointmentService.searchAppointments(keyword);
        for (Appointment a : results) {
            tableModel.addRow(new Object[]{
                a.getId(),
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
    }

    private void populateFormFromTable(int row) {
        selectedAppointmentId = (int) tableModel.getValueAt(row, 0);
        txtId.setText(String.valueOf(selectedAppointmentId));
        txtCode.setText((String) tableModel.getValueAt(row, 1));
        Object dateObj = tableModel.getValueAt(row, 2);
        txtDate.setText(dateObj != null ? dateObj.toString() : "");
        cmbTimeSlot.setSelectedItem((String) tableModel.getValueAt(row, 3));
        String custName = (String) tableModel.getValueAt(row, 4);
        String petName = (String) tableModel.getValueAt(row, 5);
        cmbVetDoctor.setSelectedItem((String) tableModel.getValueAt(row, 6));
        cmbStatus.setSelectedItem((String) tableModel.getValueAt(row, 7));
        txtReason.setText((String) tableModel.getValueAt(row, 8));

        for (int i = 0; i < cmbCustomer.getItemCount(); i++) {
            if (cmbCustomer.getItemAt(i).name.equalsIgnoreCase(custName)) {
                cmbCustomer.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < cmbPet.getItemCount(); i++) {
            if (cmbPet.getItemAt(i).name.equalsIgnoreCase(petName)) {
                cmbPet.setSelectedIndex(i);
                break;
            }
        }
    }

    private void executeBookingTransaction() {
        try {
            CustomerItem cust = (CustomerItem) cmbCustomer.getSelectedItem();
            if (cust == null) {
                throw new ValidationException("Please select a pet owner.");
            }

            PetItem pet = (PetItem) cmbPet.getSelectedItem();
            if (pet == null) {
                throw new ValidationException("Please select a pet owned by the selected customer.");
            }

            String vet = (String) cmbVetDoctor.getSelectedItem();
            String dateStr = txtDate.getText().trim();
            LocalDate apptDate = ValidationHelper.validateDate(dateStr, "Appointment Date");
            String time = (String) cmbTimeSlot.getSelectedItem();
            String status = (String) cmbStatus.getSelectedItem();
            String reason = txtReason.getText().trim();

            Appointment appt = new Appointment();
            appt.setId(selectedAppointmentId > 0 ? selectedAppointmentId : 0);
            appt.setAppointmentCode(txtCode.getText());
            appt.setCustomerId(cust.id);
            appt.setPetId(pet.id);
            appt.setVetDoctor(vet);
            appt.setAppointmentDate(apptDate);
            appt.setAppointmentTime(time);
            appt.setStatus(status);
            appt.setReason(reason);

            boolean success = appointmentService.bookAppointment(appt);
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Appointment Scheduled Successfully!\nAppointment Code: " + appt.getAppointmentCode(),
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE
                );
                clearForm();
                loadAppointmentData();
            }
        } catch (InvalidAppointmentException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Appointment Rule Error:\n" + ex.getMessage(),
                "InvalidAppointmentException",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error during booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelectedAppointment() {
        if (selectedAppointmentId <= 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment from the table to cancel.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel Appointment ID #" + selectedAppointmentId + "?",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                appointmentService.updateStatus(selectedAppointmentId, "Cancelled");
                JOptionPane.showMessageDialog(this, "Appointment marked as Cancelled.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAppointmentData();
            } catch (InvalidAppointmentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedAppointmentId = -1;
        txtId.setText("");
        txtCode.setText("");
        txtDate.setText(LocalDate.now().plusDays(1).toString());
        txtReason.setText("Routine Health Checkup");
        cmbStatus.setSelectedIndex(0);
        appointmentTable.clearSelection();
    }
}
