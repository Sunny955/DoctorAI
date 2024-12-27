package com.user_service.dto.Response;

public abstract class BaseResponse {
    protected String status;
    protected String message;

    public BaseResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }
}
