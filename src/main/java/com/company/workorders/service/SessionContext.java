package com.company.workorders.service;

import com.company.workorders.model.User;

/**
 * Stores the currently authenticated user for the app session.
 */
public final class SessionContext {
    private static User currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }
}