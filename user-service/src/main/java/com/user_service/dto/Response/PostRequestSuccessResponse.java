package com.user_service.dto.Response;

public abstract class PostRequestSuccessResponse {
    protected final String message;
    protected final String status;

    protected final Object data;

    protected PostRequestSuccessResponse(String message, String status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }
}
