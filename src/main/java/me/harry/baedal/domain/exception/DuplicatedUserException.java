package me.harry.baedal.domain.exception;

import me.harry.baedal.application.exception.BadRequestException;

public class DuplicatedUserException extends BadRequestException {
    public DuplicatedUserException(String message) {
        super(message);
    }
}
