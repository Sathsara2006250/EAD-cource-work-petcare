package com.petcare.service;

import com.petcare.dao.UserDAO;
import com.petcare.exception.AuthenticationException;
import com.petcare.exception.ValidationException;
import com.petcare.model.User;
import com.petcare.util.SessionManager;
import com.petcare.util.ValidationHelper;

/**
 * Service for user authentication, registration, and session control.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String password) throws AuthenticationException, ValidationException {
        ValidationHelper.requireNonEmpty(username, "Username");
        ValidationHelper.requireNonEmpty(password, "Password");

        User user = userDAO.authenticate(username.trim(), password);
        if (user == null) {
            throw new AuthenticationException("Invalid username or password. Please try again.");
        }

        SessionManager.setCurrentUser(user);
        return user;
    }

    public void registerCustomerUser(String username, String password, String fullName, String email, String phone) throws ValidationException, AuthenticationException {
        ValidationHelper.requireNonEmpty(username, "Username");
        ValidationHelper.requireNonEmpty(password, "Password");
        ValidationHelper.requireNonEmpty(fullName, "Full Name");
        ValidationHelper.validateEmail(email);
        ValidationHelper.validatePhone(phone);

        if (password.length() < 4) {
            throw new ValidationException("Password must be at least 4 characters long.");
        }

        if (userDAO.findByUsername(username.trim()) != null) {
            throw new AuthenticationException("Username '" + username + "' is already taken. Please choose another.");
        }

        User user = new User(username.trim(), password, fullName.trim(), "Customer", email.trim(), phone.trim());
        userDAO.create(user);
    }
}
