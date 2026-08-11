package com.zenda.activatenow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Payload for POST /api/students/{id}/activate
 *
 * Validation rules (mirrored on the Angular reactive form for instant feedback,
 * and re-checked here as the source of truth):
 *  - phoneNumber: must be "+91" followed by exactly 10 digits
 *  - email: must match a standard "...@...com" style address
 *  - panNumber: standard Indian PAN format (5 letters, 4 digits, 1 letter)
 */
public class ActivationRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+91[0-9]{10}$", message = "Phone must be in format +91XXXXXXXXXX (10 digits)")
    private String phoneNumber;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "PAN must match format AAAAA9999A")
    private String panNumber;

    @NotBlank(message = "Name as in PAN card is required")
    @Size(min = 2, message = "Name must contain at least 2 characters")
    private String nameAsInPan;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.com$", message = "Email must be a valid ...@...com address")
    private String email;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getNameAsInPan() {
        return nameAsInPan;
    }

    public void setNameAsInPan(String nameAsInPan) {
        this.nameAsInPan = nameAsInPan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
