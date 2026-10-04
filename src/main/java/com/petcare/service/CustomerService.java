package com.petcare.service;

import com.petcare.dao.CustomerDAO;
import com.petcare.exception.ValidationException;
import com.petcare.model.Customer;
import com.petcare.util.ValidationHelper;
import java.time.LocalDate;
import java.util.List;

/**
 * Business Service for Customer operations.
 */
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public CustomerService(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    public Customer getCustomerById(int id) {
        return customerDAO.findById(id);
    }

    public Customer getCustomerByUserId(int userId) {
        return customerDAO.findByUserId(userId);
    }

    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return customerDAO.findAll();
        }
        return customerDAO.search(keyword.trim());
    }

    public boolean saveCustomer(Customer customer) throws ValidationException {
        validateCustomer(customer);
        if (customer.getId() > 0) {
            return customerDAO.update(customer);
        } else {
            if (customer.getRegistrationDate() == null) {
                customer.setRegistrationDate(LocalDate.now());
            }
            return customerDAO.create(customer);
        }
    }

    public boolean deleteCustomer(int id) {
        return customerDAO.delete(id);
    }

    public int getTotalCustomersCount() {
        return customerDAO.count();
    }

    private void validateCustomer(Customer c) throws ValidationException {
        if (c == null) {
            throw new ValidationException("Customer object cannot be null.");
        }
        ValidationHelper.requireNonEmpty(c.getFullName(), "Customer Full Name");
        ValidationHelper.validateEmail(c.getEmail());
        ValidationHelper.validatePhone(c.getPhone());
        ValidationHelper.requireNonEmpty(c.getAddress(), "Customer Address");
    }
}
