package me.harry.baedal.domain.exception;

import me.harry.baedal.application.exception.BadRequestException;

public class LoginFailException extends BadRequestException {
    public LoginFailException(String message) {
        super(message);
    }
}
