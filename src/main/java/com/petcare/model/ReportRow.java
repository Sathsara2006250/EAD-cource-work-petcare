package com.petcare.model;

import java.time.LocalDate;

/**
 * Composite Report DTO for multi-table JOIN operations:
 * appointments JOIN customers JOIN pets JOIN treatments.
 */
public class ReportRow {
    private String appointmentCode;
    private LocalDate appointmentDate;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String petName;
    private String species;
    private String breed;
    private String vetDoctor;
    private String diagnosis;
    private String procedureDetails;
    private double medicationCost;
    private double treatmentCost;
    private double totalAmount;
    private String paymentStatus;

    public ReportRow() {}

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getVetDoctor() { return vetDoctor; }
    public void setVetDoctor(String vetDoctor) { this.vetDoctor = vetDoctor; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getProcedureDetails() { return procedureDetails; }
    public void setProcedureDetails(String procedureDetails) { this.procedureDetails = procedureDetails; }

    public double getMedicationCost() { return medicationCost; }
    public void setMedicationCost(double medicationCost) { this.medicationCost = medicationCost; }

    public double getTreatmentCost() { return treatmentCost; }
    public void setTreatmentCost(double treatmentCost) { this.treatmentCost = treatmentCost; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
