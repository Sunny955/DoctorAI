package com.user_service.dto.Response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ForgotPasswordResponse extends BaseResponse{
    public ForgotPasswordResponse(String message, String status) {
        super(status, message);
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
