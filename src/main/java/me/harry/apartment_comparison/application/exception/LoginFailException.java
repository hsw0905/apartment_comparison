package me.harry.apartment_comparison.application.exception;

public class LoginFailException extends BadRequestException {
    public LoginFailException(String message) {
        super(message);
    }
}
