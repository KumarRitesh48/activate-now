package com.zenda.activatenow.dto;

public class ActivationResponse {

    private boolean success;
    private String message;
    private boolean activated;

    public ActivationResponse() {
    }

    public ActivationResponse(boolean success, String message, boolean activated) {
        this.success = success;
        this.message = message;
        this.activated = activated;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
