package com.petcare.dao;

import com.petcare.exception.DatabaseException;
import com.petcare.model.Pet;
import com.petcare.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Pet operations (DAO Pattern).
 */
public class PetDAO {

    public List<Pet> findAll() throws DatabaseException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT p.*, c.full_name AS customer_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.id " +
                     "ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToPet(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch pets: " + e.getMessage(), e);
        }
        return list;
    }

    public Pet findById(int id) throws DatabaseException {
        String sql = "SELECT p.*, c.full_name AS customer_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.id " +
                     "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPet(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pet by ID: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Pet> findByCustomerId(int customerId) throws DatabaseException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT p.*, c.full_name AS customer_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.id " +
                     "WHERE p.customer_id = ? " +
                     "ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch pets for customer ID: " + e.getMessage(), e);
        }
        return list;
    }

    public List<Pet> search(String keyword) throws DatabaseException {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT p.*, c.full_name AS customer_name " +
                     "FROM pets p " +
                     "JOIN customers c ON p.customer_id = c.id " +
                     "WHERE p.name LIKE ? OR p.species LIKE ? OR p.breed LIKE ? OR c.full_name LIKE ? " +
                     "ORDER BY p.id DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            stmt.setString(1, query);
            stmt.setString(2, query);
            stmt.setString(3, query);
            stmt.setString(4, query);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPet(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to search pets: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean create(Pet pet) throws DatabaseException {
        String sql = "INSERT INTO pets (customer_id, name, species, breed, age, gender, weight, medical_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pet.getCustomerId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            stmt.setInt(5, pet.getAge());
            stmt.setString(6, pet.getGender());
            stmt.setDouble(7, pet.getWeight());
            stmt.setString(8, pet.getMedicalNotes());
            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        pet.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create pet: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean update(Pet pet) throws DatabaseException {
        String sql = "UPDATE pets SET customer_id = ?, name = ?, species = ?, breed = ?, age = ?, gender = ?, weight = ?, medical_notes = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pet.getCustomerId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            stmt.setInt(5, pet.getAge());
            stmt.setString(6, pet.getGender());
            stmt.setDouble(7, pet.getWeight());
            stmt.setString(8, pet.getMedicalNotes());
            stmt.setInt(9, pet.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update pet: " + e.getMessage(), e);
        }
    }

    public boolean delete(int id) throws DatabaseException {
        String sql = "DELETE FROM pets WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete pet: " + e.getMessage(), e);
        }
    }

    public int count() throws DatabaseException {
        String sql = "SELECT COUNT(*) FROM pets";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to count pets: " + e.getMessage(), e);
        }
        return 0;
    }

    private Pet mapResultSetToPet(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getInt("id"));
        p.setCustomerId(rs.getInt("customer_id"));
        p.setCustomerName(rs.getString("customer_name"));
        p.setName(rs.getString("name"));
        p.setSpecies(rs.getString("species"));
        p.setBreed(rs.getString("breed"));
        p.setAge(rs.getInt("age"));
        p.setGender(rs.getString("gender"));
        p.setWeight(rs.getDouble("weight"));
        p.setMedicalNotes(rs.getString("medical_notes"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
