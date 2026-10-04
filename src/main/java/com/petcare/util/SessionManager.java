package com.petcare.util;

import com.petcare.model.User;

/**
 * Manages active user session and security context.
 */
public class SessionManager {

    private static User currentUser;

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public static synchronized boolean isManager() {
        return currentUser != null && "Manager".equalsIgnoreCase(currentUser.getRole());
    }

    public static synchronized boolean isCustomer() {
        return currentUser != null && "Customer".equalsIgnoreCase(currentUser.getRole());
    }

    public static synchronized void logout() {
        currentUser = null;
    }
}
