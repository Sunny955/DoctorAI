package com.user_service.dto.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateResponse extends BaseResponse{
    private Object data;

    public UserUpdateResponse(String status, String message, Object data) {
        super(status, message);
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }
}
