package com.petcare.service;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.TreatmentDAO;
import com.petcare.exception.ValidationException;
import com.petcare.model.Treatment;
import com.petcare.util.ValidationHelper;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Service for Treatment Records & Medical Billing Transaction (Transaction UI 2).
 */
public class TreatmentService {

    private final TreatmentDAO treatmentDAO;
    private final AppointmentDAO appointmentDAO;

    public TreatmentService() {
        this.treatmentDAO = new TreatmentDAO();
        this.appointmentDAO = new AppointmentDAO();
    }

    public TreatmentService(TreatmentDAO treatmentDAO, AppointmentDAO appointmentDAO) {
        this.treatmentDAO = treatmentDAO;
        this.appointmentDAO = appointmentDAO;
    }

    public List<Treatment> getAllTreatments() {
        return treatmentDAO.findAll();
    }

    public Treatment getTreatmentById(int id) {
        return treatmentDAO.findById(id);
    }

    public Treatment getTreatmentByAppointmentId(int appointmentId) {
        return treatmentDAO.findByAppointmentId(appointmentId);
    }

    public List<Treatment> searchTreatments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return treatmentDAO.findAll();
        }
        return treatmentDAO.search(keyword.trim());
    }

    /**
     * Executes Treatment & Billing Transaction:
     * 1. Validates inputs & financial calculations
     * 2. Persists treatment bill
     * 3. Automatically updates appointment status to 'Completed'
     */
    public boolean saveTreatmentTransaction(Treatment treatment) throws ValidationException {
        validateTreatment(treatment);

        // Calculate total amount
        double total = treatment.getMedicationCost() + treatment.getTreatmentCost();
        treatment.setTotalAmount(total);

        if (treatment.getTreatmentDate() == null) {
            treatment.setTreatmentDate(LocalDate.now());
        }

        boolean success;
        if (treatment.getId() > 0) {
            success = treatmentDAO.update(treatment);
        } else {
            // Ensure single treatment per appointment
            Treatment existing = treatmentDAO.findByAppointmentId(treatment.getAppointmentId());
            if (existing != null) {
                treatment.setId(existing.getId());
                success = treatmentDAO.update(treatment);
            } else {
                success = treatmentDAO.create(treatment);
            }
        }

        if (success) {
            // Automatically complete the associated appointment in the transaction
            appointmentDAO.updateStatus(treatment.getAppointmentId(), "Completed");
        }

        return success;
    }

    public boolean deleteTreatment(int id) {
        return treatmentDAO.delete(id);
    }

    public double getTotalRevenue() {
        return treatmentDAO.calculateTotalRevenue();
    }

    public int getTotalTreatmentsCount() {
        return treatmentDAO.count();
    }

    private void validateTreatment(Treatment t) throws ValidationException {
        if (t == null) {
            throw new ValidationException("Treatment record cannot be null.");
        }
        if (t.getAppointmentId() <= 0) {
            throw new ValidationException("Please link the treatment to a valid Appointment.");
        }
        ValidationHelper.requireNonEmpty(t.getDiagnosis(), "Clinical Diagnosis");
        ValidationHelper.requireNonEmpty(t.getProcedureDetails(), "Procedure Details");
        if (t.getMedicationCost() < 0.0) {
            throw new ValidationException("Medication cost cannot be negative.");
        }
        if (t.getTreatmentCost() < 0.0) {
            throw new ValidationException("Treatment / Consultation fee cannot be negative.");
        }
        ValidationHelper.requireNonEmpty(t.getStatus(), "Payment Status");
    }
}
