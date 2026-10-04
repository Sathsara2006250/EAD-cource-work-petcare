package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.ReportRow;
import com.petcare.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reports (joins across 4 tables: appointments, customers, pets, treatments).
 */
public class ReportDAO {

    public List<ReportRow> getComprehensiveVeterinaryReport(String speciesFilter, String statusFilter) throws DatabaseException {
        List<ReportRow> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    a.appointment_code, " +
            "    a.appointment_date, " +
            "    c.full_name AS customer_name, " +
            "    c.phone AS customer_phone, " +
            "    c.email AS customer_email, " +
            "    p.name AS pet_name, " +
            "    p.species, " +
            "    p.breed, " +
            "    a.vet_doctor, " +
            "    t.diagnosis, " +
            "    t.procedure_details, " +
            "    t.medication_cost, " +
            "    t.treatment_cost, " +
            "    t.total_amount, " +
            "    t.status AS payment_status " +
            "FROM appointments a " +
            "JOIN customers c ON a.customer_id = c.id " +
            "JOIN pets p ON a.pet_id = p.id " +
            "JOIN treatments t ON t.appointment_id = a.id " +
            "WHERE 1=1 "
        );

        if (speciesFilter != null && !speciesFilter.isEmpty() && !"All".equalsIgnoreCase(speciesFilter)) {
            sql.append("AND p.species = ? ");
        }
        if (statusFilter != null && !statusFilter.isEmpty() && !"All".equalsIgnoreCase(statusFilter)) {
            sql.append("AND t.status = ? ");
        }
        sql.append("ORDER BY a.appointment_date DESC, t.id DESC");

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIdx = 1;
            if (speciesFilter != null && !speciesFilter.isEmpty() && !"All".equalsIgnoreCase(speciesFilter)) {
                stmt.setString(paramIdx++, speciesFilter);
            }
            if (statusFilter != null && !statusFilter.isEmpty() && !"All".equalsIgnoreCase(statusFilter)) {
                stmt.setString(paramIdx++, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportRow row = new ReportRow();
                    row.setAppointmentCode(rs.getString("appointment_code"));
                    Date d = rs.getDate("appointment_date");
                    if (d != null) {
                        row.setAppointmentDate(d.toLocalDate());
                    }
                    row.setCustomerName(rs.getString("customer_name"));
                    row.setCustomerPhone(rs.getString("customer_phone"));
                    row.setCustomerEmail(rs.getString("customer_email"));
                    row.setPetName(rs.getString("pet_name"));
                    row.setSpecies(rs.getString("species"));
                    row.setBreed(rs.getString("breed"));
                    row.setVetDoctor(rs.getString("vet_doctor"));
                    row.setDiagnosis(rs.getString("diagnosis"));
                    row.setProcedureDetails(rs.getString("procedure_details"));
                    row.setMedicationCost(rs.getDouble("medication_cost"));
                    row.setTreatmentCost(rs.getDouble("treatment_cost"));
                    row.setTotalAmount(rs.getDouble("total_amount"));
                    row.setPaymentStatus(rs.getString("payment_status"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to generate comprehensive report: " + e.getMessage(), e);
        }
        return list;
    }
}
