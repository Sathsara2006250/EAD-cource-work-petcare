package com.petcare.model;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Appointment entity representing Scheduled veterinary visits (Transaction 1).
 */
public class Appointment {
    private int id;
    private String appointmentCode;
    private int customerId;
    private String customerName; // Joined
    private int petId;
    private String petName;       // Joined
    private String vetDoctor;
    private LocalDate appointmentDate;
    private String appointmentTime;
    private String status;        // "Scheduled", "In-Progress", "Completed", "Cancelled"
    private String reason;
    private Timestamp createdAt;

    public Appointment() {}

    public Appointment(int id, String appointmentCode, int customerId, int petId, String vetDoctor, LocalDate appointmentDate, String appointmentTime, String status, String reason) {
        this.id = id;
        this.appointmentCode = appointmentCode;
        this.customerId = customerId;
        this.petId = petId;
        this.vetDoctor = vetDoctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reason = reason;
    }

    public Appointment(String appointmentCode, int customerId, int petId, String vetDoctor, LocalDate appointmentDate, String appointmentTime, String status, String reason) {
        this.appointmentCode = appointmentCode;
        this.customerId = customerId;
        this.petId = petId;
        this.vetDoctor = vetDoctor;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.reason = reason;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAppointmentCode() { return appointmentCode; }
    public void setAppointmentCode(String appointmentCode) { this.appointmentCode = appointmentCode; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getVetDoctor() { return vetDoctor; }
    public void setVetDoctor(String vetDoctor) { this.vetDoctor = vetDoctor; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return appointmentCode + " - " + petName + " (" + appointmentDate + " " + appointmentTime + ")";
    }
}
