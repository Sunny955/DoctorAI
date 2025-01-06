package com.diagnosis_service.exceptions;

import com.diagnosis_service.dto.Response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), "Got an exception",HttpStatus.INTERNAL_SERVER_ERROR.value(),System.currentTimeMillis());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
