package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Treatment;
import com.petcare.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Treatment & Medical Billing operations (DAO Pattern - Transaction 2).
 */
public class TreatmentDAO {

    public List<Treatment> findAll() throws DatabaseException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT t.*, a.appointment_code, a.vet_doctor, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM treatments t " +
                     "JOIN appointments a ON t.appointment_id = a.id " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "ORDER BY t.treatment_date DESC, t.id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToTreatment(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch treatments: " + e.getMessage(), e);
        }
        return list;
    }

    public Treatment findById(int id) throws DatabaseException {
        String sql = "SELECT t.*, a.appointment_code, a.vet_doctor, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM treatments t " +
                     "JOIN appointments a ON t.appointment_id = a.id " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE t.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTreatment(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public Treatment findByAppointmentId(int appointmentId) throws DatabaseException {
        String sql = "SELECT t.*, a.appointment_code, a.vet_doctor, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM treatments t " +
                     "JOIN appointments a ON t.appointment_id = a.id " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE t.appointment_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTreatment(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find treatment by appointment ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Treatment> search(String keyword) throws DatabaseException {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT t.*, a.appointment_code, a.vet_doctor, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM treatments t " +
                     "JOIN appointments a ON t.appointment_id = a.id " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.appointment_code LIKE ? OR c.full_name LIKE ? OR p.name LIKE ? OR t.diagnosis LIKE ? OR t.status LIKE ? " +
                     "ORDER BY t.treatment_date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, query);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToTreatment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search treatments: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean create(Treatment t) throws DatabaseException {
        String sql = "INSERT INTO treatments (appointment_id, diagnosis, procedure_details, medication_prescribed, medication_cost, treatment_cost, total_amount, treatment_date, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, t.getAppointmentId());
            stmt.setString(2, t.getDiagnosis());
            stmt.setString(3, t.getProcedureDetails());
            stmt.setString(4, t.getMedicationPrescribed());
            stmt.setDouble(5, t.getMedicationCost());
            stmt.setDouble(6, t.getTreatmentCost());
            stmt.setDouble(7, t.getTotalAmount());
            stmt.setDate(8, Date.valueOf(t.getTreatmentDate()));
            stmt.setString(9, t.getStatus());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        t.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create treatment record: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean update(Treatment t) throws DatabaseException {
        String sql = "UPDATE treatments SET appointment_id = ?, diagnosis = ?, procedure_details = ?, medication_prescribed = ?, medication_cost = ?, treatment_cost = ?, total_amount = ?, treatment_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, t.getAppointmentId());
            stmt.setString(2, t.getDiagnosis());
            stmt.setString(3, t.getProcedureDetails());
            stmt.setString(4, t.getMedicationPrescribed());
            stmt.setDouble(5, t.getMedicationCost());
            stmt.setDouble(6, t.getTreatmentCost());
            stmt.setDouble(7, t.getTotalAmount());
            stmt.setDate(8, Date.valueOf(t.getTreatmentDate()));
            stmt.setString(9, t.getStatus());
            stmt.setInt(10, t.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update treatment record: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM treatments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete treatment record: " + e.getMessage(), e);
        }
    }

    public double calculateTotalRevenue() throws DatabaseException {
        String sql = "SELECT SUM(total_amount) FROM treatments WHERE status = 'Paid'";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to calculate total revenue: " + e.getMessage(), e);
        }
        return 0.0;
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM treatments";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count treatments: " + e.getMessage(), e);
        }
        return 0;
    }

    private Treatment mapResultSetToTreatment(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setId(rs.getInt("id"));
        t.setAppointmentId(rs.getInt("appointment_id"));
        t.setAppointmentCode(rs.getString("appointment_code"));
        t.setCustomerName(rs.getString("customer_name"));
        t.setPetName(rs.getString("pet_name"));
        t.setVetDoctor(rs.getString("vet_doctor"));
        t.setDiagnosis(rs.getString("diagnosis"));
        t.setProcedureDetails(rs.getString("procedure_details"));
        t.setMedicationPrescribed(rs.getString("medication_prescribed"));
        t.setMedicationCost(rs.getDouble("medication_cost"));
        t.setTreatmentCost(rs.getDouble("treatment_cost"));
        t.setTotalAmount(rs.getDouble("total_amount"));
        Date d = rs.getDate("treatment_date");
        if (d != null) {
            t.setTreatmentDate(d.toLocalDate());
        }
        t.setStatus(rs.getString("status"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        return t;
    }
}
