package com.petcare.service;

import com.petcare.dao.AppointmentDAO;
import com.petcare.dao.CustomerDAO;
import com.petcare.dao.PetDAO;
import com.petcare.exception.InvalidAppointmentException;
import com.petcare.exception.ValidationException;
import com.petcare.model.Appointment;
import com.petcare.model.Pet;
import com.petcare.util.ValidationHelper;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Service for Appointment Operations & Booking Transaction (Transaction UI 1).
 * Enforces business rules and throws InvalidAppointmentException when violated.
 */
public class AppointmentService {

    private final AppointmentDAO appointmentDAO;
    private final CustomerDAO customerDAO;
    private final PetDAO petDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
        this.customerDAO = new CustomerDAO();
        this.petDAO = new PetDAO();
    }

    public AppointmentService(AppointmentDAO appointmentDAO, CustomerDAO customerDAO, PetDAO petDAO) {
        this.appointmentDAO = appointmentDAO;
        this.customerDAO = customerDAO;
        this.petDAO = petDAO;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.findAll();
    }

    public List<Appointment> getAppointmentsByCustomerId(int customerId) {
        return appointmentDAO.findByCustomerId(customerId);
    }

    public Appointment getAppointmentById(int id) {
        return appointmentDAO.findById(id);
    }

    public List<Appointment> searchAppointments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return appointmentDAO.findAll();
        }
        return appointmentDAO.search(keyword.trim());
    }

    /**
     * Executes the Appointment Booking Transaction with strict business rule checks.
     * @throws InvalidAppointmentException when scheduling rules are violated
     * @throws ValidationException when form inputs are invalid
     */
    public boolean bookAppointment(Appointment appt) throws InvalidAppointmentException, ValidationException {
        validateAppointmentInputs(appt);

        // Rule 1: Appointment date cannot be in the past for new appointments
        if (appt.getId() <= 0 && appt.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new InvalidAppointmentException(
                "Invalid Appointment Date: Cannot schedule appointments in the past (" + appt.getAppointmentDate() + ")."
            );
        }

        // Rule 2: Pet must belong to the selected customer
        Pet pet = petDAO.findById(appt.getPetId());
        if (pet == null || pet.getCustomerId() != appt.getCustomerId()) {
            throw new InvalidAppointmentException(
                "Invalid Pet Selection: The selected pet does not belong to the selected customer."
            );
        }

        // Rule 3: Doctor schedule conflict detection (prevent double-booking)
        boolean conflict = appointmentDAO.hasVetScheduleConflict(
            appt.getVetDoctor(),
            appt.getAppointmentDate(),
            appt.getAppointmentTime(),
            appt.getId()
        );
        if (conflict) {
            throw new InvalidAppointmentException(
                "Schedule Conflict: " + appt.getVetDoctor() + " is already booked for " + 
                appt.getAppointmentDate() + " at " + appt.getAppointmentTime() + ". Please select another time slot."
            );
        }

        // Generate code if new
        if (appt.getId() <= 0 && (appt.getAppointmentCode() == null || appt.getAppointmentCode().trim().isEmpty())) {
            appt.setAppointmentCode(appointmentDAO.generateNextCode());
        }

        if (appt.getStatus() == null || appt.getStatus().trim().isEmpty()) {
            appt.setStatus("Scheduled");
        }

        if (appt.getId() > 0) {
            return appointmentDAO.update(appt);
        } else {
            return appointmentDAO.create(appt);
        }
    }

    public boolean updateStatus(int appointmentId, String newStatus) throws InvalidAppointmentException {
        if (appointmentId <= 0) {
            throw new InvalidAppointmentException("Invalid appointment ID.");
        }
        return appointmentDAO.updateStatus(appointmentId, newStatus);
    }

    public boolean deleteAppointment(int id) {
        return appointmentDAO.delete(id);
    }

    public int getTotalAppointmentsCount() {
        return appointmentDAO.count();
    }

    public int getTodayAppointmentsCount() {
        return appointmentDAO.countTodayAppointments();
    }

    private void validateAppointmentInputs(Appointment a) throws ValidationException {
        if (a == null) {
            throw new ValidationException("Appointment cannot be null.");
        }
        if (a.getCustomerId() <= 0) {
            throw new ValidationException("Please select a customer.");
        }
        if (a.getPetId() <= 0) {
            throw new ValidationException("Please select a pet.");
        }
        ValidationHelper.requireNonEmpty(a.getVetDoctor(), "Veterinary Doctor");
        if (a.getAppointmentDate() == null) {
            throw new ValidationException("Please select an appointment date.");
        }
        ValidationHelper.requireNonEmpty(a.getAppointmentTime(), "Appointment Time");
        ValidationHelper.requireNonEmpty(a.getReason(), "Reason for visit");
    }
}
