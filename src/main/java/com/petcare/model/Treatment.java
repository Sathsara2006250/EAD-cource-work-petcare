package com.petcare.model;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Treatment & Billing entity representing Veterinary medical care and fees (Transaction 2).
 */
public class Treatment {
    private int id;
    private int appointmentId;
    private String appointmentCode; // Joined
    private String customerName;    // Joined
    private String petName;         // Joined
    private String vetDoctor;       // Joined
    private String diagnosis;
    private String procedureDetails;
    private String medicationPrescribed;
    private double medicationCost;
    private double treatmentCost;
    private double totalAmount;
    private LocalDate treatmentDate;
    private String status;          // "Paid", "Pending", "Waived"
    private Timestamp createdAt;

    public Treatment() {}

    public Treatment(int id, int appointmentId, String diagnosis, String procedureDetails, String medicationPrescribed, double medicationCost, double treatmentCost, double totalAmount, LocalDate treatmentDate, String status) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.procedureDetails = procedureDetails;
        this.medicationPrescribed = medicationPrescribed;
        this.medicationCost = medicationCost;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.treatmentDate = treatmentDate;
        this.status = status;
    }

    public Treatment(int appointmentId, String diagnosis, String procedureDetails, String medicationPrescribed, double medicationCost, double treatmentCost, double totalAmount, LocalDate treatmentDate, String status) {
        this.appointmentId = appointmentId;
        this.diagnosis = diagnosis;
        this.procedureDetails = procedureDetails;
        this.medicationPrescribed = medicationPrescribed;
        this.medicationCost = medicationCost;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.treatmentDate = treatmentDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getVetDoctor() { return vetDoctor; }
    public void setVetDoctor(String vetDoctor) { this.vetDoctor = vetDoctor; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getProcedureDetails() { return procedureDetails; }
    public void setProcedureDetails(String procedureDetails) { this.procedureDetails = procedureDetails; }

    public String getMedicationPrescribed() { return medicationPrescribed; }
    public void setMedicationPrescribed(String medicationPrescribed) { this.medicationPrescribed = medicationPrescribed; }

    public double getMedicationCost() { return medicationCost; }
    public void setMedicationCost(double medicationCost) { this.medicationCost = medicationCost; }

    public double getTreatmentCost() { return treatmentCost; }
    public void setTreatmentCost(double treatmentCost) { this.treatmentCost = treatmentCost; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getTreatmentDate() { return treatmentDate; }
    public void setTreatmentDate(LocalDate treatmentDate) { this.treatmentDate = treatmentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Treatment #" + id + " for " + petName + " ($" + String.format("%.2f", totalAmount) + ")";
    }
}
