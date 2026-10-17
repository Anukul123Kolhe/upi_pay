package com.simupi.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request payload for sending money to another UPI user.
 */
public class SendMoneyRequest {

    @NotBlank(message = "Receiver UPI ID or 10-digit mobile number is required")
    private String receiverQuery;

    @NotNull(message = "Transfer amount is required")
    @DecimalMin(value = "1.00", message = "Transfer amount must be at least ₹1.00")
    @DecimalMax(value = "100000.00", message = "Transfer amount cannot exceed ₹1,00,000.00 per transaction")
    private BigDecimal amount;

    @Size(max = 100, message = "Note cannot exceed 100 characters")
    private String note;

    @NotBlank(message = "UPI PIN is required")
    @Pattern(regexp = "^\\d{4}|\\d{6}$", message = "UPI PIN must be 4 or 6 digits")
    private String upiPin;

    public SendMoneyRequest() {}

    public SendMoneyRequest(String receiverQuery, BigDecimal amount, String note, String upiPin) {
        this.receiverQuery = receiverQuery;
        this.amount = amount;
        this.note = note;
        this.upiPin = upiPin;
    }

    public String getReceiverQuery() {
        return receiverQuery;
    }

    public void setReceiverQuery(String receiverQuery) {
        this.receiverQuery = receiverQuery;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUpiPin() {
        return upiPin;
    }

    public void setUpiPin(String upiPin) {
        this.upiPin = upiPin;
    }
}
