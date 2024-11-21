package com.cookie.domain.user.exception;

import com.cookie.domain.user.entity.User;

public class RegistrationRequiredException extends RuntimeException {

    private final User user;

    public RegistrationRequiredException(User user) {
        super("Registration is required.");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
