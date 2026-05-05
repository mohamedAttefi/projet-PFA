package com.company.workorders.service;

import com.company.workorders.model.User;

/**
 * Result of a login attempt.
 */
public class AuthResult {
    private final boolean success;
    private final User user;
    private final String message;

    private AuthResult(boolean success, User user, String message) {
        this.success = success;
        this.user = user;
        this.message = message;
    }

    public static AuthResult success(User user) {
        return new AuthResult(true, user, null);
    }

    public static AuthResult failure(String message) {
        return new AuthResult(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}