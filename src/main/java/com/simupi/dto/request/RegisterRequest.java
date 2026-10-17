package com.simupi.dto.request;

import com.simupi.model.enums.AccountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Request payload for registering a new user along with their initial virtual bank account and UPI PIN.
 */
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    private String mobile;

    @NotBlank(message = "Email address is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 50, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotNull(message = "Initial opening balance is required")
    @DecimalMin(value = "1000.00", message = "Opening balance must be at least ₹1,000.00")
    private BigDecimal openingBalance;

    @NotBlank(message = "UPI PIN is required")
    @Pattern(regexp = "^\\d{4}|\\d{6}$", message = "UPI PIN must be either 4 or 6 digits")
    private String upiPin;

    public RegisterRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public String getUpiPin() {
        return upiPin;
    }

    public void setUpiPin(String upiPin) {
        this.upiPin = upiPin;
    }
}
