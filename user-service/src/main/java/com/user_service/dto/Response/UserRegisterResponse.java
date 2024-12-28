package com.user_service.dto.Response;



public class UserRegisterResponse extends PostRequestSuccessResponse {
    public UserRegisterResponse(String message, String status, UserResponse data) {
        super(message, status, data);
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
