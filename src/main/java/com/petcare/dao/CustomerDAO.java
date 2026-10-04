package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Customer;
import com.petcare.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customer operations (DAO Pattern).
 */
public class CustomerDAO {

    public List<Customer> findAll() throws DatabaseException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch customers: " + e.getMessage(), e);
        }
        return list;
    }

    public Customer findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find customer by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public Customer findByUserId(int userId) throws DatabaseException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find customer by user ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Customer> search(String keyword) throws DatabaseException {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR address LIKE ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            stmt.setString(1, query);
            stmt.setString(2, query);
            stmt.setString(3, query);
            stmt.setString(4, query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search customers: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean create(Customer customer) throws DatabaseException {
        String sql = "INSERT INTO customers (user_id, full_name, email, phone, address, registration_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (customer.getUserId() != null) {
                stmt.setInt(1, customer.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, customer.getFullName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setDate(6, Date.valueOf(customer.getRegistrationDate()));
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        customer.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create customer: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean update(Customer customer) throws DatabaseException {
        String sql = "UPDATE customers SET full_name = ?, email = ?, phone = ?, address = ?, registration_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getFullName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setDate(5, Date.valueOf(customer.getRegistrationDate()));
            stmt.setInt(6, customer.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update customer: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete customer: " + e.getMessage(), e);
        }
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM customers";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count customers: " + e.getMessage(), e);
        }
        return 0;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id"));
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            c.setUserId(userId);
        }
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("phone"));
        c.setAddress(rs.getString("address"));
        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            c.setRegistrationDate(regDate.toLocalDate());
        }
        return c;
    }
}
