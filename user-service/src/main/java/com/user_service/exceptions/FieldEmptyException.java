package com.user_service.exceptions;

public class FieldEmptyException extends RuntimeException{
    public FieldEmptyException(String message) {
        super(message);
    }
}
