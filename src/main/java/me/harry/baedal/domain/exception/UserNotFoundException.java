package me.harry.baedal.domain.exception;

import me.harry.baedal.application.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
