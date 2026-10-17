package com.simupi.pattern.factory;

import com.simupi.model.BankAccount;
import com.simupi.model.CurrentAccount;
import com.simupi.model.SavingsAccount;
import com.simupi.model.User;
import com.simupi.model.enums.AccountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.security.SecureRandom;

/**
 * Design Pattern: Factory Pattern.
 * Encapsulates the instantiation of different BankAccount subclasses
 * (SavingsAccount vs CurrentAccount), decoupling account creation logic from services/controllers.
 */
@Component
public class BankAccountFactory {

    private final SecureRandom random = new SecureRandom();

    /**
     * Creates a new BankAccount instance configured with account-type specific defaults.
     *
     * @param type           The account category (SAVINGS or CURRENT)
     * @param user           The account owner
     * @param bankName       The issuing bank name (e.g., HDFC Bank, SBI)
     * @param openingBalance Initial deposit amount
     * @return Configured concrete BankAccount instance
     */
    public BankAccount createAccount(AccountType type, User user, String bankName, BigDecimal openingBalance) {
        String accountNumber = generateAccountNumber();
        String ifscCode = generateIfsc(bankName);

        if (type == null) {
            type = AccountType.SAVINGS;
        }

        switch (type) {
            case CURRENT:
                return new CurrentAccount(user, accountNumber, bankName, ifscCode, openingBalance);
            case SAVINGS:
            default:
                return new SavingsAccount(user, accountNumber, bankName, ifscCode, openingBalance);
        }
    }

    /**
     * Generates a 16-digit simulated bank account number.
     */
    private String generateAccountNumber() {
        StringBuilder sb = new StringBuilder("50100");
        for (int i = 0; i < 11; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * Generates a realistic 11-character IFSC code based on bank name prefix.
     */
    private String generateIfsc(String bankName) {
        String prefix = "SIMB";
        if (bankName != null && bankName.toUpperCase().contains("HDFC")) {
            prefix = "HDFC";
        } else if (bankName != null && bankName.toUpperCase().contains("SBI")) {
            prefix = "SBIN";
        } else if (bankName != null && bankName.toUpperCase().contains("ICICI")) {
            prefix = "ICIC";
        } else if (bankName != null && bankName.toUpperCase().contains("AXIS")) {
            prefix = "UTIB";
        }
        return prefix + "000" + (1000 + random.nextInt(9000));
    }
}
