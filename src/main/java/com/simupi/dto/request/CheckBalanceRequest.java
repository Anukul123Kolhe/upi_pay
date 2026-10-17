package com.simupi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request payload for querying account balance securely using UPI PIN.
 */
public class CheckBalanceRequest {

    @NotBlank(message = "UPI PIN is required to check balance")
    @Pattern(regexp = "^\\d{4}|\\d{6}$", message = "UPI PIN must be 4 or 6 digits")
    private String upiPin;

    public CheckBalanceRequest() {}

    public CheckBalanceRequest(String upiPin) {
        this.upiPin = upiPin;
    }

    public String getUpiPin() {
        return upiPin;
    }

    public void setUpiPin(String upiPin) {
        this.upiPin = upiPin;
    }
}
