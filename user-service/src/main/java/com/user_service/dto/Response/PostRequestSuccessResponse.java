package com.user_service.dto.Response;

public abstract class PostRequestSuccessResponse extends BaseResponse {
    protected final Object data;

    protected PostRequestSuccessResponse(String message, String status, Object data) {
        super(status, message);
        this.data = data;
    }
}
