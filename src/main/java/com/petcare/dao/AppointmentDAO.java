package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Appointment;
import com.petcare.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointment operations (DAO Pattern - Transaction 1).
 */
public class AppointmentDAO {

    public List<Appointment> findAll() throws DatabaseException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM appointments a " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch appointments: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Appointment> findByCustomerId(int customerId) throws DatabaseException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM appointments a " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.customer_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time ASC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch appointments for customer: " + e.getMessage(), e);
        }
        return list;
    }

    public Appointment findById(int id) throws DatabaseException {
        String sql = "SELECT a.*, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM appointments a " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAppointment(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find appointment by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Appointment> search(String keyword) throws DatabaseException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, c.full_name AS customer_name, p.name AS pet_name " +
                     "FROM appointments a " +
                     "JOIN customers c ON a.customer_id = c.id " +
                     "JOIN pets p ON a.pet_id = p.id " +
                     "WHERE a.appointment_code LIKE ? OR c.full_name LIKE ? OR p.name LIKE ? OR a.vet_doctor LIKE ? OR a.status LIKE ? " +
                     "ORDER BY a.appointment_date DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, query);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAppointment(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search appointments: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Checks if a vet already has an appointment booked on the same date and time.
     */
    public boolean hasVetScheduleConflict(String vetDoctor, LocalDate date, String time, int excludeAppointmentId) throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE vet_doctor = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled' AND id != ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vetDoctor);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, time);
            stmt.setInt(4, excludeAppointmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to verify vet schedule availability: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean create(Appointment appt) throws DatabaseException {
        String sql = "INSERT INTO appointments (appointment_code, customer_id, pet_id, vet_doctor, appointment_date, appointment_time, status, reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, appt.getAppointmentCode());
            stmt.setInt(2, appt.getCustomerId());
            stmt.setInt(3, appt.getPetId());
            stmt.setString(4, appt.getVetDoctor());
            stmt.setDate(5, Date.valueOf(appt.getAppointmentDate()));
            stmt.setString(6, appt.getAppointmentTime());
            stmt.setString(7, appt.getStatus());
            stmt.setString(8, appt.getReason());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        appt.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create appointment: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean update(Appointment appt) throws DatabaseException {
        String sql = "UPDATE appointments SET customer_id = ?, pet_id = ?, vet_doctor = ?, appointment_date = ?, appointment_time = ?, status = ?, reason = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appt.getCustomerId());
            stmt.setInt(2, appt.getPetId());
            stmt.setString(3, appt.getVetDoctor());
            stmt.setDate(4, Date.valueOf(appt.getAppointmentDate()));
            stmt.setString(5, appt.getAppointmentTime());
            stmt.setString(6, appt.getStatus());
            stmt.setString(7, appt.getReason());
            stmt.setInt(8, appt.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update appointment: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(int appointmentId, String newStatus) throws DatabaseException {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, appointmentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update appointment status: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete appointment: " + e.getMessage(), e);
        }
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count appointments: " + e.getMessage(), e);
        }
        return 0;
    }

    public int countTodayAppointments() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURRENT_DATE()";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count today's appointments: " + e.getMessage(), e);
        }
        return 0;
    }

    public String generateNextCode() throws DatabaseException {
        String sql = "SELECT MAX(id) FROM appointments";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int nextNum = 1001;
            if (rs.next()) {
                int maxId = rs.getInt(1);
                nextNum = 1001 + maxId;
            }
            return "APT-" + nextNum;
        } catch (SQLException e) {
            return "APT-" + System.currentTimeMillis() % 10000;
        }
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setAppointmentCode(rs.getString("appointment_code"));
        a.setCustomerId(rs.getInt("customer_id"));
        a.setCustomerName(rs.getString("customer_name"));
        a.setPetId(rs.getInt("pet_id"));
        a.setPetName(rs.getString("pet_name"));
        a.setVetDoctor(rs.getString("vet_doctor"));
        Date d = rs.getDate("appointment_date");
        if (d != null) {
            a.setAppointmentDate(d.toLocalDate());
        }
        a.setAppointmentTime(rs.getString("appointment_time"));
        a.setStatus(rs.getString("status"));
        a.setReason(rs.getString("reason"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        return a;
    }
}
