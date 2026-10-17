package com.simupi.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request payload for creating a collect request (pull payment).
 */
public class CollectMoneyRequest {

    @NotBlank(message = "Payer UPI ID or 10-digit mobile number is required")
    private String payerQuery;

    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "1.00", message = "Requested amount must be at least ₹1.00")
    @DecimalMax(value = "50000.00", message = "Collect request amount cannot exceed ₹50,000.00")
    private BigDecimal amount;

    @Size(max = 100, message = "Note cannot exceed 100 characters")
    private String note;

    public CollectMoneyRequest() {}

    public CollectMoneyRequest(String payerQuery, BigDecimal amount, String note) {
        this.payerQuery = payerQuery;
        this.amount = amount;
        this.note = note;
    }

    public String getPayerQuery() {
        return payerQuery;
    }

    public void setPayerQuery(String payerQuery) {
        this.payerQuery = payerQuery;
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
}
