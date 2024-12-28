package com.user_service.dto.Response;

import lombok.Setter;

@Setter
public class LogoutResponse extends BaseResponse {

    public LogoutResponse(String status, String message) {
        super(status, message);
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
