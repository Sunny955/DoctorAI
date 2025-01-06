package com.diagnosis_service.dto.Response;

public class ErrorResponseDto {
    private String message;
    private String details;
    private int status;
    private long timestamp;

    public ErrorResponseDto(String message, String details, int status, long timestamp) {
        this.message = message;
        this.details = details;
        this.status = status;
        this.timestamp = timestamp;
    }
}
