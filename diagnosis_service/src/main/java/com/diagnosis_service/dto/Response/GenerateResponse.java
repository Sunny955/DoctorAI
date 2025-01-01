package com.diagnosis_service.dto.Response;

public class GenerateResponse {
    private String status;
    private String data;

    public GenerateResponse() {}

    public GenerateResponse(String status, String data) {
        this.status = status;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(String data) {
        this.data = data;
    }
}
