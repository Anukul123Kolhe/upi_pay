package com.simupi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request payload for setting or resetting UPI PIN.
 */
public class SetPinRequest {

    @NotBlank(message = "Account password is required to change PIN")
    private String password;

    @NotBlank(message = "New UPI PIN is required")
    @Pattern(regexp = "^\\d{4}|\\d{6}$", message = "New UPI PIN must be either 4 or 6 digits")
    private String newPin;

    public SetPinRequest() {}

    public SetPinRequest(String password, String newPin) {
        this.password = password;
        this.newPin = newPin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }
}
