package com.user_service.exceptions;

public class PasswordLengthException extends RuntimeException {
    public PasswordLengthException(String message) {
        super(message);
    }
}
