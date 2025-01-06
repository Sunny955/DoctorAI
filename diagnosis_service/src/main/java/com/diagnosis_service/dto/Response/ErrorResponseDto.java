package com.diagnosis_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private String message;
    private String details;
    private int status;
    private long timestamp;
}
