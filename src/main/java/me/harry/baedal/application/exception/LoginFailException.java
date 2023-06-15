package me.harry.baedal.application.exception;

public class LoginFailException extends BadRequestException {
    public LoginFailException(String message) {
        super(message);
    }
}
