package com.simupi.model;

import com.simupi.exception.SelfTransferException;
import com.simupi.model.enums.TransactionType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

/**
 * OOP: Inheritance - SendMoneyTransaction extends Transaction.
 * Represents an immediate peer-to-peer (P2P) push payment initiated by the sender.
 */
@Entity
@DiscriminatorValue("SEND_MONEY")
public class SendMoneyTransaction extends Transaction {

    public static final BigDecimal MAX_TRANSACTION_LIMIT = new BigDecimal("100000.00");

    public SendMoneyTransaction() {
        super();
    }

    public SendMoneyTransaction(String utr, User sender, User receiver, BankAccount senderAccount,
                                BankAccount receiverAccount, BigDecimal amount, String note) {
        super(utr, sender, receiver, senderAccount, receiverAccount, amount, note);
    }

    // OOP: Polymorphism - Overriding calculateCharges: P2P UPI transfers are zero-rated (Free)
    @Override
    public BigDecimal calculateCharges() {
        return BigDecimal.ZERO;
    }

    // OOP: Polymorphism - Overriding transaction validation rules
    @Override
    public void validateRules() {
        if (getSender() == null || getReceiver() == null) {
            throw new IllegalArgumentException("Both sender and receiver must be specified for a send-money transfer.");
        }

        // Rule 1: No self-transfer permitted
        if (getSender().getId() != null && getSender().getId().equals(getReceiver().getId())) {
            throw new SelfTransferException();
        }

        // Rule 2: Amount must be strictly positive
        if (getAmount() == null || getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly greater than zero.");
        }

        // Rule 3: Single transaction limit capped at ₹1,00,000 (standard NPCI simulated guideline)
        if (getAmount().compareTo(MAX_TRANSACTION_LIMIT) > 0) {
            throw new IllegalArgumentException(String.format("Transfer amount ₹%s exceeds the maximum single transfer limit of ₹%s.",
                    getAmount(), MAX_TRANSACTION_LIMIT));
        }
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.SEND_MONEY;
    }
}
